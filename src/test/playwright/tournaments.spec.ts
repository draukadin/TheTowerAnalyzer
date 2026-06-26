import { test, expect } from '@playwright/test';
import { goToApp, mockApi } from './helpers/mock-api';

const CONDITIONS = [
  { id: 1, name: 'Orb Resistance', acronym: 'OR',  category: 'HEAT' },
  { id: 2, name: 'More Bosses',    acronym: 'MB',  category: 'OVERHEAT' },
  { id: 3, name: 'Health Decay',   acronym: 'HLD', category: 'OVERHEAT' },
];

const TOURNAMENT = {
  id: 1,
  date: '2026-06-24',
  league: 'LEGENDS',
  conditions: [CONDITIONS[0], CONDITIONS[1]],
};

async function gotoTournamentView(page: any) {
  await goToApp(page);
  await mockApi(page, '**/api/tournaments/conditions', CONDITIONS);
  await mockApi(page, '**/api/tournaments', [TOURNAMENT]);
  await page.evaluate(() => (window as any).showView('tournament'));
  // Wait for tabs to appear
  await expect(page.locator('.tourn-tab').first()).toBeVisible({ timeout: 5000 });
}

// ── Section 1 — Navigation and tabs ──────────────────────────────────────────

test('1.1: tournament view renders three tabs', async ({ page }) => {
  await gotoTournamentView(page);
  await expect(page.locator('.tourn-tab')).toHaveCount(3);
});

test('1.2: History tab is active by default', async ({ page }) => {
  await gotoTournamentView(page);
  await expect(page.locator('.tourn-tab.active')).toContainText('History');
});

test('1.3: History tab shows existing tournament entry', async ({ page }) => {
  await gotoTournamentView(page);
  await expect(page.locator('#tournamentBody')).toContainText('LEGENDS', { timeout: 5000 });
});

test('1.4: Search tab is labelled Search', async ({ page }) => {
  await gotoTournamentView(page);
  await expect(page.locator('.tourn-tab').nth(1)).toContainText('Search');
});

test('1.5: Import tab is labelled Import', async ({ page }) => {
  await gotoTournamentView(page);
  await expect(page.locator('.tourn-tab').nth(2)).toContainText('Import');
});

// ── Section 2 — History tab buttons ──────────────────────────────────────────

test('2.1: Add Tournament Battle Conditions button is visible in History tab', async ({ page }) => {
  await gotoTournamentView(page);
  await expect(page.locator('button', { hasText: '+ Add Tournament Battle Conditions' })).toBeVisible();
});

test('2.2: Fetch Battle Conditions button is visible next to Add button', async ({ page }) => {
  await gotoTournamentView(page);
  await expect(page.locator('button', { hasText: 'Fetch Battle Conditions' })).toBeVisible();
});

// ── Section 3 — Import tab ───────────────────────────────────────────────────

test('3.1: clicking Import tab switches to import view', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('.tourn-tab', { hasText: 'Import' }).click();
  await expect(page.locator('#tcsvDate')).toBeVisible({ timeout: 5000 });
});

test('3.2: Import tab date picker is pre-filled with a Wed or Sat', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('.tourn-tab', { hasText: 'Import' }).click();

  const value = await page.locator('#tcsvDate').inputValue();
  const day = new Date(value + 'T00:00:00Z').getUTCDay();
  expect([3, 6]).toContain(day); // 3=Wednesday, 6=Saturday
});

test('3.3: Import tab has a file input for CSV', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('.tourn-tab', { hasText: 'Import' }).click();
  await expect(page.locator('#tcsvFile')).toBeVisible({ timeout: 5000 });
});

test('3.4: Import CSV button is present', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('.tourn-tab', { hasText: 'Import' }).click();
  await expect(page.locator('#tcsvImportBtn')).toBeVisible({ timeout: 5000 });
});

test('3.5: Sync from S3 button is present', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('.tourn-tab', { hasText: 'Import' }).click();
  await expect(page.locator('#tcsvSyncBtn')).toBeVisible({ timeout: 5000 });
});

// ── Section 4 — Import tab client-side validation ────────────────────────────

test('4.1: clicking Import CSV without selecting a file shows error', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('.tourn-tab', { hasText: 'Import' }).click();
  await expect(page.locator('#tcsvImportBtn')).toBeVisible({ timeout: 5000 });

  await page.locator('#tcsvImportBtn').click();
  // No file selected → "Choose a CSV file." message
  await expect(page.locator('#tcsvStatus')).toContainText('Choose a CSV file', { timeout: 3000 });
});

test('4.2: setting a non-Wed/Sat date shows validation error', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('.tourn-tab', { hasText: 'Import' }).click();
  await expect(page.locator('#tcsvDate')).toBeVisible({ timeout: 5000 });

  // 2026-06-22 is a Monday
  await page.locator('#tcsvDate').fill('2026-06-22');
  await page.locator('#tcsvImportBtn').click();

  await expect(page.locator('#tcsvStatus')).toContainText('Wednesday or Saturday', { timeout: 3000 });
});

// ── Section 5 — Sync from S3 ─────────────────────────────────────────────────

