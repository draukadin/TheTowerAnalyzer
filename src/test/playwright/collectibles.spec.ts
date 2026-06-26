import { test, expect } from '@playwright/test';
import { goToApp, mockApi } from './helpers/mock-api';

const RELIC = {
  id: 1, name: 'Chrono Relic', rarity: 'Legendary', type: 'Milestone',
  stat: 'Lab Speed', value: 0.10, condition: 'Reach wave 4500 in T:XII', owned: false,
};

// ── Section 1 — Relics add modal ─────────────────────────────────────────────

test('1.1: clicking Add Relic opens the add relic modal', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/relics', [RELIC]);
  await mockApi(page, '**/api/gem-store/rotation', []);

  await page.evaluate(() => (window as any).showView('relics'));
  await expect(page.locator('.btn-primary', { hasText: '+ Add Relic' })).toBeVisible({ timeout: 5000 });

  await page.locator('.btn-primary', { hasText: '+ Add Relic' }).click();
  await expect(page.locator('#addRelicModal')).toBeVisible();
  await expect(page.locator('#newRelicName')).toBeVisible();
});

test('1.2: filling and saving a new relic fires POST /relics', async ({ page }) => {
  let postBody: any = null;
  await goToApp(page);
  await mockApi(page, '**/api/relics', []);
  await mockApi(page, '**/api/gem-store/rotation', []);
  await page.route('**/api/relics', async route => {
    if (route.request().method() === 'POST') {
      postBody = JSON.parse(route.request().postData() ?? '{}');
      await route.fulfill({ status: 201, contentType: 'application/json', body: JSON.stringify({ id: 99, ...postBody }) });
    } else {
      await route.fulfill({ contentType: 'application/json', body: '[]' });
    }
  });

  await page.evaluate(() => (window as any).showView('relics'));
  await page.locator('.btn-primary', { hasText: '+ Add Relic' }).click({ timeout: 5000 });

  await page.locator('#newRelicName').fill('Test Relic');
  await page.locator('#newRelicRarity').selectOption('Legendary');
  await page.locator('#newRelicStat').selectOption('Lab Speed');
  await page.locator('#newRelicValue').fill('0.10');
  await page.locator('#newRelicCondition').fill('Complete wave 4500');
  await page.locator('#addRelicModal .btn-primary').click();

  await expect.poll(() => postBody, { timeout: 5000 }).not.toBeNull();
  expect(postBody.name).toBe('Test Relic');
  expect(postBody.bonusStat).toBe('Lab Speed');
});

// ── Section 2 — Cosmetics add modal ──────────────────────────────────────────

test('2.1: clicking Add Cosmetic opens the cosmetic modal', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/cosmetics', []);

  await page.evaluate(() => (window as any).showView('cosmetics'));
  await expect(page.locator('.btn-primary', { hasText: '+ Add Cosmetic' })).toBeVisible({ timeout: 5000 });

  await page.locator('.btn-primary', { hasText: '+ Add Cosmetic' }).click();
  await expect(page.locator('#addCosmeticModal')).toBeVisible();
});

test('2.2: event category shows event fields and hides item fields', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/cosmetics', []);

  await page.evaluate(() => (window as any).showView('cosmetics'));
  await page.locator('.btn-primary', { hasText: '+ Add Cosmetic' }).click({ timeout: 5000 });

  await page.locator('#newCosmeticCategory').selectOption('event');
  await expect(page.locator('.field-event').first()).toBeVisible();
  await expect(page.locator('.field-item').first()).toBeHidden();
});

test('2.3: milestone_skin category shows milestone fields', async ({ page }) => {
  await goToApp(page);
  await mockApi(page, '**/api/cosmetics', []);

  await page.evaluate(() => (window as any).showView('cosmetics'));
  await page.locator('.btn-primary', { hasText: '+ Add Cosmetic' }).click({ timeout: 5000 });

  await page.locator('#newCosmeticCategory').selectOption('milestone_skin');
  await expect(page.locator('.field-milestone').first()).toBeVisible();
  await expect(page.locator('.field-event').first()).toBeHidden();
});
