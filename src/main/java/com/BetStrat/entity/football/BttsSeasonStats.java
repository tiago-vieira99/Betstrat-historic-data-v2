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
@Table(name = "BttsSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteambtts", columnNames = { "teamID", "season", "competition" }) })
public class BttsSeasonStats extends StrategySeasonStats {

    @Column(name = "btts_rate")
    private double bttsRate;

    @Column(name = "num_btts")
    private int numBtts;

    public BttsSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 1.5 - 2.2
        super.setMaxSeqScale(StrategyDurationScaleEnum.SHORT.getValue());
    }

}
