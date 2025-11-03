package com.agentinfinity2.more_commands;

import net.neoforged.neoforge.common.ModConfigSpec;

// This class holds all configuration keys for your mod.
public class ModConfig {

    public static final ModConfigSpec SPEC;
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // --- Backup Feature Configuration ---

    // 1. Backup Directory Path
    public static final ModConfigSpec.ConfigValue<String> BACKUP_PATH_KEY;

    // 2. Backup Interval in Minutes
    public static final ModConfigSpec.IntValue BACKUP_INTERVAL_MINUTES;

    static {

        // --- BACKUP SETTINGS SECTION ---
        BUILDER.push("backupSettings");

        BACKUP_PATH_KEY = BUILDER
                .comment("The path where world backups will be saved, relative to the server root (e.g., 'backups').")
                .define("backupPath", "backups");

        BACKUP_INTERVAL_MINUTES = BUILDER
                .comment("The interval (in minutes) between automated world backups. Set to 0 to disable this feature.")
                .defineInRange("backupIntervalMinutes", 3, 0, 60); // Default: 3 min, Max: 60 min

        BUILDER.pop();

        // Finalize the configuration specification
        SPEC = BUILDER.build();
    }

    // Note: No extra methods are needed here; the configurations are accessed via ModConfig.BACKUP_PATH_KEY.get()
}