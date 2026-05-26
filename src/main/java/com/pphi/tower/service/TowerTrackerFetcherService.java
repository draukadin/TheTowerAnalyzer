package com.pphi.tower.service;

import com.pphi.tower.config.AppConfig;
import com.pphi.tower.repository.GoogleSheetsRepository;
import org.springframework.stereotype.Service;

@Service
public class TowerTrackerFetcherService {

    private final AppConfig appConfig;
    private final GoogleSheetsRepository googleSheetsRepository;

    public TowerTrackerFetcherService(
            AppConfig appConfig,
            GoogleSheetsRepository googleSheetsRepository) {
        this.appConfig = appConfig;
        this.googleSheetsRepository = googleSheetsRepository;
    }
}
