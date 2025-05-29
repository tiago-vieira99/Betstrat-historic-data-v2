package com.BetStrat.entity.basketball;

import com.BetStrat.entity.StrategySeasonStats;
import com.BetStrat.enums.StrategyDurationScaleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ShortBasketWinsSeasonStats", uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamshortbasketwins", columnNames = { "teamID", "season", "competition" }) })
public class ShortBasketWinsSeasonStats extends StrategySeasonStats {

    @Column(name = "shortWinsRate")
    private double shortWinsRate;

    @Column(name = "winsRate")
    private double winsRate;

    @Column(name = "num_shortWins")
    private int numShortWins;

    @Column(name = "num_wins")
    private int numWins;

    @Override
    public void maxSeqScale() {
        // avg odds : 3 - 3.3 TODO
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM_LONG.getValue());
    }

}
