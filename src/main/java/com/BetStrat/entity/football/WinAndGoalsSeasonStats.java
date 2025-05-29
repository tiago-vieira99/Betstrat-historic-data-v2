package com.BetStrat.entity.football;

import com.BetStrat.entity.StrategySeasonStats;
import com.BetStrat.enums.StrategyDurationScaleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "WinAndGoalsSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamwandg", columnNames = { "teamID", "season", "competition" }) })
public class WinAndGoalsSeasonStats extends StrategySeasonStats {

    @Column(name = "win_and_goals_rate")
    private double winAndGoalsRate;

    @Column(name = "winsRate")
    private double winsRate;

    @Column(name = "num_wins")
    private int numWins;

    @Column(name = "num_wins_and_goals")
    private int numWinsAndGoals;

    public WinAndGoalsSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 2.1 - 3.2
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM.getValue());
    }

}
