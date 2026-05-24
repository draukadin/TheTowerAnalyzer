package com.pphi.tower.web;

import com.pphi.tower.model.UserProfile;
import com.pphi.tower.repository.UserProfileRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final UserProfileRepository repository;

    public UserProfileController(UserProfileRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public UserProfile getProfile() {
        return repository.load();
    }

    @PutMapping
    public UserProfile updateProfile(@RequestBody UserProfile profile) {
        repository.save(profile);
        return profile;
    }
}
