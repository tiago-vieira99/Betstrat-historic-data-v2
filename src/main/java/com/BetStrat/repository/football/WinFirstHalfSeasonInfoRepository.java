package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.WinFirstHalfSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WinFirstHalfSeasonInfoRepository implements PanacheRepository<WinFirstHalfSeasonStats> {

    @CacheResult(cacheName = "getFootballWinFirstHalfStatsByTeam")
    public List<WinFirstHalfSeasonStats> getFootballWinFirstHalfStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}