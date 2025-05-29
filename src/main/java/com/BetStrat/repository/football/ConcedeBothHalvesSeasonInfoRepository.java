package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.ConcedeBothHalvesSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ConcedeBothHalvesSeasonInfoRepository implements PanacheRepository<ConcedeBothHalvesSeasonStats> {

    @CacheResult(cacheName = "ListConcedeBothHalvesSeasonStats")
    public List<ConcedeBothHalvesSeasonStats> getFootballConcedeBothHalvesStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}