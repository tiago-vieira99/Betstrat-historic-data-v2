package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.NoGoalsFestSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class NoGoalsFestSeasonInfoRepository implements PanacheRepository<NoGoalsFestSeasonStats> {

    @CacheResult(cacheName = "getNoGoalsFestStatsByTeam")
    public List<NoGoalsFestSeasonStats> getNoGoalsFestStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}