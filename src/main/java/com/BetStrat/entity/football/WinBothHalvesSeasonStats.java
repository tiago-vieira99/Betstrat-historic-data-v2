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
@Table(name = "WinBothHalvesSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamwbh", columnNames = { "teamID", "season", "competition" }) })
public class WinBothHalvesSeasonStats extends StrategySeasonStats {

    @Column(name = "win_both_halves_rate")
    private double winBothHalvesRate;

    @Column(name = "num_wins_both_halves")
    private int numWinsBothHalves;

    @Column(name = "winsRate")
    private double winsRate;

    @Column(name = "num_wins")
    private int numWins;

    public WinBothHalvesSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 2.3 - 5
        super.setMaxSeqScale(StrategyDurationScaleEnum.LONG.getValue());
    }

}
