package com.BetStrat;

import com.BetStrat.entity.Team;
import com.BetStrat.repository.TeamRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.List;

@Path("/test/team")
public class TeamResource {

    @Inject
    TeamRepository teamRepository;

    @GET
    @Path("/all")
    public List<Team> getAllTeams() {
        List<Team> all = (List<Team>) teamRepository.findAll().list();
        return all;
    }

    @GET
    @Path("/name/{name}")
    public Team getTeamByName(@PathParam("name") String name) {
        return teamRepository.getTeamByName(name);
    }
}

