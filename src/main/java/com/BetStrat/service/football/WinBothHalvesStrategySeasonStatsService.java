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
import com.BetStrat.entity.football.WinBothHalvesSeasonStats;
import com.BetStrat.enums.TeamScoreEnum;
import com.BetStrat.repository.HistoricMatchRepository;
import com.BetStrat.repository.TeamRepository;
import com.BetStrat.repository.football.WinBothHalvesSeasonInfoRepository;
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
public class WinBothHalvesStrategySeasonStatsService extends StrategyScoreCalculator<WinBothHalvesSeasonStats> implements StrategySeasonStatsInterface<WinBothHalvesSeasonStats> {

    private static final Logger LOGGER = LoggerFactory.getLogger(WinBothHalvesStrategySeasonStatsService.class);

    @Inject
    private WinBothHalvesSeasonInfoRepository winBothHalvesSeasonInfoRepository;

    @Inject
    private HistoricMatchRepository historicMatchRepository;

    @Inject
    private TeamRepository teamRepository;

    @Override
    public WinBothHalvesSeasonStats insertStrategySeasonStats(WinBothHalvesSeasonStats strategySeasonStats) {
        LOGGER.info("Inserted " + strategySeasonStats.getClass() + " for " + strategySeasonStats.getTeam().getName() + " and season " + strategySeasonStats.getSeason());
        winBothHalvesSeasonInfoRepository.persist(strategySeasonStats);
        return strategySeasonStats;
    }

