package com.tylermackj.hardcoremp.handlers;

import com.tylermackj.hardcoremp.HardcoreMP;
import com.tylermackj.hardcoremp.utils.Utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

public class JoinHandler {

    public static void registerEvents() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity.isPlayer()) {
                HardcoreMP.LOGGER.info("Player joined " + entity.getName());
                MutableText teamOptionsText = Text.of("Join team:").copy();
                for (Team team : world.getScoreboard().getTeams()) {
                    teamOptionsText.append(
                        Text.of("\n    ").copy().append(
                            team.getDisplayName().copy().setStyle(
                                team.getDisplayName().getStyle().withClickEvent(
                                    new ClickEvent.RunCommand("/hardcore-mp join " + team.getName())
                                ).withHoverEvent(
                                    new HoverEvent.ShowText(Text.of("Join ").copy().append(team.getDisplayName()))
                                ).withUnderline(true)
                            )
                        )
                    );
                }
                MutableText teamCreateText = Text.of("Create new team").copy().append(
                    Text.of("\n    ").copy().append(
                        Text.of("Manual").copy().setStyle(
                            Style.EMPTY.withClickEvent(new ClickEvent.SuggestCommand("/hardcore-mp create ")).withUnderline(true)
                        )
                    ).append("    ").append(
                        Text.of("Automatic").copy().setStyle(
                            Style.EMPTY.withClickEvent(new ClickEvent.RunCommand("/hardcore-mp create automatic")).withUnderline(true)
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
