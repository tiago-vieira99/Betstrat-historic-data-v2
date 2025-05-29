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
@Table(name = "CleanSheetSeasonStats", uniqueConstraints = { @UniqueConstraint(name = "uniqueseasonandcompetitionforteamcleansheet", columnNames = { "teamID", "season", "competition" }) })
public class CleanSheetSeasonStats extends StrategySeasonStats {

    @Column(name = "clean_sheet_rate")
    private double cleanSheetRate;

    @Column(name = "num_clean_sheets")
    private int numCleanSheets;

    public CleanSheetSeasonStats() {
        maxSeqScale();
    }

    @Override
    public void maxSeqScale() {
        // avg odds : 2.4 - 3.2
        super.setMaxSeqScale(StrategyDurationScaleEnum.MEDIUM.getValue());
    }
}
