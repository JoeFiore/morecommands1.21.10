package com.agentinfinity2.more_commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.server.MinecraftServer;

public class StopServerCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("stopserver")
                        // Requires permission level 4 (server ops/console)
                        .requires(source -> source.hasPermission(0))
                        .executes(context -> handleCommand(context.getSource()))
        );
    }

    private static int handleCommand(CommandSourceStack source) {

        // Announce the start of the shutdown sequence
        source.getServer().getPlayerList().broadcastSystemMessage(
                Component.literal("🚨 Server Shutdown initiated! Starting 5-second countdown...").withStyle(ChatFormatting.RED, ChatFormatting.BOLD),
                false
        );

        // Start the countdown sequence
        startCountdown(source.getServer(), 5);

        return 1;
    }

    /**
     * Handles the delayed, recursive shutdown process using a dedicated thread
     * for time delay, and server.execute() for thread safety.
     * @param server The MinecraftServer instance.
     * @param secondsRemaining The number of seconds left in the countdown.
     */
    private static void startCountdown(MinecraftServer server, int secondsRemaining) {

        if (secondsRemaining <= 0) {
            // BASE CASE: Countdown finished. All code here runs on the main server thread.

            // Announce final message
            server.getPlayerList().broadcastSystemMessage(
                    Component.literal("🛑 Goodbye!").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD),
                    false
            );

            // Execute the final server shutdown on the main server thread
            server.halt(false); // 'halt(false)' is the method to stop the server gracefully

            return;
        }

        // RECURSIVE STEP: Announce time remaining and schedule the next step.

        Component countdownMessage;

        if (secondsRemaining > 2) {
            countdownMessage = Component.literal("Shutting down in " + secondsRemaining + " seconds...").withStyle(ChatFormatting.YELLOW);
        } else {
            // Use an intensifying color for the final two seconds
            countdownMessage = Component.literal("Shutting down in " + secondsRemaining + " seconds!").withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD);
        }

        // Send the message to all players (must be run on the main server thread)
        server.getPlayerList().broadcastSystemMessage(countdownMessage, false);

        // Schedule the next step using a new Java Thread for the 1-second delay
        new Thread(() -> {
            try {
                // Wait for 1 second (1000 milliseconds) outside of the main server thread
                Thread.sleep(1000);

                // Use server.execute() to safely put the next step back on the main server thread
                server.execute(() -> {
                    startCountdown(server, secondsRemaining - 1);
                });
            } catch (InterruptedException e) {
                // Handle interruption if necessary
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}