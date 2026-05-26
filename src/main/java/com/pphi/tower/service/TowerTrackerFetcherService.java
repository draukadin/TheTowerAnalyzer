package com.pphi.tower.service;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.pphi.tower.config.AppConfig;
import com.pphi.tower.model.ScaleSuffix;
import com.pphi.tower.model.TowerNumber;
import com.pphi.tower.model.sheets.Currencies;
import com.pphi.tower.model.sheets.TowerTrackerRanges;
import com.pphi.tower.repository.GoogleSheetsRepository;
import com.pphi.tower.util.CurrenciesLayout;
import com.pphi.tower.util.ValueRangeUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

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

    public Currencies fetchCurrencies() throws IOException {
        List<ValueRange> ranges = googleSheetsRepository.readRanges(TowerTrackerRanges.CURRENCIES);
        return new Currencies(
                parseTowerNumber(ValueRangeUtils.getValue(ranges, CurrenciesLayout.COINS_ROW,            CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.GEMS_ROW,             CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.STONES_ROW,           CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.MEDALS_ROW,           CurrenciesLayout.AMOUNT_COL)),
                parseTowerNumber(ValueRangeUtils.getValue(ranges, CurrenciesLayout.ELITE_CELLS_ROW,      CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.KEYS_ROW,             CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.TOKENS_ROW,           CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.BITS_ROW,             CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.TOURNAMENT_TIX_ROW,   CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.MODULE_TIX_ROW,       CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.CANNON_SHARDS_ROW,    CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.ARMOR_SHARDS_ROW,     CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.GENERATOR_SHARDS_ROW, CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.CORE_SHARDS_ROW,      CurrenciesLayout.AMOUNT_COL)),
                parseInt(ValueRangeUtils.getValue(ranges,         CurrenciesLayout.REROLL_SHARDS_ROW,    CurrenciesLayout.AMOUNT_COL))
        );
    }

    private TowerNumber parseTowerNumber(String value) {
        if (value.isBlank() || value.equals("0")) return new TowerNumber(BigDecimal.ZERO, null);
        char lastChar = value.charAt(value.length() - 1);
        ScaleSuffix suffix = ScaleSuffix.fromSuffix(String.valueOf(lastChar));
        String numPart = suffix != null ? value.substring(0, value.length() - 1) : value;
        return new TowerNumber(new BigDecimal(numPart.replace(",", "")), suffix);
    }

    private int parseInt(String value) {
        if (value.isBlank()) return 0;
        return Integer.parseInt(value.replace(",", ""));
    }
}
