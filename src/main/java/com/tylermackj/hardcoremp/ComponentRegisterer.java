package com.tylermackj.hardcoremp;

import org.ladysnake.cca.api.v3.component.ComponentKey;
import org.ladysnake.cca.api.v3.component.ComponentRegistryV3;
import org.ladysnake.cca.api.v3.entity.EntityComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.entity.EntityComponentInitializer;
import org.ladysnake.cca.api.v3.entity.RespawnCopyStrategy;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentFactoryRegistry;
import org.ladysnake.cca.api.v3.scoreboard.ScoreboardComponentInitializer;

import com.tylermackj.hardcoremp.types.PlayerData;
import com.tylermackj.hardcoremp.types.TeamData;

import net.minecraft.util.Identifier;

public final class ComponentRegisterer implements EntityComponentInitializer, ScoreboardComponentInitializer {
    public static final ComponentKey<PlayerData> PLAYER_DATA = 
        ComponentRegistryV3.INSTANCE.getOrCreate(Identifier.of("hardcore-mp:playerdata"), PlayerData.class);
    public static final ComponentKey<TeamData> TEAM_DATA =
        ComponentRegistryV3.INSTANCE.getOrCreate(Identifier.of("hardcore-mp:teamdata"), TeamData.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(PLAYER_DATA, player -> new PlayerData(player), RespawnCopyStrategy.ALWAYS_COPY);
    }

    @Override
    public void registerScoreboardComponentFactories(ScoreboardComponentFactoryRegistry registry) {
        registry.registerTeamComponent(TEAM_DATA, (team, scoreboard, server) -> new TeamData(team));
    }
}
