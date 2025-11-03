package com.agentinfinity2.more_commands;

import com.agentinfinity2.more_commands.ModConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import java.util.zip.Deflater;
import java.io.BufferedOutputStream;
import net.minecraft.world.level.storage.LevelResource;

// Use project EventBusSubscriber so static @SubscribeEvent methods are auto-registered
@EventBusSubscriber(modid = MoreCommands.MODID)
public class BackupScheduler {

    // Scheduled executor for running periodic backups off the server thread
    private static java.util.concurrent.ScheduledExecutorService scheduler;

    // The server instance (cached when server starts)
    private static MinecraftServer server;

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();

        int intervalMinutes = 3;

        MoreCommands.LOGGER.info("Automated world backups enabled every {} minutes.", intervalMinutes);

        // Create scheduler if not already created
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = java.util.concurrent.Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "morecommands-backup-thread");
                t.setDaemon(true);
                return t;
            });
        }

        // Schedule first run after `intervalMinutes` and repeat every `intervalMinutes`
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Ensure world save and backup run on the main server thread
                server.execute(() -> createBackup(server));
            } catch (Exception ex) {
                MoreCommands.LOGGER.error("Error scheduling backup: {}", ex.getMessage());
            }
        }, intervalMinutes, intervalMinutes, java.util.concurrent.TimeUnit.MINUTES);
    }

    /**
     * Executes the world save and compression logic. Must be run on the main server thread.
     */
    private static void createBackup(MinecraftServer server) {
        if (server == null) return;

        try {
            // Determine world path and backups directory next to the world folder
            Path worldPath = server.getWorldPath(LevelResource.ROOT);
            Path backupDir = worldPath.resolveSibling("backups");

            // Ensure the backup directory exists
            Files.createDirectories(backupDir);
            // 1. Force the world to save and sync chunks
            server.saveAllChunks(true, true, true);

            // 2. Prepare paths and filename
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String timestamp = dateFormat.format(new Date());
            String serverName = server.getWorldData().getLevelName().replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = serverName + "_backup_" + timestamp + ".zip";

            Path zipPath = backupDir.resolve(fileName);

            // 3. Perform the zip compression on a separate thread to avoid freezing the server
            new Thread(() -> {
                try {
                    zipWorldDirectory(worldPath, zipPath, server);
                    // On success, do not broadcast to players (silent success)
                    MoreCommands.LOGGER.info("Backup created: {}", zipPath);
                    // Enforce retention: keep only the latest 5 backups
                    try {
                        cleanupOldBackups(backupDir, 5, server);
                    } catch (Exception ex) {
                        MoreCommands.LOGGER.error("Failed to cleanup old backups: {}", ex.getMessage(), ex);
                        String errorType = ex.getClass().getSimpleName();
                        String errMsg = ex.getMessage() == null ? "" : (": " + ex.getMessage());
                        server.execute(() -> {
                            server.getPlayerList().broadcastSystemMessage(
                                    Component.literal("❌ Backup cleanup FAILED (" + errorType + ")" + errMsg).withStyle(ChatFormatting.DARK_RED), false
                            );
                        });
                    }
                } catch (IOException e) {
                    // Log full error and notify players with error type and message
                    MoreCommands.LOGGER.error("Failed to create world backup: {}", e.getMessage(), e);
                    String errorType = e.getClass().getSimpleName();
                    String errMsg = e.getMessage() == null ? "" : (": " + e.getMessage());
                    server.execute(() -> {
                        server.getPlayerList().broadcastSystemMessage(
                                Component.literal("❌ Backup FAILED (" + errorType + ")" + errMsg).withStyle(ChatFormatting.DARK_RED), false
                        );
                    });
                }
            }, "morecommands-backup-worker").start();

        } catch (IOException e) {
            MoreCommands.LOGGER.error("Failed to set up backup directory: {}", e.getMessage(), e);
            String errorType = e.getClass().getSimpleName();
            String errMsg = e.getMessage() == null ? "" : (": " + e.getMessage());
            server.execute(() -> {
                server.getPlayerList().broadcastSystemMessage(
                        Component.literal("❌ Backup FAILED (" + errorType + ")" + errMsg).withStyle(ChatFormatting.DARK_RED), false
                );
            });
        }
    }

    /**
     * Removes older backups, keeping only the newest {@code maxKeep} files.
     */
    private static void cleanupOldBackups(Path backupDir, int maxKeep, MinecraftServer server) throws IOException {
        if (!Files.exists(backupDir) || !Files.isDirectory(backupDir)) return;

        try (java.util.stream.Stream<Path> stream = Files.list(backupDir)) {
            java.util.List<Path> files = stream
                    .filter(Files::isRegularFile)
                    .sorted((a, b) -> {
                        try {
                            return Long.compare(Files.getLastModifiedTime(b).toMillis(), Files.getLastModifiedTime(a).toMillis());
                        } catch (IOException e) {
                            return 0;
                        }
                    })
                    .collect(java.util.stream.Collectors.toList());

            if (files.size() <= maxKeep) return;

            for (int i = maxKeep; i < files.size(); i++) {
                Path p = files.get(i);
                try {
                    Files.deleteIfExists(p);
                    MoreCommands.LOGGER.info("Deleted old backup: {}", p);
                } catch (IOException ex) {
                    // bubble up so caller can broadcast the failure
                    throw ex;
                }
            }
        }
    }

    /**
     * Zips the contents of the world directory.
     */
    private static void zipWorldDirectory(Path sourceDir, Path zipPath, MinecraftServer server) throws IOException {
        // Only include key world files/folders to avoid zipping the whole server or backups folder
        String[] includeTop = new String[]{
                "level.dat",
                "level.dat_old",
                "session.lock",
                "region",
                "data",
                "playerdata",
                "advancements",
                "poi",
                "datapacks",
                "entities",
                "stats",
                "DIM-1",
                "DIM1"
        };

        List<Path> toAdd = new ArrayList<>();
        for (String name : includeTop) {
            Path p = sourceDir.resolve(name);
            if (Files.exists(p)) {
                toAdd.add(p);
            }
        }

        // Create parent dir if needed
        Files.createDirectories(zipPath.getParent());

        try (ZipOutputStream zs = new ZipOutputStream(new BufferedOutputStream(Files.newOutputStream(zipPath)))) {
            // Use stronger compression to reduce archive size
            zs.setLevel(Deflater.BEST_COMPRESSION);

            for (Path root : toAdd) {
                if (Files.isDirectory(root)) {
                    // Walk directory and add files
                    Files.walk(root)
                            .filter(path -> !Files.isDirectory(path))
                            .forEach(path -> {
                                // Compute relative path inside the archive from the world root
                                Path rel = sourceDir.relativize(path);
                                String entryName = rel.toString().replace('\\', '/');
                                try {
                                    ZipEntry zipEntry = new ZipEntry(entryName);
                                    zs.putNextEntry(zipEntry);
                                    Files.copy(path, zs);
                                    zs.closeEntry();
                                } catch (IOException e) {
                                    MoreCommands.LOGGER.warn("Failed to zip file: {} - {}", path, e.getMessage());
                                }
                            });
                } else {
                    // Single file (like level.dat)
                    Path rel = sourceDir.relativize(root);
                    String entryName = rel.toString().replace('\\', '/');
                    try {
                        ZipEntry zipEntry = new ZipEntry(entryName);
                        zs.putNextEntry(zipEntry);
                        Files.copy(root, zs);
                        zs.closeEntry();
                    } catch (IOException e) {
                        MoreCommands.LOGGER.warn("Failed to zip file: {} - {}", root, e.getMessage());
                    }
                }
            }
        }
    }
}