test('5.1: Sync from S3 button calls POST /tournaments/sync', async ({ page }) => {
  let syncCalled = false;
  await goToApp(page);
  await mockApi(page, '**/api/tournaments/conditions', CONDITIONS);
  await mockApi(page, '**/api/tournaments', [TOURNAMENT]);
  await page.route('**/api/tournaments/sync', async route => {
    if (route.request().method() === 'POST') {
      syncCalled = true;
      await route.fulfill({ status: 200, contentType: 'application/json', body: '[]' });
    } else {
      await route.continue();
    }
  });

  await page.evaluate(() => (window as any).showView('tournament'));
  await page.locator('.tourn-tab', { hasText: 'Import' }).click();
  await expect(page.locator('#tcsvSyncBtn')).toBeVisible({ timeout: 5000 });

  await page.locator('#tcsvSyncBtn').click();

  await expect.poll(() => syncCalled, { timeout: 5000 }).toBe(true);
});

test('5.2: Sync from S3 with empty result shows nothing synced', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/tournaments/conditions', CONDITIONS);
  await mockApi(page, '**/api/tournaments', [TOURNAMENT]);
  await mockApi(page, '**/api/tournaments/sync', []);

  await page.evaluate(() => (window as any).showView('tournament'));
  await page.locator('.tourn-tab', { hasText: 'Import' }).click();
  await expect(page.locator('#tcsvSyncBtn')).toBeVisible({ timeout: 5000 });

  await page.locator('#tcsvSyncBtn').click();

  await expect(page.locator('#tcsvSyncStatus')).toContainText('Nothing', { timeout: 5000 });
});

// ── Section 6 — Load from S3 modal ───────────────────────────────────────────

test('6.1: clicking Fetch Battle Conditions opens the S3 modal', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('button', { hasText: 'Fetch Battle Conditions' }).click();
  await expect(page.locator('#tournamentS3Modal')).toBeVisible({ timeout: 5000 });
});

test('6.2: S3 modal has a date input', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('button', { hasText: 'Fetch Battle Conditions' }).click();
  await expect(page.locator('#ts3Date')).toBeVisible();
});

test('6.3: S3 modal date is pre-filled with a Wed or Sat', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('button', { hasText: 'Fetch Battle Conditions' }).click();

  const value = await page.locator('#ts3Date').inputValue();
  const day = new Date(value + 'T00:00:00Z').getUTCDay();
  expect([3, 6]).toContain(day);
});

test('6.4: S3 modal has a Load from S3 button', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('button', { hasText: 'Fetch Battle Conditions' }).click();
  await expect(page.locator('#ts3LoadBtn')).toBeVisible();
});

test('6.5: S3 modal has a Cancel button that closes the modal', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('button', { hasText: 'Fetch Battle Conditions' }).click();
  await expect(page.locator('#tournamentS3Modal')).toBeVisible();

  await page.locator('#tournamentS3Modal button', { hasText: 'Cancel' }).click();
  await expect(page.locator('#tournamentS3Modal')).toBeHidden({ timeout: 3000 });
});

test('6.6: clicking overlay outside S3 modal closes it', async ({ page }) => {
  await gotoTournamentView(page);
  await page.locator('button', { hasText: 'Fetch Battle Conditions' }).click();
  await expect(page.locator('#tournamentS3Modal')).toBeVisible();

  // Click the overlay backdrop (the modal-overlay div itself, not the inner modal)
  await page.locator('#tournamentS3Modal').click({ position: { x: 5, y: 5 } });
  await expect(page.locator('#tournamentS3Modal')).toBeHidden({ timeout: 3000 });
});

test('6.7: S3 modal 404 response shows not-found message with link', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/tournaments/conditions', CONDITIONS);
  await mockApi(page, '**/api/tournaments', [TOURNAMENT]);
  await mockApi(page, '**/api/tournaments/fetch-from-s3**', { message: 'Not found' }, 404);

  await page.evaluate(() => (window as any).showView('tournament'));
  await page.locator('button', { hasText: 'Fetch Battle Conditions' }).click({ timeout: 5000 });
  await page.locator('#ts3LoadBtn').click();

  await expect(page.locator('#ts3Status')).toContainText('No data found', { timeout: 5000 });
});

test('6.8: S3 modal success closes modal', async ({ page }) => {
  const importResult = {
    date: '2026-06-24',
    conditionsPerLeague: { LEGENDS: 2 },
    warnings: [],
  };
  await goToApp(page);
  await mockApi(page, '**/api/tournaments/conditions', CONDITIONS);
  await mockApi(page, '**/api/tournaments', [TOURNAMENT]);
  await mockApi(page, '**/api/tournaments/fetch-from-s3**', importResult);

  await page.evaluate(() => (window as any).showView('tournament'));
  await page.locator('button', { hasText: 'Fetch Battle Conditions' }).click({ timeout: 5000 });
  await page.locator('#ts3Date').fill('2026-06-24');
  await page.locator('#ts3LoadBtn').click();

  await expect(page.locator('#tournamentS3Modal')).toBeHidden({ timeout: 5000 });
});
