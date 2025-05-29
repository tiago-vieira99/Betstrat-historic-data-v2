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
@Table(name = "WinsMarginSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamfwm", columnNames = { "teamID", "season", "competition" }) })
public class WinsMarginSeasonStats extends StrategySeasonStats {

    @Column(name = "winsRate")
    private double winsRate;

    @Column(name = "marginWinsRate")
    private double marginWinsRate;

    @Column(name = "num_wins")
    private int numWins;

    @Column(name = "num_marginWins")
    private int numMarginWins;

    public WinsMarginSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 1.9 - 2.3
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM_SHORT.getValue());
    }

}
