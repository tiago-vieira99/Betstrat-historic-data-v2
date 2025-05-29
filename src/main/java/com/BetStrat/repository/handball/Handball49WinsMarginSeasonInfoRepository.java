package com.BetStrat.repository.handball;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.handball.Handball49WinsMarginSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class Handball49WinsMarginSeasonInfoRepository implements PanacheRepository<Handball49WinsMarginSeasonStats> {

    @CacheResult(cacheName = "getHandball49WinsMarginStatsByTeam")
    public List<Handball49WinsMarginSeasonStats> getHandball49WinsMarginStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}