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
@Table(name = "NoWinFirstHalfSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamnowfh", columnNames = { "teamID", "season", "competition" }) })
public class NoWinFirstHalfSeasonStats extends StrategySeasonStats {

    @Column(name = "no_win_first_half_rate")
    private double noWinFirstHalfRate;

    @Column(name = "num_no_wins_first_half")
    private int numNoWinsFirstHalf;

    public NoWinFirstHalfSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM_SHORT.getValue());
    }

}
