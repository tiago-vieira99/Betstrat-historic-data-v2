package com.BetStrat.repository.handball;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.handball.Handball712WinsMarginSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class Handball712WinsMarginSeasonInfoRepository implements PanacheRepository<Handball712WinsMarginSeasonStats> {

    @CacheResult(cacheName = "getHandball712WinsMarginStatsByTeam")
    public List<Handball712WinsMarginSeasonStats> getHandball712WinsMarginStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}