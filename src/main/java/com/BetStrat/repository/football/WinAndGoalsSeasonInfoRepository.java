package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.WinAndGoalsSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WinAndGoalsSeasonInfoRepository implements PanacheRepository<WinAndGoalsSeasonStats> {

    @CacheResult(cacheName = "getFootballWinAndGoalsStatsByTeam")
    public List<WinAndGoalsSeasonStats> getFootballWinAndGoalsStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}