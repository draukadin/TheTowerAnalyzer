import { test, expect } from '@playwright/test';
import { goToApp, mockApi } from './helpers/mock-api';

const MODULE = {
  id: 1,
  code: 'SpA',
  name: 'Speed of Attack',
  type: 'Cannon',
  rarity: 'Epic',
  owned: true,
  level: 100,
  stars: 0,
  abilityValues: { Epic: '5%', Legendary: '7.5%', Mythic: '10%', Ancestral: '15%' },
  effectTemplate: 'Attack speed +5%',
  substats: [],
  copies: [],
  presets: [],
  shatteredEpics: 0,
};

const SUBSTATS = {
  Cannon: [{ key: 'crit_chance', label: 'Crit Chance', minRarity: 'Common' }],
  Generator: [], Armor: [], Core: [],
};

const BANS = [
  { moduleType: 'Cannon',    maxBans: 2, banned: [] },
  { moduleType: 'Generator', maxBans: 0, banned: [] },
  { moduleType: 'Armor',     maxBans: 0, banned: [] },
  { moduleType: 'Core',      maxBans: 0, banned: [] },
];

// ── Section 1 — Module state PUT ─────────────────────────────────────────────

test('1.1: changing module level fires PUT /modules/:id/state', async ({ page }) => {
  let putBody: any = null;
  await goToApp(page);
  await mockApi(page, '**/api/modules/substats', SUBSTATS);
  await mockApi(page, '**/api/modules', [MODULE]);
  await page.route('**/api/modules/1/state', async route => {
    if (route.request().method() === 'PUT') {
      putBody = JSON.parse(route.request().postData() ?? '{}');
      await route.fulfill({ status: 200, body: '' });
    } else {
      await route.continue();
    }
  });

  await page.evaluate(() => (window as any).showView('modules'));
  await expect(page.locator('.mod-card')).toBeVisible({ timeout: 5000 });

  // The level input in mod-controls has onchange="modSaveState(...)"
  const levelInput = page.locator('.mod-input[type="number"]').first();
  await levelInput.fill('120');
  await levelInput.press('Tab');

  await expect.poll(() => putBody, { timeout: 5000 }).not.toBeNull();
  expect(putBody.level).toBe(120);
  expect(putBody.owned).toBe(true);
});

// ── Section 2 — Effect Bans ───────────────────────────────────────────────────

test('2.1: effect bans view renders all four type cards', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/modules/substats', SUBSTATS);
  await mockApi(page, '**/api/modules/bans', BANS);

  await page.evaluate(() => (window as any).showView('effectbans'));
  await expect(page.locator('.eb-card')).toHaveCount(4, { timeout: 5000 });
});

test('2.2: clicking Ban fires PUT /modules/bans/:type/:key', async ({ page }) => {
  let banUrl = '';
  await goToApp(page);
  await mockApi(page, '**/api/modules/substats', SUBSTATS);
  await mockApi(page, '**/api/modules/bans', BANS);
  await page.route('**/api/modules/bans/**', async route => {
    if (route.request().method() === 'PUT') {
      banUrl = route.request().url();
      await route.fulfill({ status: 200, body: '' });
    } else {
      // Let GET pass through to the mock
      await route.fulfill({ contentType: 'application/json', body: JSON.stringify(BANS) });
    }
  });

  await page.evaluate(() => (window as any).showView('effectbans'));
  await expect(page.locator('.eb-card')).toHaveCount(4, { timeout: 5000 });

  // Select the Crit Chance option in the Cannon card's dropdown
  await page.locator('#eb-sel-Cannon').selectOption('crit_chance');
  await page.locator('.eb-card').first().locator('.eb-add-btn').click();

  await expect.poll(() => banUrl, { timeout: 5000 }).toContain('/bans/Cannon/crit_chance');
});

// ── Section 3 — Shard Rate ────────────────────────────────────────────────────

test('3.1: shard rate view loads analysis data and renders stat cards', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/modules', []);
  await mockApi(page, '**/api/analysis/shards**', {
    windowDays: 30,
    cannonRate: { perHour: 5.2, daysToGoal: 120 },
    armorRate:  { perHour: 3.1, daysToGoal: 200 },
    generatorRate: { perHour: 2.0, daysToGoal: 300 },
    coreRate: { perHour: 1.5, daysToGoal: 400 },
  });

  await page.evaluate(() => (window as any).showView('shards'));
  await expect(page.locator('#shardsPanel')).not.toContainText('Loading', { timeout: 5000 });
});
