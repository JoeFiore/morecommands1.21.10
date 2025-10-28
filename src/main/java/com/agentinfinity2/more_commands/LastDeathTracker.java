package com.agentinfinity2.more_commands;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Use the project's EventBusSubscriber to auto-register static @SubscribeEvent handlers
@EventBusSubscriber(modid = MoreCommands.MODID)
public class LastDeathTracker {

    // Map to store the last death location for each player (UUID -> DeathLocation)
    // UUID is used to uniquely identify players across sessions.
    private static final Map<UUID, DeathLocation> DEATH_LOCATIONS = new HashMap<>();

    // --- Death Event Listener ---

    // This event fires whenever an entity dies. We check if it was a player.
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {

            // 1. Get the exact location and dimension
            BlockPos pos = player.blockPosition();
            ResourceKey<Level> dimension = player.level().dimension();

            // 2. Create the DeathLocation object
            DeathLocation deathLoc = new DeathLocation(
                    pos.getX(), pos.getY(), pos.getZ(), dimension
            );

            // 3. Store it in our static map
            DEATH_LOCATIONS.put(player.getUUID(), deathLoc);
        }
    }

    // --- Public Getter Method for the Command ---

    public static DeathLocation getLastDeathLocation(UUID playerUUID) {
        return DEATH_LOCATIONS.get(playerUUID);
    }

    // --- Data Record ---

    // A simple record to hold the coordinates and dimension cleanly
    public record DeathLocation(
            int x,
            int y,
            int z,
            ResourceKey<Level> dimension
    ) {}
}