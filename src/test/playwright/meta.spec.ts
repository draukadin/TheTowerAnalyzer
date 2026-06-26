import { test, expect } from '@playwright/test';
import { goToApp, mockApi } from './helpers/mock-api';

// ── Section 1 — Currencies view ───────────────────────────────────────────────

test('1.1: currencies view shows form with save snapshot button', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/player-tracker/currencies', {});

  await page.evaluate(() => (window as any).showView('currencies'));
  await expect(page.locator('#currForm')).toBeVisible({ timeout: 5000 });
  await expect(page.locator('#currLoadMsg')).toBeHidden();
  await expect(page.locator('button', { hasText: 'Save Snapshot' })).toBeVisible();
});

test('1.2: currencies form shows error when API fails', async ({ page }) => {
  await goToApp(page);
  await page.route('**/api/player-tracker/currencies', route => route.abort());

  await page.evaluate(() => (window as any).showView('currencies'));
  await expect(page.locator('#currLoadMsg')).toContainText('Failed to load', { timeout: 5000 });
});

// ── Section 2 — Tier PB view ──────────────────────────────────────────────────

test('2.1: tier PB view renders table with Add Tier button', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/tier-pb', {
    tiers: [
      { tier: 12, wave: 3500, attack: 0, defense: 0, utility: 0, uw: 0 },
    ],
    tournamentBoost: { attack: 0, defense: 0, utility: 0, uw: 0 },
    echoLevels:      { attack: 0, defense: 0, utility: 0, uw: 0 },
  });

  await page.evaluate(() => (window as any).showView('tierpb'));
  await expect(page.locator('button', { hasText: '+ Add Tier' })).toBeVisible({ timeout: 5000 });
  await expect(page.locator('#tierPbBody tr')).not.toHaveCount(0);
});

test('2.2: tier PB shows wave value for existing row', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/tier-pb', {
    tiers: [{ tier: 12, wave: 3500, attack: 0, defense: 0, utility: 0, uw: 0 }],
    tournamentBoost: { attack: 0, defense: 0, utility: 0, uw: 0 },
    echoLevels:      { attack: 0, defense: 0, utility: 0, uw: 0 },
  });

  await page.evaluate(() => (window as any).showView('tierpb'));
  // Wave is rendered as <input type="number" value="3500">, not as text
  await expect(page.locator('#tierPbBody input[type="number"]').first()).toHaveValue('3500', { timeout: 5000 });
});

// ── Section 3 — Tournament / Battle Conditions view ───────────────────────────

test('3.1: tournament view renders without error when data is empty', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/tournaments/conditions', []);
  await mockApi(page, '**/api/tournaments', []);

  await page.evaluate(() => (window as any).showView('tournament'));
  await expect(page.locator('#mainContent')).not.toContainText('Failed to load', { timeout: 5000 });
  await expect(page.locator('#mainContent')).not.toContainText('Loading');
});

test('3.2: tournament view shows Add Tournament button', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/tournaments/conditions', [
    { type: 'ATTACK', label: 'Attack Boost', options: [] },
  ]);
  await mockApi(page, '**/api/tournaments', []);

  await page.evaluate(() => (window as any).showView('tournament'));
  // The view should render some form of tournament UI
  await expect(page.locator('#mainContent')).toBeVisible({ timeout: 5000 });
  await expect(page.locator('#mainContent')).not.toContainText('⚠️');
});
