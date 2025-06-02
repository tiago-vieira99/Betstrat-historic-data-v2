package com.BetStrat.service;

import com.BetStrat.entity.HistoricMatch;
import com.BetStrat.entity.StrategySeasonStats;
import com.BetStrat.entity.Team;
import java.util.HashMap;
import java.util.List;

public interface StrategySeasonStatsInterface<T extends StrategySeasonStats> {

    T insertStrategySeasonStats(T strategySeasonStats);

    List<T> getStatsByStrategyAndTeam(Team team, String strategyName);

    void updateStrategySeasonStats(Team team, String strategyName);

    Team updateTeamScore (Team team);

    String calculateScoreBySeason (Team team, String season, String strategyName);

    HashMap<String, Object> getSimulatedMatchesByStrategyAndSeason(String season, Team team, String strategyName);

    boolean matchFollowStrategyRules(HistoricMatch historicMatch, String teamName, String strategyName);

}
