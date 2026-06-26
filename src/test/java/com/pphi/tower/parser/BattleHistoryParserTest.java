package com.pphi.tower.parser;

import com.pphi.tower.exceptions.FieldToLineCountMismatchException;
import com.pphi.tower.model.ScaleSuffix;
import com.pphi.tower.model.TowerNumber;
import com.pphi.tower.model.battlehistory.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BattleHistoryParserTest {

    private BattleHistoryParser parser;

    @BeforeEach
    void setUp() {
        parser = new BattleHistoryParser();
    }

    // ── parseTowerNumber ─────────────────────────────────────────────────────

    @Test
    void parseTowerNumber_trillion() {
        TowerNumber n = parser.parseTowerNumber("Label\t5.0T");
        assertThat(n.scaleSuffix()).isEqualTo(ScaleSuffix.TRILLION);
        assertThat(n.amount().doubleValue()).isEqualTo(5.0);
    }

    @Test
    void parseTowerNumber_million() {
        TowerNumber n = parser.parseTowerNumber("Label\t2.5M");
        assertThat(n.scaleSuffix()).isEqualTo(ScaleSuffix.MILLION);
        assertThat(n.amount().doubleValue()).isEqualTo(2.5);
    }

    @Test
    void parseTowerNumber_thousand() {
        TowerNumber n = parser.parseTowerNumber("Label\t100K");
        assertThat(n.scaleSuffix()).isEqualTo(ScaleSuffix.THOUSAND);
        assertThat(n.amount().doubleValue()).isEqualTo(100.0);
    }

    @Test
    void parseTowerNumber_zero_noSuffix() {
        TowerNumber n = parser.parseTowerNumber("Label\t0");
        assertThat(n.amount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(n.scaleSuffix()).isNull();
    }

    @Test
    void parseTowerNumber_cashPrefix_stripped() {
        // Cash amounts have a "$" prefix in the raw value
        TowerNumber n = parser.parseTowerNumber("Label\t$3.0M");
        assertThat(n.scaleSuffix()).isEqualTo(ScaleSuffix.MILLION);
        assertThat(n.amount().doubleValue()).isEqualTo(3.0);
    }

    // ── parse(List<String>) — full 16-section fixture ────────────────────────

    private static final String FIXTURE = """
            Battle Report
            Tower Era	1.0.0
            Battle Report Date	Jan 1, 2024 00:00
            Game Time	1h 30m 45s
            Real Time	0d 2h 0m 0s
            Tier	10
            Wave	500
            Killed By	Fast
            Coins Earned	1.5T
            Coins Per Hour	2.0T
            Cells Earned	3.0M
            Cells Per Hour	4.0M
            Records
            Highest Coins/Min	1.0T
            Largest Wave Skip	0
            Most Coins From Wave Skip	0
            Most Cells From Wave Skip	0
            Largest SM Stack	0
            Largest Golden Combo	0
            Most Coins From Golden Combo	0
            Largest ILM Charge	0
            Damage
            Damage Dealt	1.0T
            Projectiles	1.0T
            Rend Armor	0
            Death Ray	0
            Thorns	1.0T
            Orbs	1.0T
            Land Mines	1.0T
            Chain Lightning	1.0T
            Smart Missiles	0
            Inner Land Mines	0
            Poison Swamp	0
            Death Wave	0
            Black Hole	0
            Flame Bot	0
            Attack Chip	0
            Electrons	0
            Damage Taken
            Tower	1.0T
            Wall	1.0T
            Bonus Health Gained
            From Death Wave	0
            Health Regenerated
            Life Steal	1.0T
            Tower Health Regen	1.0T
            Wall Health Regen	1.0T
            Damage Blocked
            Defense %	1.0T
            Defense Absolute	1.0T
            Chrono Field	0
            Chain Thunder	0
            Flame Bot	0
            Primordial Collapse	0
            Negative Mass Projector	0
            Utility
            Recovery Packages	0
            Free Attack Upgrades	0
            Free Defense Upgrades	0
            Free Utility Upgrades	0
            Enemy Attack Level Skipped	0
            Enemy Health Level Skipped	0
            Counts
            Projectile Count	1.0T
            Land Mines Spawned	100
            Thunder Bot Stuns	50
            Waves Skipped	0
            Death Defy	0
            Hits Absorbed By Energy Shield	0
            Nuke	0
            Second Wind	0
            Demon Mode	0
            Enemies Hit By
            Projectiles	1.0T
            Thorns	1.0T
            Orbs	1.0T
            Death Ray	0
            Chain Lightning	1.0T
            Smart Missiles	0
            Inner Land Mines	0
            Poison Swamp	0
            Death Wave	0
            Black Hole	0
            Chrono Field	0
            Land Mines	1.0T
            Thunder Bot	0
            Flame Bot	0
            Attack Chip	0
            Orbital Augment	0
            Killed With Effect Active
            Golden Tower	0
            Death Wave	0
            Spotlight	0
            Amplify Bot	0
            Golden Bot	0
            Death Penalty	0
            Total Enemies
            Total Enemies	50000
            Basic	30000
            Fast	5000
            Tank	2000
            Ranged	3000
            Boss	500
            Protector	200
            Vampires	100
            Rays	100
            Scatters	100
            Saboteur	0
            Commander	0
            Overcharge	0
            Summoned Enemies	0
            Coins
            Coins Earned	1.5T
            Coins Per Kill	1.0T
            Other Coin Bonuses	0
            Critical Coin	0
            Golden Tower	0
            Golden Combo	0
            Death Wave	0
            Spotlight	0
            Black Hole	0
            Orbs	0
            Golden Bot	0
            Wave Skip	0
            Coins Per Wave	0
            Coins Fetched	0
            Bounty Coins	0
            Cash
            Cash Earned	$3.0M
            Golden Tower	0
            Interest Earned	0
            Currencies
            Cells Earned	3.0M
            Gems	10
            Ad Gems	0
            Gem Block Tapped	0
            Fetch Gems	0
            Medals	5
            Re-Roll Shards Earned	0
            Re-Roll Shards Fetched	0
            Cannon Shards	0
            Armor Shards	0
            Generator Shards	0
            Core Shards	0
            Common Modules	0
            Rare Modules	0
            Enemies Destroyed By
            Projectiles	900
            Thorns	500
            Land Mines	300
            Orbs	5000
            Chain Lightning	3000
            Smart Missiles	0
            Inner Land Mines	100
            Poison Swamp	100
            Death Ray	0
            Black Hole	100
            Flame Bot	0
            Other	0
            """;

    private List<String> fixtureLines() {
        return Arrays.stream(FIXTURE.split("\n"))
                .filter(l -> !l.isBlank())
                .toList();
    }

    @Test
    void parse_allSixteenSectionsPresent() {
        var history = parser.parse(fixtureLines());
        assertThat(history.sectionMap()).hasSize(16);
        assertThat(history.sectionMap().keySet()).containsAll(
                java.util.Arrays.asList(SectionHeader.values()));
    }

    @Test
    void parse_battleReport_fields() {
        var history = parser.parse(fixtureLines());
        var report = (BattleReport) history.sectionMap().get(SectionHeader.BATTLE_REPORT);
        assertThat(report.tier()).isEqualTo(10);
        assertThat(report.wave()).isEqualTo(500);
        assertThat(report.killedBy()).isEqualTo("Fast");
        assertThat(report.towerEra().major()).isEqualTo(1);
    }

    @Test
    void parse_duration_hms() {
        var history = parser.parse(fixtureLines());
        var report = (BattleReport) history.sectionMap().get(SectionHeader.BATTLE_REPORT);
        assertThat(report.gameTime()).isEqualTo(
                Duration.ofHours(1).plusMinutes(30).plusSeconds(45));
    }

    @Test
    void parse_duration_withDays() {
        var history = parser.parse(fixtureLines());
        var report = (BattleReport) history.sectionMap().get(SectionHeader.BATTLE_REPORT);
        // realTime is "0d 2h 0m 0s" → 2 hours
        assertThat(report.realTime()).isEqualTo(Duration.ofHours(2));
    }

    @Test
    void parse_instant_losAngelesTimezone() {
        var history = parser.parse(fixtureLines());
        var report = (BattleReport) history.sectionMap().get(SectionHeader.BATTLE_REPORT);
        var zdt = report.battleReportDate().atZone(ZoneId.of("America/Los_Angeles"));
        assertThat(zdt.getYear()).isEqualTo(2024);
        assertThat(zdt.getMonthValue()).isEqualTo(1);
        assertThat(zdt.getDayOfMonth()).isEqualTo(1);
    }

    @Test
    void parse_towerNumbers_parsedCorrectly() {
        var history = parser.parse(fixtureLines());
        var report = (BattleReport) history.sectionMap().get(SectionHeader.BATTLE_REPORT);
        assertThat(report.coinsEarned().scaleSuffix()).isEqualTo(ScaleSuffix.TRILLION);
        assertThat(report.cellsEarned().scaleSuffix()).isEqualTo(ScaleSuffix.MILLION);
    }

    @Test
    void parse_totalEnemies_longFields() {
        var history = parser.parse(fixtureLines());
        var totals = (TotalEnemies) history.sectionMap().get(SectionHeader.TOTAL_ENEMIES);
        assertThat(totals.totalEnemies()).isEqualTo(50000L);
        assertThat(totals.basic()).isEqualTo(30000L);
    }

    @Test
    void parse_currencies_longFields() {
        var history = parser.parse(fixtureLines());
        var curr = (Currencies) history.sectionMap().get(SectionHeader.CURRENCIES);
        assertThat(curr.gems()).isEqualTo(10L);
        assertThat(curr.medals()).isEqualTo(5L);
    }

    @Test
    void parse_cash_dollarPrefix_stripped() {
        var history = parser.parse(fixtureLines());
        var cash = (Cash) history.sectionMap().get(SectionHeader.CASH);
        assertThat(cash.cashEarned().scaleSuffix()).isEqualTo(ScaleSuffix.MILLION);
        assertThat(cash.cashEarned().amount().doubleValue()).isEqualTo(3.0);
    }

    @Test
    void parse_fieldCountMismatch_throwsException() {
        // Only provide one field for BATTLE_REPORT (needs 11)
        List<String> bad = List.of(
                "Battle Report",
                "Tier\t10"
        );
        assertThatThrownBy(() -> parser.parse(bad))
                .isInstanceOf(FieldToLineCountMismatchException.class);
    }
}