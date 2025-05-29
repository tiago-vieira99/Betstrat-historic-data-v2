package com.BetStrat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Report",  uniqueConstraints = { @UniqueConstraint(name = "uniquereport", columnNames = { "strategy", "season" }) })
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Report implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

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

    @Column(name = "season")
    private String season;

    @Column(name = "strategy")
    private String strategy;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "report_map", columnDefinition = "jsonb")
    private Map<String, Object> reportMap = new HashMap<>();

}
