package com.BetStrat.entity.hockey;

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
@Table(name = "HockeyDrawSeasonStats", uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamhd", columnNames = { "teamID", "season", "competition" }) })
public class HockeyDrawSeasonStats extends StrategySeasonStats {

    @Column(name = "drawRate")
    private double drawRate;

    @Column(name = "num_draws")
    private int numDraws;

    @Override
    public void maxSeqScale() {
        // avg odds : 3 - 3.3 TODO
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM_LONG.getValue());
    }

}
