package com.willr27.blocklings.command;

import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.willr27.blocklings.Blocklings;
import com.willr27.blocklings.entity.blockling.BlocklingType;
import com.willr27.blocklings.entity.blockling.attribute.BlocklingAttributes.Level;
import com.willr27.blocklings.network.NetworkHandler;
import com.willr27.blocklings.network.messages.SetLevelCommandMessage;
import com.willr27.blocklings.network.messages.SetTypeCommandMessage;
import com.willr27.blocklings.network.messages.SetXpCommandMessage;
import com.willr27.blocklings.util.BlocklingsTranslatableComponent;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

/**
 * Handles the setup of blocklings commands.
 */
@Mod.EventBusSubscriber(modid = Blocklings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BlocklingsCommands
{
    /**
     * Whether to show blockling spawn debug information for each player.
     */
    @Nonnull
    public static HashMap<UUID, Boolean> debugSpawns = new HashMap<>();

    /**
     * Registers argument types.
     */
    public static void init()
    {
        ArgumentTypes.register("blocklings:blockling_type", BlocklingTypeArgument.class, new BlocklingTypeArgument.Serializer());
        ArgumentTypes.register("blocklings:level", BlocklingLevelArgument.class, new BlocklingLevelArgument.Serializer());
    }

    /**
     * Registers the custom blocklings commands.
     */
    @SubscribeEvent
    public static void onRegisterCommands(@Nonnull RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
                literal("blocklings").requires(source -> source.hasPermission(2)).then(
                        literal("set").then(
                                literal("type").then(
                                        literal("primary").then(
                                                argument("type", new BlocklingTypeArgument())
                                                      .executes(context -> executeTypeCommand(context, false)))).then(
                                        literal("natural").then(
                                                argument("type", new BlocklingTypeArgument())
                                                      .executes(context -> executeTypeCommand(context, true))))).then(
                                literal("level").then(
                                        argument("level", new BlocklingLevelArgument()).then(
                                                argument("value", IntegerArgumentType.integer(Level.MIN, Level.MAX))
                                                        .executes(context -> executeLevelCommand(context))))).then(
                                literal("xp").then(
                                        argument("level", new BlocklingLevelArgument()).then(
                                                argument("value", IntegerArgumentType.integer(0))
                                                        .executes(context -> executeXpCommand(context)))))).then(
                        literal("debug").then(
                                literal("spawns").then(
                                        argument("value", BoolArgumentType.bool())
                                            .executes(context -> executeDebugSpawnsCommand(context))))));
    }

    /**
     * Executes the set blockling type commands.
     *
     * @param natural whether the type being set is the natural type or not.
     */
    private static int executeTypeCommand(@Nonnull CommandContext<CommandSourceStack> context, boolean natural)
    {
        CommandSourceStack source = context.getSource();
        Player player = (Player) source.getEntity();

        if (player == null)
        {
            return 1;
        }

        BlocklingType blocklingType = context.getArgument("type", BlocklingType.class);

        NetworkHandler.sendToClient(player, new SetTypeCommandMessage(blocklingType.key, natural));

        return 0;
    }

    /**
     * Executes the set blockling level commands.
     */
    private static int executeLevelCommand(@Nonnull CommandContext<CommandSourceStack> context)
    {
        CommandSourceStack source = context.getSource();
        Player player = (Player) source.getEntity();

        if (player == null)
        {
            return 1;
        }

        Level level = context.getArgument("level", Level.class);
        int value = context.getArgument("value", Integer.class);

        NetworkHandler.sendToClient(player, new SetLevelCommandMessage(level, value));

        return 0;
    }

    /**
     * Executes the set blockling xp commands.
     */
    private static int executeXpCommand(@Nonnull CommandContext<CommandSourceStack> context)
    {
        CommandSourceStack source = context.getSource();
        Player player = (Player) source.getEntity();

        if (player == null)
        {
            return 1;
        }

        Level level = context.getArgument("level", Level.class);
        int value = context.getArgument("value", Integer.class);

        NetworkHandler.sendToClient(player, new SetXpCommandMessage(level, value));

        return 0;
    }

    /**
     * Executes the debug spawns command.
     */
    private static int executeDebugSpawnsCommand(@Nonnull CommandContext<CommandSourceStack> context)
    {
        CommandSourceStack source = context.getSource();
        Player player = (Player) source.getEntity();

        if (player == null)
        {
            return 1;
        }

        UUID playerId = player.getUUID();
        boolean enabled = context.getArgument("value", Boolean.class);

        debugSpawns.put(playerId, enabled);

        if (enabled)
        {
            player.sendMessage(new BlocklingsTranslatableComponent("command.debug.spawns.enabled"), Util.NIL_UUID);
        }
        else
        {
            player.sendMessage(new BlocklingsTranslatableComponent("command.debug.spawns.disabled"), Util.NIL_UUID);
        }

        return 0;
    }

    /**
     * Represents a command argument of a blockling type.
     */
    public static class BlocklingTypeArgument implements ArgumentType<BlocklingType>
    {
        /**
         * Examples of blockling types.
         */
        private static final Collection<String> EXAMPLES = Arrays.asList("oak_log", "stone", "diamond");

        @Override
        public BlocklingType parse(StringReader stringReader) throws CommandSyntaxException
        {
            String argString = stringReader.readUnquotedString();

            return BlocklingType.find(argString.toLowerCase());
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder suggestionsBuilder)
        {
            return SharedSuggestionProvider.suggest(BlocklingType.TYPES.stream().map(blocklingType -> blocklingType.key), suggestionsBuilder);
        }

        @Override
        public Collection<String> getExamples()
        {
            return EXAMPLES;
        }

        public static class Serializer implements ArgumentSerializer<BlocklingTypeArgument>
        {
            @Override
            public void serializeToNetwork(BlocklingTypeArgument argument, FriendlyByteBuf buffer)
            {
            }

            @Override
            public BlocklingTypeArgument deserializeFromNetwork(FriendlyByteBuf buffer)
            {
                return new BlocklingTypeArgument();
            }

            @Override
            public void serializeToJson(BlocklingTypeArgument argument, JsonObject json)
            {
            }
        }
    }

    /**
     * Represents a command argument of a blockling type.
     */
    public static class BlocklingLevelArgument implements ArgumentType<Level>
    {
        /**
         * The error to throw if the argument is invalid.
         */
        public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((obj) -> new BlocklingsTranslatableComponent("command.argument.level.invalid", obj));

        @Override
        public Level parse(StringReader stringReader) throws CommandSyntaxException
        {
            String argString = stringReader.readUnquotedString();

            try
            {
                if (argString.equalsIgnoreCase("all"))
                {
                    return Level.TOTAL;
                }

                Level level = Level.valueOf(argString.toUpperCase());

                if (level != Level.TOTAL)
                {
                    return level;
                }
            }
            catch (IllegalArgumentException e) { }

            throw ERROR_INVALID_VALUE.create(argString);
        }

        @Override
        public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder suggestionsBuilder)
        {
            List<String> levels = Arrays.stream(Level.values()).filter(level -> level != Level.TOTAL).map(level -> level.name().toLowerCase()).collect(Collectors.toList());
            levels.add("all");

            return SharedSuggestionProvider.suggest(levels, suggestionsBuilder);
        }

        public static class Serializer implements ArgumentSerializer<BlocklingLevelArgument>
        {
            @Override
            public void serializeToNetwork(BlocklingLevelArgument argument, FriendlyByteBuf buffer)
            {
            }

            @Override
            public BlocklingLevelArgument deserializeFromNetwork(FriendlyByteBuf buffer)
            {
                return new BlocklingLevelArgument();
            }

            @Override
            public void serializeToJson(BlocklingLevelArgument argument, JsonObject json)
            {
            }
        }
    }
}
