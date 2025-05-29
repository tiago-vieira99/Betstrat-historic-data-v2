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
@Table(name = "NoGoalsFestSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamnogf", columnNames = { "teamID", "season", "competition" }) })
public class NoGoalsFestSeasonStats extends StrategySeasonStats {

    @Column(name = "no_goals_fest_rate")
    private double noGoalsFestRate;

    @Column(name = "num_no_goals_fest")
    private int numNoGoalsFest;

    public NoGoalsFestSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 1.6 - 2.2
        super.setMaxSeqScale(StrategyDurationScaleEnum.SHORT.getValue());
    }

}
