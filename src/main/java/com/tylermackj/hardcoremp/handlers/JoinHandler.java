package com.tylermackj.hardcoremp.handlers;

import java.util.ArrayList;
import java.util.Comparator;

import com.tylermackj.hardcoremp.HardcoreMP;
import com.tylermackj.hardcoremp.utils.Utils;
import com.tylermackj.hardcoremp.utils.WordProvider.NamePair;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class JoinHandler {
    private static class NamePair implements Comparable<NamePair> {
        Text displayName;
        String name;

        public NamePair(Text displayName, String name) {
            this.displayName = displayName;
            this.name = name;
        }

        @Override
        public int compareTo(NamePair o) {
            return this.displayName.getLiteralString().toLowerCase().compareTo(o.displayName.getLiteralString().toLowerCase());
        }
    }

    public static void registerEvents() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity.isPlayer()) {
                HardcoreMP.LOGGER.info("Player joined " + entity.getName());
                ArrayList<NamePair> teamNames = new ArrayList<>();
                for (Team team : world.getScoreboard().getTeams()) {
                    teamNames.add(new NamePair(team.getDisplayName(), team.getName()));
                }
                teamNames.sort(null);
                MutableText teamOptionsText = Text.of("Join team:").copy();
                for (NamePair name : teamNames) {
                    teamOptionsText.append(
                        Text.of("\n    ").copy().append(
                            name.displayName.copy().setStyle(
                                name.displayName.getStyle().withClickEvent(
                                    new ClickEvent.RunCommand("/" + HardcoreMP.MOD_ID + " join " + name.name)
                                ).withHoverEvent(
                                    new HoverEvent.ShowText(Text.of("Join ").copy().append(name.name))
                                ).withUnderline(true)
                            )
                        )
                    );
                }
                MutableText teamCreateText = Text.of("Create team:").copy().append(
                    Text.of("\n    ").copy().append(
                        Text.of("Manual").copy().setStyle(
                            Style.EMPTY.withClickEvent(new ClickEvent.SuggestCommand("/" + HardcoreMP.MOD_ID + " create ")).withUnderline(true)
                        )
                    ).append("    ").append(
                        Text.of("Automatic").copy().setStyle(
                            Style.EMPTY.withClickEvent(new ClickEvent.RunCommand("/" + HardcoreMP.MOD_ID + " create automatic")).withUnderline(true)
                        )

                    )
                );
                if (entity.getScoreboardTeam() == null) {
                    ((ServerPlayerEntity) entity).sendMessage(Text.empty().append(teamOptionsText).append(Text.of("\n")).append(teamCreateText));
                }
                else {
                    Utils.checkAttemptUuid((ServerPlayerEntity) entity);
                }
            }
        });
    }

}
