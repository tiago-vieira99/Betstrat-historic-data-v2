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
@Table(name = "ConcedeBothHalvesSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamcbh", columnNames = { "teamID", "season", "competition" }) })
public class ConcedeBothHalvesSeasonStats extends StrategySeasonStats {

    @Column(name = "concede_both_halves_rate")
    private double concedeBothHalvesRate;

    @Column(name = "num_concede_both_halves")
    private int numConcedeBothHalves;

    public ConcedeBothHalvesSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 2.3 - 4
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM.getValue());
    }

}
