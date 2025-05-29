package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.CleanSheetSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class CleanSheetSeasonInfoRepository implements PanacheRepository<CleanSheetSeasonStats> {

    @CacheResult(cacheName = "ListCleanSheetSeasonStats")
    public List<CleanSheetSeasonStats> getFootballCleanSheetStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}