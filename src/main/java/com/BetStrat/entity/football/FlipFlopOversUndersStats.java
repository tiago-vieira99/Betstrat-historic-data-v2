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
@Table(name = "FlipFlopOversUndersStats", uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamff", columnNames = { "teamID", "season", "competition" }) })
public class FlipFlopOversUndersStats extends StrategySeasonStats {

    @Column(name = "oversRate")
    private double oversRate;

    @Column(name = "undersRate")
    private double undersRate;

    @Column(name = "num_overs")
    private int numOvers;

    @Column(name = "num_unders")
    private int numUnders;

    public FlipFlopOversUndersStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 1.7 - 2
        super.setMaxSeqScale(StrategyDurationScaleEnum.SHORT.getValue());
    }

}
