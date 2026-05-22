package com.pphi.tower.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "drive")
public class DriveProperties {

    private String credentialsFile;
    private String tokensDir;
    private String applicationName;
    private String battleReportsFolderId;

    public String getCredentialsFile() { return credentialsFile; }
    public void setCredentialsFile(String v) { this.credentialsFile = v; }

    public String getTokensDir() { return tokensDir; }
    public void setTokensDir(String v) { this.tokensDir = v; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String v) { this.applicationName = v; }

    public String getBattleReportsFolderId() { return battleReportsFolderId; }
    public void setBattleReportsFolderId(String v) { this.battleReportsFolderId = v; }
}
