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
@Table(name = "WinsSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamw", columnNames = { "teamID", "season", "competition" }) })
public class WinsSeasonStats extends StrategySeasonStats {

    @Column(name = "winsRate")
    private double winsRate;

    @Column(name = "num_wins")
    private int numWins;

    public WinsSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 1.4 - 2.2
        super.setMaxSeqScale(StrategyDurationScaleEnum.SHORT.getValue());
    }

}
