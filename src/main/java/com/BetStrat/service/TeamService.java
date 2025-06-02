package com.BetStrat.service;

import com.BetStrat.entity.StrategySeasonStats;
import com.BetStrat.entity.Team;
import com.BetStrat.repository.HistoricMatchRepository;
import com.BetStrat.repository.TeamRepository;
import com.BetStrat.service.basketball.ComebackStrategySeasonStatsService;
import com.BetStrat.service.basketball.LongBasketWinsStrategySeasonStatsService;
import com.BetStrat.service.basketball.ShortBasketWinsStrategySeasonStatsService;
import com.BetStrat.service.football.BttsOneHalfStrategySeasonStatsService;
import com.BetStrat.service.football.BttsStrategySeasonStatsService;
import com.BetStrat.service.football.CleanSheetStrategySeasonStatsService;
import com.BetStrat.service.football.ConcedeBothHalvesSeasonStatsService;
import com.BetStrat.service.football.DrawStrategySeasonStatsService;
import com.BetStrat.service.football.EuroHandicapStrategySeasonStatsService;
import com.BetStrat.service.football.FirstHalfBiggerSeasonStatsService;
import com.BetStrat.service.football.FlipFlopOversUndersStatsServiceStrategy;
import com.BetStrat.service.football.GoalsFestStrategySeasonStatsService;
import com.BetStrat.service.football.NoBttsStrategySeasonStatsService;
import com.BetStrat.service.football.NoGoalsFestStrategySeasonStatsService;
import com.BetStrat.service.football.NoWinFirstHalfStrategySeasonStatsService;
import com.BetStrat.service.football.NoWinsStrategySeasonStatsService;
import com.BetStrat.service.football.ScoreBothHalvesSeasonStatsService;
import com.BetStrat.service.football.SecondHalfBiggerSeasonStatsService;
import com.BetStrat.service.football.WinAndGoalsStrategySeasonStatsService;
import com.BetStrat.service.football.WinBothHalvesStrategySeasonStatsService;
import com.BetStrat.service.football.WinFirstHalfStrategySeasonStatsService;
import com.BetStrat.service.football.WinsMarginHomeStrategySeasonStatsService;
import com.BetStrat.service.football.WinsMarginStrategySeasonStatsService;
import com.BetStrat.service.football.WinsStrategySeasonStatsService;
import com.BetStrat.service.handball.HandballWinsMargin16StrategySeasonStatsService;
import com.BetStrat.service.handball.HandballWinsMargin49StrategySeasonStatsService;
import com.BetStrat.service.handball.HandballWinsMargin712StrategySeasonStatsService;
import com.BetStrat.service.hockey.HockeyDrawStrategySeasonStatsService;
import com.BetStrat.service.hockey.WinsMargin3StrategySeasonStatsService;
import com.BetStrat.service.hockey.WinsMarginAny2StrategySeasonStatsService;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.transaction.Transactional;

@Dependent
public class TeamService<T extends StrategySeasonStats> extends StrategyMappingPattern {

    private static final Logger LOGGER = LoggerFactory.getLogger(TeamService.class);

    private StrategySeasonStatsService statsBySeasonService;
    private TeamRepository teamRepository;
    private HistoricMatchRepository historicMatchRepository;

    @Inject
    public TeamService(ComebackStrategySeasonStatsService comebackStrategySeasonstatsService,
                                  LongBasketWinsStrategySeasonStatsService longBasketWinsStrategySeasonstatsService,
                                  ShortBasketWinsStrategySeasonStatsService shortBasketWinsStrategySeasonstatsService,
                                  DrawStrategySeasonStatsService drawStrategySeasonstatsService,
                                  EuroHandicapStrategySeasonStatsService euroHandicapStrategySeasonstatsService,
                                  FlipFlopOversUndersStatsServiceStrategy flipFlopOversUndersInfoService,
                                  GoalsFestStrategySeasonStatsService goalsFestStrategySeasonstatsService,
                                  NoGoalsFestStrategySeasonStatsService noGoalsFestStrategySeasonstatsService,
                                  WinsMarginStrategySeasonStatsService winsMarginStrategySeasonstatsService,
                                  WinsMarginHomeStrategySeasonStatsService winsMarginHomeStrategySeasonStatsService,
                                  WinsStrategySeasonStatsService winsStrategySeasonStatsService,
                                  NoWinsStrategySeasonStatsService noWinsStrategySeasonStatsService,
                                  CleanSheetStrategySeasonStatsService cleanSheetStrategySeasonStatsService,
                                  BttsStrategySeasonStatsService bttsStrategySeasonStatsService,
                                  BttsOneHalfStrategySeasonStatsService bttsOneHalfStrategySeasonStatsService,
                                  NoBttsStrategySeasonStatsService noBttsStrategySeasonStatsService,
                                  ScoreBothHalvesSeasonStatsService scoreBothHalvesSeasonStatsService,
                                  ConcedeBothHalvesSeasonStatsService concedeBothHalvesSeasonStatsService,
                                  WinBothHalvesStrategySeasonStatsService winBothHalvesStrategySeasonStatsService,
                                  WinFirstHalfStrategySeasonStatsService winFirstHalfStrategySeasonStatsService,
                                  NoWinFirstHalfStrategySeasonStatsService noWinFirstHalfStrategySeasonStatsService,
                                  WinAndGoalsStrategySeasonStatsService winAndGoalsStrategySeasonStatsService,
                                  SecondHalfBiggerSeasonStatsService secondHalfBiggerSeasonStatsService,
                                  FirstHalfBiggerSeasonStatsService firstHalfBiggerSeasonStatsService,
                                  HandballWinsMargin16StrategySeasonStatsService handballWinsMargin16StrategySeasonstatsService,
                                  HandballWinsMargin49StrategySeasonStatsService handballWinsMargin49StrategySeasonstatsService,
                                  HandballWinsMargin712StrategySeasonStatsService handballWinsMargin712StrategySeasonstatsService,
                                  HockeyDrawStrategySeasonStatsService hockeyDrawStrategySeasonstatsService,
                                  WinsMargin3StrategySeasonStatsService winsMargin3StrategySeasonstatsService,
                                  WinsMarginAny2StrategySeasonStatsService winsMarginAny2StrategySeasonstatsService) {
        super(comebackStrategySeasonstatsService, longBasketWinsStrategySeasonstatsService, shortBasketWinsStrategySeasonstatsService, drawStrategySeasonstatsService, euroHandicapStrategySeasonstatsService,
                flipFlopOversUndersInfoService, goalsFestStrategySeasonstatsService, noGoalsFestStrategySeasonstatsService, winsMarginStrategySeasonstatsService, winsMarginHomeStrategySeasonStatsService,
                winsStrategySeasonStatsService, noWinsStrategySeasonStatsService, cleanSheetStrategySeasonStatsService, bttsStrategySeasonStatsService, bttsOneHalfStrategySeasonStatsService, noBttsStrategySeasonStatsService, scoreBothHalvesSeasonStatsService,
                concedeBothHalvesSeasonStatsService, winBothHalvesStrategySeasonStatsService, winFirstHalfStrategySeasonStatsService, noWinFirstHalfStrategySeasonStatsService, winAndGoalsStrategySeasonStatsService, secondHalfBiggerSeasonStatsService,
                firstHalfBiggerSeasonStatsService,
                handballWinsMargin16StrategySeasonstatsService, handballWinsMargin49StrategySeasonstatsService, handballWinsMargin712StrategySeasonstatsService, hockeyDrawStrategySeasonstatsService, winsMargin3StrategySeasonstatsService,
                winsMarginAny2StrategySeasonstatsService);
    }

