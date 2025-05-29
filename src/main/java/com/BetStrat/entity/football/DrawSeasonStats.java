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
@Table(name = "DrawSeasonStats", uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamdh", columnNames = { "teamID", "season", "competition" }) })
public class DrawSeasonStats extends StrategySeasonStats {

    @Column(name = "drawRate")
    private double drawRate;

    @Column(name = "num_draws")
    private int numDraws;

    public DrawSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 3 - 3.3
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM_LONG.getValue());
    }
}
