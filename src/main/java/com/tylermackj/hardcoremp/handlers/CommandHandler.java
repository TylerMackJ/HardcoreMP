package com.tylermackj.hardcoremp.handlers;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.tylermackj.hardcoremp.HardcoreMP;
import com.tylermackj.hardcoremp.utils.TeamSuggestionProvider;
import com.tylermackj.hardcoremp.utils.WordProvider;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class CommandHandler {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registeryAccess, enviroment) -> {
            dispatcher.register(CommandManager.literal(HardcoreMP.MOD_ID)
                .then(CommandManager.literal("join")
                    .then(CommandManager.argument("team", StringArgumentType.word())
                        .suggests(new TeamSuggestionProvider())
                        .executes(CommandHandler::joinTeam)
                    ) 
                )
                .then(CommandManager.literal("leave")
                    .executes(CommandHandler::leaveTeam)
                )
                .then(CommandManager.literal("create")
                    .then(CommandManager.literal("manual")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                            .executes(CommandHandler::createTeam) 
                        )
                    )
                    .then(CommandManager.literal("automatic")
                        .executes(CommandHandler::generateTeam) 
                    )
                )
            );
        });
    }

    private static int joinTeam(CommandContext<ServerCommandSource> context) {
        String team = StringArgumentType.getString(context, "team");
        context.getSource().sendFeedback(() -> Text.literal("Joining team: " + team), false);
        context.getSource().getPlayer().getServer().getCommandManager().executeWithPrefix(context.getSource(), "team join " + team);
        return 1;
    }

    private static int leaveTeam(CommandContext<ServerCommandSource> context) {
        context.getSource().sendFeedback(() -> Text.literal("Leaving team"), false);
        context.getSource().getPlayer().getServer().getCommandManager().executeWithPrefix(context.getSource(), "team leave @s");
        return 1;
    }

    private static int createTeam(CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        context.getSource().getPlayer().getServer().getCommandManager().executeWithPrefix(context.getSource(), "team add " + name.replaceAll(" ", "_") + " " + name);
        context.getSource().getPlayer().getServer().getCommandManager().executeWithPrefix(context.getSource(), "team join " + name.replaceAll(" ", "_"));
        return 1;
    }

    private static int generateTeam(CommandContext<ServerCommandSource> context) {
        WordProvider.NamePair namePair = WordProvider.getInstance().getPair();
        context.getSource().getPlayer().getServer().getCommandManager().executeWithPrefix(context.getSource(), "team add " + namePair.getCodeName() + " \"" + namePair.getDisplayName() + "\"");
        context.getSource().getPlayer().getServer().getCommandManager().executeWithPrefix(context.getSource(), "team join " + namePair.getCodeName());
        return 1; 
    }
}
