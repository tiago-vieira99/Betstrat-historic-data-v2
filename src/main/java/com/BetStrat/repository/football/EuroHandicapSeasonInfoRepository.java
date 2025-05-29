package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.EuroHandicapSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class EuroHandicapSeasonInfoRepository implements PanacheRepository<EuroHandicapSeasonStats> {

    @CacheResult(cacheName = "ListEuroHandicapSeasonStats")
    public List<EuroHandicapSeasonStats> getEuroHandicapStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}