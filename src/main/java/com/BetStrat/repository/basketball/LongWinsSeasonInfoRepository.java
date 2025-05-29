package com.BetStrat.repository.basketball;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.basketball.LongBasketWinsSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class LongWinsSeasonInfoRepository implements PanacheRepository<LongBasketWinsSeasonStats> {

    @CacheResult(cacheName = "getLongBasketWinsStatsByTeam")
    public List<LongBasketWinsSeasonStats> getLongBasketWinsStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}