package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.WinsMarginSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WinsMarginSeasonInfoRepository implements PanacheRepository<WinsMarginSeasonStats> {

    @CacheResult(cacheName = "getFootballWinsMarginStatsByTeam")
    public List<WinsMarginSeasonStats> getFootballWinsMarginStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}