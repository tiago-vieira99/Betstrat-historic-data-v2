package com.BetStrat.service.handball;

import static com.BetStrat.constants.BetStratConstants.SEASONS_LIST;
import static com.BetStrat.constants.BetStratConstants.SUMMER_SEASONS_BEGIN_MONTH_LIST;
import static com.BetStrat.constants.BetStratConstants.SUMMER_SEASONS_LIST;
import static com.BetStrat.constants.BetStratConstants.WINTER_SEASONS_BEGIN_MONTH_LIST;
import static com.BetStrat.constants.BetStratConstants.WINTER_SEASONS_LIST;
import static com.BetStrat.utils.Utils.calculateCoeffVariation;
import static com.BetStrat.utils.Utils.calculateSD;

import com.BetStrat.entity.HistoricMatch;
import com.BetStrat.entity.Team;
import com.BetStrat.entity.handball.Handball16WinsMarginSeasonStats;
import com.BetStrat.enums.TeamScoreEnum;
import com.BetStrat.repository.HistoricMatchRepository;
import com.BetStrat.repository.handball.Handball16WinsMarginSeasonInfoRepository;
import com.BetStrat.service.StrategyScoreCalculator;
import com.BetStrat.service.StrategySeasonStatsInterface;
import com.BetStrat.utils.Utils;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ApplicationScoped
@Transactional
public class HandballWinsMargin16StrategySeasonStatsService extends StrategyScoreCalculator<Handball16WinsMarginSeasonStats> implements StrategySeasonStatsInterface<Handball16WinsMarginSeasonStats> {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandballWinsMargin16StrategySeasonStatsService.class);

    @Inject
    private Handball16WinsMarginSeasonInfoRepository handball16WinsMarginSeasonInfoRepository;

    @Inject
    private HistoricMatchRepository historicMatchRepository;

    @Override
    public Handball16WinsMarginSeasonStats insertStrategySeasonStats(Handball16WinsMarginSeasonStats strategySeasonStats) {
        LOGGER.info("Inserted " + strategySeasonStats.getClass() + " for " + strategySeasonStats.getTeam().getName() + " and season " + strategySeasonStats.getSeason());
        handball16WinsMarginSeasonInfoRepository.persist(strategySeasonStats);
        return strategySeasonStats;
    }

    @Override
    public List<Handball16WinsMarginSeasonStats> getStatsByStrategyAndTeam(Team team, String strategyName) {
        return handball16WinsMarginSeasonInfoRepository.getHandball16WinsMarginStatsByTeam(team);
    }

    @Override
    public HashMap<String, Object> getSimulatedMatchesByStrategyAndSeason(String season, Team team, String strategyName) {
        return null;
    }

    @Override
    public boolean matchFollowStrategyRules(HistoricMatch historicMatch, String teamName, String strategyName) {
        return false;
    }

    @Override
    public void updateStrategySeasonStats(Team team, String strategyName) {
        List<Handball16WinsMarginSeasonStats> statsByTeam = handball16WinsMarginSeasonInfoRepository.getHandball16WinsMarginStatsByTeam(team);
        List<String> seasonsList = null;

        if (SUMMER_SEASONS_BEGIN_MONTH_LIST.contains(team.getBeginSeason())) {
            seasonsList = SUMMER_SEASONS_LIST;
        } else if (WINTER_SEASONS_BEGIN_MONTH_LIST.contains(team.getBeginSeason())) {
            seasonsList = WINTER_SEASONS_LIST;
        }

        for (String season : seasonsList) {
            if (!statsByTeam.stream().filter(s -> s.getSeason().equals(season)).findAny().isPresent()) {

                List<HistoricMatch> teamMatchesBySeason = historicMatchRepository.getTeamMatchesBySeason(team, season);
                String mainCompetition = Utils.findMainCompetition(teamMatchesBySeason);
                List<HistoricMatch> filteredMatches = teamMatchesBySeason.stream().filter(t -> t.getCompetition().equals(mainCompetition)).collect(Collectors.toList());
                filteredMatches.sort(HistoricMatch.matchDateComparator);

                Handball16WinsMarginSeasonStats handball16WinsMarginSeasonInfo = new Handball16WinsMarginSeasonStats();

                ArrayList<Integer> noMarginWinsSequence = new ArrayList<>();
                int count = 0;
                int totalWins= 0;
                for (HistoricMatch historicMatch : filteredMatches) {
                    String res = historicMatch.getFtResult();
                    count++;
                    int homeResult = Integer.parseInt(res.split(":")[0]);
                    int awayResult = 0;
                    if (res.contains("(")){
                        awayResult = homeResult;
                    } else {
                        awayResult = Integer.parseInt(res.split(":")[1]);
                    }

                    if ((historicMatch.getHomeTeam().equals(team.getName()) && homeResult > awayResult) || (historicMatch.getAwayTeam().equals(team.getName()) && homeResult < awayResult)) {
                        totalWins++;
                        if (Math.abs(homeResult - awayResult) <= 6 && Math.abs(homeResult - awayResult) >= 1) {
                            noMarginWinsSequence.add(count);
                            count = 0;
                        }
                    }
                }

                int totalMarginWins = noMarginWinsSequence.size();

                noMarginWinsSequence.add(count);
                if (noMarginWinsSequence.get(noMarginWinsSequence.size()-1) != 0) {
                    noMarginWinsSequence.add(-1);
                }

                handball16WinsMarginSeasonInfo.setCompetition(mainCompetition);
                handball16WinsMarginSeasonInfo.setMarginWinsRate(Utils.beautifyDoubleValue(100*totalMarginWins/totalWins));
                handball16WinsMarginSeasonInfo.setNegativeSequence(noMarginWinsSequence.toString());
                handball16WinsMarginSeasonInfo.setNumMarginWins(totalMarginWins);
                handball16WinsMarginSeasonInfo.setNumMatches(filteredMatches.size());
                handball16WinsMarginSeasonInfo.setNumWins(totalWins);
                handball16WinsMarginSeasonInfo.setWinsRate(Utils.beautifyDoubleValue(100*totalWins/ filteredMatches.size()));

                double stdDev =  Utils.beautifyDoubleValue(calculateSD(noMarginWinsSequence));
                handball16WinsMarginSeasonInfo.setStdDeviation(stdDev);
                handball16WinsMarginSeasonInfo.setCoefDeviation(Utils.beautifyDoubleValue(calculateCoeffVariation(stdDev, noMarginWinsSequence)));

                handball16WinsMarginSeasonInfo.setSeason(season);
                handball16WinsMarginSeasonInfo.setTeam(team);
                handball16WinsMarginSeasonInfo.setUrl(team.getUrl());
                insertStrategySeasonStats(handball16WinsMarginSeasonInfo);
            }
        }
    }

    @Override
    public int calculateHistoricMaxNegativeSeq(List<Handball16WinsMarginSeasonStats> statsByTeam) {
        return 0;
    }

    @Override
    public double calculateHistoricAvgNegativeSeq(List<Handball16WinsMarginSeasonStats> statsByTeam) {
        return 0;
    }

    @Override
    public Team updateTeamScore(Team teamByName) {
        List<Handball16WinsMarginSeasonStats> statsByTeam = handball16WinsMarginSeasonInfoRepository.getHandball16WinsMarginStatsByTeam(teamByName);
        Collections.sort(statsByTeam, new SortStatsDataBySeason());
        Collections.reverse(statsByTeam);

        if (statsByTeam.size() < 3) {
            teamByName.setHandball16MarginWinsScore(TeamScoreEnum.INSUFFICIENT_DATA.getValue());
        } else {
            int last3SeasonsMarginWinsRateScore = calculateLast3SeasonsRateScore(statsByTeam);
            int allSeasonsMarginWinsRateScore = calculateAllSeasonsRateScore(statsByTeam);
            int last3SeasonsTotalWinsRateScore = calculateLast3SeasonsTotalWinsRateScore(statsByTeam);
            int allSeasonsTotalWinsRateScore = calculateAllSeasonsTotalWinsRateScore(statsByTeam);
            int last3SeasonsmaxSeqWOMarginWinsScore = calculateLast3SeasonsMaxSeqWOGreenScore(statsByTeam);
            int allSeasonsmaxSeqWOMarginWinsScore = calculateAllSeasonsMaxSeqWOGreenScore(statsByTeam);
            int last3SeasonsStdDevScore = calculateLast3SeasonsStdDevScore(statsByTeam);
            int allSeasonsStdDevScore = calculateAllSeasonsStdDevScore(statsByTeam);
            int totalMatchesScore = calculateLeagueMatchesScore(statsByTeam.get(0).getNumMatches());

            double last3SeasonsWinsAvg = (last3SeasonsTotalWinsRateScore + last3SeasonsMarginWinsRateScore) / 2;
            double allSeasonsWinsAvg = (allSeasonsTotalWinsRateScore + allSeasonsMarginWinsRateScore) / 2;

            double last3SeasonsScore = Utils.beautifyDoubleValue(0.3*last3SeasonsWinsAvg + 0.4*last3SeasonsmaxSeqWOMarginWinsScore + 0.3*last3SeasonsStdDevScore);
            double allSeasonsScore = Utils.beautifyDoubleValue(0.3*allSeasonsWinsAvg + 0.4*allSeasonsmaxSeqWOMarginWinsScore + 0.3*allSeasonsStdDevScore);

            double totalScore = Utils.beautifyDoubleValue(0.75*last3SeasonsScore + 0.20*allSeasonsScore + 0.05*totalMatchesScore);

            teamByName.setHandball16MarginWinsScore(calculateFinalRating(totalScore));
        }

        return teamByName;
    }

    @Override
    public String calculateScoreBySeason(Team team, String season, String strategy) {
        return null;
    }

    public String calculateFinalRating(double score) {
        return super.calculateFinalRating(score);
    }

    @Override
    public int calculateLast3SeasonsRateScore(List<Handball16WinsMarginSeasonStats> statsByTeam) {
        double marginWinsRates = 0;
        for (int i=0; i<3; i++) {
            marginWinsRates += statsByTeam.get(i).getMarginWinsRate();
        }

        double avgMarginWinsRate = Utils.beautifyDoubleValue(marginWinsRates / 3);

        if (isBetween(avgMarginWinsRate,80,100)) {
            return 100;
        } else if(isBetween(avgMarginWinsRate,70,80)) {
            return 80;
        } else if(isBetween(avgMarginWinsRate,50,70)) {
            return 60;
        } else if(isBetween(avgMarginWinsRate,0,50)) {
            return 30;
        }
        return 0;
    }

    @Override
    public int calculateAllSeasonsRateScore(List<Handball16WinsMarginSeasonStats> statsByTeam) {
        double marginWinsRates = 0;
        for (int i=0; i<statsByTeam.size(); i++) {
            marginWinsRates += statsByTeam.get(i).getMarginWinsRate();
        }

        double avgMarginWinsRate = Utils.beautifyDoubleValue(marginWinsRates / statsByTeam.size());

        if (isBetween(avgMarginWinsRate,80,100)) {
            return 100;
        } else if(isBetween(avgMarginWinsRate,70,80)) {
            return 80;
        } else if(isBetween(avgMarginWinsRate,50,70)) {
            return 60;
        } else if(isBetween(avgMarginWinsRate,0,50)) {
            return 30;
        }
        return 0;
    }

    @Override
    public int calculateLast3SeasonsTotalWinsRateScore(List<Handball16WinsMarginSeasonStats> statsByTeam) {
        double totalWinsRates = 0;
        for (int i=0; i<3; i++) {
            totalWinsRates += statsByTeam.get(i).getWinsRate();
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
    public int calculateAllSeasonsTotalWinsRateScore(List<Handball16WinsMarginSeasonStats> statsByTeam) {
        double totalWinsRates = 0;
        for (int i=0; i<statsByTeam.size(); i++) {
            totalWinsRates += statsByTeam.get(i).getWinsRate();
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

    @Override
    public int calculateLast3SeasonsMaxSeqWOGreenScore(List<Handball16WinsMarginSeasonStats> statsByTeam) {
        int maxValue = 0;
        for (int i=0; i<3; i++) {
            String sequenceStr = statsByTeam.get(i).getNegativeSequence().replaceAll("[\\[\\]\\s]", "");
            List<Integer> sequenceList = Arrays.asList(sequenceStr.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList());
            if (Collections.max(sequenceList) > maxValue) {
                maxValue = Collections.max(sequenceList);
            }
        }

        if (isBetween(maxValue,0,7)) {
            return 100;
        } else if(isBetween(maxValue,7,8)) {
            return 90;
        } else if(isBetween(maxValue,8,9)) {
            return 70;
        } else if(isBetween(maxValue,9,10)) {
            return 50;
        } else if(isBetween(maxValue,10,25)) {
            return 30;
        }
        return 0;
    }

    @Override
    public int calculateAllSeasonsMaxSeqWOGreenScore(List<Handball16WinsMarginSeasonStats> statsByTeam) {
        int maxValue = 0;
        for (int i=0; i<statsByTeam.size(); i++) {
            String sequenceStr = statsByTeam.get(i).getNegativeSequence().replaceAll("[\\[\\]\\s]", "");
            List<Integer> sequenceList = Arrays.asList(sequenceStr.split(",")).stream().map(Integer::parseInt).collect(Collectors.toList());
            if (Collections.max(sequenceList) > maxValue) {
                maxValue = Collections.max(sequenceList);
            }
        }

        if (isBetween(maxValue,0,7)) {
            return 100;
        } else if(isBetween(maxValue,7,8)) {
            return 90;
        } else if(isBetween(maxValue,8,9)) {
            return 70;
        } else if(isBetween(maxValue,9,10)) {
            return 50;
        } else if(isBetween(maxValue,10,25)) {
            return 30;
        }
        return 0;
    }

    @Override
    public int calculateLast3SeasonsStdDevScore(List<Handball16WinsMarginSeasonStats> statsByTeam) {
        double sumStdDev = 0;
        for (int i=0; i<3; i++) {
            sumStdDev += statsByTeam.get(i).getStdDeviation();
        }

        double avgStdDev = Utils.beautifyDoubleValue(sumStdDev/3);

        if (isBetween(avgStdDev,0,1.8)) {
            return 100;
        } else if(isBetween(avgStdDev,1.8,2.0)) {
            return 80;
        } else if(isBetween(avgStdDev,2.0,2.2)) {
            return 70;
        } else if(isBetween(avgStdDev,2.2,2.4)) {
            return 50;
        } else if(isBetween(avgStdDev,2.4,25)) {
            return 30;
        }
        return 0;
    }

    @Override
    public int calculateAllSeasonsStdDevScore(List<Handball16WinsMarginSeasonStats> statsByTeam) {
        double sumStdDev = 0;
        for (int i=0; i<statsByTeam.size(); i++) {
            sumStdDev += statsByTeam.get(i).getStdDeviation();
        }

        double avgStdDev = Utils.beautifyDoubleValue(sumStdDev/statsByTeam.size());

        if (isBetween(avgStdDev,0,1.8)) {
            return 100;
        } else if(isBetween(avgStdDev,1.8,2.0)) {
            return 80;
        } else if(isBetween(avgStdDev,2.0,2.2)) {
            return 70;
        } else if(isBetween(avgStdDev,2.2,2.4)) {
            return 50;
        } else if(isBetween(avgStdDev,2.4,25)) {
            return 30;
        }
        return 0;
    }

    static class SortStatsDataBySeason implements Comparator<Handball16WinsMarginSeasonStats> {

        @Override
        public int compare(Handball16WinsMarginSeasonStats a, Handball16WinsMarginSeasonStats b) {
            return Integer.valueOf(SEASONS_LIST.indexOf(a.getSeason()))
                    .compareTo(Integer.valueOf(SEASONS_LIST.indexOf(b.getSeason())));
        }
    }

}