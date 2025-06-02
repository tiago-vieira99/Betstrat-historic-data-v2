package com.BetStrat.service;

import com.BetStrat.entity.StrategySeasonStats;
import com.BetStrat.entity.basketball.ComebackSeasonStats;
import com.BetStrat.entity.basketball.LongBasketWinsSeasonStats;
import com.BetStrat.entity.basketball.ShortBasketWinsSeasonStats;
import com.BetStrat.entity.football.BttsOneHalfSeasonStats;
import com.BetStrat.entity.football.BttsSeasonStats;
import com.BetStrat.entity.football.CleanSheetSeasonStats;
import com.BetStrat.entity.football.ConcedeBothHalvesSeasonStats;
import com.BetStrat.entity.football.DrawSeasonStats;
import com.BetStrat.entity.football.EuroHandicapSeasonStats;
import com.BetStrat.entity.football.FirstHalfBiggerSeasonStats;
import com.BetStrat.entity.football.FlipFlopOversUndersStats;
import com.BetStrat.entity.football.GoalsFestSeasonStats;
import com.BetStrat.entity.football.NoBttsSeasonStats;
import com.BetStrat.entity.football.NoGoalsFestSeasonStats;
import com.BetStrat.entity.football.NoWinFirstHalfSeasonStats;
import com.BetStrat.entity.football.NoWinsSeasonStats;
import com.BetStrat.entity.football.ScoreBothHalvesSeasonStats;
import com.BetStrat.entity.football.SecondHalfBiggerSeasonStats;
import com.BetStrat.entity.football.WinAndGoalsSeasonStats;
import com.BetStrat.entity.football.WinBothHalvesSeasonStats;
import com.BetStrat.entity.football.WinFirstHalfSeasonStats;
import com.BetStrat.entity.football.WinsMarginHomeSeasonStats;
import com.BetStrat.entity.football.WinsMarginSeasonStats;
import com.BetStrat.entity.football.WinsSeasonStats;
import com.BetStrat.entity.handball.Handball16WinsMarginSeasonStats;
import com.BetStrat.entity.handball.Handball49WinsMarginSeasonStats;
import com.BetStrat.entity.handball.Handball712WinsMarginSeasonStats;
import com.BetStrat.entity.hockey.HockeyDrawSeasonStats;
import com.BetStrat.entity.hockey.WinsMargin3SeasonStats;
import com.BetStrat.entity.hockey.WinsMarginAny2SeasonStats;
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
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class StrategyMappingPattern {

    // Create a map to store service implementations for each type
    public final Map<Class<? extends StrategySeasonStats>, StrategySeasonStatsInterface<?>> serviceMap;

    // Strategy Pattern
    // Inject the required service implementations into the constructor
    public StrategyMappingPattern(ComebackStrategySeasonStatsService comebackStrategySeasonstatsService,
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
        // Initialize the map
        serviceMap = new HashMap<>();
        // Associate each service with its corresponding type
        // Basket Strategies
        serviceMap.put(ComebackSeasonStats.class, comebackStrategySeasonstatsService);
        serviceMap.put(LongBasketWinsSeasonStats.class, longBasketWinsStrategySeasonstatsService);
        serviceMap.put(ShortBasketWinsSeasonStats.class, shortBasketWinsStrategySeasonstatsService);
        // Football Strategies
        serviceMap.put(DrawSeasonStats.class, drawStrategySeasonstatsService);
        serviceMap.put(EuroHandicapSeasonStats.class, euroHandicapStrategySeasonstatsService);
        serviceMap.put(FlipFlopOversUndersStats.class, flipFlopOversUndersInfoService);
        serviceMap.put(GoalsFestSeasonStats.class, goalsFestStrategySeasonstatsService);
        serviceMap.put(WinsMarginSeasonStats.class, winsMarginStrategySeasonstatsService);
        serviceMap.put(WinsSeasonStats.class, winsStrategySeasonStatsService);
        serviceMap.put(NoWinsSeasonStats.class, noWinsStrategySeasonStatsService);
        serviceMap.put(CleanSheetSeasonStats.class, cleanSheetStrategySeasonStatsService);
        serviceMap.put(NoGoalsFestSeasonStats.class, noGoalsFestStrategySeasonstatsService);
        serviceMap.put(BttsSeasonStats.class, bttsStrategySeasonStatsService);
        serviceMap.put(BttsOneHalfSeasonStats.class, bttsOneHalfStrategySeasonStatsService);
        serviceMap.put(NoBttsSeasonStats.class, noBttsStrategySeasonStatsService);
        serviceMap.put(ScoreBothHalvesSeasonStats.class, scoreBothHalvesSeasonStatsService);
        serviceMap.put(ConcedeBothHalvesSeasonStats.class, concedeBothHalvesSeasonStatsService);
        serviceMap.put(WinBothHalvesSeasonStats.class, winBothHalvesStrategySeasonStatsService);
        serviceMap.put(WinFirstHalfSeasonStats.class, winFirstHalfStrategySeasonStatsService);
        serviceMap.put(NoWinFirstHalfSeasonStats.class, noWinFirstHalfStrategySeasonStatsService);
        serviceMap.put(WinAndGoalsSeasonStats.class, winAndGoalsStrategySeasonStatsService);
        serviceMap.put(SecondHalfBiggerSeasonStats.class, secondHalfBiggerSeasonStatsService);
        serviceMap.put(FirstHalfBiggerSeasonStats.class, firstHalfBiggerSeasonStatsService);
        serviceMap.put(WinsMarginHomeSeasonStats.class, winsMarginHomeStrategySeasonStatsService);
        // Handball Strategies
        serviceMap.put(Handball16WinsMarginSeasonStats.class, handballWinsMargin16StrategySeasonstatsService);
        serviceMap.put(Handball49WinsMarginSeasonStats.class, handballWinsMargin49StrategySeasonstatsService);
        serviceMap.put(Handball712WinsMarginSeasonStats.class, handballWinsMargin712StrategySeasonstatsService);
        // Hockey Strategies
        serviceMap.put(HockeyDrawSeasonStats.class, hockeyDrawStrategySeasonstatsService);
        serviceMap.put(WinsMargin3SeasonStats.class, winsMargin3StrategySeasonstatsService);
        serviceMap.put(WinsMarginAny2SeasonStats.class, winsMarginAny2StrategySeasonstatsService);
    }

    // For the map field, you can have a getter:
    public Map<Class<? extends StrategySeasonStats>, StrategySeasonStatsInterface<?>> getServiceMap() {
        return serviceMap;
    }


}
