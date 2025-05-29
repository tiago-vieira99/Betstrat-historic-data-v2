package com.BetStrat.repository.hockey;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.hockey.WinsMargin3SeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WinsMargin3SeasonInfoRepository implements PanacheRepository<WinsMargin3SeasonStats> {

    @CacheResult(cacheName = "getHockeyWinsMargin3StatsByTeam")
    public List<WinsMargin3SeasonStats> getHockeyWinsMargin3StatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}