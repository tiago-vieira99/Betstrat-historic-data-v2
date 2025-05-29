package com.BetStrat.repository;

import com.BetStrat.entity.Report;
import io.quarkus.cache.CacheResult;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import java.util.List;

public class ReportRepository implements PanacheRepository<Report> {

    @CacheResult(cacheName = "reports")
    public List<Report> findAllReports() {
        return listAll(); // Replaces findAll()
    }

    @CacheResult(cacheName = "getReportsBySeasonAndStrategy")
    public List<Report> getReportsBySeasonAndStrategy(String season, String strategy) {
        return find("season = ?1 and strategy = ?2", season, strategy).list();
    }

}