package com.pphi.tower.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WorkshopRepository {

    private final JdbcTemplate jdbc;

    public WorkshopRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ── Records ───────────────────────────────────────────────────────────────

    public record WorkshopItem(
            long id,
            String name,
            String category,
            boolean isPlus,
            int sortOrder,
            int maxLevel,
            int currentLevel,
            // Workshop (non-plus) unlock
            Long unlockGroupId,
            Double unlockGroupCost,
            boolean unlockGroupPurchased,
            // Workshop+ unlock phase 1 (lab)
            String plusUnlockLabName,
            // Workshop+ unlock phase 2 (cumulative spend threshold within category)
            Double plusUnlockCumulativeSpend
    ) {}

    public record WorkshopLevelCost(int level, double baseCost) {}

    /** Total coins spent on Workshop+ items within one category by the player so far. */
    public record PlusCategorySpend(String category, double totalSpent) {}

    /**
     * For each Workshop+ item that is not yet unlocked, shows how much more the player
     * must spend in that category before it becomes available.
     */
    public record PlusUnlockProgress(
            long itemId,
            String itemName,
            String category,
            int sortOrder,
            double threshold,
            double spent,
            double remaining,
            boolean labCompleted
    ) {}

    public record WorkshopDiscounts(
            double attackCostMult,
            double defenseCostMult,
            double utilityCostMult,
            double plusAttackCostMult,
            double plusDefenseCostMult,
            double plusUtilityCostMult
    ) {}

    // ── Queries ───────────────────────────────────────────────────────────────

    /** All Workshop and Workshop+ items with their current player state. */
    public List<WorkshopItem> getAll() {
        return jdbc.query("""
                SELECT
                    wi.id, wi.name, wc.name AS category, wi.is_plus,
                    wi.sort_order, wi.max_level,
                    COALESCE(wis.current_level, 0) AS current_level,
                    wi.unlock_group_id,
                    wug.unlock_cost AS unlock_group_cost,
                    COALESCE(wugs.is_purchased, 0) AS unlock_group_purchased,
                    wi.plus_unlock_lab_name,
                    wi.plus_unlock_cumulative_spend
                FROM workshop_item wi
                JOIN workshop_category wc ON wc.id = wi.category_id
                LEFT JOIN workshop_item_state wis ON wis.workshop_item_id = wi.id
                LEFT JOIN workshop_unlock_group wug ON wug.id = wi.unlock_group_id
                LEFT JOIN workshop_unlock_group_state wugs ON wugs.unlock_group_id = wi.unlock_group_id
                ORDER BY wi.is_plus, wc.id, wi.sort_order
                """,
                (rs, i) -> new WorkshopItem(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("category"),
                        rs.getInt("is_plus") == 1,
                        rs.getInt("sort_order"),
                        rs.getInt("max_level"),
                        rs.getInt("current_level"),
                        rs.getObject("unlock_group_id") != null ? rs.getLong("unlock_group_id") : null,
                        rs.getObject("unlock_group_cost") != null ? rs.getDouble("unlock_group_cost") : null,
                        rs.getInt("unlock_group_purchased") == 1,
                        rs.getString("plus_unlock_lab_name"),
                        rs.getObject("plus_unlock_cumulative_spend") != null
                                ? rs.getDouble("plus_unlock_cumulative_spend") : null
                ));
    }

    /** Level costs for a single item, ordered by level. */
    public List<WorkshopLevelCost> getCosts(long workshopItemId) {
        return jdbc.query("""
                SELECT level, base_cost
                FROM workshop_item_level_cost
                WHERE workshop_item_id = ?
                ORDER BY level
                """,
                (rs, i) -> new WorkshopLevelCost(rs.getInt("level"), rs.getDouble("base_cost")),
                workshopItemId);
    }

    /**
     * Set a Workshop item's current level.
     * Also records the implied coin spend into the category's cumulative-spend tracker
     * so Workshop+ unlock progress stays accurate.
     */
    public void updateLevel(long workshopItemId, int newLevel) {
        jdbc.update("""
                INSERT INTO workshop_item_state (workshop_item_id, current_level) VALUES (?,?)
                ON CONFLICT(workshop_item_id) DO UPDATE SET current_level = excluded.current_level
                """, workshopItemId, newLevel);
    }

    /** Mark a Workshop (non-plus) unlock group as purchased. */
    public void purchaseUnlockGroup(long unlockGroupId) {
        jdbc.update("""
                INSERT INTO workshop_unlock_group_state (unlock_group_id, is_purchased) VALUES (?,1)
                ON CONFLICT(unlock_group_id) DO UPDATE SET is_purchased = 1
                """, unlockGroupId);
    }

    // ── Workshop+ unlock progress ─────────────────────────────────────────────

    /**
     * Computes how many coins the player has spent on Workshop+ items per category.
     * Spend = SUM of base_cost for levels 1..current_level for each item,
     * applying no discount (thresholds use base costs).
     */
    public List<PlusCategorySpend> getPlusCategorySpend() {
        return jdbc.query("""
                SELECT wc.name AS category,
                       COALESCE(SUM(spent.level_total), 0) AS total_spent
                FROM workshop_category wc
                LEFT JOIN (
                    SELECT wi.category_id,
                           SUM(wilc.base_cost) AS level_total
                    FROM workshop_item wi
                    JOIN workshop_item_state wis ON wis.workshop_item_id = wi.id
                    JOIN workshop_item_level_cost wilc ON wilc.workshop_item_id = wi.id
                                                      AND wilc.level <= wis.current_level
                    WHERE wi.is_plus = 1
                    GROUP BY wi.id
                ) spent ON spent.category_id = wc.id
                GROUP BY wc.id, wc.name
                ORDER BY wc.id
                """,
                (rs, i) -> new PlusCategorySpend(
                        rs.getString("category"),
                        rs.getDouble("total_spent")));
    }

    /**
     * For every locked Workshop+ item that has a cumulative-spend threshold,
     * returns how much has been spent vs what is needed, and whether the prerequisite
     * lab has been completed (phase 1 gate).
     */
    public List<PlusUnlockProgress> getPlusUnlockProgress() {
        return jdbc.query("""
                SELECT
                    wi.id   AS item_id,
                    wi.name AS item_name,
                    wc.name AS category,
                    wi.sort_order,
                    wi.plus_unlock_cumulative_spend AS threshold,
                    COALESCE(cat_spend.total_spent, 0) AS spent,
                    MAX(0.0, wi.plus_unlock_cumulative_spend - COALESCE(cat_spend.total_spent, 0)) AS remaining,
                    CASE WHEN wi.plus_unlock_lab_name IS NULL THEN 1
                         WHEN lab_state.current_level > 0    THEN 1
                         ELSE 0 END AS lab_completed
                FROM workshop_item wi
                JOIN workshop_category wc ON wc.id = wi.category_id
                -- per-category spend for this category
                LEFT JOIN (
                    SELECT wi2.category_id,
                           COALESCE(SUM(wilc.base_cost), 0) AS total_spent
                    FROM workshop_item wi2
                    JOIN workshop_item_state wis2 ON wis2.workshop_item_id = wi2.id
                    JOIN workshop_item_level_cost wilc ON wilc.workshop_item_id = wi2.id
                                                      AND wilc.level <= wis2.current_level
                    WHERE wi2.is_plus = 1
                    GROUP BY wi2.category_id
                ) cat_spend ON cat_spend.category_id = wi.category_id
                -- lab completion check (phase 1 gate)
                LEFT JOIN (
                    SELECT l.name, COALESCE(ps.current_level, 0) AS current_level
                    FROM lab l
                    LEFT JOIN lab_player_state ps ON ps.lab_id = l.id
                ) lab_state ON lab_state.name = wi.plus_unlock_lab_name
                WHERE wi.is_plus = 1
                  AND wi.plus_unlock_cumulative_spend IS NOT NULL
                  AND COALESCE((SELECT wis3.current_level FROM workshop_item_state wis3
                                WHERE wis3.workshop_item_id = wi.id), 0) = 0
                ORDER BY wc.id, wi.sort_order
                """,
                (rs, i) -> new PlusUnlockProgress(
                        rs.getLong("item_id"),
                        rs.getString("item_name"),
                        rs.getString("category"),
                        rs.getInt("sort_order"),
                        rs.getDouble("threshold"),
                        rs.getDouble("spent"),
                        rs.getDouble("remaining"),
                        rs.getInt("lab_completed") == 1));
    }

    // ── Discounts ─────────────────────────────────────────────────────────────

    /**
     * Reads all six workshop discount labs and computes the effective cost multiplier
     * for each category (Workshop and Workshop+).
     * Each discount lab reduces cost by 0.5% per level.
     */
    public WorkshopDiscounts getDiscounts() {
        double atkDisc  = labDiscountMult("Workshop Attack Discount");
        double defDisc  = labDiscountMult("Workshop Defense Discount");
        double utlDisc  = labDiscountMult("Workshop Utility Discount");
        double pAtkDisc = labDiscountMult("Enhancement Attack - Coin Discount");
        double pDefDisc = labDiscountMult("Enhancement Defense - Coin Discount");
        double pUtlDisc = labDiscountMult("Enhancement Utility - Coin Discount");
        return new WorkshopDiscounts(atkDisc, defDisc, utlDisc, pAtkDisc, pDefDisc, pUtlDisc);
    }

    private double labDiscountMult(String labName) {
        Integer level = jdbc.queryForObject("""
                SELECT COALESCE(ps.current_level, 0)
                FROM lab l
                LEFT JOIN lab_player_state ps ON ps.lab_id = l.id
                WHERE l.name = ?
                """, Integer.class, labName);
        int lvl = level != null ? level : 0;
        return Math.max(0.0, 1.0 - lvl * 0.005);
    }

    // ── Presets ───────────────────────────────────────────────────────────────

    public record PresetUnlock(boolean workshopUnlocked, boolean workshopPlusUnlocked) {}

    public record Preset(int id, boolean isPlus, int slot, String name) {}

    public record PresetItem(long workshopItemId, String itemName, int targetLevel) {}

    public PresetUnlock getPresetUnlocks() {
        boolean ws = Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT is_unlocked FROM workshop_preset_unlock WHERE is_plus = 0", Boolean.class));
        boolean wsp = Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT is_unlocked FROM workshop_preset_unlock WHERE is_plus = 1", Boolean.class));
        return new PresetUnlock(ws, wsp);
    }

    public void setPresetUnlocked(boolean isPlus, boolean unlocked) {
        jdbc.update("UPDATE workshop_preset_unlock SET is_unlocked = ? WHERE is_plus = ?",
                unlocked ? 1 : 0, isPlus ? 1 : 0);
    }

    public List<Preset> getPresets(boolean isPlus) {
        return jdbc.query("""
                SELECT id, is_plus, slot, name FROM workshop_preset
                WHERE is_plus = ? ORDER BY slot
                """,
                (rs, i) -> new Preset(
                        rs.getInt("id"),
                        rs.getInt("is_plus") == 1,
                        rs.getInt("slot"),
                        rs.getString("name")),
                isPlus ? 1 : 0);
    }

    public int upsertPreset(boolean isPlus, int slot, String name) {
        jdbc.update("""
                INSERT INTO workshop_preset (is_plus, slot, name) VALUES (?,?,?)
                ON CONFLICT(is_plus, slot) DO UPDATE SET name = excluded.name
                """, isPlus ? 1 : 0, slot, name);
        return jdbc.queryForObject(
                "SELECT id FROM workshop_preset WHERE is_plus = ? AND slot = ?",
                Integer.class, isPlus ? 1 : 0, slot);
    }

    public List<PresetItem> getPresetItems(int presetId) {
        return jdbc.query("""
                SELECT wpi.workshop_item_id, wi.name AS item_name, wpi.target_level
                FROM workshop_preset_item wpi
                JOIN workshop_item wi ON wi.id = wpi.workshop_item_id
                WHERE wpi.preset_id = ?
                ORDER BY wi.sort_order
                """,
                (rs, i) -> new PresetItem(
                        rs.getLong("workshop_item_id"),
                        rs.getString("item_name"),
                        rs.getInt("target_level")),
                presetId);
    }

    public void setPresetItems(int presetId, List<PresetItem> items) {
        jdbc.update("DELETE FROM workshop_preset_item WHERE preset_id = ?", presetId);
        if (items.isEmpty()) return;
        List<Object[]> batch = items.stream()
                .map(it -> new Object[]{presetId, it.workshopItemId(), it.targetLevel()})
                .toList();
        jdbc.batchUpdate(
                "INSERT INTO workshop_preset_item (preset_id, workshop_item_id, target_level) VALUES (?,?,?)",
                batch);
    }

    public void deletePreset(int presetId) {
        jdbc.update("DELETE FROM workshop_preset WHERE id = ?", presetId);
    }

    // ── Markdown context ──────────────────────────────────────────────────────

    public String toMarkdownContext() {
        List<WorkshopItem> items = getAll();
        WorkshopDiscounts disc = getDiscounts();
        List<PlusUnlockProgress> progress = getPlusUnlockProgress();

        StringBuilder sb = new StringBuilder();
        sb.append("## Workshop\n\n");
        sb.append("Discounts — Attack: ").append(pct(disc.attackCostMult()))
          .append(", Defense: ").append(pct(disc.defenseCostMult()))
          .append(", Utility: ").append(pct(disc.utilityCostMult())).append("\n\n");

        appendItemTable(sb, items, false);

        sb.append("\n## Workshop+\n\n");
        sb.append("Discounts — Attack: ").append(pct(disc.plusAttackCostMult()))
          .append(", Defense: ").append(pct(disc.plusDefenseCostMult()))
          .append(", Utility: ").append(pct(disc.plusUtilityCostMult())).append("\n\n");

        appendItemTable(sb, items, true);

        if (!progress.isEmpty()) {
            sb.append("\n### Workshop+ Unlock Progress\n\n");
            sb.append("| Item | Category | Lab Done | Spent | Threshold | Remaining |\n");
            sb.append("|------|----------|----------|-------|-----------|----------|\n");
            for (PlusUnlockProgress p : progress) {
                sb.append("| ").append(p.itemName())
                  .append(" | ").append(p.category())
                  .append(" | ").append(p.labCompleted() ? "Yes" : "No")
                  .append(" | ").append(fmt(p.spent()))
                  .append(" | ").append(fmt(p.threshold()))
                  .append(" | ").append(fmt(p.remaining()))
                  .append(" |\n");
            }
        }

        return sb.toString();
    }

    private void appendItemTable(StringBuilder sb, List<WorkshopItem> all, boolean plus) {
        String lastCategory = null;
        for (WorkshopItem item : all) {
            if (item.isPlus() != plus) continue;
            if (!item.category().equals(lastCategory)) {
                lastCategory = item.category();
                sb.append("### ").append(lastCategory).append("\n\n");
                sb.append("| Item | Level | Max | Unlocked |\n");
                sb.append("|------|-------|-----|----------|\n");
            }
            boolean unlocked = plus
                    ? item.currentLevel() > 0 || item.plusUnlockLabName() != null
                    : item.unlockGroupPurchased();
            sb.append("| ").append(item.name())
              .append(" | ").append(item.currentLevel())
              .append(" | ").append(item.maxLevel())
              .append(" | ").append(unlocked ? "Yes" : "No")
              .append(" |\n");
        }
    }

    private String pct(double mult) {
        int pctOff = (int) Math.round((1.0 - mult) * 100);
        return pctOff + "% off";
    }

    private String fmt(double v) {
        if (v >= 1_000_000_000_000.0) return String.format("%.1fT", v / 1_000_000_000_000.0);
        if (v >= 1_000_000_000.0)     return String.format("%.1fB", v / 1_000_000_000.0);
        if (v >= 1_000_000.0)         return String.format("%.1fM", v / 1_000_000.0);
        if (v >= 1_000.0)             return String.format("%.0fK", v / 1_000.0);
        return String.format("%.0f", v);
    }
}
