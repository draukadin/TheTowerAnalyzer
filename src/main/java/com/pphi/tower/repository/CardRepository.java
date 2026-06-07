package com.pphi.tower.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CardRepository {

    private final JdbcTemplate jdbc;

    public CardRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ── Records ───────────────────────────────────────────────────────────────

    public record CardData(
            long   id,
            String name,
            String rarity,
            String description,
            String valueUnit,
            double level1, double level2, double level3, double level4,
            double level5, double level6, double level7,
            Integer milestoneUnlockTier,
            Integer milestoneUnlockWave,
            // mastery definition
            String masteryDescription,
            int    masteryStoneCost,
            String masteryValueUnit,
            double masteryLevel0, double masteryLevel1, double masteryLevel2,
            double masteryLevel3, double masteryLevel4, double masteryLevel5,
            double masteryLevel6, double masteryLevel7, double masteryLevel8,
            double masteryLevel9,
            // player state
            int starLevel,
            int copiesOwned,
            int masteryLevel,
            // mastery gate: current level of this card's "<Name> Mastery" lab (0 = lab not started)
            int masteryLabLevel
    ) {}

    public record CardSlot(
            int    slotNumber,
            int    unlockCost,
            String unlockCurrency,
            boolean owned
    ) {}

    public record CardPreset(
            int    id,
            int    slot,
            String name
    ) {}

    public record PresetAssignment(
            int    slotNumber,
            long   cardId,
            String cardName
    ) {}

    // ── Card queries ──────────────────────────────────────────────────────────

    /** All 31 cards with their current player state and mastery lab level. */
    public List<CardData> getAllCards() {
        return jdbc.query("""
                SELECT
                    c.id, c.name, c.rarity, c.description, c.value_unit,
                    c.level_1, c.level_2, c.level_3, c.level_4,
                    c.level_5, c.level_6, c.level_7,
                    c.milestone_unlock_tier, c.milestone_unlock_wave,
                    c.mastery_description, c.mastery_stone_cost, c.mastery_value_unit,
                    c.mastery_level_0, c.mastery_level_1, c.mastery_level_2,
                    c.mastery_level_3, c.mastery_level_4, c.mastery_level_5,
                    c.mastery_level_6, c.mastery_level_7, c.mastery_level_8,
                    c.mastery_level_9,
                    COALESCE(ps.star_level,    1) AS star_level,
                    COALESCE(ps.copies_owned,  0) AS copies_owned,
                    COALESCE(ps.mastery_level, 0) AS mastery_level,
                    COALESCE(lps.current_level, 0) AS mastery_lab_level
                FROM card c
                LEFT JOIN card_player_state ps  ON ps.card_id = c.id
                LEFT JOIN lab l                 ON l.name = c.name || ' Mastery'
                LEFT JOIN lab_player_state lps  ON lps.lab_id = l.id
                ORDER BY c.rarity, c.name
                """,
                (rs, i) -> new CardData(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getString("rarity"),
                        rs.getString("description"),
                        rs.getString("value_unit"),
                        rs.getDouble("level_1"), rs.getDouble("level_2"),
                        rs.getDouble("level_3"), rs.getDouble("level_4"),
                        rs.getDouble("level_5"), rs.getDouble("level_6"),
                        rs.getDouble("level_7"),
                        rs.getObject("milestone_unlock_tier") != null
                                ? rs.getInt("milestone_unlock_tier") : null,
                        rs.getObject("milestone_unlock_wave") != null
                                ? rs.getInt("milestone_unlock_wave") : null,
                        rs.getString("mastery_description"),
                        rs.getInt("mastery_stone_cost"),
                        rs.getString("mastery_value_unit"),
                        rs.getDouble("mastery_level_0"), rs.getDouble("mastery_level_1"),
                        rs.getDouble("mastery_level_2"), rs.getDouble("mastery_level_3"),
                        rs.getDouble("mastery_level_4"), rs.getDouble("mastery_level_5"),
                        rs.getDouble("mastery_level_6"), rs.getDouble("mastery_level_7"),
                        rs.getDouble("mastery_level_8"), rs.getDouble("mastery_level_9"),
                        rs.getInt("star_level"),
                        rs.getInt("copies_owned"),
                        rs.getInt("mastery_level"),
                        rs.getInt("mastery_lab_level")
                ));
    }

    /** Current level of a card's mastery lab (0 = lab not started). Used for controller-side validation. */
    public int getMasteryLabLevel(long cardId) {
        Integer level = jdbc.queryForObject("""
                SELECT COALESCE(lps.current_level, 0)
                FROM card c
                JOIN lab l ON l.name = c.name || ' Mastery'
                LEFT JOIN lab_player_state lps ON lps.lab_id = l.id
                WHERE c.id = ?
                """, Integer.class, cardId);
        return level != null ? level : 0;
    }

    // ── Player-state mutations ────────────────────────────────────────────────

    /** Update the star level (1–7) for a card. */
    public void updateStarLevel(long cardId, int starLevel) {
        jdbc.update("""
                INSERT INTO card_player_state (card_id, star_level) VALUES (?, ?)
                ON CONFLICT(card_id) DO UPDATE SET star_level = excluded.star_level
                """, cardId, starLevel);
    }

    /** Update the number of copies owned for a card. */
    public void updateCopiesOwned(long cardId, int copiesOwned) {
        jdbc.update("""
                INSERT INTO card_player_state (card_id, copies_owned) VALUES (?, ?)
                ON CONFLICT(card_id) DO UPDATE SET copies_owned = excluded.copies_owned
                """, cardId, copiesOwned);
    }

    /** Update the mastery level (0–9) for a card. */
    public void updateMasteryLevel(long cardId, int masteryLevel) {
        jdbc.update("""
                INSERT INTO card_player_state (card_id, mastery_level) VALUES (?, ?)
                ON CONFLICT(card_id) DO UPDATE SET mastery_level = excluded.mastery_level
                """, cardId, masteryLevel);
    }

    // ── Slot queries ──────────────────────────────────────────────────────────

    /** All 28 card slots with their unlock cost, currency display hint, and owned flag. */
    public List<CardSlot> getAllSlots() {
        return jdbc.query("""
                SELECT slot_number, unlock_cost, unlock_currency, owned
                FROM card_slot
                ORDER BY slot_number
                """,
                (rs, i) -> new CardSlot(
                        rs.getInt("slot_number"),
                        rs.getInt("unlock_cost"),
                        rs.getString("unlock_currency"),
                        rs.getInt("owned") != 0
                ));
    }

    /** Mark a slot as owned or unowned. Slot 1 is always owned; enforce this at the controller layer. */
    public void updateSlotOwned(int slotNumber, boolean owned) {
        jdbc.update("UPDATE card_slot SET owned = ? WHERE slot_number = ?", owned ? 1 : 0, slotNumber);
    }

    // ── Preset queries ────────────────────────────────────────────────────────

    /** All card presets ordered by slot number. */
    public List<CardPreset> getAllPresets() {
        return jdbc.query("""
                SELECT id, slot, name FROM card_preset ORDER BY slot
                """,
                (rs, i) -> new CardPreset(
                        rs.getInt("id"),
                        rs.getInt("slot"),
                        rs.getString("name")
                ));
    }

    /**
     * Create a new preset in a given slot (slots 2–5; slot 1 is seeded by default).
     * The Card Presets lab must be checked at the service/controller layer before calling this.
     * @return the new preset's id
     */
    public int createPreset(int slot, String name) {
        Long id = jdbc.queryForObject("""
                INSERT INTO card_preset (slot, name) VALUES (?, ?) RETURNING id
                """, Long.class, slot, name);
        if (id == null) throw new RuntimeException("Failed to create card preset");
        return id.intValue();
    }

    /** Rename an existing preset. */
    public void renamePreset(int presetId, String name) {
        jdbc.update("UPDATE card_preset SET name = ? WHERE id = ?", name, presetId);
    }

    /**
     * Delete a preset and all its assignments (cascade handles assignments automatically).
     * Slot 1 (the default preset) should not be deleted; enforce this at the controller layer.
     */
    public void deletePreset(int presetId) {
        jdbc.update("DELETE FROM card_preset WHERE id = ?", presetId);
    }

    // ── Preset-assignment queries ─────────────────────────────────────────────

    /** All card assignments for a preset, ordered by slot number. */
    public List<PresetAssignment> getAssignments(int presetId) {
        return jdbc.query("""
                SELECT cpa.slot_number, cpa.card_id, c.name AS card_name
                FROM card_preset_assignment cpa
                JOIN card c ON c.id = cpa.card_id
                WHERE cpa.preset_id = ?
                ORDER BY cpa.slot_number
                """,
                (rs, i) -> new PresetAssignment(
                        rs.getInt("slot_number"),
                        rs.getLong("card_id"),
                        rs.getString("card_name")
                ),
                presetId);
    }

    /**
     * Assign a card to a specific slot within a preset.
     * Replaces any existing card in that slot, and removes the card from any other slot
     * in the same preset (a card can only appear once per preset).
     */
    public void assignCard(int presetId, int slotNumber, long cardId) {
        // Remove the card from any other slot in this preset first
        jdbc.update("""
                DELETE FROM card_preset_assignment
                WHERE preset_id = ? AND card_id = ? AND slot_number != ?
                """, presetId, cardId, slotNumber);
        // Upsert: place (or move) the card into the target slot
        jdbc.update("""
                INSERT INTO card_preset_assignment (preset_id, slot_number, card_id) VALUES (?, ?, ?)
                ON CONFLICT(preset_id, slot_number) DO UPDATE SET card_id = excluded.card_id
                """, presetId, slotNumber, cardId);
    }

    /** Remove whatever card is assigned to a slot in a preset. No-op if the slot is empty. */
    public void unassignSlot(int presetId, int slotNumber) {
        jdbc.update("""
                DELETE FROM card_preset_assignment WHERE preset_id = ? AND slot_number = ?
                """, presetId, slotNumber);
    }

    /** Remove a specific card from a preset entirely, regardless of which slot it occupies. */
    public void unassignCard(int presetId, long cardId) {
        jdbc.update("""
                DELETE FROM card_preset_assignment WHERE preset_id = ? AND card_id = ?
                """, presetId, cardId);
    }

    /**
     * Replace all assignments in a preset atomically.
     * Clears existing assignments and inserts the new list in one go.
     */
    public void setAssignments(int presetId, List<PresetAssignment> assignments) {
        jdbc.update("DELETE FROM card_preset_assignment WHERE preset_id = ?", presetId);
        if (assignments.isEmpty()) return;
        List<Object[]> batch = assignments.stream()
                .map(a -> new Object[]{presetId, a.slotNumber(), a.cardId()})
                .toList();
        jdbc.batchUpdate("""
                INSERT INTO card_preset_assignment (preset_id, slot_number, card_id) VALUES (?,?,?)
                """, batch);
    }

    // ── Markdown context ──────────────────────────────────────────────────────

    public String toMarkdownContext() {
        List<CardData>   cards  = getAllCards();
        List<CardSlot>   slots  = getAllSlots();
        List<CardPreset> presets = getAllPresets();

        StringBuilder sb = new StringBuilder();

        // Slot ownership summary
        List<CardSlot> gemSlots = slots.stream().filter(s -> s.unlockCurrency().equals("GEM")).toList();
        List<CardSlot> keySlots = slots.stream().filter(s -> s.unlockCurrency().equals("KEY")).toList();
        long gemOwned   = gemSlots.stream().filter(CardSlot::owned).count();
        int  gemsSpent  = gemSlots.stream().filter(CardSlot::owned).mapToInt(CardSlot::unlockCost).sum();
        int  gemsNeeded = gemSlots.stream().filter(s -> !s.owned()).mapToInt(CardSlot::unlockCost).sum();
        long keyOwned   = keySlots.stream().filter(CardSlot::owned).count();
        int  keysSpent  = keySlots.stream().filter(CardSlot::owned).mapToInt(CardSlot::unlockCost).sum();
        int  keysNeeded = keySlots.stream().filter(s -> !s.owned()).mapToInt(CardSlot::unlockCost).sum();
        sb.append("## Card Slots\n\n");
        sb.append("| Currency | Owned | Total | Spent | To Unlock All |\n");
        sb.append("|----------|-------|-------|-------|---------------|\n");
        sb.append("| Gems     | ").append(gemOwned).append(" | ").append(gemSlots.size())
          .append(" | ").append(gemsSpent).append(" | ").append(gemsNeeded).append(" |\n");
        sb.append("| Keys     | ").append(keyOwned).append(" | ").append(keySlots.size())
          .append(" | ").append(keysSpent).append(" | ").append(keysNeeded).append(" |\n\n");

        // Card collection summary
        long owned   = cards.stream().filter(c -> c.copiesOwned() > 0).count();
        sb.append("## Cards (").append(owned).append(" / ").append(cards.size()).append(" owned)\n\n");
        sb.append("| Name | Rarity | Star | Copies | Mastery |\n");
        sb.append("|------|--------|------|--------|--------|\n");
        for (CardData c : cards) {
            sb.append("| ").append(c.name())
              .append(" | ").append(c.rarity())
              .append(" | ").append(c.starLevel())
              .append(" | ").append(c.copiesOwned())
              .append(" | ").append(c.masteryLevel())
              .append(" |\n");
        }

        // Presets summary
        sb.append("\n## Card Presets\n\n");
        for (CardPreset preset : presets) {
            sb.append("### ").append(preset.name()).append(" (slot ").append(preset.slot()).append(")\n\n");
            List<PresetAssignment> assignments = getAssignments(preset.id());
            if (assignments.isEmpty()) {
                sb.append("_(empty)_\n\n");
            } else {
                sb.append("| Slot | Card |\n");
                sb.append("|------|------|\n");
                for (PresetAssignment a : assignments) {
                    sb.append("| ").append(a.slotNumber())
                      .append(" | ").append(a.cardName())
                      .append(" |\n");
                }
                sb.append("\n");
            }
        }

        return sb.toString();
    }
}
