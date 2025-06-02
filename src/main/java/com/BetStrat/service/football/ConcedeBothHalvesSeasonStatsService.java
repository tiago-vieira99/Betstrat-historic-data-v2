package com.BetStrat.service.football;

import static com.BetStrat.constants.BetStratConstants.*;
import static com.BetStrat.utils.Utils.calculateCoeffVariation;
import static com.BetStrat.utils.Utils.calculateSD;

import com.BetStrat.dto.SimulatedMatchDto;
import com.BetStrat.entity.HistoricMatch;
import com.BetStrat.entity.StrategySeasonStats;
import com.BetStrat.entity.Team;
import com.BetStrat.entity.football.ConcedeBothHalvesSeasonStats;
import com.BetStrat.enums.TeamScoreEnum;
import com.BetStrat.repository.HistoricMatchRepository;
import com.BetStrat.repository.TeamRepository;
import com.BetStrat.repository.football.ConcedeBothHalvesSeasonInfoRepository;
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
public class ConcedeBothHalvesSeasonStatsService extends StrategyScoreCalculator<ConcedeBothHalvesSeasonStats> implements StrategySeasonStatsInterface<ConcedeBothHalvesSeasonStats> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConcedeBothHalvesSeasonStatsService.class);

    @Inject
    private ConcedeBothHalvesSeasonInfoRepository concedeBothHalvesSeasonInfoRepository;

    @Inject
    private HistoricMatchRepository historicMatchRepository;

    @Inject
    private TeamRepository teamRepository;

    @Override
    public ConcedeBothHalvesSeasonStats insertStrategySeasonStats(ConcedeBothHalvesSeasonStats strategySeasonStats) {
        LOGGER.info("Inserted " + strategySeasonStats.getClass() + " for " + strategySeasonStats.getTeam().getName() + " and season " + strategySeasonStats.getSeason());
        concedeBothHalvesSeasonInfoRepository.persist(strategySeasonStats);
        return strategySeasonStats;
    }

    @Override
    public List<ConcedeBothHalvesSeasonStats> getStatsByStrategyAndTeam(Team team, String strategyName) {
        return concedeBothHalvesSeasonInfoRepository.getFootballConcedeBothHalvesStatsByTeam(team);
    }

    @Override
    public void updateStrategySeasonStats(Team team, String strategyName) {
        List<ConcedeBothHalvesSeasonStats> statsByTeam = concedeBothHalvesSeasonInfoRepository.getFootballConcedeBothHalvesStatsByTeam(team);
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

                ConcedeBothHalvesSeasonStats ConcedeBothHalvesSeasonStats = new ConcedeBothHalvesSeasonStats();

                ArrayList<Integer> strategySequence = new ArrayList<>();
                int count = 0;
                for (HistoricMatch historicMatch : teamMatchesBySeason) {
                    count++;
                    try {
                        if (matchFollowStrategyRules(historicMatch, team.getName(), null)) {
                            strategySequence.add(count);
                            count = 0;
                        }
                    } catch (NumberFormatException e) {
                        return;
                    }
                }

                int totalConcedeBothHalves = strategySequence.size();

                strategySequence.add(count);
                HistoricMatch lastMatch = teamMatchesBySeason.get(teamMatchesBySeason.size() - 1);
                if (!matchFollowStrategyRules(lastMatch, team.getName(), null)) {
                    strategySequence.add(-1);
                }

                if (totalConcedeBothHalves == 0) {
                    ConcedeBothHalvesSeasonStats.setConcedeBothHalvesRate(0);
                } else {
                    ConcedeBothHalvesSeasonStats.setConcedeBothHalvesRate(Utils.beautifyDoubleValue(100*totalConcedeBothHalves/teamMatchesBySeason.size()));
                }
                ConcedeBothHalvesSeasonStats.setCompetition("all");
                ConcedeBothHalvesSeasonStats.setNegativeSequence(strategySequence.toString());
                ConcedeBothHalvesSeasonStats.setNumConcedeBothHalves(totalConcedeBothHalves);
                ConcedeBothHalvesSeasonStats.setNumMatches(teamMatchesBySeason.size());

                double stdDev =  Utils.beautifyDoubleValue(calculateSD(strategySequence));
                ConcedeBothHalvesSeasonStats.setStdDeviation(stdDev);
                ConcedeBothHalvesSeasonStats.setCoefDeviation(Utils.beautifyDoubleValue(calculateCoeffVariation(stdDev, strategySequence)));
                ConcedeBothHalvesSeasonStats.setSeason(season);
                ConcedeBothHalvesSeasonStats.setTeam(team);
                ConcedeBothHalvesSeasonStats.setUrl(newSeasonUrl);
                insertStrategySeasonStats(ConcedeBothHalvesSeasonStats);
            }
        }
        team.setConcedeBothHalvesMaxRedRun(calculateHistoricMaxNegativeSeq(statsByTeam));
        team.setConcedeBothHalvesAvgRedRun((int)Math.round(calculateHistoricAvgNegativeSeq(statsByTeam)));
        teamRepository.persist(team);
    }

    @Override
    public int calculateHistoricMaxNegativeSeq(List<ConcedeBothHalvesSeasonStats> statsByTeam) {
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
    public double calculateHistoricAvgNegativeSeq(List<ConcedeBothHalvesSeasonStats> statsByTeam) {
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
        List<ConcedeBothHalvesSeasonStats> statsByTeam = concedeBothHalvesSeasonInfoRepository.getFootballConcedeBothHalvesStatsByTeam(teamByName);
        Collections.sort(statsByTeam, StrategySeasonStats.strategySeasonSorter);
        Collections.reverse(statsByTeam);

        if (statsByTeam.size() < 3 || statsByTeam.stream().filter(s -> s.getNumMatches() < 15).findAny().isPresent()) {
            teamByName.setConcedeBothHalvesScore(TeamScoreEnum.INSUFFICIENT_DATA.getValue());
        } else {
            double totalScore = calculateTotalFinalScore(statsByTeam);
            teamByName.setConcedeBothHalvesScore(calculateFinalRating(totalScore));
        }

        return teamByName;
    }

    private double calculateTotalFinalScore(List<ConcedeBothHalvesSeasonStats> statsByTeam) {
        int last3SeasonsConcedeBothHalvesRateScore = calculateLast3SeasonsRateScore(statsByTeam);
        int allSeasonsConcedeBothHalvesRateScore = calculateAllSeasonsRateScore(statsByTeam);
        int last3SeasonsmaxSeqWOGreenScore = calculateLast3SeasonsMaxSeqWOGreenScore(statsByTeam);
        int allSeasonsmaxSeqWOGreenScore = calculateAllSeasonsMaxSeqWOGreenScore(statsByTeam);
        int last3SeasonsStdDevScore = calculateLast3SeasonsStdDevScore(statsByTeam);
        int allSeasonsStdDevScore = calculateAllSeasonsStdDevScore(statsByTeam);
        int last3SeasonsCoefDevScore = calculateLast3SeasonsCoefDevScore(statsByTeam);
        int allSeasonsCoefDevScore = calculateAllSeasonsCoefDevScore(statsByTeam);
        int totalMatchesScore = calculateLeagueMatchesScore(statsByTeam.get(0).getNumMatches());

        return Utils.beautifyDoubleValue(0.15*last3SeasonsConcedeBothHalvesRateScore + 0.05*allSeasonsConcedeBothHalvesRateScore +
            0.15*last3SeasonsmaxSeqWOGreenScore + 0.07*allSeasonsmaxSeqWOGreenScore +
            0.2*last3SeasonsCoefDevScore + 0.11*allSeasonsCoefDevScore +
            0.18*last3SeasonsStdDevScore + 0.07*allSeasonsStdDevScore + 0.02*totalMatchesScore);
    }

    @Override
    public String calculateScoreBySeason(Team team, String season, String strategy) {
        List<ConcedeBothHalvesSeasonStats> statsByTeam = concedeBothHalvesSeasonInfoRepository.getFootballConcedeBothHalvesStatsByTeam(team);
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
    public HashMap<String, Object> getSimulatedMatchesByStrategyAndSeason(String season, Team team, String strategyName) {
        HashMap<String, Object> simuMapForSeason = new HashMap<>();
        List<SimulatedMatchDto> matchesBetted = new ArrayList<>();
        List<HistoricMatch> teamMatchesBySeason = historicMatchRepository.getTeamMatchesBySeason(team, season);
        Collections.sort(teamMatchesBySeason, HistoricMatch.matchDateComparator);

        if (teamMatchesBySeason.size() == 0) {
            return simuMapForSeason;
        }

        List<ConcedeBothHalvesSeasonStats> statsByTeam = concedeBothHalvesSeasonInfoRepository.getFootballConcedeBothHalvesStatsByTeam(team);
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
            if ((actualNegativeSequence >= Math.max(DEFAULT_BAD_RUN_TO_NEW_SEQ, (maxNegativeSeqForSeason - statsByTeam.get(0).getMaxSeqScale() / 10) / 2))) {
                isActiveSequence = true;
            }

            boolean isMatchGreen = false;
            try {
                isMatchGreen = matchFollowStrategyRules(historicMatch, team.getName(), null);
            } catch (NumberFormatException e) {
                return null;
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
                if (isMatchGreen) {
                    simulatedMatchDto.setIsGreen(true);
                    actualNegativeSequence = 0;
                    isActiveSequence = false;
                } else {
                    simulatedMatchDto.setIsGreen(false);
                }
                matchesBetted.add(simulatedMatchDto);
            } else {
                if (!isMatchGreen) {
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
        String htRes = historicMatch.getHtResult().split("\\(")[0];
        int homeResult = Integer.parseInt(res.split(":")[0]);
        int awayResult = Integer.parseInt(res.split(":")[1]);
        int homeHTResult = Integer.parseInt(htRes.split(":")[0]);
        int awayHTResult = Integer.parseInt(htRes.split(":")[1]);
        int home2HTResult = homeResult - homeHTResult;
        int away2HTResult = awayResult - awayHTResult;
        if ( (historicMatch.getHomeTeam().equals(teamName) && awayHTResult > 0 && away2HTResult > 0) ||
                (historicMatch.getAwayTeam().equals(teamName) && homeHTResult > 0 && home2HTResult > 0) ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int calculateLast3SeasonsRateScore(List<ConcedeBothHalvesSeasonStats> statsByTeam) {
        double GoalsFestRates = 0;
        for (int i=0; i<3; i++) {
            GoalsFestRates += statsByTeam.get(i).getConcedeBothHalvesRate();
        }

        double avgWinsRate = Utils.beautifyDoubleValue(GoalsFestRates / 3);

        if (super.isBetween(avgWinsRate,40,100)) {
            return 100;
        } else if(super.isBetween(avgWinsRate,30,40)) {
            return 80;
        } else if(super.isBetween(avgWinsRate,25,30)) {
            return 60;
        } else if(super.isBetween(avgWinsRate,0,25)) {
            return 30;
        }
        return 0;
    }

    @Override
    public int calculateAllSeasonsRateScore(List<ConcedeBothHalvesSeasonStats> statsByTeam) {
        double GoalsFestRates = 0;
        for (int i=0; i<statsByTeam.size(); i++) {
            GoalsFestRates += statsByTeam.get(i).getConcedeBothHalvesRate();
        }

        double avgWinsRate = Utils.beautifyDoubleValue(GoalsFestRates / statsByTeam.size());

        if (super.isBetween(avgWinsRate,40,100)) {
            return 100;
        } else if(super.isBetween(avgWinsRate,30,40)) {
            return 80;
        } else if(super.isBetween(avgWinsRate,25,30)) {
            return 60;
        } else if(super.isBetween(avgWinsRate,0,25)) {
            return 30;
        }
        return 0;
    }

    @Override
    public int calculateLast3SeasonsTotalWinsRateScore(List<ConcedeBothHalvesSeasonStats> statsByTeam) {
        return 0;
    }

    @Override
    public int calculateAllSeasonsTotalWinsRateScore(List<ConcedeBothHalvesSeasonStats> statsByTeam) {
        return 0;
    }

}