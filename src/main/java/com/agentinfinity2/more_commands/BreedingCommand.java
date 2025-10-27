package com.agentinfinity2.more_commands;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent; // <-- NEW REQUIRED IMPORT
import net.minecraft.network.chat.ClickEvent; // <-- REQUIRED FOR CLICKABLE LINK
import net.minecraft.network.chat.ClickEvent.Action; // <-- REQUIRED FOR CLICKABLE LINK

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

public class BreedingCommand {

    // --- 1. Collection of supported entity names for suggestions ---
    private static final Collection<String> BREEDABLE_ENTITIES = Arrays.asList(
            "chicken",
            "cow",
            "horse",
            "donkey",
            "goat",
            "mooshroom",
            "sheep",
            "pig",
            "wolf",
            "cat",
            "axolotl",
            "llama",
            "rabbit",
            "turtle",
            "panda",
            "fox",
            "bee",
            "strider",
            "hoglin",
            "frog",
            "camel",
            "sniffer",
            "armadillo"
            // Add all other entities your command supports here!
    );

    // --- 2. The SuggestionProvider method ---
    // This is the functional interface that generates the suggested text when the player presses Tab.
    private static final SuggestionProvider<CommandSourceStack> SUGGEST_ENTITIES = (context, builder) -> {
        // This takes the list of entities and builds the suggestions based on what the player has already typed.
        return net.minecraft.commands.SharedSuggestionProvider.suggest(BREEDABLE_ENTITIES, builder);
    };
    // --- Command Registration Method ---
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Register the base command: /breeding
        dispatcher.register(
                Commands.literal("breeding")
                        // Execute logic for the base command: /breeding
                        .executes(BreedingCommand::handleNoArgument)

                        // Add the required argument: /breeding <entity>
                        .then(Commands.argument("entity", StringArgumentType.string())
                                // Execute logic for the command with an argument: /breeding <entity>
                                .suggests(SUGGEST_ENTITIES)
                                .executes(BreedingCommand::handleWithArgument)
                        )
        );
    }

    // --- Logic for: /breeding (No arguments) ---
    private static int handleNoArgument(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        // Send the usage message to the player who executed the command
        context.getSource().sendSuccess(
                () -> Component.literal("Usage: ")
                        .append(Component.literal("/breeding <entity>").withStyle(ChatFormatting.YELLOW))
                        .append(Component.literal("\nIf any tips are shown they will be in this color").withStyle(ChatFormatting.LIGHT_PURPLE)),
                false // 'false' means it won't be announced to the whole server
        );
        // Return 1 to indicate successful execution
        return 1;
    }

    // --- Logic for: /breeding <entity> (With an argument) ---
    private static int handleWithArgument(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        // 1. Get the 'entity' argument that the player typed
        String entityName = StringArgumentType.getString(context, "entity").toLowerCase();

        // 2. Determine the breeding information based on the input
        Component message;

        switch (entityName) {
            case "chicken":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Chicken").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it any kind of "))
                        .append(Component.literal("Seed").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nChicken's can be used as a renewable source for food and feathers.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "cow":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Cow").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Wheat").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nCows have 3 different types of use when it comes to items. It can give you Leather, Beef, or Milk.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            // Add more entities here!
            case "horse":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Untamed Horse").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Golden Apple").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Enchanted Golden Apple").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Golden Carrot").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."));
                message = Component.literal("\nTo tame a ")
                        .append(Component.literal("Untamed Horse").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Sugar").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Apple").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Wheat").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Hay Bale").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nHorses have unique statstics like speed and jump height, they come in 35 different coat combos.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "donkey":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Donkey").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Golden Apple").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Enchanted Golden Apple").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Golden Carrot").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nDonkeys can be equipped with chests.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "goat":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Goat").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Wheat").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nGoat's only spawn in cold mountain biomes, specifically on snowy slopes, jagged peaks, and frozen peaks.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "mooshroom":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Mooshroom").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Wheat").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nMooshroom cow's can only spawn in the Mooshroom Biome.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "sheep":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Sheep").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Wheat").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nSheep's can be easy to find, and sometimes hard.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "pig":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Pig").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Carrots").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Potatos").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Beetroots").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nPigs can also be led with a carrot on a stick").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "wolf":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Tamed Wolf").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Any type of meat cooked/uncooked").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", most meat works"))
                        .append(Component.literal("."))
                        .append(Component.literal("\nTo tame a "))
                        .append(Component.literal("Untamed Wolf").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Bones").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nTamed Wolf's can take the most amount of items to breed with.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "cat":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Cat").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Raw Cod").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Raw Salmon").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nCreepers are scared of cats, you can use them to protect your base :)").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "axolotl":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Axolotl").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Bucket of Tropical Fish").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nOnly the bucketed version will work.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "llama":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Llama").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Hay Bale").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nYou can tame Llamas by mounting over and over again.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "rabbit":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Rabbit").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Dandelions").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Carrots").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Golden Carrots.").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nRabbits will always run from players unless they hold the breeding items.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "turtle":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Turtle").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Seagrass").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nTurtle eggs can only be obtained by Silk Touch").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "panda":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Panda").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Bamboo").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nPandas can eat bamboo without a player giving them.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "fox":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Fox").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Sweet Berries").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal(", or "))
                        .append(Component.literal("Glow Berries").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nFox's can eat berries without player input.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "bee":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Bee").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Flowers").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nYou can feed Bee's any flowers including Wither Roses.. But giving them Wither Roses doesn't anger the bee nor does it give them the wither effect.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "strider":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Strider").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Warped Fungus").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nStriders can still move when /tick freeze is active.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "hoglin":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Hoglin").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Crismson Fungus").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nHoglins continue to attack during the breeding process.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "frog":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Frog").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Slime Balls").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nFrogs lay eggs on water that take up to ten minutes to hatch.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "camel":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Camel").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Cactus").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nDespite being bred from cacti, it still takes damage from walking into cactus.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "sniffer":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Sniffer").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Torchflower Seeds").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."))
                        .append(Component.literal("\nSniffers lay eggs and they take 20 minutes to hatch, unless on a moss block which it then takes 10.").withStyle(ChatFormatting.LIGHT_PURPLE));
                break;
            case "armadillo":
                message = Component.literal("To breed a ")
                        .append(Component.literal("Armadillo").withStyle(ChatFormatting.GOLD))
                        .append(Component.literal(", feed it "))
                        .append(Component.literal("Spider Eyes").withStyle(ChatFormatting.GREEN))
                        .append(Component.literal("."));
                break;
            default:
                // 1. Start the message with the "Entity not found" text
                message = Component.literal("I don't have breeding information for '")
                        .append(Component.literal(entityName).withStyle(ChatFormatting.RED))
                        .append(Component.literal("'. Make sure your spelling is right."));

                // 2. Append a line break (\n) and the start of the second message
                message = ((MutableComponent) message).append(Component.literal("\nIf you spelt it right and are still having problems, go and make an issue at "));

                // 3. Append the clickable link
                ((MutableComponent) message).append(
                        Component.literal("https://github.com/JoeFiore/morecommands1.21.10/issues")
                                .withStyle(style -> style
                                        .withColor(ChatFormatting.BLUE)
                                        // *** THE ABSOLUTE FINAL FIX: Use the constructor of the OpenUrl record ***
                                        .withClickEvent(new net.minecraft.network.chat.ClickEvent.OpenUrl(
                                                URI.create("https://github.com/JoeFiore/morecommands1.21.10/issues")
                                        ))
                                        .withUnderlined(true)
                                )
                );
                break;
        }

        // 3. Send the final message to the player
        Component finalMessage = message;
        context.getSource().sendSuccess(() -> finalMessage, false);

        return 1;
    }
}