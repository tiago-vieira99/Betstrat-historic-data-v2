package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.NoWinsSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class NoWinsSeasonInfoRepository implements PanacheRepository<NoWinsSeasonStats> {

    @CacheResult(cacheName = "getFootballNoWinsStatsByTeam")
    public List<NoWinsSeasonStats> getFootballNoWinsStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}