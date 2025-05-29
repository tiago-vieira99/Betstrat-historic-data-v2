package com.BetStrat.repository.handball;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.handball.Handball16WinsMarginSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class Handball16WinsMarginSeasonInfoRepository implements PanacheRepository<Handball16WinsMarginSeasonStats> {

    @CacheResult(cacheName = "getHandball16WinsMarginStatsByTeam")
    public List<Handball16WinsMarginSeasonStats> getHandball16WinsMarginStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}