package com.pphi.tower.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "tower")
public class AppConfig {

    private String battleReportsFolderId;
    private Cells cells = new Cells();

    public static class Cells {
        private int windowDaysDefault = 30;
        private int windowDaysMin = 3;
        private int windowDaysMax = 90;

        public int getWindowDaysDefault() { return windowDaysDefault; }
        public void setWindowDaysDefault(int v) { this.windowDaysDefault = v; }
        public int getWindowDaysMin() { return windowDaysMin; }
        public void setWindowDaysMin(int v) { this.windowDaysMin = v; }
        public int getWindowDaysMax() { return windowDaysMax; }
        public void setWindowDaysMax(int v) { this.windowDaysMax = v; }
    }

    public String getBattleReportsFolderId() { return battleReportsFolderId; }
    public void setBattleReportsFolderId(String v) { this.battleReportsFolderId = v; }

    public Cells getCells() { return cells; }
    public void setCells(Cells v) { this.cells = v; }
}
