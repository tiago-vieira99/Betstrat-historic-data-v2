package com.BetStrat.repository.basketball;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.basketball.ComebackSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ComebackSeasonInfoRepository implements PanacheRepository<ComebackSeasonStats> {

    @CacheResult(cacheName = "getComebackStatsByTeam")
    public List<ComebackSeasonStats> getComebackStatsByTeam(Team team) {
        return find("team_id = ?1", team).list();
    }

}