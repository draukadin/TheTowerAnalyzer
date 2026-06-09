package com.pphi.tower.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "drive")
public class DriveProperties {

    private String oauthCredentialsFile;
    private String tokensDir;
    private String applicationName;
    private String battleReportsFolderId;
    private String backupFolderId;

    public String getOauthCredentialsFile() { return oauthCredentialsFile; }
    public void setOauthCredentialsFile(String v) { this.oauthCredentialsFile = v; }

    public String getTokensDir() { return tokensDir; }
    public void setTokensDir(String v) { this.tokensDir = v; }

    public String getApplicationName() { return applicationName; }
    public void setApplicationName(String v) { this.applicationName = v; }

    public String getBattleReportsFolderId() { return battleReportsFolderId; }
    public void setBattleReportsFolderId(String v) { this.battleReportsFolderId = v; }

    public String getBackupFolderId() { return backupFolderId; }
    public void setBackupFolderId(String v) { this.backupFolderId = v; }
}
