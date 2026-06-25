import { test, expect } from '@playwright/test';
import { goToApp, mockApi } from './helpers/mock-api';

const REPORT_SUMMARY = {
  id: 'run-1',
  runNumber: 42,
  tier: 12,
  wave: 3500,
  runType: 'Milestone',
  battleDate: '2024-01-15',
  killedBy: 'Ranged',
  towerEra: 'T:XII',
  realTimeSeconds: 7200,
  gameTimeSeconds: 14400,
};

const REPORT_PAYLOAD = {
  sectionMap: {
    BATTLE_REPORT: { coinsEarned: { raw: 1e15, display: '1Qa' }, cellsEarned: { raw: 500000, display: '500K' }, towerEra: 'T:XII' },
    DAMAGE: { damageDealt: { raw: 1e18, display: '1Qi' } },
    DAMAGE_BLOCKED: {}, DAMAGE_TAKEN: {}, COINS: {}, CASH: {}, CURRENCIES: {},
    RECORDS: {}, UTILITY: {}, COUNTS: {}, TOTAL_ENEMIES: {},
    ENEMIES_DESTROYED_BY: {}, KILLED_WITH_EFFECT_ACTIVE: {},
    BONUS_HEALTH_GAINED: {}, HEALTH_REGENERATED: {},
  },
};

const DIAGNOSIS = {
  primaryFailure: 'VAMPIRE_SURGE',
  confidence: 'HIGH',
  explanation: 'Vampire surge overwhelmed defenses.',
  swarmKillShare: 0.45, heavyKillShare: 0.20, blockEfficiency: 0.80,
  vampireDensity: 0.35, rangedDensity: 0.15, lifeStealRaw: 1e12,
  observations: [],
};

// ── Section 1 — Sidebar ───────────────────────────────────────────────────────

test('1.1: sidebar renders report item when reports loaded', async ({ page }) => {
  await mockApi(page, '**/api/reports', [REPORT_SUMMARY]);
  await mockApi(page, '**/api/reports/duplicates', []);
  await mockApi(page, '**/api/setup/status', { step: 'complete' });
  await mockApi(page, '**/api/auth/status', { status: 'authenticated' });
  await mockApi(page, '**/api/modules/substats', {});
  await page.goto('/');

  await expect(page.locator('#reportList .report-item')).toHaveCount(1);
  await expect(page.locator('#reportList')).toContainText('#42');
  await expect(page.locator('#reportList')).toContainText('T12');
});

// ── Section 2 — Fetch button ──────────────────────────────────────────────────

test('2.1: fetch button fires POST /reports/fetch', async ({ page }) => {
  let fetchCalled = false;
  await goToApp(page);
  await page.route('**/api/reports/fetch', async route => {
    if (route.request().method() === 'POST') fetchCalled = true;
    await route.fulfill({ contentType: 'application/json', body: JSON.stringify({ processed: 0 }) });
  });
  await page.route('**/api/reports', route =>
    route.fulfill({ contentType: 'application/json', body: '[]' }));

  await page.locator('#fetchBtn').click();
  await expect(page.locator('#fetchBtn')).toContainText('No new reports', { timeout: 5000 });
  expect(fetchCalled).toBe(true);
});

test('2.2: fetch button shows count when reports returned', async ({ page }) => {
  await goToApp(page);
  await page.route('**/api/reports/fetch', route =>
    route.fulfill({ contentType: 'application/json', body: JSON.stringify({ processed: 3 }) }));
  await page.route('**/api/reports', route =>
    route.fulfill({ contentType: 'application/json', body: JSON.stringify([REPORT_SUMMARY]) }));

  await page.locator('#fetchBtn').click();
  await expect(page.locator('#fetchBtn')).toContainText('Fetched 3 reports', { timeout: 5000 });
});

// ── Section 3 — Report tabs ───────────────────────────────────────────────────

test('3.1: clicking a report item shows stats/diagnosis/compare tabs', async ({ page }) => {
  await mockApi(page, '**/api/reports', [REPORT_SUMMARY]);
  await mockApi(page, '**/api/reports/duplicates', []);
  await mockApi(page, `**/api/reports/${REPORT_SUMMARY.id}`, REPORT_PAYLOAD);
  await mockApi(page, '**/api/setup/status', { step: 'complete' });
  await mockApi(page, '**/api/auth/status', { status: 'authenticated' });
  await mockApi(page, '**/api/modules/substats', {});
  await page.goto('/');

  await page.locator('#reportList .report-item').click();
  await expect(page.locator('.tab[data-tab="stats"]')).toBeVisible();
  await expect(page.locator('.tab[data-tab="diagnosis"]')).toBeVisible();
  await expect(page.locator('.tab[data-tab="compare"]')).toBeVisible();
});

