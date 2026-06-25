import { test, expect } from '@playwright/test';
import { goToApp, mockApi } from './helpers/mock-api';

const LAB = {
  id: 1,
  name: 'Attack Speed',
  category: 'Attack',
  currentLevel: 5,
  maxLevel: 200,
  targetLevel: null,
};

const LAB_MULTIPLIERS = { speedMult: 1.0, costMult: 1.0 };

async function gotoLabsView(page: any) {
  await goToApp(page);
  await mockApi(page, '**/api/labs', [LAB]);
  await mockApi(page, '**/api/labs/costs', { 1: [] });
  await mockApi(page, '**/api/labs/multipliers', LAB_MULTIPLIERS);
  await page.evaluate(() => (window as any).showView('labs'));
  await expect(page.locator('.labs-search')).toBeVisible({ timeout: 5000 });
}

// ── Section 1 — Lab search ────────────────────────────────────────────────────

test('1.1: search filters the lab table to matching rows', async ({ page }) => {
  await gotoLabsView(page);
  await page.locator('.labs-search').fill('Attack Speed');
  await expect(page.locator('.labs-table tbody tr').first()).toContainText('Attack Speed');
});

test('1.2: search with no match shows empty table', async ({ page }) => {
  await gotoLabsView(page);
  await page.locator('.labs-search').fill('XyzNoMatch');
  await expect(page.locator('.labs-table tbody tr')).toHaveCount(0);
});

// ── Section 2 — Level change → PUT /labs/:id/state ───────────────────────────

test('2.1: changing lab level fires PUT /labs/:id/state with new level', async ({ page }) => {
  let putBody: any = null;
  await goToApp(page);
  await mockApi(page, '**/api/labs', [LAB]);
  await mockApi(page, '**/api/labs/costs', { 1: [] });
  await mockApi(page, '**/api/labs/multipliers', LAB_MULTIPLIERS);
  await page.route('**/api/labs/1/state', async route => {
    if (route.request().method() === 'PUT') {
      putBody = JSON.parse(route.request().postData() ?? '{}');
      await route.fulfill({ status: 200, body: '' });
    } else {
      await route.continue();
    }
  });

  await page.evaluate(() => (window as any).showView('labs'));
  await expect(page.locator('.labs-search')).toBeVisible({ timeout: 5000 });

  // The level cell uses mkSpin which renders type="text" class="spin-val" with an onblur handler
  const levelInput = page.locator('.labs-table .spin-val').first();
  await levelInput.fill('10');
  await levelInput.press('Tab');

  await expect.poll(() => putBody, { timeout: 5000 }).not.toBeNull();
  expect(putBody.currentLevel).toBe(10);
});

// ── Section 3 — Lab Planner ───────────────────────────────────────────────────

test('3.1: lab planner view renders lab slot cards', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/labs', [LAB]);
  await mockApi(page, '**/api/lab-slots', [
    { slotNumber: 1, speed: 1.0, plans: [] },
    { slotNumber: 2, speed: 1.0, plans: [] },
    { slotNumber: 3, speed: 1.0, plans: [] },
    { slotNumber: 4, speed: 1.0, plans: [] },
    { slotNumber: 5, speed: 1.0, plans: [] },
  ]);
  await page.evaluate(() => (window as any).showView('labplanner'));
  await expect(page.locator('.lp-card')).toHaveCount(5, { timeout: 5000 });
});

// ── Section 4 — Cell Income ───────────────────────────────────────────────────

test('4.1: cell income view renders CPH panel after API response', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/analysis/cells**', {
    windowDays: 30,
    cellsPerHour: 25000,
    totalCells: 18000000,
    runCount: 12,
    breakdown: [],
  });
  await page.evaluate(() => (window as any).showView('cells'));
  await expect(page.locator('#cellsPanel')).not.toContainText('Loading', { timeout: 5000 });
});

// ── Section 5 — Lab Speed ─────────────────────────────────────────────────────

test('5.1: lab speed view renders stat cards in labPanel', async ({ page }) => {
  await goToApp(page);
  const labSpeedResponse = {
    runsAnalyzed: 20,
    averageCellsPerHour: 25000,
    effectiveCellsPerHour: 20000,
    deadTimeStats: {
      hoursSinceLastRun: 2, totalActiveHours: 10, totalDeadHours: 5,
      totalCalendarHours: 15, deadTimePercent: 33.3,
    },
    cellReserve: { burnRatePerHour: 0, cellsOnHand: 0, spendableCells: 0, burndownHours: null },
    optimalCombination: {
      slots: ['None', 'None', 'None', 'None', 'None'],
      netCellsPerHour: 5000,
      totalCostPerHour: 0,
      totalCostPerDay: 0,
    },
    farmingCombination: {
      slots: ['None', 'None', 'None', 'None', 'None'],
      netCellsPerHour: 5000,
      totalCostPerHour: 0,
      totalCostPerDay: 0,
    },
    slots: [],
  };
  await mockApi(page, '**/api/analysis/lab-speed**', labSpeedResponse);
  await page.evaluate(() => (window as any).showView('labspeed'));
  await expect(page.locator('#labPanel .stat-card')).toHaveCount(4, { timeout: 5000 });
});
