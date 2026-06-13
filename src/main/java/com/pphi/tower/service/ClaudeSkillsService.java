package com.pphi.tower.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages bundled Claude skill files.
 *
 * Skills are shipped as classpath resources under {@code skills/*.md} and installed
 * to the user's {@code ~/.claude/skills/} directory so Claude Code picks them up
 * automatically. The {@code tta-} filename prefix is used for all bundled skills,
 * making it safe to overwrite on upgrade without touching anything the user created.
 *
 * Future skill management (listing installed skills, adding user-defined skills,
 * toggling skills) should be added here and exposed via a dedicated controller.
 */
@Service
public class ClaudeSkillsService {

    private static final Logger log = LoggerFactory.getLogger(ClaudeSkillsService.class);

    private static final String CLASSPATH_PATTERN = "classpath:skills/*.md";

    /**
     * Installs all bundled skill files to {@code ~/.claude/skills/}.
     * Existing files with the same name are overwritten (upgrade-safe).
     * Non-fatal: logs errors but does not throw.
     *
     * @return number of skills successfully installed
     */
    public int installBundledSkills() {
        Path skillsDir = resolveSkillsDir();
        if (skillsDir == null) return 0;

        List<Resource> resources = loadBundledSkills();
        int installed = 0;
        for (Resource resource : resources) {
            try {
                String filename = resource.getFilename();
                if (filename == null) continue;
                Files.copy(resource.getInputStream(),
                        skillsDir.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
                installed++;
                log.debug("Installed skill: {}", filename);
            } catch (IOException e) {
                log.warn("Failed to install skill {}: {}", resource.getFilename(), e.getMessage());
            }
        }
        log.info("Installed {}/{} bundled skills to {}", installed, resources.size(), skillsDir);
        return installed;
    }

    /**
     * Returns all skill resources bundled with the application.
     * Useful for a future skill management UI to display available skills.
     */
    public List<Resource> loadBundledSkills() {
        try {
            return List.of(new PathMatchingResourcePatternResolver().getResources(CLASSPATH_PATTERN));
        } catch (IOException e) {
            log.warn("Could not enumerate bundled skills: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Returns the names of skill files currently installed in {@code ~/.claude/skills/}.
     * Useful for a future skill management UI to show what is active.
     */
    public List<String> listInstalledSkillNames() {
        Path skillsDir = resolveSkillsDir();
        if (skillsDir == null || !Files.exists(skillsDir)) return List.of();
        try {
            List<String> names = new ArrayList<>();
            try (var stream = Files.list(skillsDir)) {
                stream.filter(p -> p.toString().endsWith(".md"))
                      .map(p -> p.getFileName().toString())
                      .forEach(names::add);
            }
            return names;
        } catch (IOException e) {
            log.warn("Could not list installed skills: {}", e.getMessage());
            return List.of();
        }
    }

    /**
     * Resolves and creates {@code ~/.claude/skills/}, or returns null if the home
     * directory cannot be determined.
     */
    private Path resolveSkillsDir() {
        String home = System.getProperty("user.home");
        if (home == null || home.isBlank()) {
            log.warn("user.home not set — cannot install skills");
            return null;
        }
        Path dir = Path.of(home, ".claude", "skills");
        try {
            Files.createDirectories(dir);
            return dir;
        } catch (IOException e) {
            log.warn("Could not create skills directory {}: {}", dir, e.getMessage());
            return null;
        }
    }
}
