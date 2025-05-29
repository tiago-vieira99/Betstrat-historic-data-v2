package com.BetStrat.repository.basketball;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.basketball.ShortBasketWinsSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ShortWinsSeasonInfoRepository implements PanacheRepository<ShortBasketWinsSeasonStats> {

    @CacheResult(cacheName = "ListShortBasketWinsSeasonInfo")
    public List<ShortBasketWinsSeasonStats> getShortBasketWinsStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}