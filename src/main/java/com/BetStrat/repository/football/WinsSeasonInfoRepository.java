package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.WinsSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WinsSeasonInfoRepository implements PanacheRepository<WinsSeasonStats> {

    @CacheResult(cacheName = "getFootballWinsStatsByTeam")
    public List<WinsSeasonStats> getFootballWinsStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}