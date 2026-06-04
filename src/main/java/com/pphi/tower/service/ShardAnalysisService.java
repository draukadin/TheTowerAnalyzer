package com.pphi.tower.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.model.ModuleLevelTable;
import com.pphi.tower.model.battlehistory.BattleHistory;
import com.pphi.tower.model.battlehistory.Currencies;
import com.pphi.tower.model.battlehistory.SectionHeader;
import com.pphi.tower.repository.CurrencySnapshotRepository;
import com.pphi.tower.repository.CurrencySnapshotRepository.ModuleLevelSnapshot;
import com.pphi.tower.repository.CurrencySnapshotRepository.SnapshotPair;
import com.pphi.tower.repository.RunRepository;
import com.pphi.tower.repository.RunRepository.ShardRunRow;
import com.pphi.tower.web.dto.ShardRateDto;
import com.pphi.tower.web.dto.ShardRateDto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;

@Service
public class ShardAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(ShardAnalysisService.class);

    private final RunRepository repository;
    private final CurrencySnapshotRepository snapshotRepository;
    private final ObjectMapper objectMapper;

    public ShardAnalysisService(RunRepository repository,
                                CurrencySnapshotRepository snapshotRepository,
                                ObjectMapper objectMapper) {
        this.repository = repository;
        this.snapshotRepository = snapshotRepository;
        this.objectMapper = objectMapper;
    }

    // ── Battle-report-based analysis ─────────────────────────────────────────

    public ShardRateDto getShardRates(int days,
                                      int cannonLevel, int armorLevel,
                                      int generatorLevel, int coreLevel,
                                      int shardCostDiscountLevel,
                                      int cannonTarget, int armorTarget,
                                      int generatorTarget, int coreTarget) {
        LocalDate to   = LocalDate.now();
        LocalDate from = to.minusDays(days);
        int clampedDiscount = Math.max(0, Math.min(30, shardCostDiscountLevel));

        List<RunDataPointDto> farmingPoints   = buildPoints(repository.findFarmingRunsWithPayload(from, to));
        ShardAverages farmingAvgs             = computeAverages(farmingPoints);
        ShardStdDev   farmingStdDev           = computeStdDev(farmingPoints, farmingAvgs);
        List<RunDataPointDto> filteredFarming = filterOutliers(farmingPoints, farmingAvgs, farmingStdDev);
        ShardAverages filteredAvgs            = computeAverages(filteredFarming);

        List<RunDataPointDto> allPoints        = buildPoints(repository.findAllRunsWithPayload(from, to));
        ShardAverages blendedAvgs              = computeAverages(allPoints);
        ShardStdDev   blendedStdDev            = computeStdDev(allPoints, blendedAvgs);
        List<RunDataPointDto> filteredBlended  = filterOutliers(allPoints, blendedAvgs, blendedStdDev);
        ShardAverages filteredBlendedAvgs      = computeAverages(filteredBlended);

        ShardProjections projections = new ShardProjections(
                project(cannonLevel,    filteredAvgs.cannon(),           clampedDiscount, cannonTarget),
                project(armorLevel,     filteredAvgs.armor(),            clampedDiscount, armorTarget),
                project(generatorLevel, filteredAvgs.generator(),        clampedDiscount, generatorTarget),
                project(coreLevel,      filteredAvgs.core(),             clampedDiscount, coreTarget));

        ShardProjections blendedProjections = new ShardProjections(
                project(cannonLevel,    filteredBlendedAvgs.cannon(),    clampedDiscount, cannonTarget),
                project(armorLevel,     filteredBlendedAvgs.armor(),     clampedDiscount, armorTarget),
                project(generatorLevel, filteredBlendedAvgs.generator(), clampedDiscount, generatorTarget),
                project(coreLevel,      filteredBlendedAvgs.core(),      clampedDiscount, coreTarget));

        return new ShardRateDto(
                days, filteredFarming.size(), filteredBlended.size(),
                filteredAvgs, filteredBlendedAvgs,
                farmingStdDev, blendedStdDev,
                filteredFarming, projections, blendedProjections);
    }

    // ── Snapshot-based analysis ───────────────────────────────────────────────

    public ShardRateDto getSnapshotBasedShardRates(int days,
                                                    int cannonLevel, int armorLevel,
                                                    int generatorLevel, int coreLevel,
                                                    int shardCostDiscountLevel,
                                                    int targetLevel) {
        LocalDateTime to   = LocalDateTime.now();
        LocalDateTime from = to.minusDays(days);
        double multiplier  = 1.0 - Math.min(30, shardCostDiscountLevel) / 100.0;

        List<SnapshotPair> pairs = snapshotRepository.findSnapshotPairsInWindow(from, to);
        Map<LocalDateTime, List<ModuleLevelSnapshot>> moduleLevels =
                snapshotRepository.findModuleLevelsBetween(from, to);

        List<RunDataPointDto> points = pairs.stream()
                .filter(pair -> {
                    double hours = Duration.between(pair.earlierTime(), pair.laterTime())
                                           .toMinutes() / 60.0;
                    return hours > 0;
                })
                .map(pair -> {
                    double hours = Duration.between(pair.earlierTime(), pair.laterTime())
                                           .toMinutes() / 60.0;
                    int cannonSpent    = shardsSpentBetween(moduleLevels, pair.earlierTime(), pair.laterTime(), "Cannon",    multiplier);
                    int armorSpent     = shardsSpentBetween(moduleLevels, pair.earlierTime(), pair.laterTime(), "Armor",     multiplier);
                    int genSpent       = shardsSpentBetween(moduleLevels, pair.earlierTime(), pair.laterTime(), "Generator", multiplier);
                    int coreSpent      = shardsSpentBetween(moduleLevels, pair.earlierTime(), pair.laterTime(), "Core",      multiplier);

                    double cannonGross = (pair.laterCannon()    - pair.earlierCannon())    + cannonSpent;
                    double armorGross  = (pair.laterArmor()     - pair.earlierArmor())     + armorSpent;
                    double genGross    = (pair.laterGenerator() - pair.earlierGenerator()) + genSpent;
                    double coreGross   = (pair.laterCore()      - pair.earlierCore())      + coreSpent;

                    return new RunDataPointDto(
                            null, pair.earlierTime().toLocalDate().toString(), 0, 0, null,
                            (long)(hours * 3600),
                            0L, 0L, 0L, 0L,
                            cannonGross / hours, armorGross / hours,
                            genGross    / hours, coreGross  / hours);
                })
                .toList();

        ShardAverages avgs    = computeAverages(points);
        ShardStdDev   stdDev  = computeStdDev(points, avgs);
        List<RunDataPointDto> filtered    = filterOutliers(points, avgs, stdDev);
        ShardAverages filteredAvgs        = computeAverages(filtered);
        ShardStdDev   filteredStdDev      = computeStdDev(filtered, filteredAvgs);

        int clampedDiscount = Math.max(0, Math.min(30, shardCostDiscountLevel));
        ShardProjections projections = new ShardProjections(
                project(cannonLevel,    filteredAvgs.cannon(),    clampedDiscount, targetLevel),
                project(armorLevel,     filteredAvgs.armor(),     clampedDiscount, targetLevel),
                project(generatorLevel, filteredAvgs.generator(), clampedDiscount, targetLevel),
                project(coreLevel,      filteredAvgs.core(),      clampedDiscount, targetLevel));

        return new ShardRateDto(
                days, filtered.size(), filtered.size(),
                filteredAvgs, filteredAvgs,
                filteredStdDev, filteredStdDev,
                filtered, projections, projections);
    }

    public int getSnapshotCount() {
        return snapshotRepository.countSnapshots();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private List<RunDataPointDto> buildPoints(List<ShardRunRow> rows) {
        return rows.stream()
                .filter(row -> row.realTimeSeconds() > 0)
                .map(row -> {
                    Currencies cur = extractCurrencies(row);
                    if (cur == null) return null;
                    double hours = row.realTimeSeconds() / 3600.0;
                    return new RunDataPointDto(
                            row.id(), row.battleDate(), row.tier(), row.wave(),
                            row.towerEra() != null ? row.towerEra().toString() : null,
                            row.realTimeSeconds(),
                            cur.cannonShards(), cur.armorShards(),
                            cur.generatorShards(), cur.coreShards(),
                            cur.cannonShards()    / hours,
                            cur.armorShards()     / hours,
                            cur.generatorShards() / hours,
                            cur.coreShards()      / hours);
                })
                .filter(p -> p != null)
                .toList();
    }

    private List<RunDataPointDto> filterOutliers(List<RunDataPointDto> pts,
                                                  ShardAverages avgs,
                                                  ShardStdDev stdDev) {
        return pts.stream().filter(p ->
                Math.abs(p.cannonPerHour()    - avgs.cannon())    <= 2 * stdDev.cannon()    &&
                Math.abs(p.armorPerHour()     - avgs.armor())     <= 2 * stdDev.armor()     &&
                Math.abs(p.generatorPerHour() - avgs.generator()) <= 2 * stdDev.generator() &&
                Math.abs(p.corePerHour()      - avgs.core())      <= 2 * stdDev.core()
        ).toList();
    }

    private ShardProjection project(int currentLevel, double ratePerHour,
                                     int discountLevel, int targetLevel) {
        int clampedLevel  = Math.max(1, Math.min(currentLevel, ModuleLevelTable.MAX_LEVEL));
        int clampedTarget = Math.max(clampedLevel + 1, Math.min(targetLevel, ModuleLevelTable.MAX_LEVEL));
        double multiplier = 1.0 - discountLevel / 100.0;

        long baseNext        = ModuleLevelTable.shardsForLevel(clampedLevel + 1);
        long baseTarget      = ModuleLevelTable.shardsRemainingTo(clampedLevel, clampedTarget);
        long effectiveNext   = Math.round(baseNext   * multiplier);
        long effectiveTarget = Math.round(baseTarget * multiplier);

        double hoursToNext   = ratePerHour > 0 ? effectiveNext   / ratePerHour : -1;
        double hoursToTarget = ratePerHour > 0 ? effectiveTarget / ratePerHour : -1;

        return new ShardProjection(
                clampedLevel, clampedTarget, discountLevel, discountLevel * 1.0,
                baseNext, effectiveNext, hoursToNext,
                baseTarget, effectiveTarget, hoursToTarget);
    }

    private int shardsSpentBetween(Map<LocalDateTime, List<ModuleLevelSnapshot>> levelMap,
                                    LocalDateTime from, LocalDateTime to,
                                    String type, double multiplier) {
        return levelMap.entrySet().stream()
                .filter(e -> e.getKey().isAfter(from) && !e.getKey().isAfter(to))
                .flatMap(e -> e.getValue().stream())
                .filter(m -> m.type().equalsIgnoreCase(type))
                .mapToInt(m -> (int)(ModuleLevelTable.shardsForLevel(m.level()) * multiplier))
                .sum();
    }

    private Currencies extractCurrencies(ShardRunRow row) {
        try {
            BattleHistory history = objectMapper.readValue(row.payload(), BattleHistory.class);
            return (Currencies) history.sectionMap().get(SectionHeader.CURRENCIES);
        } catch (Exception e) {
            log.warn("Failed to deserialize payload for run {}: {}", row.id(), e.getMessage());
            return null;
        }
    }

    private ShardAverages computeAverages(List<RunDataPointDto> pts) {
        if (pts.isEmpty()) return new ShardAverages(0, 0, 0, 0);
        return new ShardAverages(
                avg(pts.stream().mapToDouble(RunDataPointDto::cannonPerHour)),
                avg(pts.stream().mapToDouble(RunDataPointDto::armorPerHour)),
                avg(pts.stream().mapToDouble(RunDataPointDto::generatorPerHour)),
                avg(pts.stream().mapToDouble(RunDataPointDto::corePerHour)));
    }

    private ShardStdDev computeStdDev(List<RunDataPointDto> pts, ShardAverages avgs) {
        if (pts.size() < 2) return new ShardStdDev(0, 0, 0, 0);
        return new ShardStdDev(
                stdDev(pts.stream().mapToDouble(RunDataPointDto::cannonPerHour),    avgs.cannon()),
                stdDev(pts.stream().mapToDouble(RunDataPointDto::armorPerHour),     avgs.armor()),
                stdDev(pts.stream().mapToDouble(RunDataPointDto::generatorPerHour), avgs.generator()),
                stdDev(pts.stream().mapToDouble(RunDataPointDto::corePerHour),      avgs.core()));
    }

    private double avg(java.util.stream.DoubleStream s) {
        OptionalDouble v = s.average();
        return v.isPresent() ? v.getAsDouble() : 0.0;
    }

    private double stdDev(java.util.stream.DoubleStream s, double mean) {
        double[] vals = s.toArray();
        if (vals.length < 2) return 0.0;
        double variance = 0;
        for (double v : vals) variance += (v - mean) * (v - mean);
        return Math.sqrt(variance / vals.length);
    }
}
