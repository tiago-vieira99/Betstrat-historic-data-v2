package com.BetStrat.repository.hockey;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.hockey.WinsMarginAny2SeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WinsMarginAny2SeasonInfoRepository implements PanacheRepository<WinsMarginAny2SeasonStats> {

    @CacheResult(cacheName = "getHockeyWinsMarginAny2StatsByTeam")
    public List<WinsMarginAny2SeasonStats> getHockeyWinsMarginAny2StatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}