package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.BttsOneHalfSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class BttsOneHalfSeasonInfoRepository implements PanacheRepository<BttsOneHalfSeasonStats> {

    @CacheResult(cacheName = "ListBttsOneHalfSeasonStats")
    public List<BttsOneHalfSeasonStats> getFootballBttsOneHalfStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}