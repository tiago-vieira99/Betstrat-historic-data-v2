package com.BetStrat.repository;

import com.BetStrat.entity.HistoricMatch;
import com.BetStrat.entity.Team;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class HistoricMatchRepository implements PanacheRepository<HistoricMatch> {

    @CacheResult(cacheName = "historicMatches")
    public List<HistoricMatch> findAllMatches() {
        return listAll();
    }

    @CacheResult(cacheName = "getTeamMatchesBySeason")
    public List<HistoricMatch> getTeamMatchesBySeason(Team teamId, String season) {
        return find("team_id = ?1 and season = ?2", teamId, season).list();
    }
}