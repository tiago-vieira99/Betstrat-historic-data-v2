package com.BetStrat.service.football;

import static com.BetStrat.constants.BetStratConstants.DEFAULT_BAD_RUN_TO_NEW_SEQ;
import static com.BetStrat.constants.BetStratConstants.SUMMER_SEASONS_BEGIN_MONTH_LIST;
import static com.BetStrat.constants.BetStratConstants.SUMMER_SEASONS_LIST;
import static com.BetStrat.constants.BetStratConstants.WINTER_SEASONS_BEGIN_MONTH_LIST;
import static com.BetStrat.constants.BetStratConstants.WINTER_SEASONS_LIST;
import static com.BetStrat.utils.Utils.calculateCoeffVariation;
import static com.BetStrat.utils.Utils.calculateSD;

import com.BetStrat.dto.SimulatedMatchDto;
import com.BetStrat.entity.HistoricMatch;
import com.BetStrat.entity.StrategySeasonStats;
import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.NoGoalsFestSeasonStats;
import com.BetStrat.enums.TeamScoreEnum;
import com.BetStrat.repository.HistoricMatchRepository;
import com.BetStrat.repository.TeamRepository;
import com.BetStrat.repository.football.NoGoalsFestSeasonInfoRepository;
import com.BetStrat.service.StrategyScoreCalculator;
import com.BetStrat.service.StrategySeasonStatsInterface;
import com.BetStrat.utils.Utils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ApplicationScoped
@Transactional
public class NoGoalsFestStrategySeasonStatsService extends StrategyScoreCalculator<NoGoalsFestSeasonStats> implements StrategySeasonStatsInterface<NoGoalsFestSeasonStats> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoGoalsFestStrategySeasonStatsService.class);

    @Inject
    private NoGoalsFestSeasonInfoRepository noGoalsFestSeasonInfoRepository;

    @Inject
    private HistoricMatchRepository historicMatchRepository;

    @Inject
    private TeamRepository teamRepository;

    @Override
    public NoGoalsFestSeasonStats insertStrategySeasonStats(NoGoalsFestSeasonStats strategySeasonStats) {
        LOGGER.info("Inserted " + strategySeasonStats.getClass() + " for " + strategySeasonStats.getTeam().getName() + " and season " + strategySeasonStats.getSeason());
        noGoalsFestSeasonInfoRepository.persist(strategySeasonStats);
        return strategySeasonStats;
    }

    @Override
    public List<NoGoalsFestSeasonStats> getStatsByStrategyAndTeam(Team team, String strategyName) {
        return noGoalsFestSeasonInfoRepository.getNoGoalsFestStatsByTeam(team);
    }

    @Override
    public HashMap<String, Object> getSimulatedMatchesByStrategyAndSeason(String season, Team team, String strategyName) {
        HashMap<String, Object> simuMapForSeason = new HashMap<>();
        List<SimulatedMatchDto> matchesBetted = new ArrayList<>();
        List<HistoricMatch> teamMatchesBySeason = historicMatchRepository.getTeamMatchesBySeason(team, season);
        Collections.sort(teamMatchesBySeason, HistoricMatch.matchDateComparator);

        if (teamMatchesBySeason.size() == 0) {
            return simuMapForSeason;
        }

        List<NoGoalsFestSeasonStats> statsByTeam = noGoalsFestSeasonInfoRepository.getNoGoalsFestStatsByTeam(team);
        Collections.sort(statsByTeam, StrategySeasonStats.strategySeasonSorter);
        Collections.reverse(statsByTeam);

        int indexOfSeason = WINTER_SEASONS_LIST.indexOf(season);
        statsByTeam = statsByTeam.stream().filter(s -> WINTER_SEASONS_LIST.indexOf(s.getSeason()) < indexOfSeason).collect(Collectors.toList());
        int avgNegativeSeqForSeason = (int) Math.round(calculateHistoricAvgNegativeSeq(statsByTeam));
        int maxNegativeSeqForSeason = calculateHistoricMaxNegativeSeq(statsByTeam);

        boolean isActiveSequence = false; //when true it always bet on the first game
        int actualNegativeSequence = 0;
        for (int i = 0; i < teamMatchesBySeason.size(); i++) {
            HistoricMatch historicMatch = teamMatchesBySeason.get(i);
            if ((actualNegativeSequence >= Math.max(DEFAULT_BAD_RUN_TO_NEW_SEQ, maxNegativeSeqForSeason - statsByTeam.get(0).getMaxSeqScale() / 10))) {
                isActiveSequence = true;
            }

            if (isActiveSequence) {
                SimulatedMatchDto simulatedMatchDto = new SimulatedMatchDto();
                simulatedMatchDto.setMatchDate(historicMatch.getMatchDate());
                simulatedMatchDto.setHomeTeam(historicMatch.getHomeTeam());
                simulatedMatchDto.setAwayTeam(historicMatch.getAwayTeam());
                simulatedMatchDto.setMatchNumber(String.valueOf(i+1));
                simulatedMatchDto.setHtResult(historicMatch.getHtResult());
                simulatedMatchDto.setFtResult(historicMatch.getFtResult());
                simulatedMatchDto.setSeason(season);
                simulatedMatchDto.setCompetition(historicMatch.getCompetition());
                if (matchFollowStrategyRules(historicMatch, team.getName(), null)) {
                    simulatedMatchDto.setIsGreen(true);
                    actualNegativeSequence = 0;
                    isActiveSequence = false;
                } else {
                    simulatedMatchDto.setIsGreen(false);
                }
                matchesBetted.add(simulatedMatchDto);
            } else {
                if (!matchFollowStrategyRules(historicMatch, team.getName(), null)) {
                    actualNegativeSequence++;
                } else {
                    actualNegativeSequence = 0;
                }
            }
        }
        simuMapForSeason.put("matchesBetted", matchesBetted);
        simuMapForSeason.put("avgNegativeSeq", avgNegativeSeqForSeason);
        simuMapForSeason.put("maxNegativeSeq", maxNegativeSeqForSeason);
        return simuMapForSeason;
    }

    @Override
    public boolean matchFollowStrategyRules(HistoricMatch historicMatch, String teamName, String strategyName) {
        String res = historicMatch.getFtResult().split("\\(")[0];
        int homeResult = Integer.parseInt(res.split(":")[0]);
        int awayResult = Integer.parseInt(res.split(":")[1]);
        if (homeResult == 0 || awayResult == 0 || homeResult+awayResult <= 2) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateStrategySeasonStats(Team team, String strategyName) {
        List<NoGoalsFestSeasonStats> statsByTeam = noGoalsFestSeasonInfoRepository.getNoGoalsFestStatsByTeam(team);
        List<String> seasonsList = null;

        if (SUMMER_SEASONS_BEGIN_MONTH_LIST.contains(team.getBeginSeason())) {
            seasonsList = SUMMER_SEASONS_LIST;
        } else if (WINTER_SEASONS_BEGIN_MONTH_LIST.contains(team.getBeginSeason())) {
            seasonsList = WINTER_SEASONS_LIST;
        }

        for (String season : seasonsList) {
            if (!statsByTeam.stream().filter(s -> s.getSeason().equals(season)).findAny().isPresent()) {
                String newSeasonUrl = "";

                List<HistoricMatch> teamMatchesBySeason = historicMatchRepository.getTeamMatchesBySeason(team, season);
                teamMatchesBySeason.sort(HistoricMatch.matchDateComparator);

                if (teamMatchesBySeason.size() == 0) {
                    continue;
                }

                NoGoalsFestSeasonStats noGoalsFestSeasonStats = new NoGoalsFestSeasonStats();

                ArrayList<Integer> strategySequence = new ArrayList<>();
                int count = 0;
                for (HistoricMatch historicMatch : teamMatchesBySeason) {
                    count++;
                    if (matchFollowStrategyRules(historicMatch, team.getName(), null)) {
                        strategySequence.add(count);
                        count = 0;
                    }
                }

                int totalNoGoalsFest = strategySequence.size();

                strategySequence.add(count);
                HistoricMatch lastMatch = teamMatchesBySeason.get(teamMatchesBySeason.size() - 1);
                if (!matchFollowStrategyRules(lastMatch, team.getName(), null)) {
                    strategySequence.add(-1);
                }

                if (totalNoGoalsFest == 0) {
                    noGoalsFestSeasonStats.setNoGoalsFestRate(0);
                } else {
                    noGoalsFestSeasonStats.setNoGoalsFestRate(Utils.beautifyDoubleValue(100*totalNoGoalsFest/teamMatchesBySeason.size()));
                }
                noGoalsFestSeasonStats.setCompetition("all");
                noGoalsFestSeasonStats.setNegativeSequence(strategySequence.toString());
                noGoalsFestSeasonStats.setNumNoGoalsFest(totalNoGoalsFest);
                noGoalsFestSeasonStats.setNumMatches(teamMatchesBySeason.size());

                double stdDev =  Utils.beautifyDoubleValue(calculateSD(strategySequence));
                noGoalsFestSeasonStats.setStdDeviation(stdDev);
                noGoalsFestSeasonStats.setCoefDeviation(Utils.beautifyDoubleValue(calculateCoeffVariation(stdDev, strategySequence)));
                noGoalsFestSeasonStats.setSeason(season);
                noGoalsFestSeasonStats.setTeam(team);
                noGoalsFestSeasonStats.setUrl(newSeasonUrl);
                insertStrategySeasonStats(noGoalsFestSeasonStats);
            }
        }
        team.setNoGoalsFestMaxRedRun(calculateHistoricMaxNegativeSeq(statsByTeam));
        team.setNoGoalsFestAvgRedRun((int)Math.round(calculateHistoricAvgNegativeSeq(statsByTeam)));
        teamRepository.persist(team);
    }

    @Override
    public int calculateHistoricMaxNegativeSeq(List<NoGoalsFestSeasonStats> statsByTeam) {
        int maxValue = 0;
        for (int i=0; i<statsByTeam.size(); i++) {
            int[] currSeqMaxValue = Arrays.stream(statsByTeam.get(i).getNegativeSequence().replaceAll("\\[","").replaceAll("\\]","")
                .replaceAll(" ","").split(",")).mapToInt(Integer::parseInt).toArray();
            if (currSeqMaxValue.length > 2) {
                for (int j = 0; j < currSeqMaxValue.length - 1; j++) {
                    if (currSeqMaxValue[j] > maxValue)
                        maxValue = currSeqMaxValue[j];
                }
            } else {
                if (currSeqMaxValue[0] > maxValue)
                    maxValue = currSeqMaxValue[0];
            }
        }

        return maxValue;
    }

    @Override
    public double calculateHistoricAvgNegativeSeq(List<NoGoalsFestSeasonStats> statsByTeam) {
        int seqValues = 0;
        int count = 0;
        for (int i=0; i<statsByTeam.size(); i++) {
            String[] arraySeq = statsByTeam.get(i).getNegativeSequence()
                .replaceAll("\\[","").replaceAll("\\]","").replaceAll(" ","").split(",");
            count += arraySeq.length - 1;
            for (int j = 0; j < arraySeq.length - 1; j++)
                seqValues += Integer.parseInt(arraySeq[j]);
        }

        return Utils.beautifyDoubleValue((double) seqValues / (double) count);
    }

    @Override
    public Team updateTeamScore(Team teamByName) {
        List<NoGoalsFestSeasonStats> statsByTeam = noGoalsFestSeasonInfoRepository.getNoGoalsFestStatsByTeam(teamByName);
        Collections.sort(statsByTeam, StrategySeasonStats.strategySeasonSorter);
        Collections.reverse(statsByTeam);

        if (statsByTeam.size() < 3 || statsByTeam.stream().filter(s -> s.getNumMatches() < 15).findAny().isPresent()) {
            teamByName.setNoGoalsFestScore(TeamScoreEnum.INSUFFICIENT_DATA.getValue());
        } else {
            double totalScore = calculateTotalFinalScore(statsByTeam);
            teamByName.setNoGoalsFestScore(calculateFinalRating(totalScore));
        }

        return teamByName;
    }

    private double calculateTotalFinalScore(List<NoGoalsFestSeasonStats> statsByTeam) {
        int last3SeasonsGreensRateScore = calculateLast3SeasonsRateScore(statsByTeam);
        int allSeasonsGreensRateScore = calculateAllSeasonsRateScore(statsByTeam);
        int last3SeasonsmaxSeqWOGreenScore = calculateLast3SeasonsMaxSeqWOGreenScore(statsByTeam);
        int allSeasonsmaxSeqWOGreenScore = calculateAllSeasonsMaxSeqWOGreenScore(statsByTeam);
        int last3SeasonsStdDevScore = calculateLast3SeasonsStdDevScore(statsByTeam);
        int allSeasonsStdDevScore = calculateAllSeasonsStdDevScore(statsByTeam);
        int last3SeasonsCoefDevScore = calculateLast3SeasonsCoefDevScore(statsByTeam);
        int allSeasonsCoefDevScore = calculateAllSeasonsCoefDevScore(statsByTeam);
        int totalMatchesScore = calculateLeagueMatchesScore(statsByTeam.get(0).getNumMatches());

        return Utils.beautifyDoubleValue(0.15*last3SeasonsGreensRateScore + 0.05*allSeasonsGreensRateScore +
            0.15*last3SeasonsmaxSeqWOGreenScore + 0.07*allSeasonsmaxSeqWOGreenScore +
            0.2*last3SeasonsCoefDevScore + 0.11*allSeasonsCoefDevScore +
            0.18*last3SeasonsStdDevScore + 0.07*allSeasonsStdDevScore + 0.02*totalMatchesScore);
    }

    @Override
    public String calculateScoreBySeason(Team team, String season, String strategy) {
        List<NoGoalsFestSeasonStats> statsByTeam = noGoalsFestSeasonInfoRepository.getNoGoalsFestStatsByTeam(team);
        Collections.sort(statsByTeam, StrategySeasonStats.strategySeasonSorter);
        Collections.reverse(statsByTeam);

        int indexOfSeason = WINTER_SEASONS_LIST.indexOf(season);
        statsByTeam = statsByTeam.stream().filter(s -> WINTER_SEASONS_LIST.indexOf(s.getSeason()) < indexOfSeason).collect(Collectors.toList());

        if (statsByTeam.size() < 3 || statsByTeam.stream().filter(s -> s.getNumMatches() < 15).findAny().isPresent()) {
            return TeamScoreEnum.INSUFFICIENT_DATA.getValue();
        } else {
            double totalScore = calculateTotalFinalScore(statsByTeam);
            return calculateFinalRating(totalScore);
        }
    }

    @Override
    public int calculateLast3SeasonsRateScore(List<NoGoalsFestSeasonStats> statsByTeam) {
        double GoalsFestRates = 0;
        for (int i=0; i<3; i++) {
            GoalsFestRates += statsByTeam.get(i).getNoGoalsFestRate();
        }

        double avgGreensRate = Utils.beautifyDoubleValue(GoalsFestRates / 3);

        if (isBetween(avgGreensRate,60,100)) {
            return 100;
        } else if(isBetween(avgGreensRate,50,60)) {
            return 90;
        } else if(isBetween(avgGreensRate,35,50)) {
            return 80;
        } else if(isBetween(avgGreensRate,30,35)) {
            return 60;
        } else if(isBetween(avgGreensRate,20,30)) {
            return 50;
        } else if(isBetween(avgGreensRate,0,20)) {
            return 30;
        }
        return 0;
    }

    @Override
    public int calculateAllSeasonsRateScore(List<NoGoalsFestSeasonStats> statsByTeam) {
        double GoalsFestRates = 0;
        for (int i=0; i<statsByTeam.size(); i++) {
            GoalsFestRates += statsByTeam.get(i).getNoGoalsFestRate();
        }

        double avgGreensRate = Utils.beautifyDoubleValue(GoalsFestRates / statsByTeam.size());

        if (isBetween(avgGreensRate,60,100)) {
            return 100;
        } else if(isBetween(avgGreensRate,50,60)) {
            return 90;
        } else if(isBetween(avgGreensRate,35,50)) {
            return 80;
        } else if(isBetween(avgGreensRate,30,35)) {
            return 60;
        } else if(isBetween(avgGreensRate,20,30)) {
            return 50;
        } else if(isBetween(avgGreensRate,0,20)) {
            return 30;
        }
        return 0;
    }
    @Override
    public int calculateLast3SeasonsTotalWinsRateScore(List<NoGoalsFestSeasonStats> statsByTeam) {
        return 0;
    }

    @Override
    public int calculateAllSeasonsTotalWinsRateScore(List<NoGoalsFestSeasonStats> statsByTeam) {
        return 0;
    }

}