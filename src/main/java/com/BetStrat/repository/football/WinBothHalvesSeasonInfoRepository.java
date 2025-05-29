package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.WinBothHalvesSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WinBothHalvesSeasonInfoRepository implements PanacheRepository<WinBothHalvesSeasonStats> {

    @CacheResult(cacheName = "getFootballWinBothHalvesStatsByTeam")
    public List<WinBothHalvesSeasonStats> getFootballWinBothHalvesStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}