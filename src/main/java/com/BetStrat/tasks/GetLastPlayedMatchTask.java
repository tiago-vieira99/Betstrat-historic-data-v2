package com.BetStrat.tasks;


import static com.BetStrat.constants.BetStratConstants.CURRENT_SUMMER_SEASON;
import static com.BetStrat.constants.BetStratConstants.CURRENT_WINTER_SEASON;
import static com.BetStrat.constants.BetStratConstants.LEAGUES_LIST;
import static com.BetStrat.constants.BetStratConstants.WINTER_SEASONS_BEGIN_MONTH_LIST;

import com.BetStrat.entity.HistoricMatch;
import com.BetStrat.entity.Team;
import com.BetStrat.repository.HistoricMatchRepository;
import com.BetStrat.repository.TeamRepository;
import com.BetStrat.utils.ScrappingUtil;
import com.BetStrat.utils.TelegramBotNotifications;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class GetLastPlayedMatchTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetLastPlayedMatchTask.class);

    @Inject
    private TeamRepository teamRepository;

    @Inject
    private HistoricMatchRepository historicMatchRepository;

    // Called once on startup
    void onStart(@Observes StartupEvent ev) throws Exception {
        execCronn();
    }

    @Scheduled(cron = "0 0 2 * * ?", timeZone = "Europe/Lisbon")//every two days at 2am
    public void execCronn() throws Exception {

        List<String> teamsToGetLastMatch = new ArrayList<>();

        for (String leagueUrl : LEAGUES_LIST) {
            JSONObject leagueTeamsScrappingData = ScrappingUtil.getLeagueTeamsScrappingData(leagueUrl);
            List<String> analysedTeams = new ArrayList<>();

            while (leagueTeamsScrappingData.keys().hasNext()) {
                String team = leagueTeamsScrappingData.keys().next().toString();
                analysedTeams.add(team);
                leagueTeamsScrappingData.remove(team);
            }

            teamsToGetLastMatch.addAll(analysedTeams);
        }
        run(teamRepository, historicMatchRepository, teamsToGetLastMatch);
    }


    public static void run(TeamRepository teamRepository, HistoricMatchRepository historicMatchRepository, List<String> teams) throws Exception {
        LOGGER.info("GetLastPlayedMatchTask() at "+ Instant.now().toString());

        int numNewMatches = 0;
        String failedTeams = "";

        for (String t : teams) {
            Team team = teamRepository.getTeamByNameAndSport(t, "Football");

            if (team != null) {
                JSONArray scrappingData = ScrappingUtil.getLastNMatchesScrappingService(team, 3);
                if (scrappingData != null) {
                    for (int i = 0; i < scrappingData.length(); i++) {
                        HistoricMatch historicMatch = new HistoricMatch();
                        try {
                            JSONObject match = (JSONObject) scrappingData.get(i);
                            historicMatch.setTeamId(team);
                            historicMatch.setMatchDate(match.getString("date"));
                            historicMatch.setHomeTeam(match.getString("homeTeam"));
                            historicMatch.setAwayTeam(match.getString("awayTeam"));
                            historicMatch.setFtResult(match.getString("ftResult"));
                            historicMatch.setHtResult(match.getString("htResult"));
                            historicMatch.setCompetition(match.getString("competition"));
                            historicMatch.setSport(team.getSport());
                            if (WINTER_SEASONS_BEGIN_MONTH_LIST.contains(team.getBeginSeason())) {
                                historicMatch.setSeason(CURRENT_WINTER_SEASON);
                            } else {
                                historicMatch.setSeason(CURRENT_SUMMER_SEASON);
                            }

                            try {
                                saveMatch(historicMatch, historicMatchRepository); // this is now a @Transactional instance method
                                LOGGER.info("Inserted match: " + historicMatch.toString());
                                numNewMatches++;
                            } catch (PersistenceException pe) {
                                LOGGER.debug("match already exists or failed: " + historicMatch.toString());
                            }
                        } catch (Exception e) {
                            LOGGER.error("match:  " + historicMatch.toString() + "\nerror:  " + e.toString());
                            failedTeams += t + " | ";
                        }
                    }
                } else {
                    failedTeams += t + " | ";
                }
            }
        }

        String telegramMessage = String.format("\u2139\uFE0F number of new matches added: " + numNewMatches + "\nfailed: " + failedTeams);
        Thread.sleep(1000);
        TelegramBotNotifications.sendToTelegram(telegramMessage);
    }

    @Transactional
    public static void saveMatch(HistoricMatch match, HistoricMatchRepository historicMatchRepository) {
        historicMatchRepository.persist(match);
    }

    //TODO cases to recommend a new sequence:
    // 1 - when the actual sequence is higher than the default_max_bad_seq
    // 2 - when the actual sequence is Max(3, -2(?) games to reach the historic avg_bad_seq)
    // 3 - when the actual sequence is Max(3, -4(?) games to reach the historic max_bad_seq)

}
