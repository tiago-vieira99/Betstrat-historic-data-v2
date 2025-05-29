package com.BetStrat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import net.bytebuddy.asm.Advice.Local;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "HistoricMatches",  uniqueConstraints = { @UniqueConstraint(name = "uniquehistoricmatch", columnNames = { "teamId", "matchDate", "homeTeam", "awayTeam", "season", "competition", "ftResult" }) })
public class HistoricMatch implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "teamID", referencedColumnName = "ID")
    private Team teamId;

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

    @Column(name = "homeTeam")
    private String homeTeam;

    @Column(name = "awayTeam")
    private String awayTeam;

    @Column(name = "season")
    private String season;

    @Column(name = "matchDate")
    private String matchDate;

    @Column(name = "ftResult")
    private String ftResult;

    @Column(name = "htResult")
    private String htResult;

    @Column(name = "competition")
    private String competition;

    @Column(name = "sport")
    private String sport;

    public static Comparator<HistoricMatch> matchDateComparator = new Comparator<HistoricMatch>() {
        private SimpleDateFormat[] dateFormats = {
            new SimpleDateFormat("dd/MM/yyyy"),
            new SimpleDateFormat("yyyy-MM-dd")
        };

        @Override
        public int compare(HistoricMatch obj1, HistoricMatch obj2) {
            Date date1 = parseDate(obj1.getMatchDate());
            Date date2 = parseDate(obj2.getMatchDate());

            if (date1 != null && date2 != null) {
                return date1.compareTo(date2);
            }

            // Handle cases where parsing fails by treating them as greater
            return 1;
        }

        private Date parseDate(String dateString) {
            for (SimpleDateFormat dateFormat : dateFormats) {
                try {
                    return dateFormat.parse(dateString);
                } catch (ParseException e) {
                    // Parsing failed, try the next format
                }
            }
            return null; // Parsing failed for all formats
        }
    };

}
