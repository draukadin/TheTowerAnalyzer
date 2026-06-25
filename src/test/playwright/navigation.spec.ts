import { test, expect } from '@playwright/test';
import { goToApp, mockApi } from './helpers/mock-api';

test.beforeEach(async ({ page }) => {
  // Catch-all so view renders don't 500 when showView is called programmatically
  await page.route('**/api/**', route =>
    route.fulfill({ contentType: 'application/json', body: '[]' }));
  await goToApp(page);
});

// ── Section 1 — Nav group toggle ──────────────────────────────────────────────

test('1.1: clicking a nav group button opens its dropdown', async ({ page }) => {
  await page.locator('#grp-labs').click();
  const labsGroup = page.locator('.nav-group').filter({ has: page.locator('#grp-labs') });
  await expect(labsGroup).toHaveClass(/open/);
});

test('1.2: clicking another group closes the first', async ({ page }) => {
  await page.locator('#grp-labs').click();
  await page.locator('#grp-upgrades').click();
  const labsGroup    = page.locator('.nav-group').filter({ has: page.locator('#grp-labs') });
  const upgradesGroup = page.locator('.nav-group').filter({ has: page.locator('#grp-upgrades') });
  await expect(labsGroup).not.toHaveClass(/open/);
  await expect(upgradesGroup).toHaveClass(/open/);
});

test('1.3: clicking outside closes all dropdowns', async ({ page }) => {
  await page.locator('#grp-labs').click();
  await page.locator('body').click({ position: { x: 10, y: 400 } });
  await expect(page.locator('.nav-group.open')).toHaveCount(0);
});

// ── Section 2 — Active-state CSS ──────────────────────────────────────────────

test('2.1: showView sets group-active on the owning group button', async ({ page }) => {
  await page.evaluate(() => (window as any).showView('workshop'));
  await expect(page.locator('#grp-upgrades')).toHaveClass(/group-active/);
  await expect(page.locator('#grp-labs')).not.toHaveClass(/group-active/);
});

test('2.2: showView sets active on the matching nav button', async ({ page }) => {
  await page.evaluate(() => (window as any).showView('workshop'));
  const workshopBtn = page.locator('.nav-btn', { hasText: 'Workshop' });
  await expect(workshopBtn).toHaveClass(/active/);
  const labsBtn = page.locator('.nav-btn', { hasText: 'Labs' }).first();
  await expect(labsBtn).not.toHaveClass(/active/);
});

test('2.3: switching views moves group-active to the new group', async ({ page }) => {
  await page.evaluate(() => (window as any).showView('workshop'));
  await page.evaluate(() => (window as any).showView('relics'));
  await expect(page.locator('#grp-collectibles')).toHaveClass(/group-active/);
  await expect(page.locator('#grp-upgrades')).not.toHaveClass(/group-active/);
});

// ── Section 3 — Correct section shown ────────────────────────────────────────

test('3.1: mainContent shows empty-state while no report is selected', async ({ page }) => {
  await expect(page.locator('#mainContent')).toContainText('Select a report');
});

test('3.2: sidebar shows No reports found when list is empty', async ({ page }) => {
  await expect(page.locator('#reportList')).toContainText('No reports found');
});
