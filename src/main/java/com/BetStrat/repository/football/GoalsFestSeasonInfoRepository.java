package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.GoalsFestSeasonStats;
import io.quarkus.cache.CacheResult;
import java.util.List;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GoalsFestSeasonInfoRepository implements PanacheRepository<GoalsFestSeasonStats> {

    @CacheResult(cacheName = "getGoalsFestStatsByTeam")
    public List<GoalsFestSeasonStats> getGoalsFestStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}