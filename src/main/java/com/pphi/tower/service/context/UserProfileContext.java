package com.pphi.tower.service.context;

import com.pphi.tower.model.UserProfile;

public class UserProfileContext implements ChatContext {

    private final UserProfile profile;

    public UserProfileContext(UserProfile profile) {
        this.profile = profile;
    }

    @Override
    public String getLabel() {
        return "Player Profile";
    }

    @Override
    public String getContent() {
        StringBuilder sb = new StringBuilder();

        append(sb, "Player Name",            profile.getPlayerName());
        append(sb, "Primary Goal",           profile.getPrimaryGoal());
        append(sb, "Lab Goals",              profile.getLabGoals());
        append(sb, "Ultimate Weapon Goals",  profile.getUltimateWeaponGoals());
        append(sb, "Module Goals",           profile.getModuleGoals());
        append(sb, "Workshop Strategy",      profile.getWorkshopStrategy());
        append(sb, "Tournament Focus",       profile.getTournamentFocus());
        append(sb, "Run Strategy",           profile.getRunStrategy());
        append(sb, "Notes",                  profile.getNotes());

        return sb.isEmpty() ? "(no profile information provided)" : sb.toString();
    }

    private void append(StringBuilder sb, String label, String value) {
        if (value != null && !value.isBlank()) {
            sb.append(label).append(": ").append(value.strip()).append("\n");
        }
    }

    @Override
    public String toString() {
        return getContent();
    }
}
