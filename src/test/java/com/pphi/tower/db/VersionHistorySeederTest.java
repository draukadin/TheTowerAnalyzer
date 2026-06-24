package com.pphi.tower.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VersionHistorySeederTest {

    @Mock JdbcTemplate jdbc;
    @Mock DatabaseInitializer init;

    @Test
    void noSeed_whenRunsExist() {
        when(jdbc.queryForObject(eq("SELECT COUNT(*) FROM tower_version"), eq(Integer.class))).thenReturn(0);
        when(jdbc.queryForObject(eq("SELECT COUNT(*) FROM runs"),          eq(Integer.class))).thenReturn(42);

        new VersionHistorySeeder(jdbc, init);

        verify(jdbc, never()).update(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void seeds100_whenBothTablesEmpty() {
        when(jdbc.queryForObject(eq("SELECT COUNT(*) FROM tower_version"), eq(Integer.class))).thenReturn(0);
        when(jdbc.queryForObject(eq("SELECT COUNT(*) FROM runs"),          eq(Integer.class))).thenReturn(0);

        new VersionHistorySeeder(jdbc, init);

        verify(jdbc).update(
                "INSERT OR IGNORE INTO tower_version (version, type, summary) VALUES (?,?,?)",
                "1.0.0", "Major", "Initial");
    }

    @Test
    void noSeed_whenVersionTableAlreadyPopulated() {
        when(jdbc.queryForObject(eq("SELECT COUNT(*) FROM tower_version"), eq(Integer.class))).thenReturn(5);

        new VersionHistorySeeder(jdbc, init);

        verify(jdbc, never()).queryForObject(eq("SELECT COUNT(*) FROM runs"), eq(Integer.class));
        verify(jdbc, never()).update(anyString(), anyString(), anyString(), anyString());
    }
}