    @Transactional
    public Team insertTeam(Team team) {
        teamRepository.persist(team);
        return team;
    }

    @Transactional
    public Team updateTeamScore (Team team, String strategy) {
        LOGGER.info("Updating score for " + team.getName() + " and strategy " + strategy);

        // Get the service implementation corresponding to the type of strategy
        StrategySeasonStatsInterface<T> service = (StrategySeasonStatsInterface<T>)
                serviceMap.get(serviceMap.keySet().stream().filter(s -> s.getSimpleName().equals(strategy)).findFirst().get());
        // Delegate the insertion to the corresponding service implementation
        Team updatedTeam = service.updateTeamScore(team);
        teamRepository.persist(updatedTeam);

        return updatedTeam;
    }

    public HashMap<String, String> getSimulatedTeamScoreByFilteredSeason (Team team, String strategy, int seasonsToDiscard) {

        HashMap<String, String> outMap = new LinkedHashMap<>();
        Team simulatedTeam = null;

        switch (strategy) {
//            case "hockeyDraw":
//                if (team.getSport().equals("Hockey")) {
//                    simulatedTeam = statsBySeasonService.updateTeamScore(team, HockeyDrawSeasonStats.class);
//                }
//                break;
//            case "hockeyWinsMarginAny2":
//                if (team.getSport().equals("Hockey")) {
//                    simulatedTeam = statsBySeasonService.updateTeamScore(team, WinsMarginAny2SeasonStats.class);
//                }
//                break;
//            case "hockeyWinsMargin3":
//                if (team.getSport().equals("Hockey")) {
//                    simulatedTeam = statsBySeasonService.updateTeamScore(team, WinsMargin3SeasonStats.class);
//                }
//                break;
//            case "footballDrawHunter":
//                if (team.getSport().equals("Football")) {
//                    LinkedHashMap<String, String> simulatedScore = statsBySeasonService.getSimulatedScorePartialSeasons(team, seasonsToDiscard);
//                    outMap.put("beginSeason", team.getBeginSeason());
//                    outMap.put("endSeason", team.getEndSeason());
//                    outMap.putAll(simulatedScore);
//                }
//                break;
//            case "footballMarginWins":
//                if (team.getSport().equals("Football")) {
//                    LinkedHashMap<String, String> simulatedScore = statsBySeasonService.getSimulatedScorePartialSeasons(team, seasonsToDiscard);
//                    outMap.put("beginSeason", team.getBeginSeason());
//                    outMap.put("endSeason", team.getEndSeason());
//                    outMap.putAll(simulatedScore);
//                }
//                break;
//            case "footballGoalsFest":
//                if (team.getSport().equals("Football")) {
//                    LinkedHashMap<String, String> simulatedScore = goalsFestSeasonInfoService.getSimulatedScorePartialSeasons(team, seasonsToDiscard);
//                    outMap.put("beginSeason", team.getBeginSeason());
//                    outMap.put("endSeason", team.getEndSeason());
//                    outMap.putAll(simulatedScore);
//                }
//                break;
//            case "footballEuroHandicap":
//                if (team.getSport().equals("Football")) {
//                    simulatedTeam = statsBySeasonService.updateTeamScore(team, EuroHandicapSeasonStats.class);
//                }
//                break;
//            case "basketComebacks":
//                if (team.getSport().equals("Basketball")) {
//                    simulatedTeam = statsBySeasonService.updateTeamScore(team, ComebackSeasonStats.class);
//                }
//                break;
            default:
                break;
        }

        return outMap;
    }

}


