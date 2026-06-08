package com.pphi.tower.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LabSlotRepository {

    private final JdbcTemplate jdbc;
    private final LabRepository labRepo;

    public LabSlotRepository(JdbcTemplate jdbc, LabRepository labRepo) {
        this.jdbc = jdbc;
        this.labRepo = labRepo;
    }

    public record PlanEntry(long id, int sortOrder, long labId, String labName,
                            int startLevel, int targetLevel,
                            double coinsAtStartLevel, double coinsTotalResearch,
                            double coinsCurrentStep, long durationSeconds) {}

    public record SlotData(int slotNumber, double cellSpeedMult, List<PlanEntry> plans,
                           double totalCoins, long totalDurationSeconds,
                           Double coinsPerDay) {}

    public List<SlotData> getAllSlots() {
        LabRepository.LabMultipliers mults = labRepo.getMultipliers();

        return jdbc.query("SELECT slot_number, cell_speed_mult FROM lab_slot ORDER BY slot_number",
                (rs, i) -> {
                    int slotNumber = rs.getInt("slot_number");
                    double cellMult = rs.getDouble("cell_speed_mult");

                    List<PlanEntry> plans = buildPlans(slotNumber, mults, cellMult);

                    double totalCoins = plans.stream().mapToDouble(PlanEntry::coinsTotalResearch).sum();
                    long totalDuration = plans.stream().mapToLong(PlanEntry::durationSeconds).sum();

                    Double coinsPerDay = null;
                    if (!plans.isEmpty()) {
                        PlanEntry current = plans.get(0);
                        // Subtract the currently-running step's cost — already paid
                        double coinsStillNeeded = current.coinsTotalResearch() - current.coinsCurrentStep();
                        if (current.durationSeconds() > 0 && coinsStillNeeded > 0) {
                            coinsPerDay = coinsStillNeeded / (current.durationSeconds() / 86400.0);
                        } else {
                            coinsPerDay = 0.0;
                        }
                    }

                    return new SlotData(slotNumber, cellMult, plans, totalCoins, totalDuration, coinsPerDay);
                });
    }

    private List<PlanEntry> buildPlans(int slotNumber, LabRepository.LabMultipliers mults, double cellMult) {
        return jdbc.query("""
                SELECT p.id, p.sort_order, p.lab_id, l.name AS lab_name,
                       p.start_level, p.target_level
                FROM lab_slot_plan p
                JOIN lab l ON l.id = p.lab_id
                WHERE p.slot_number = ?
                ORDER BY p.sort_order
                """,
                (rs, i) -> {
                    long planId = rs.getLong("id");
                    int sortOrder = rs.getInt("sort_order");
                    long labId = rs.getLong("lab_id");
                    String labName = rs.getString("lab_name");
                    int startLevel = rs.getInt("start_level");
                    int targetLevel = rs.getInt("target_level");

                    List<LabRepository.LabLevelCost> costs = labRepo.getCosts(labId);

                    double coinsAtStart = costs.stream()
                            .filter(c -> c.level() == startLevel && c.coinCost() != null)
                            .mapToDouble(c -> c.coinCost() * mults.costMult())
                            .findFirst().orElse(0.0);

                    double totalCoins = costs.stream()
                            .filter(c -> c.level() > startLevel && c.level() <= targetLevel && c.coinCost() != null)
                            .mapToDouble(c -> c.coinCost() * mults.costMult())
                            .sum();

                    // Cost of just the first step (startLevel → startLevel+1) — already paid while running
                    double coinsCurrentStep = costs.stream()
                            .filter(c -> c.level() == startLevel + 1 && c.coinCost() != null)
                            .mapToDouble(c -> c.coinCost() * mults.costMult())
                            .findFirst().orElse(0.0);

                    double rawDurationSeconds = costs.stream()
                            .filter(c -> c.level() > startLevel && c.level() <= targetLevel && c.durationSeconds() != null)
                            .mapToDouble(LabRepository.LabLevelCost::durationSeconds)
                            .sum();

                    long duration = Math.round(rawDurationSeconds / (mults.speedMult() * cellMult));

                    return new PlanEntry(planId, sortOrder, labId, labName,
                            startLevel, targetLevel, coinsAtStart, totalCoins, coinsCurrentStep, duration);
                }, slotNumber);
    }

    public void updateCellSpeed(int slotNumber, double cellSpeedMult) {
        jdbc.update("UPDATE lab_slot SET cell_speed_mult=? WHERE slot_number=?", cellSpeedMult, slotNumber);
    }

    @Transactional
    public void addPlan(int slotNumber, long labId, int startLevel, int targetLevel) {
        Integer maxOrder = jdbc.queryForObject(
                "SELECT COALESCE(MAX(sort_order),0) FROM lab_slot_plan WHERE slot_number=?",
                Integer.class, slotNumber);
        int nextOrder = (maxOrder != null ? maxOrder : 0) + 1;
        jdbc.update("INSERT INTO lab_slot_plan(slot_number,sort_order,lab_id,start_level,target_level) VALUES(?,?,?,?,?)",
                slotNumber, nextOrder, labId, startLevel, targetLevel);
    }

    public void deletePlan(long planId) {
        jdbc.update("DELETE FROM lab_slot_plan WHERE id=?", planId);
    }

    public void updatePlan(long planId, int startLevel, int targetLevel) {
        jdbc.update("UPDATE lab_slot_plan SET start_level=?,target_level=? WHERE id=?",
                startLevel, targetLevel, planId);
    }

    @Transactional
    public void movePlan(long planId, String direction) {
        var row = jdbc.queryForMap("SELECT slot_number, sort_order FROM lab_slot_plan WHERE id=?", planId);
        int slotNumber = ((Number) row.get("slot_number")).intValue();
        int sortOrder = ((Number) row.get("sort_order")).intValue();

        int targetOrder = "up".equals(direction) ? sortOrder - 1 : sortOrder + 1;

        Long swapId = null;
        try {
            swapId = jdbc.queryForObject(
                    "SELECT id FROM lab_slot_plan WHERE slot_number=? AND sort_order=?",
                    Long.class, slotNumber, targetOrder);
        } catch (Exception ignored) {}

        if (swapId == null) return;

        jdbc.update("UPDATE lab_slot_plan SET sort_order=-1 WHERE id=?", planId);
        jdbc.update("UPDATE lab_slot_plan SET sort_order=? WHERE id=?", sortOrder, swapId);
        jdbc.update("UPDATE lab_slot_plan SET sort_order=? WHERE id=?", targetOrder, planId);
    }
}
