package com.pphi.tower.service;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.pphi.tower.config.AppConfig;
import com.pphi.tower.model.ScaleSuffix;
import com.pphi.tower.model.TowerNumber;
import com.pphi.tower.model.sheets.Currencies;
import com.pphi.tower.model.sheets.TowerTrackerRanges;
import com.pphi.tower.model.sheets.modules.EquippedModule;
import com.pphi.tower.model.sheets.modules.Module;
import com.pphi.tower.model.sheets.modules.Preset;
import com.pphi.tower.model.sheets.uw.*;
import com.pphi.tower.repository.GoogleSheetsRepository;
import com.pphi.tower.repository.ModulePresetRepository;
import com.pphi.tower.util.CurrenciesLayout;
import com.pphi.tower.util.ModuleUtils;
import com.pphi.tower.util.ValueRangeConcatenation;
import com.pphi.tower.util.ValueRangeUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TowerTrackerFetcherService {

    private final AppConfig appConfig;
    private final GoogleSheetsRepository googleSheetsRepository;
    private final ModulePresetRepository modulePresetRepository;

    public TowerTrackerFetcherService(
            AppConfig appConfig,
            GoogleSheetsRepository googleSheetsRepository,
            ModulePresetRepository modulePresetRepository) {
        this.appConfig = appConfig;
        this.googleSheetsRepository = googleSheetsRepository;
        this.modulePresetRepository = modulePresetRepository;
    }

    // -------------------------------------------------------------------------
    // Currencies
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Labs
    // -------------------------------------------------------------------------

    public String fetchLabs() throws IOException {
        StringBuilder sb = new StringBuilder();
        appendSection(sb, "Main Labs",             TowerTrackerRanges.MAIN_LABS);
        appendSection(sb, "Attack Labs",           TowerTrackerRanges.ATTACK_LABS);
        appendSection(sb, "Defense Labs",          TowerTrackerRanges.DEFENSE_LABS);
        appendSection(sb, "Utility Labs",          TowerTrackerRanges.UTILITY_LABS);
        appendSection(sb, "Ultimate Weapon Labs",  TowerTrackerRanges.ULTIMATE_WEAPON_LABS);
        appendSection(sb, "Card Labs",             TowerTrackerRanges.CARD_LABS);
        appendSection(sb, "Perk Labs",             TowerTrackerRanges.PERK_LABS);
        appendSection(sb, "Bot Labs",              TowerTrackerRanges.BOT_LABS);
        appendSection(sb, "Enemy Labs",            TowerTrackerRanges.ENEMY_LABS);
        appendSection(sb, "Module Labs",           TowerTrackerRanges.MODULE_LABS);
        appendSection(sb, "Battle Condition Labs", TowerTrackerRanges.BATTLE_CONDITION_LABS);
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Ultimate Weapons
    // -------------------------------------------------------------------------

    public List<UltimateWeapon> fetchUltimateWeapons() throws IOException {
        List<UltimateWeapon> weapons = new ArrayList<>();
        weapons.add(new ChainLightning(googleSheetsRepository.readRanges(TowerTrackerRanges.CHAIN_LIGHTNING)));
        weapons.add(new SmartMissiles(googleSheetsRepository.readRanges(TowerTrackerRanges.SMART_MISSILES)));
        weapons.add(new DeathWave(googleSheetsRepository.readRanges(TowerTrackerRanges.DEATH_WAVE)));
        weapons.add(new ChronoField(googleSheetsRepository.readRanges(TowerTrackerRanges.CHRONO_FIELD)));
        weapons.add(new InnerLandMines(googleSheetsRepository.readRanges(TowerTrackerRanges.INNER_LAND_MINES)));
        weapons.add(new GoldenTower(googleSheetsRepository.readRanges(TowerTrackerRanges.GOLDEN_TOWER)));
        weapons.add(new PoisonSwamp(googleSheetsRepository.readRanges(TowerTrackerRanges.POISON_SWAMP)));
        weapons.add(new BlackHole(googleSheetsRepository.readRanges(TowerTrackerRanges.BLACK_HOLE)));
        weapons.add(new Spotlight(googleSheetsRepository.readRanges(TowerTrackerRanges.SPOTLIGHT)));
        return weapons;
    }

    // -------------------------------------------------------------------------
    // Modules
    // -------------------------------------------------------------------------

    public Map<String, EquippedModule> fetchModulePreset(Preset preset) {
        return modulePresetRepository.getActiveModules(preset);
    }

    public String fetchModuleInventory() throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Module> entry : ModuleUtils.getAllModules().entrySet()) {
            String displayName = entry.getKey();
            Module module      = entry.getValue();
            sb.append(String.format("### %s [TYPE: %s]%n%n", displayName, module.type()));
            sb.append(ValueRangeConcatenation.toMarkdownTable(googleSheetsRepository.readRanges(module)));
            sb.append("\n");
        }
        return sb.toString();
    }

    public String fetchModuleSubStats() throws IOException {
        StringBuilder sb = new StringBuilder();
        appendSection(sb, "Cannon Sub-Stats",    TowerTrackerRanges.CANNON_SUB_STATS);
        appendSection(sb, "Armor Sub-Stats",     TowerTrackerRanges.ARMOR_SUB_STATS);
        appendSection(sb, "Generator Sub-Stats", TowerTrackerRanges.GENERATOR_SUB_STATS);
        appendSection(sb, "Core Sub-Stats",      TowerTrackerRanges.CORE_SUB_STATS);
        appendSection(sb, "Rarity Chance",       TowerTrackerRanges.RARITY_CHANCE);
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Workshop
    // -------------------------------------------------------------------------

    public String fetchWorkshop() throws IOException {
        StringBuilder sb = new StringBuilder();
        appendSection(sb, "Workshop",     TowerTrackerRanges.WORKSHOP);
        appendSection(sb, "Enhancements", TowerTrackerRanges.ENHANCEMENTS);
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Guardians
    // -------------------------------------------------------------------------

    public String fetchGuardians() throws IOException {
        return ValueRangeConcatenation.toMarkdownTable(googleSheetsRepository.readRanges(TowerTrackerRanges.GUARDIANS));
    }

    // -------------------------------------------------------------------------
    // Bots
    // -------------------------------------------------------------------------

    public String fetchBots() throws IOException {
        return ValueRangeConcatenation.toMarkdownTable(googleSheetsRepository.readRanges(TowerTrackerRanges.BOTS));
    }

    // -------------------------------------------------------------------------
    // Tier / Wave
    // -------------------------------------------------------------------------

    public String fetchTierWave() throws IOException {
        return ValueRangeConcatenation.toMarkdownTable(googleSheetsRepository.readRanges(TowerTrackerRanges.TIER_WAVE));
    }

    // -------------------------------------------------------------------------
    // Version History
    // -------------------------------------------------------------------------

    public String fetchVersionHistory() throws IOException {
        return ValueRangeConcatenation.toMarkdownTable(googleSheetsRepository.readRanges(TowerTrackerRanges.VERSION_HISTORY));
    }

    // -------------------------------------------------------------------------
    // Relics
    // -------------------------------------------------------------------------

    public String fetchRelics() throws IOException {
        return ValueRangeConcatenation.toMarkdownTable(googleSheetsRepository.readRanges(TowerTrackerRanges.RELICS));
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private void appendSection(StringBuilder sb, String label, TowerTrackerRanges range) throws IOException {
        sb.append("### ").append(label).append("\n\n");
        sb.append(ValueRangeConcatenation.toMarkdownTable(googleSheetsRepository.readRanges(range)));
        sb.append("\n");
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
