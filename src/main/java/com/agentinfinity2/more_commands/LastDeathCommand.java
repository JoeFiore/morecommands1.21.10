package com.agentinfinity2.more_commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.resources.ResourceKey;

// Import the DeathLocation record from the tracker class
import com.agentinfinity2.more_commands.LastDeathTracker.DeathLocation;

import java.net.URI;
import java.util.UUID;


public class LastDeathCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("ld")
                        .requires(source -> source.getEntity() instanceof ServerPlayer) // Only players can use this
                        .executes(context -> handleCommand(context.getSource()))
        );
    }

    private static int handleCommand(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) {
            source.sendFailure(Component.literal("Only players can use this command."));
            return 0;
        }

        UUID playerUUID = player.getUUID();
        DeathLocation deathLoc = LastDeathTracker.getLastDeathLocation(playerUUID);

        if (deathLoc == null) {
            source.sendFailure(Component.literal("You haven't died since the server last started!").withStyle(ChatFormatting.RED));
            return 0;
        }

        // --- Formatting the Output ---

        MutableComponent message = Component.literal("Your Last Death: ")
                .withStyle(ChatFormatting.GOLD);

        // 1. Get dimension name
        String dimName = getDimensionName(deathLoc.dimension());

        // 2. Create the coordinates link
        // The /tp command needs to be built into the clickable text
        String coordsText = String.format(" [ %d, %d, %d ] in %s",
                deathLoc.x(), deathLoc.y(), deathLoc.z(), dimName);

        // Command string to be executed when the text is clicked
        String tpCommand = String.format("/tp @s %d %d %d",
                deathLoc.x(), deathLoc.y(), deathLoc.z());

        message.append(
                Component.literal(coordsText)
                        .withStyle(style -> style
                                .withColor(ChatFormatting.AQUA)
                                // FIX: Use the specific RunCommand Record as required by ClickEvent.java
                                .withClickEvent(new net.minecraft.network.chat.ClickEvent.RunCommand(tpCommand))
                                .withUnderlined(true)
                        )
        );

        // 3. Add a tip
        message.append(Component.literal("\nClick coordinates to teleport!").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));

        source.sendSuccess(() -> message, false);
        return 1;
    }

    // Helper method to make dimension names cleaner
    private static String getDimensionName(ResourceKey<Level> dimension) {
        if (dimension.equals(Level.OVERWORLD)) {
            return "Overworld";
        } else if (dimension.equals(Level.NETHER)) {
            return "The Nether";
        } else if (dimension.equals(Level.END)) {
            return "The End";
        }
        return dimension.location().toString();
    }
}