    @Override
    public List<WinBothHalvesSeasonStats> getStatsByStrategyAndTeam(Team team, String strategyName) {
        return winBothHalvesSeasonInfoRepository.getFootballWinBothHalvesStatsByTeam(team);
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

        List<WinBothHalvesSeasonStats> statsByTeam = winBothHalvesSeasonInfoRepository.getFootballWinBothHalvesStatsByTeam(team);
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
        if ( (historicMatch.getHomeTeam().equals(teamName) && homeHTResult > awayHTResult && home2HTResult > away2HTResult) ||
                (historicMatch.getAwayTeam().equals(teamName) && awayHTResult > homeHTResult && away2HTResult > home2HTResult) ) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateStrategySeasonStats(Team team, String strategyName) {
        List<WinBothHalvesSeasonStats> statsByTeam = winBothHalvesSeasonInfoRepository.getFootballWinBothHalvesStatsByTeam(team);
        List<String> seasonsList = null;

        if (SUMMER_SEASONS_BEGIN_MONTH_LIST.contains(team.getBeginSeason())) {
            seasonsList = SUMMER_SEASONS_LIST;
        } else if (WINTER_SEASONS_BEGIN_MONTH_LIST.contains(team.getBeginSeason())) {
            seasonsList = WINTER_SEASONS_LIST;
        }

        for (String season : seasonsList) {
            if (!statsByTeam.stream().anyMatch(s -> s.getSeason().equals(season))) {
                String newSeasonUrl = "";

                List<HistoricMatch> filteredMatches = historicMatchRepository.getTeamMatchesBySeason(team, season);
                filteredMatches.sort(HistoricMatch.matchDateComparator);

                if (filteredMatches.isEmpty()) {
                    continue;
                }

                WinBothHalvesSeasonStats winBothHalvesSeasonStats = new WinBothHalvesSeasonStats();

                ArrayList<Integer> negativeSequence = new ArrayList<>();
                int count = 0;
                int totalWins= 0;
                for (HistoricMatch historicMatch : filteredMatches) {
                    count++;
                    String res = historicMatch.getFtResult().split("\\(")[0];
                    int homeResult = Integer.parseInt(res.split(":")[0]);
                    int awayResult = Integer.parseInt(res.split(":")[1]);
                    if ((historicMatch.getHomeTeam().equals(team.getName()) && homeResult>awayResult) || (historicMatch.getAwayTeam().equals(team.getName()) && homeResult<awayResult)) {
                        totalWins++;
                        try {
                            if (matchFollowStrategyRules(historicMatch, team.getName(), null)) {
                                negativeSequence.add(count);
                                count = 0;
                            }
                        } catch (NumberFormatException e) {
                            return;
                        }
                    }
                }

                int totalWinBothHalves = negativeSequence.size();

                negativeSequence.add(count);
                HistoricMatch lastMatch = filteredMatches.get(filteredMatches.size() - 1);
                if (!matchFollowStrategyRules(lastMatch, team.getName(), null)) {
                    negativeSequence.add(-1);
                }

                if (totalWins == 0) {
                    winBothHalvesSeasonStats.setWinBothHalvesRate(0);
                    winBothHalvesSeasonStats.setWinsRate(0);
                } else {
                    winBothHalvesSeasonStats.setWinBothHalvesRate(Utils.beautifyDoubleValue(100*totalWinBothHalves/filteredMatches.size()));
                    winBothHalvesSeasonStats.setWinsRate(Utils.beautifyDoubleValue(100*totalWins/filteredMatches.size()));
                }
                winBothHalvesSeasonStats.setCompetition("all");
                winBothHalvesSeasonStats.setNegativeSequence(negativeSequence.toString());
                winBothHalvesSeasonStats.setNumMatches(filteredMatches.size());
                winBothHalvesSeasonStats.setNumWinsBothHalves(totalWinBothHalves);
                winBothHalvesSeasonStats.setNumWins(totalWins);

                double stdDev =  Utils.beautifyDoubleValue(calculateSD(negativeSequence));
                winBothHalvesSeasonStats.setStdDeviation(stdDev);
                winBothHalvesSeasonStats.setCoefDeviation(Utils.beautifyDoubleValue(calculateCoeffVariation(stdDev, negativeSequence)));
                winBothHalvesSeasonStats.setSeason(season);
                winBothHalvesSeasonStats.setTeam(team);
                winBothHalvesSeasonStats.setUrl(newSeasonUrl);
                insertStrategySeasonStats(winBothHalvesSeasonStats);
            }
        }
        team.setWinBothHalvesMaxRedRun(calculateHistoricMaxNegativeSeq(statsByTeam));
        team.setWinBothHalvesAvgRedRun((int)Math.round(calculateHistoricAvgNegativeSeq(statsByTeam)));
        teamRepository.persist(team);
    }

    @Override
    public int calculateHistoricMaxNegativeSeq(List<WinBothHalvesSeasonStats> statsByTeam) {
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
    public double calculateHistoricAvgNegativeSeq(List<WinBothHalvesSeasonStats> statsByTeam) {
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
        List<WinBothHalvesSeasonStats> statsByTeam = winBothHalvesSeasonInfoRepository.getFootballWinBothHalvesStatsByTeam(teamByName);
        Collections.sort(statsByTeam, StrategySeasonStats.strategySeasonSorter);
        Collections.reverse(statsByTeam);

        if (statsByTeam.size() < 3) {
            teamByName.setWinBothHalvesScore(TeamScoreEnum.INSUFFICIENT_DATA.getValue());
        } else {
            double totalScore = calculateTotalFinalScore(statsByTeam);
            teamByName.setWinBothHalvesScore(calculateFinalRating(totalScore));
        }

        return teamByName;
    }

    private double calculateTotalFinalScore(List<WinBothHalvesSeasonStats> statsByTeam) {
        int last3SeasonsGreensRateScore = calculateLast3SeasonsRateScore(statsByTeam);
        int allSeasonsGreensRateScore = calculateAllSeasonsRateScore(statsByTeam);
        int last3SeasonsTotalWinsRateScore = calculateLast3SeasonsTotalWinsRateScore(statsByTeam);
        int allSeasonsTotalWinsRateScore = calculateAllSeasonsTotalWinsRateScore(statsByTeam);
        int last3SeasonsmaxSeqWOGreensScore = calculateLast3SeasonsMaxSeqWOGreenScore(statsByTeam);
        int allSeasonsmaxSeqWOGreensScore = calculateAllSeasonsMaxSeqWOGreenScore(statsByTeam);
        int last3SeasonsStdDevScore = calculateLast3SeasonsStdDevScore(statsByTeam);
        int allSeasonsStdDevScore = calculateAllSeasonsStdDevScore(statsByTeam);
        int last3SeasonsCoefDevScore = calculateLast3SeasonsCoefDevScore(statsByTeam);
        int allSeasonsCoefDevScore = calculateAllSeasonsCoefDevScore(statsByTeam);
        int totalMatchesScore = calculateLeagueMatchesScore(statsByTeam.get(0).getNumMatches());

        return Utils.beautifyDoubleValue(0.1*last3SeasonsGreensRateScore + 0.05*allSeasonsGreensRateScore +
            0.1*last3SeasonsTotalWinsRateScore + 0.05*allSeasonsTotalWinsRateScore +
            0.14*last3SeasonsmaxSeqWOGreensScore + 0.05*allSeasonsmaxSeqWOGreensScore +
            0.2*last3SeasonsCoefDevScore + 0.07*allSeasonsCoefDevScore +
            0.15*last3SeasonsStdDevScore + 0.07*allSeasonsStdDevScore + 0.02*totalMatchesScore);
    }

    @Override
    public String calculateScoreBySeason(Team team, String season, String strategy) {
        List<WinBothHalvesSeasonStats> statsByTeam = winBothHalvesSeasonInfoRepository.getFootballWinBothHalvesStatsByTeam(team);
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
    public int calculateLast3SeasonsRateScore(List<WinBothHalvesSeasonStats> statsByTeam) {
        double winsRates = 0;
        for (int i=0; i<3; i++) {
            winsRates += statsByTeam.get(i).getWinBothHalvesRate();
        }

        double avgWinsRate = Utils.beautifyDoubleValue(winsRates / 3);

        if (isBetween(avgWinsRate,80,100)) {
            return 100;
        } else if(isBetween(avgWinsRate,70,80)) {
            return 80;
        } else if(isBetween(avgWinsRate,50,70)) {
            return 60;
        } else if(isBetween(avgWinsRate,0,50)) {
            return 30;
        }
        return 0;
    }

    @Override
    public int calculateAllSeasonsRateScore(List<WinBothHalvesSeasonStats> statsByTeam) {
        double winsRates = 0;
        for (int i=0; i<statsByTeam.size(); i++) {
            winsRates += statsByTeam.get(i).getWinBothHalvesRate();
        }

        double avgWinsRate = Utils.beautifyDoubleValue(winsRates / statsByTeam.size());

        if (isBetween(avgWinsRate,80,100)) {
            return 100;
        } else if(isBetween(avgWinsRate,70,80)) {
            return 80;
        } else if(isBetween(avgWinsRate,50,70)) {
            return 60;
        } else if(isBetween(avgWinsRate,0,50)) {
            return 30;
        }
        return 0;
    }

    @Override
    public int calculateLast3SeasonsTotalWinsRateScore(List<WinBothHalvesSeasonStats> statsByTeam) {
        double totalWinsRates = 0;
        for (int i=0; i<3; i++) {
            totalWinsRates += statsByTeam.get(i).getWinBothHalvesRate();
        }

        double avgWinsRate = Utils.beautifyDoubleValue(totalWinsRates / 3);

        if (isBetween(avgWinsRate,80,100)) {
            return 100;
        } else if(isBetween(avgWinsRate,70,80)) {
            return 90;
        } else if(isBetween(avgWinsRate,60,70)) {
            return 80;
        } else if(isBetween(avgWinsRate,50,60)) {
            return 70;
        } else if(isBetween(avgWinsRate,40,50)) {
            return 60;
        } else if(isBetween(avgWinsRate,0,40)) {
            return 30;
        }
        return 0;
    }

    @Override
    public int calculateAllSeasonsTotalWinsRateScore(List<WinBothHalvesSeasonStats> statsByTeam) {
        double totalWinsRates = 0;
        for (int i=0; i<statsByTeam.size(); i++) {
            totalWinsRates += statsByTeam.get(i).getWinBothHalvesRate();
        }

        double avgWinsRate = Utils.beautifyDoubleValue(totalWinsRates / statsByTeam.size());

        if (isBetween(avgWinsRate,80,100)) {
            return 100;
        } else if(isBetween(avgWinsRate,70,80)) {
            return 90;
        } else if(isBetween(avgWinsRate,60,70)) {
            return 80;
        } else if(isBetween(avgWinsRate,50,60)) {
            return 70;
        } else if(isBetween(avgWinsRate,40,50)) {
            return 60;
        } else if(isBetween(avgWinsRate,0,40)) {
            return 30;
        }
        return 0;
    }

}