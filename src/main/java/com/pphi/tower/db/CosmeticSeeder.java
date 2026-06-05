package com.pphi.tower.db;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CosmeticSeeder {

    private final JdbcTemplate jdbc;

    public CosmeticSeeder(JdbcTemplate jdbc, DatabaseInitializer init) {
        this.jdbc = jdbc;
        seed();
    }

    private void seed() {
        Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM cosmetic_category", Integer.class);
        if (count != null && count > 0) return;
        seedCategories();
        seedEvents();
        seedMilestoneSkins();
        seedSongs();
        seedGuardians();
        seedMenus();
        seedProfileBanners();
    }

    private void seedCategories() {
        category("tower_skin",      "Event Tower",       0.004);
        category("background_skin", "Event Background",  0.008);
        category("milestone_skin",  "Tier Skins",        0.004);
        category("song",            "Songs",             0.006);
        category("guardian",        "Guardians",         0.006);
        category("menu",            "Menu",              0.006);
        category("profile_banner",  "Profile Banners",   0.006);
    }

    // Each row in the spreadsheet represents one event with a paired tower skin and background skin.
    private void seedEvents() {
        event("Interstellar",     2, "Star",            false, "Interstellar",   false);
        event("Volcano",          2, "Eye of the Lord", false, "Volcano",         false);
        event("Plasma Returns",   2, "Plasma Ball",     true,  "Plasma Field",    true);
        event("Honey",            2, "Bee",             true,  "Honeycomb",       true);
        event("Aurora",           2, "North Spirit",    true,  "Aurora",          true);
        event("Aliens",           2, "Alien",           true,  "Alien Ship",      true);
        event("Ocean Night",      2, "Water Droplet",   true,  "Ocean Night",     true);
        event("Cherry Blossom",   3, "Cherry Blossom",  true,  "Sakura",          true);
        event("Easter",           1, "Bunny",           true,  "Easter",          true);
        event("Retrowave",        2, "Neo Turbo",       true,  "Retrowave",       true);
        event("Prismatic Lines",  1, "Prisma",          false, "Prismatic Lines", false);
        event("Cobweb",           2, "Spider",          true,  "Cobweb",          true);
        event("Into the Matrix",  2, "Sentinel",        true,  "Matrix",          true);
        event("Viral Outbreak",   1, "Virus",           true,  "Virus Field",     true);
        event("Full Moon",        1, "Howling Wolf",    false, "Mountain Night",  false);
        event("Sands of Time",    1, "Hourglass",       false, "Sandstorm",       false);
        event("Autumn",           2, "Autumn Leaf",     true,  "Autumn Forest",   true);
        event("Halloween",        1, "Pumpkin",         false, "Haunted House",   false);
        event("Retro Arcade",     2, "Invader",         true,  "Arcade",          true);
        event("New Year",         2, "Toast Glass",     true,  "New Year",        true);
        event("Dark Strands",     1, "Dark Tower",      false, "Dark Strands",    false);
        event("Deep Blue Sea",    1, "Dive Helmet",     false, "Deep Sea",        false);
        event("Faster Than Light",1, "Starship",        false, "Hyper Space",     false);
        event("Invaders",         1, "Elite Tower",     true,  "Invasion",        true);
        event("Sunset Fishing",   1, "Fisherman",       true,  "Sunset River",    true);
        event("Into The Storm",   1, "Storm Eye",       true,  "Hurricane",       true);
        event("Rainfall",         1, "Umbrella",        false, "Rainfall",        false);
        event("Tower's Channel",  1, "Noise Tower",     true,  "TV Wall",         true);
        event("Abduction",        1, "Unlucky Cow",     false, "Abduction",       false);
        event("Snowstorm",        1, "Snowman",         true,  "Snowstorm",       true);
        event("Meowy Night",      1, "Black Cat",       false, "Forest of Cats",  false);
        event("Gravity",          1, "Black Hole",      false, "Event Horizon",   false);
        event("What Time Is It?", 1, "Pocket Watch",    true,  "Clock Tower",     true);
        event("Pi",               1, "Neon Pi",         true,  "Pi Disk",         true);
        event("Koi Pond",         1, "Frog",            true,  "Koi Pond",        true);
        event("Camping",          1, "Marshmallow",     true,  "Camping",         true);
        event("Cthulhu",          1, "Cthulhu",         true,  "Cthulhu",         true);
        event("Cyberpunk",        1, "Flying Car",      true,  "Cyberpunk",       true);
        event("Crystal Cave",     1, "Crystal",         true,  "Crystal Cave",    true);
        event("Amusement Park",   1, "Balloon",         true,  "Amusement Park",  true);
        event("Valentine",        1, "Heart",           true,  "Valentine",       true);
        event("Glitch",           1, "Glitch",          true,  "Glitch",          true);
        event("Neuron",           1, "Brain",           true,  "Neuron",          true);
        event("Guild Season 1",   1, "Crown",           false, "Throne Room",     false);
        event("Guild Season 2",   1, "Mech Warrior",    true,  "Mech World",      true);
        event("Guild Season 3",   1, "Dj",              true,  "Party",           true);
        event("Guild Season 4",   1, "Pixel Soldier",   true,  "Pixel Alien War", true);
        event("Guild Season 5",   1, "Restless Eye",    true,  "Crimson Horror",  true);
        event("Guild Season 6",   1, "Shining Star",    true,  "Cozy Cosmos",     true);
        event("Guild Season 7",   1, "Space Telescope", true,  "Supernova",       true);
        event("Guild Season 8",   1, "Bear",            true,  "Claw Machine",    true);
        event("Guild Season 9",   1, "Rabbit In Hat",   false, "Magician",        true);
    }

    private void seedMilestoneSkins() {
        milestoneSkin(1,  "Shuriken",      true,  "Tier 1",  "Free");
        milestoneSkin(2,  "Donut",         true,  "Tier 2",  "Pass 1");
        milestoneSkin(3,  "Yin-Yang",      true,  "Tier 3",  "Free");
        milestoneSkin(4,  "Smile",         true,  "Tier 4",  "Free");
        milestoneSkin(5,  "Butterfly",     false, "Tier 5",  "Pass 2");
        milestoneSkin(6,  "Sheep",         true,  "Tier 6",  "Free");
        milestoneSkin(7,  "Fried Egg",     true,  "Tier 7",  "Free");
        milestoneSkin(8,  "Mush-mush",     false, "Tier 8",  "Pass 3");
        milestoneSkin(9,  "Turtle",        true,  "Tier 9",  "Free");
        milestoneSkin(10, "Cheese",        true,  "Tier 10", "Free");
        milestoneSkin(11, "Cat",           false, "Tier 11", "Pass 4");
        milestoneSkin(12, "Skull",         true,  "Tier 12", "Free");
        milestoneSkin(13, "Creepy Clown",  false, "Tier 13", "Free");
        milestoneSkin(14, "Panda",         false, "Tier 14", "Pass 5");
        milestoneSkin(15, "Tech Tree",     false, "Tier 15", "Free");
        milestoneSkin(16, "Cactus",        false, "Tier 16", "Free");
        milestoneSkin(17, "Dragon",        false, "Tier 17", "Pass 6");
        milestoneSkin(18, "Rhino",         false, "Tier 18", "Free");
        milestoneSkin(19, "Atomic",        false, "Tier 19", "Free");
        milestoneSkin(20, "Cyber",         false, "Tier 20", "Pass 7");
        milestoneSkin(21, "Eclipse",       false, "Tier 21", "Free");
    }

    private void seedSongs() {
        song("Krisu - Oceans Sings",        true);
        song("Krisu - Hiding in Himalaya",  true);
        song("Krisu - Forest Bathing",      true);
    }

    private void seedGuardians() {
        guardian("Butter", false);
        guardian("Muse",   true);
        guardian("Finn",   true);
        guardian("Nyra",   true);
        guardian("Rolo",   true);
        guardian("Glenn",  true);
        guardian("Zepe",   true);
        guardian("Iris",   true);
        guardian("Silk",   true);
        guardian("Mickey", true);
        guardian("Gaia",   true);
        guardian("Arwing", true);
        guardian("Frank",  true);
        guardian("Earl",   true);
        guardian("Mei",    true);
        guardian("Shelly", false);
        guardian("Disco",  false);
    }

    private void seedMenus() {
        menu("Dark Being",      false);
        menu("Mech World",      true);
        menu("Party",           true);
        menu("Pixel Alien War", true);
        menu("Crimson Horror",  true);
        menu("Cosy Cosmos",     true);
        menu("Supernova",       true);
        menu("Claw Machine",    true);
        menu("Magician",        false);
    }

    private void seedProfileBanners() {
        profileBanner("Mech World",      true);
        profileBanner("Party",           true);
        profileBanner("Pixel Alien War", true);
        profileBanner("Crimson Horror",  true);
        profileBanner("Cosy Cosmos",     true);
        profileBanner("Supernova",       true);
        profileBanner("Claw Machine",    true);
        profileBanner("Magician",        true);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void category(String id, String displayName, double bonusPerItem) {
        jdbc.update("INSERT OR IGNORE INTO cosmetic_category (id, display_name, bonus_per_item) VALUES (?,?,?)",
                id, displayName, bonusPerItem);
    }

    private void event(String eventName, int reroll,
                       String towerSkin, boolean towerOwned,
                       String bgSkin,    boolean bgOwned) {
        jdbc.update("INSERT OR IGNORE INTO cosmetic_event (name, reroll_multiplier) VALUES (?,?)",
                eventName, reroll);
        Long eventId = jdbc.queryForObject("SELECT id FROM cosmetic_event WHERE name = ?", Long.class, eventName);
        item("tower_skin",      towerSkin, towerOwned, eventId, null, null, null);
        item("background_skin", bgSkin,    bgOwned,    eventId, null, null, null);
    }

    private void milestoneSkin(int number, String name, boolean owned, String tier, String unlock) {
        item("milestone_skin", name, owned, null, number, tier, unlock);
    }

    private void song(String name, boolean owned)          { item("song",           name, owned, null, null, null, null); }
    private void guardian(String name, boolean owned)      { item("guardian",       name, owned, null, null, null, null); }
    private void menu(String name, boolean owned)          { item("menu",           name, owned, null, null, null, null); }
    private void profileBanner(String name, boolean owned) { item("profile_banner", name, owned, null, null, null, null); }

    private void item(String categoryId, String name, boolean owned,
                      Long eventId, Integer milestoneNumber, String milestoneTier, String milestoneUnlock) {
        jdbc.update("""
                INSERT OR IGNORE INTO cosmetic_item
                    (category_id, name, owned, event_id, milestone_number, milestone_tier, milestone_unlock)
                VALUES (?,?,?,?,?,?,?)
                """,
                categoryId, name, owned ? 1 : 0,
                eventId, milestoneNumber, milestoneTier, milestoneUnlock);
    }
}
