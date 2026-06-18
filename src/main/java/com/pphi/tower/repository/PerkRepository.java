package com.pphi.tower.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PerkRepository {

    private final JdbcTemplate jdbc;

    public PerkRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public record PerkEntry(long id, String name, String type, int maxPicks,
                             Double baseValue, String valueFormula, String valueUnit,
                             String downsideDesc) {}

    public record PerkSettings(List<String> bans, List<String> ranking, String firstChoice) {}

    public List<PerkEntry> getAll() {
        return jdbc.query(
                "SELECT id, name, type, max_picks, base_value, value_formula, value_unit, downside_desc FROM perk ORDER BY type, id",
                (rs, i) -> new PerkEntry(
                        rs.getLong("id"), rs.getString("name"), rs.getString("type"),
                        rs.getInt("max_picks"),
                        rs.getObject("base_value") != null ? rs.getDouble("base_value") : null,
                        rs.getString("value_formula"), rs.getString("value_unit"),
                        rs.getString("downside_desc")));
    }

    public PerkSettings getSettings() {
        List<String> bans = jdbc.queryForList(
                "SELECT p.name FROM perk_ban pb JOIN perk p ON p.id = pb.perk_id ORDER BY p.name",
                String.class);
        List<String> ranking = jdbc.queryForList(
                "SELECT p.name FROM perk_auto_pick_ranking pr JOIN perk p ON p.id = pr.perk_id ORDER BY pr.rank",
                String.class);
        List<String> firstChoice = jdbc.queryForList(
                "SELECT p.name FROM perk_first_choice fc JOIN perk p ON p.id = fc.perk_id",
                String.class);
        return new PerkSettings(bans, ranking, firstChoice.isEmpty() ? null : firstChoice.get(0));
    }

    public void setFirstChoice(Long perkId) {
        if (perkId == null) {
            jdbc.execute("DELETE FROM perk_first_choice");
        } else {
            jdbc.update("INSERT INTO perk_first_choice (id, perk_id) VALUES (1, ?) ON CONFLICT(id) DO UPDATE SET perk_id = excluded.perk_id", perkId);
        }
    }

    public void setBans(List<Long> perkIds) {
        jdbc.execute("DELETE FROM perk_ban");
        for (Long id : perkIds) {
            jdbc.update("INSERT INTO perk_ban (perk_id) VALUES (?)", id);
        }
    }

    public void setRanking(List<Long> perkIds) {
        jdbc.execute("DELETE FROM perk_auto_pick_ranking");
        for (int i = 0; i < perkIds.size(); i++) {
            jdbc.update("INSERT INTO perk_auto_pick_ranking (rank, perk_id) VALUES (?, ?)",
                    i + 1, perkIds.get(i));
        }
    }
}
