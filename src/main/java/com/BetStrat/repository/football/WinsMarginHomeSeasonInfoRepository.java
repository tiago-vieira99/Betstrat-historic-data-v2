package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.WinsMarginHomeSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WinsMarginHomeSeasonInfoRepository implements PanacheRepository<WinsMarginHomeSeasonStats> {

    @CacheResult(cacheName = "getFootballWinsMarginHomeStatsByTeam")
    public List<WinsMarginHomeSeasonStats> getFootballWinsMarginHomeStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}