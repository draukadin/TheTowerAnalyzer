package com.pphi.tower.repository;

import com.pphi.tower.web.dto.ConversationTurn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ChatHistoryRepository {

    private final JdbcTemplate jdbc;

    public ChatHistoryRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void save(String reportId1, String reportId2, String role, String message) {
        save(reportId1, reportId2, role, message, null);
    }

    public void save(String reportId1, String reportId2, String role, String message, String thoughtSignature) {
        jdbc.update(
                "INSERT INTO chat_history (report_id_1, report_id_2, role, message, thought_signature, created_at) VALUES (?, ?, ?, ?, ?, ?)",
                reportId1, reportId2, role, message, thoughtSignature, System.currentTimeMillis());
    }

    public List<ConversationTurn> findByPair(String reportId1, String reportId2) {
        return jdbc.query(
                "SELECT role, message, thought_signature FROM chat_history WHERE report_id_1 = ? AND report_id_2 = ? ORDER BY id ASC",
                (rs, rowNum) -> new ConversationTurn(
                        rs.getString("role"),
                        rs.getString("message"),
                        rs.getString("thought_signature")),
                reportId1, reportId2);
    }

    public void deleteByPair(String reportId1, String reportId2) {
        jdbc.update(
                "DELETE FROM chat_history WHERE report_id_1 = ? AND report_id_2 = ?",
                reportId1, reportId2);
    }
}