test('3.2: switching to diagnosis tab fires GET /diagnosis endpoint', async ({ page }) => {
  let diagnosisCalled = false;
  await mockApi(page, '**/api/reports', [REPORT_SUMMARY]);
  await mockApi(page, '**/api/reports/duplicates', []);
  await mockApi(page, `**/api/reports/${REPORT_SUMMARY.id}`, REPORT_PAYLOAD);
  await mockApi(page, '**/api/setup/status', { step: 'complete' });
  await mockApi(page, '**/api/auth/status', { status: 'authenticated' });
  await mockApi(page, '**/api/modules/substats', {});
  await page.route(`**/api/reports/${REPORT_SUMMARY.id}/diagnosis`, async route => {
    diagnosisCalled = true;
    await route.fulfill({ contentType: 'application/json', body: JSON.stringify(DIAGNOSIS) });
  });
  await page.goto('/');

  await page.locator('#reportList .report-item').click();
  await page.locator('.tab[data-tab="diagnosis"]').click();
  await expect(page.locator('.diag-card')).toBeVisible({ timeout: 5000 });
  await expect(page.locator('.diag-title')).toContainText('VAMPIRE SURGE');
  expect(diagnosisCalled).toBe(true);
});

test('3.3: switching to compare tab shows compare select', async ({ page }) => {
  await mockApi(page, '**/api/reports', [REPORT_SUMMARY]);
  await mockApi(page, '**/api/reports/duplicates', []);
  await mockApi(page, `**/api/reports/${REPORT_SUMMARY.id}`, REPORT_PAYLOAD);
  await mockApi(page, '**/api/setup/status', { step: 'complete' });
  await mockApi(page, '**/api/auth/status', { status: 'authenticated' });
  await mockApi(page, '**/api/modules/substats', {});
  await page.goto('/');

  await page.locator('#reportList .report-item').click();
  await page.locator('.tab[data-tab="compare"]').click();
  await expect(page.locator('.compare-controls')).toBeVisible();
});

// ── Section 4 — Delete modal ──────────────────────────────────────────────────

test('4.1: delete button opens delete modal', async ({ page }) => {
  await mockApi(page, '**/api/reports', [REPORT_SUMMARY]);
  await mockApi(page, '**/api/reports/duplicates', []);
  await mockApi(page, '**/api/setup/status', { step: 'complete' });
  await mockApi(page, '**/api/auth/status', { status: 'authenticated' });
  await mockApi(page, '**/api/modules/substats', {});
  await page.goto('/');

  await page.locator('.report-delete-btn').click();
  await expect(page.locator('#deleteModal')).toBeVisible();
  await expect(page.locator('#deleteModalTitle')).toContainText('Delete Report #42');
});

test('4.2: confirm delete fires DELETE with deleteSourceFile=true', async ({ page }) => {
  let deleteUrl = '';
  await mockApi(page, '**/api/reports', [REPORT_SUMMARY]);
  await mockApi(page, '**/api/reports/duplicates', []);
  await mockApi(page, '**/api/setup/status', { step: 'complete' });
  await mockApi(page, '**/api/auth/status', { status: 'authenticated' });
  await mockApi(page, '**/api/modules/substats', {});
  await page.route(`**/api/reports/${REPORT_SUMMARY.id}**`, async route => {
    if (route.request().method() === 'DELETE') {
      deleteUrl = route.request().url();
      await route.fulfill({ status: 204, body: '' });
    } else {
      await route.continue();
    }
  });
  await page.route('**/api/reports/duplicates', route =>
    route.fulfill({ contentType: 'application/json', body: '[]' }));
  await page.goto('/');

  await page.locator('.report-delete-btn').click();
  await page.locator('.btn-danger').click();
  await expect(page.locator('#deleteModal')).toBeHidden({ timeout: 3000 });
  expect(deleteUrl).toContain('deleteSourceFile=true');
});
