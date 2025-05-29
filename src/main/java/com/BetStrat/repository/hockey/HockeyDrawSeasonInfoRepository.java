package com.BetStrat.repository.hockey;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.hockey.HockeyDrawSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class HockeyDrawSeasonInfoRepository implements PanacheRepository<HockeyDrawSeasonStats> {

    @CacheResult(cacheName = "getHockeyDrawStatsByTeam")
    public List<HockeyDrawSeasonStats> getHockeyDrawStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}