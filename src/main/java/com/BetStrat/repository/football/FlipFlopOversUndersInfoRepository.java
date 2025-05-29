package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.FlipFlopOversUndersStats;
import io.quarkus.cache.CacheResult;
import java.util.List;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FlipFlopOversUndersInfoRepository implements PanacheRepository<FlipFlopOversUndersStats> {

    @CacheResult(cacheName = "getFlipFlopStatsByTeam")
    public List<FlipFlopOversUndersStats> getFlipFlopStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }

}