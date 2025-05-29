package com.BetStrat.entity;

import static com.BetStrat.constants.BetStratConstants.SEASONS_LIST;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Setter
@Getter
@MappedSuperclass
public abstract class StrategySeasonStats implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teamID", referencedColumnName = "ID")
    private Team team;

    @ApiModelProperty(example = "dd-MM-yyyy hh:mm:ss")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @CreationTimestamp
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @ApiModelProperty(example = "dd-MM-yyyy hh:mm:ss")
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "description")
    private String description;

    @Column(name = "season")
    private String season;

    @Column(name = "competition")
    private String competition;

    @Column(name = "url")
    private String url;

    @Column(name = "num_matches")
    private int numMatches;

    @Column(name = "standard_deviation")
    private double stdDeviation;

    @Column(name = "coefficient_deviation")
    private double coefDeviation;

    @Column(name = "negative_sequence")
    private String negativeSequence;

    @Column(name = "max_seq_scale")
    private int maxSeqScale;

    @SneakyThrows
    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    public abstract void maxSeqScale();

    public static Comparator<StrategySeasonStats> strategySeasonSorter = new Comparator<StrategySeasonStats>() {
        @Override
        public int compare(StrategySeasonStats a, StrategySeasonStats b) {
            return Integer.valueOf(SEASONS_LIST.indexOf(a.getSeason()))
                .compareTo(Integer.valueOf(SEASONS_LIST.indexOf(b.getSeason())));
        }
    };

}
