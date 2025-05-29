package com.BetStrat.repository;

import com.BetStrat.entity.Team;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class TeamRepository implements PanacheRepository<Team> {

    @CacheResult(cacheName = "historicTeams")
    public List<Team> findAllTeams() {
        return listAll(); // Equivalent to JpaRepository.findAll()
    }

    @CacheResult(cacheName = "getTeamByName")
    public Team getTeamByName(String name) {
        System.out.println("Fetching teams from DB...");
        return find("name", name).firstResult();
    }

    @CacheResult(cacheName = "getTeamByNameAndSport")
    public Team getTeamByNameAndSport(String name, String sport) {
        return find("name = ?1 and sport = ?2", name, sport).firstResult();
    }
}