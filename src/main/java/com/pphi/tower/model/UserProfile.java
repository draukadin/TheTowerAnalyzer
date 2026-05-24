package com.pphi.tower.model;

public class UserProfile {

    private String playerName = "";
    private String primaryGoal = "";
    private String labGoals = "";
    private String ultimateWeaponGoals = "";
    private String moduleGoals = "";
    private String workshopStrategy = "";
    private String tournamentFocus = "";
    private String runStrategy = "";
    private String notes = "";

    public UserProfile() {}

    public String getPlayerName() { return playerName; }
    public void setPlayerName(String v) { this.playerName = v; }

    public String getPrimaryGoal() { return primaryGoal; }
    public void setPrimaryGoal(String v) { this.primaryGoal = v; }

    public String getLabGoals() { return labGoals; }
    public void setLabGoals(String v) { this.labGoals = v; }

    public String getUltimateWeaponGoals() { return ultimateWeaponGoals; }
    public void setUltimateWeaponGoals(String v) { this.ultimateWeaponGoals = v; }

    public String getModuleGoals() { return moduleGoals; }
    public void setModuleGoals(String v) { this.moduleGoals = v; }

    public String getWorkshopStrategy() { return workshopStrategy; }
    public void setWorkshopStrategy(String v) { this.workshopStrategy = v; }

    public String getTournamentFocus() { return tournamentFocus; }
    public void setTournamentFocus(String v) { this.tournamentFocus = v; }

    public String getRunStrategy() { return runStrategy; }
    public void setRunStrategy(String v) { this.runStrategy = v; }

    public String getNotes() { return notes; }
    public void setNotes(String v) { this.notes = v; }
}
