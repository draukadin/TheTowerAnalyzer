package com.pphi.tower.service;

import com.google.api.services.sheets.v4.model.ValueRange;
import com.pphi.tower.model.ScaleSuffix;
import com.pphi.tower.model.TowerNumber;
import com.pphi.tower.model.sheets.Currencies;
import com.pphi.tower.model.sheets.TowerTrackerRanges;
import com.pphi.tower.model.sheets.cards.CardPresetType;
import com.pphi.tower.model.sheets.modules.EquippedModule;
import com.pphi.tower.model.sheets.modules.Module;
import com.pphi.tower.model.sheets.modules.Preset;
import com.pphi.tower.repository.GoogleSheetsRepository;
import com.pphi.tower.repository.ModulePresetRepository;
import com.pphi.tower.repository.UwRepository;
import com.pphi.tower.util.CurrenciesLayout;
import com.pphi.tower.util.ModuleUtils;
import com.pphi.tower.util.ValueRangeConcatenation;
import com.pphi.tower.util.ValueRangeUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class TowerTrackerFetcherService {

    private final GoogleSheetsRepository googleSheetsRepository;
    private final ModulePresetRepository modulePresetRepository;
    private final UwRepository uwRepository;

    public TowerTrackerFetcherService(
            GoogleSheetsRepository googleSheetsRepository,
            ModulePresetRepository modulePresetRepository,
            UwRepository uwRepository) {
        this.googleSheetsRepository = googleSheetsRepository;
        this.modulePresetRepository = modulePresetRepository;
        this.uwRepository = uwRepository;
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

    public String fetchLabTierList() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                Ranking hierarchy (best to worst): S+ > S > A > B > C > D > F
                QOL = Quality of Life
                Some labs have two rankings (e.g. "F / A") where the first applies without a prerequisite and the second applies once that prerequisite is met (e.g. Spotlight+).
                Columns: Tier Available | Lab Name | Ranking 1 | Ranking 2 | Notes

                """);
        sb.append(ValueRangeConcatenation.toMarkdownTable(googleSheetsRepository.readRanges(TowerTrackerRanges.LAB_TIER_LIST)));
        return sb.toString();
    }

    public String fetchLabPlanning() throws IOException {
        StringBuilder sb = new StringBuilder();
        appendSection(sb, "Lab Slot 1 Planning",             TowerTrackerRanges.LAB_SLOT_ONE_PLANNING);
        appendSection(sb, "Lab Slot 2 Planning",             TowerTrackerRanges.LAB_SLOT_TWO_PLANNING);
        appendSection(sb, "Lab Slot 3 Planning",             TowerTrackerRanges.LAB_SLOT_THREE_PLANNING);
        appendSection(sb, "Lab Slot 4 Planning",             TowerTrackerRanges.LAB_SLOT_FOUR_PLANNING);
        appendSection(sb, "Lab Slot 5 Planning",             TowerTrackerRanges.LAB_SLOT_FIVE_PLANNING);
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Ultimate Weapons
    // -------------------------------------------------------------------------

    public List<UwRepository.UwPlayerData> fetchUltimateWeapons() {
        return uwRepository.getAllUwState();
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
    // Cards
    // -------------------------------------------------------------------------

    public String fetchCards() throws IOException {
        return ValueRangeConcatenation.toMarkdownTable(googleSheetsRepository.readRanges(TowerTrackerRanges.CARDS));
    }

    public List<String> fetchCardPreset(CardPresetType type) throws IOException {
        TowerTrackerRanges range = type == CardPresetType.FARMING
                ? TowerTrackerRanges.CARD_PRESET_FARMING
                : TowerTrackerRanges.CARD_PRESET_TOURNAMENT;
        var valueRanges = googleSheetsRepository.readRanges(range);
        if (valueRanges.isEmpty()) return List.of();
        var rows = ValueRangeUtils.getRows(valueRanges.get(0));
        return rows.stream()
                .map(row -> row.isEmpty() ? "" : Objects.toString(row.get(0), ""))
                .toList();
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
