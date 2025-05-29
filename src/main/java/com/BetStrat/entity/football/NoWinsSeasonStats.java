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
@Table(name = "NoWinsSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamnow", columnNames = { "teamID", "season", "competition" }) })
public class NoWinsSeasonStats extends StrategySeasonStats {

    @Column(name = "no_wins_rate")
    private double noWinsRate;

    @Column(name = "num_no_wins")
    private int numNoWins;

    public NoWinsSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 1.6 - 2.7
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM_SHORT.getValue());
    }

}
