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
@Table(name = "FirstHalfBiggerSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteam1hb", columnNames = { "teamID", "season", "competition" }) })
public class FirstHalfBiggerSeasonStats extends StrategySeasonStats {

    @Column(name = "first_half_bigger_rate")
    private double firstHalfBiggerRate;

    @Column(name = "num_first_half_bigger")
    private int numFirstHalfBigger;

    public FirstHalfBiggerSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 2.9 - 3.2
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM.getValue());
    }

}
