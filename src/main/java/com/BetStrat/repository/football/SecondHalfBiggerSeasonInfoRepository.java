package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.SecondHalfBiggerSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class SecondHalfBiggerSeasonInfoRepository implements PanacheRepository<SecondHalfBiggerSeasonStats> {

    @CacheResult(cacheName = "getFootballSecondHalfBiggerStatsByTeam")
    public List<SecondHalfBiggerSeasonStats> getFootballSecondHalfBiggerStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}