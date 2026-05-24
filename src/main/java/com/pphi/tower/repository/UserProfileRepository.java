package com.pphi.tower.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pphi.tower.model.UserProfile;
import org.springframework.stereotype.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Repository
public class UserProfileRepository {

    private final Path profilePath;
    private final ObjectMapper objectMapper;

    public UserProfileRepository(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        String appData = System.getenv("APPDATA");
        this.profilePath = Paths.get(appData, "TheTowerAnalyzer", "user-profile.json");
    }

    public UserProfile load() {
        if (!Files.exists(profilePath)) return new UserProfile();
        try {
            return objectMapper.readValue(profilePath.toFile(), UserProfile.class);
        } catch (Exception e) {
            return new UserProfile();
        }
    }

    public void save(UserProfile profile) {
        try {
            Files.createDirectories(profilePath.getParent());
            objectMapper.writeValue(profilePath.toFile(), profile);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user profile", e);
        }
    }
}
