package com.BetStrat.repository.football;

import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.ScoreBothHalvesSeasonStats;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ScoreBothHalvesSeasonInfoRepository implements PanacheRepository<ScoreBothHalvesSeasonStats> {

    @CacheResult(cacheName = "getFootballScoreBothHalvesStatsByTeam")
    public List<ScoreBothHalvesSeasonStats> getFootballScoreBothHalvesStatsByTeam(Team team) {
        return find("teamId", team.getId()).list();
    }
}