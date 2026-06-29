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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Manages bundled Claude skill files.
 *
 * Skills are shipped as classpath resources under {@code skills/<skill-name>/SKILL.md}
 * (with optional {@code references/} supporting files) and installed to the user's
 * {@code ~/.claude/skills/} directory so Claude Code picks them up automatically.
 * Claude Code discovers a skill by its {@code <skill-name>/SKILL.md} layout, so the
 * full folder structure below {@code skills/} is preserved on copy.
 *
 * All bundled skills use the {@code tower-} folder prefix, making it safe to overwrite
 * on upgrade without touching anything the user created.
 *
 * The Claude Desktop / claude.ai apps do not read {@code ~/.claude/skills/}; they load
 * skills uploaded through their own UI. For that path, {@link #exportSkillPackages(Path)}
 * produces an uploadable zip per skill.
 *
 * Future skill management (listing installed skills, adding user-defined skills,
 * toggling skills) should be added here and exposed via a dedicated controller.
 */
@Service
public class ClaudeSkillsService {

    private static final Logger log = LoggerFactory.getLogger(ClaudeSkillsService.class);

    private static final String CLASSPATH_PATTERN = "classpath:skills/**/*.md";

    /** Marker used to recover a resource's path relative to the {@code skills/} root. */
    private static final String SKILLS_MARKER = "/skills/";

    /**
     * Installs all bundled skill files to {@code ~/.claude/skills/}, preserving the
     * {@code <skill-name>/SKILL.md} folder structure (and any {@code references/} files).
     * Existing files at the same relative path are overwritten (upgrade-safe).
     * Non-fatal: logs errors but does not throw.
     *
     * @return number of skill files successfully installed
     */
    public int installBundledSkills() {
        Path skillsDir = resolveSkillsDir();
        if (skillsDir == null) return 0;

        List<Resource> resources = loadBundledSkills();
        int installed = 0;
        for (Resource resource : resources) {
            String relativePath = relativeSkillPath(resource);
            if (relativePath == null) continue;
            try {
                Path target = skillsDir.resolve(relativePath);
                Files.createDirectories(target.getParent());
                try (var in = resource.getInputStream()) {
                    Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
                }
                installed++;
                log.debug("Installed skill file: {}", relativePath);
            } catch (IOException e) {
                log.warn("Failed to install skill {}: {}", relativePath, e.getMessage());
            }
        }
        log.info("Installed {}/{} bundled skill files to {}", installed, resources.size(), skillsDir);
        return installed;
    }

    /**
     * Exports each bundled skill as a standalone {@code <skill-name>.zip} into
     * {@code targetDir}, ready to upload to the Claude Desktop / claude.ai
     * "Customize → Skills" UI (which does not read {@code ~/.claude/skills/}).
     *
     * Each zip's root is the skill folder (e.g. {@code tower-uw/SKILL.md},
     * {@code tower-uw/references/...}), matching Anthropic's required structure.
     * Existing zips of the same name are overwritten. Non-fatal: logs errors but
     * does not throw.
     *
     * @return the paths of the zip files successfully written
     */
    public List<Path> exportSkillPackages(Path targetDir) {
        try {
            Files.createDirectories(targetDir);
        } catch (IOException e) {
            log.warn("Could not create skill package directory {}: {}", targetDir, e.getMessage());
            return List.of();
        }

        // Group bundled resources by their top-level skill folder (e.g. "tower-uw").
        Map<String, List<Resource>> bySkill = new LinkedHashMap<>();
        for (Resource resource : loadBundledSkills()) {
            String rel = relativeSkillPath(resource);
            if (rel == null) continue;
            int slash = rel.indexOf('/');
            if (slash < 0) continue; // file sitting directly under skills/ — not a packaged skill
            bySkill.computeIfAbsent(rel.substring(0, slash), k -> new ArrayList<>()).add(resource);
        }

        List<Path> written = new ArrayList<>();
        for (Map.Entry<String, List<Resource>> entry : bySkill.entrySet()) {
            Path zipPath = targetDir.resolve(entry.getKey() + ".zip");
            try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipPath))) {
                for (Resource resource : entry.getValue()) {
                    String rel = relativeSkillPath(resource);
                    if (rel == null) continue;
                    zos.putNextEntry(new ZipEntry(rel)); // zip entry names use '/' — rel already does
                    try (var in = resource.getInputStream()) {
                        in.transferTo(zos);
                    }
                    zos.closeEntry();
                }
                written.add(zipPath);
                log.debug("Exported skill package: {}", zipPath);
            } catch (IOException e) {
                log.warn("Failed to export skill package {}: {}", zipPath, e.getMessage());
            }
        }
        log.info("Exported {} skill package(s) to {}", written.size(), targetDir);
        return written;
    }

    /**
     * Recovers a resource's path relative to the {@code skills/} classpath root
     * (e.g. {@code tower-uw/references/tta-black-hole.md}), working for both
     * filesystem (dev) and jar-packaged resources. Returns null if the resource
     * cannot be located under the skills root or appears to be a directory entry.
     */
    private String relativeSkillPath(Resource resource) {
        try {
            String url = resource.getURL().toString().replace('\\', '/');
            int idx = url.lastIndexOf(SKILLS_MARKER);
            if (idx < 0) {
                log.warn("Skill resource outside skills root: {}", url);
                return null;
            }
            String rel = url.substring(idx + SKILLS_MARKER.length());
            // Skip directory entries (jars may enumerate them); we only copy files.
            return rel.isBlank() || rel.endsWith("/") ? null : rel;
        } catch (IOException e) {
            log.warn("Could not resolve URL for skill resource: {}", e.getMessage());
            return null;
        }
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
     * Returns the names of skills currently installed in {@code ~/.claude/skills/}.
     * Each skill is a subdirectory containing a {@code SKILL.md}, so this lists the
     * skill folder names. Useful for a future skill management UI to show what is active.
     */
    public List<String> listInstalledSkillNames() {
        Path skillsDir = resolveSkillsDir();
        if (skillsDir == null || !Files.exists(skillsDir)) return List.of();
        try (var stream = Files.list(skillsDir)) {
            return stream.filter(Files::isDirectory)
                         .filter(p -> Files.exists(p.resolve("SKILL.md")))
                         .map(p -> p.getFileName().toString())
                         .sorted()
                         .toList();
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
