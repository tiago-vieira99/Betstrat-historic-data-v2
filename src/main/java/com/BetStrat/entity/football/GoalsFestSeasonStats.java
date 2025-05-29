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
@Table(name = "GoalsFestSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamgf", columnNames = { "teamID", "season", "competition" }) })
public class GoalsFestSeasonStats extends StrategySeasonStats {

    @Column(name = "goalsFestRate")
    private double goalsFestRate;

    @Column(name = "num_goalsFest")
    private int numGoalsFest;

    public GoalsFestSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 1.7 - 2.4
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM_SHORT.getValue());
    }

}
