package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.DrawSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class DrawSeasonInfoRepository implements PanacheRepository<DrawSeasonStats> {

    @CacheResult(cacheName = "ListDrawSeasonStats")
    public List<DrawSeasonStats> getFootballDrawStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}