import { test, expect } from '@playwright/test';
import { goToApp, mockApi } from './helpers/mock-api';

const WS_ITEM = {
  id: 1, name: 'Attack Speed', category: 'ATTACK',
  currentLevel: 3, maxLevel: 20, isPlus: false,
  unlockGroupPurchased: true, unlockGroupId: null, unlockGroupCost: 0,
  targetLevel: null,
};
const WS_DISCOUNTS = {
  attackCostMult: 0.9, defenseCostMult: 1.0, utilityCostMult: 1.0,
  plusAttackCostMult: 1.0, plusDefenseCostMult: 1.0, plusUtilityCostMult: 1.0,
};
const WS_PLUS_PROGRESS = { currentSpend: 0, unlockSpend: 0, unlocked: false };

const CARD = {
  id: 1, name: 'Cash Storm', rarity: 'COMMON',
  starLevel: 1, copiesOwned: 1, masteryLevel: 0, masteryLabLevel: 3,
  level1: 1.5, level2: 2.0, level3: 2.5, level4: 3.0, level5: 3.5, level6: 4.0, level7: 4.5,
  valueUnit: 'PERCENT',
};

const BOT = {
  id: 1, name: 'Flame Bot', unlocked: false, botPlusUnlocked: false,
  unlockOrder: null, stats: [],
};

async function gotoWorkshopView(page: any) {
  await goToApp(page);
  await mockApi(page, '**/api/workshop', [WS_ITEM]);
  await mockApi(page, '**/api/workshop/discounts', WS_DISCOUNTS);
  await mockApi(page, '**/api/workshop/plus/unlock-progress', WS_PLUS_PROGRESS);
  await page.evaluate(() => (window as any).showView('workshop'));
  await expect(page.locator('#wsBody')).toBeVisible({ timeout: 5000 });
}

// ── Section 1 — Workshop PUT level ────────────────────────────────────────────

test('1.1: changing workshop level fires PUT /workshop/:id/level', async ({ page }) => {
  let putBody: any = null;
  await goToApp(page);
  await mockApi(page, '**/api/workshop', [WS_ITEM]);
  await mockApi(page, '**/api/workshop/discounts', WS_DISCOUNTS);
  await mockApi(page, '**/api/workshop/plus/unlock-progress', WS_PLUS_PROGRESS);
  await mockApi(page, '**/api/workshop/1/costs', []);
  await page.route('**/api/workshop/1/level', async route => {
    if (route.request().method() === 'PUT') {
      putBody = JSON.parse(route.request().postData() ?? '{}');
      await route.fulfill({ status: 200, body: '' });
    } else {
      await route.continue();
    }
  });

  await page.evaluate(() => (window as any).showView('workshop'));
  await expect(page.locator('#wsBody')).toBeVisible({ timeout: 5000 });

  const levelInput = page.locator('.ws-level-input').first();
  await levelInput.fill('8');
  await levelInput.press('Tab');

  await expect.poll(() => putBody, { timeout: 5000 }).not.toBeNull();
  expect(putBody.level).toBe(8);
});

// ── Section 2 — Cards PUT star-level ─────────────────────────────────────────

test('2.1: changing card star level fires PUT /cards/:id/star-level', async ({ page }) => {
  let putBody: any = null;
  await goToApp(page);
  await mockApi(page, '**/api/cards', [CARD]);
  await mockApi(page, '**/api/cards/slots', []);
  await mockApi(page, '**/api/cards/presets', []);
  await page.route('**/api/cards/1/star-level', async route => {
    if (route.request().method() === 'PUT') {
      putBody = JSON.parse(route.request().postData() ?? '{}');
      await route.fulfill({ status: 200, body: '' });
    } else {
      await route.continue();
    }
  });

  await page.evaluate(() => (window as any).showView('cards'));
  await expect(page.locator('#cardsBody')).toBeVisible({ timeout: 5000 });

  // Star level selector: first .cards-inline-select is the star select
  const starSelect = page.locator('.cards-inline-select').first();
  await starSelect.selectOption('2');

  await expect.poll(() => putBody, { timeout: 5000 }).not.toBeNull();
  expect(putBody.starLevel).toBe(2);
});

test('2.2: cards preset selector tab is present', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/cards', [CARD]);
  await mockApi(page, '**/api/cards/slots', []);
  await mockApi(page, '**/api/cards/presets', [{ id: 1, name: 'Main' }]);
  await mockApi(page, '**/api/cards/presets/1/assignments', []);

  await page.evaluate(() => (window as any).showView('cards'));
  await expect(page.locator('.cards-tab-btn', { hasText: 'Presets' })).toBeVisible({ timeout: 5000 });
});

// ── Section 3 — Bots unlock toggle ───────────────────────────────────────────

test('3.1: bot unlock toggle fires PUT /bots/:id/unlocked', async ({ page }) => {
  let putBody: any = null;
  await goToApp(page);
  await mockApi(page, '**/api/bots', [BOT]);
  await mockApi(page, '**/api/bots/unlock-costs', [500, 1000, 2000]);
  await mockApi(page, '**/api/bots/presets', []);
  await mockApi(page, '**/api/bots/level-values', []);
  await page.route('**/api/bots/1/unlocked', async route => {
    if (route.request().method() === 'PUT') {
      putBody = JSON.parse(route.request().postData() ?? '{}');
      await route.fulfill({ status: 200, body: '' });
    } else {
      await route.continue();
    }
  });

  await page.evaluate(() => (window as any).showView('bots'));
  await expect(page.locator('.bot-card')).toBeVisible({ timeout: 5000 });

  // Check the "Unlocked" checkbox in the bot card
  const unlockCheckbox = page.locator('.bot-card input[type="checkbox"]').first();
  await unlockCheckbox.check();

  await expect.poll(() => putBody, { timeout: 5000 }).not.toBeNull();
  expect(putBody.unlocked).toBe(true);
});

// ── Section 4 — Guardian add-chip modal ──────────────────────────────────────

test('4.1: guardian add-chip modal opens when + Add Chip is clicked', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/guardian', { unlocked: false, slots: [], chips: [] });
  await mockApi(page, '**/api/guardian/presets', []);
  await mockApi(page, '**/api/guardian/level-values', []);

  await page.evaluate(() => (window as any).showView('guardian'));
  await expect(page.locator('#guardianBody')).toBeVisible({ timeout: 5000 });

  await page.locator('button', { hasText: '+ Add Chip' }).click();
  await expect(page.locator('#guardianAddChipModal')).toBeVisible();
  await expect(page.locator('#gnName')).toBeVisible();
});
