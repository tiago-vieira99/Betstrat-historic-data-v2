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
@Table(name = "SecondHalfBiggerSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteam2hb", columnNames = { "teamID", "season", "competition" }) })
public class SecondHalfBiggerSeasonStats extends StrategySeasonStats {

    @Column(name = "second_half_bigger_rate")
    private double secondHalfBiggerRate;

    @Column(name = "num_second_half_bigger")
    private int numScondHalfBigger;

    public SecondHalfBiggerSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 1.9 - 2.2
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM_SHORT.getValue());
    }

}
