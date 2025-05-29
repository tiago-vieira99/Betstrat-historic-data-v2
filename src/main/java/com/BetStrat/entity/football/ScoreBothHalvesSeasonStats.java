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
@Table(name = "ScoreBothHalvesSeasonStats",  uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamsbh", columnNames = { "teamID", "season", "competition" }) })
public class ScoreBothHalvesSeasonStats extends StrategySeasonStats {

    @Column(name = "score_both_halves_rate")
    private double scoreBothHalvesRate;

    @Column(name = "num_score_both_halves")
    private int numScoreBothHalves;

    public ScoreBothHalvesSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 1.8 - 3.1
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM.getValue());
    }

}
