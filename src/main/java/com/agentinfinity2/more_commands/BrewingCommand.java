package com.agentinfinity2.more_commands; // Your package

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BrewingCommand {

    // --- 1. The Data: Potions and their recipes ---
    // Key: Potion Name (used in the command)
    // Value: Brewing steps/description
    private static final Map<String, List<String>> POTION_RECIPES;

    static {
        POTION_RECIPES = new java.util.HashMap<>();
        // --- ADD ALL YOUR RECIPES HERE ---
        POTION_RECIPES.put("swiftness", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Sugar",
                "Effect: Speed",
                "Result: Potion of Swiftness"
        ));

        POTION_RECIPES.put("slowness", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Sugar",
                "Corrupting Ingredient: Fermented Spider Eye",
                "Effect: Slowness",
                "Result: Potion of Slowness"
        ));

        // ... continue adding the rest of the 13 recipes using POTION_RECIPES.put("key", Arrays.asList(...)) ...

        POTION_RECIPES.put("jump_boost", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Rabbit's Foot",
                "Effect: Jump Boost",
                "Result: Potion of Jump Boost"
        ));

        POTION_RECIPES.put("healing", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Glistering Melon Slice",
                "Effect: Instant Health",
                "Result: Potion of Healing"
        ));

        POTION_RECIPES.put("damage", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Glistering Melon Slice",
                "Corrupting Ingredient: Fermented Spider Eye",
                "Effect: Instant Damage",
                "Result: Potion of Harming/Damage"
        ));

        POTION_RECIPES.put("poison", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Spider Eye",
                "Effect: Poison",
                "Result: Potion of Poison"
        ));

        POTION_RECIPES.put("water_breathing", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Pufferfish",
                "Effect: Water Breathing",
                "Result: Potion of Water Breathing"
        ));

        POTION_RECIPES.put("fire_resistance", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Magma Cream",
                "Effect: Fire Resistance",
                "Result: Potion of Fire Resistance"
        ));

        POTION_RECIPES.put("strength", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Blaze Powder",
                "Effect: Strength",
                "Result: Potion of Strength"
        ));

        POTION_RECIPES.put("weakness", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Blaze Powder",
                "Corrupting Ingredient: Fermented Spider Eye",
                "Effect: Weakness",
                "Result: Potion of Weakness"
        ));

        POTION_RECIPES.put("regeneration", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Ghast Tear",
                "Effect: Regeneration",
                "Result: Potion of Regeneration"
        ));

        POTION_RECIPES.put("night_vision", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Golden Carrot",
                "Effect: Night Vision",
                "Result: Potion of Night Vision"
        ));

        POTION_RECIPES.put("invisibility", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Golden Carrot",
                "Corrupting Ingredient: Fermented Spider Eye",
                "Effect: Invisibility",
                "Result: Potion of Invisibility"
        ));

        POTION_RECIPES.put("turtle_master", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Turtle Shell",
                "Effect: Slowness and Resistance",
                "Result: Potion of the Turtle Master"
        ));

        POTION_RECIPES.put("slow_falling", Arrays.asList(
                "Base: Awkward Potion (Nether Wart + Water Bottle)",
                "Ingredient 1: Phantom Membrane",
                "Effect: Slow Falling",
                "Result: Potion of Slow Falling"
        ));
    }

    // List of keys for Tab Completion
    private static final List<String> POTION_KEYS = POTION_RECIPES.keySet().stream()
            .sorted()
            .collect(Collectors.toList());

    // ... rest of the command code is unchanged ...

    // Tab Completion Provider
    private static final SuggestionProvider<CommandSourceStack> POTION_SUGGESTIONS =
            (context, builder) -> SharedSuggestionProvider.suggest(POTION_KEYS, builder);

    // --- 2. Registration and Argument Handling ---
    public static void register(com.mojang.brigadier.CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("brewing")
                        .then(Commands.argument("potion", StringArgumentType.string())
                                .suggests(POTION_SUGGESTIONS) // Attach the tab completion
                                .executes(BrewingCommand::handleCommand)
                        )
                        // Fallback for /brewing with no arguments
                        .executes(context -> {
                            context.getSource().sendFailure(Component.literal("Usage: /brewing <potion_name>"));
                            return 0;
                        })
        );

    }
    // --- 3. Execution Logic ---
    private static int handleCommand(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String potionName = StringArgumentType.getString(context, "potion").toLowerCase();
        CommandSourceStack source = context.getSource();
        MutableComponent message;

        if (POTION_RECIPES.containsKey(potionName)) {
            // Success: Potion found! Build the recipe message.
            List<String> steps = POTION_RECIPES.get(potionName);

            // Start the message with the potion name
            message = Component.literal("Brewing Steps for ")
                    .append(Component.literal(potionName).withStyle(ChatFormatting.GOLD))
                    .append(Component.literal(":\n"));

            // Append each step on a new line
            for (String step : steps) {
                message.append(Component.literal("  • ").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(step + "\n").withStyle(ChatFormatting.WHITE));
            }

        } else {
            // Failure: Potion not found. Use the standard error message template.
            message = Component.literal("I don't have a brewing recipe for '")
                    .append(Component.literal(potionName).withStyle(ChatFormatting.RED))
                    .append(Component.literal("'. Make sure your spelling is right."));

            // Add the GitHub link (using the fix we established)
            message.append(Component.literal("\nTo make a request, go and make an issue at "));
            message.append(
                    Component.literal("https://github.com/JoeFiore/morecommands1.21.10/issues")
                            .withStyle(style -> style
                                    .withColor(ChatFormatting.BLUE)
                                    .withClickEvent(new ClickEvent.OpenUrl(
                                            URI.create("https://github.com/JoeFiore/morecommands1.21.10/issues")
                                    ))
                                    .withUnderlined(true)
                            )
            );
        }

        // Send the final message
        source.sendSuccess(() -> message, false);
        return 1;
    }
}
