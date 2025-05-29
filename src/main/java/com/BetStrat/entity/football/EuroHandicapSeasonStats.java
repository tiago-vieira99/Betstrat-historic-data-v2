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
@Table(name = "EuroHandicapSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteameh", columnNames = { "teamID", "season", "competition" }) })
public class EuroHandicapSeasonStats extends StrategySeasonStats {
    private static final long serialVersionUID = 1L;

    @Column(name = "winsRate")
    private double winsRate;

    @Column(name = "marginWinsRate")
    private double marginWinsRate;

    @Column(name = "num_wins")
    private int numWins;

    @Column(name = "num_marginWins")
    private int numMarginWins;

    public EuroHandicapSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds :  3.5 - 5
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM_LONG.getValue());
    }

}
