import { test, expect } from '@playwright/test';
import { goToApp, mockApi } from './helpers/mock-api';

async function gotoAdminView(page: any) {
  await goToApp(page);
  // /backup/list returning 409 hides the restore section (legacy/Drive mode)
  await page.route('**/api/backup/list', route =>
    route.fulfill({ status: 409, body: '' }));
  await page.evaluate(() => (window as any).showView('admin'));
  await expect(page.locator('#backupBtn')).toBeVisible({ timeout: 5000 });
}

// ── Section 1 — QR modal ──────────────────────────────────────────────────────

test('1.1: Show Tasker QR Code button opens QR modal', async ({ page }) => {
  await gotoAdminView(page);
  await page.locator('button', { hasText: 'Show Tasker QR Code' }).click();
  await expect(page.locator('#qrModal')).toBeVisible();
  await expect(page.locator('#qrModal img')).toBeVisible();
});

test('1.2: clicking outside QR modal closes it', async ({ page }) => {
  await gotoAdminView(page);
  await page.locator('button', { hasText: 'Show Tasker QR Code' }).click();
  await expect(page.locator('#qrModal')).toBeVisible();
  // Click the overlay backdrop (the modal itself at position 10,10 is outside the inner box)
  await page.locator('#qrModal').click({ position: { x: 10, y: 10 } });
  await expect(page.locator('#qrModal')).toBeHidden();
});

// ── Section 2 — Backup database ───────────────────────────────────────────────

test('2.1: backup button fires POST /backup/database and shows success', async ({ page }) => {
  let backupCalled = false;
  await goToApp(page);
  await page.route('**/api/backup/list', route =>
    route.fulfill({ status: 409, body: '' }));
  await page.route('**/api/backup/database', async route => {
    if (route.request().method() === 'POST') {
      backupCalled = true;
      await route.fulfill({
        contentType: 'application/json',
        body: JSON.stringify({ target: 'drive', fileName: 'analyzer-2024-01-15.db' }),
      });
    } else {
      await route.continue();
    }
  });

  await page.evaluate(() => (window as any).showView('admin'));
  await expect(page.locator('#backupBtn')).toBeVisible({ timeout: 5000 });
  await page.locator('#backupBtn').click();

  await expect(page.locator('#backupStatus')).toContainText('✓', { timeout: 5000 });
  expect(backupCalled).toBe(true);
});

test('2.2: backup error shows failure message', async ({ page }) => {
  await goToApp(page);
  await page.route('**/api/backup/list', route =>
    route.fulfill({ status: 409, body: '' }));
  await page.route('**/api/backup/database', route =>
    route.fulfill({ status: 500, body: 'Internal Server Error' }));

  await page.evaluate(() => (window as any).showView('admin'));
  await expect(page.locator('#backupBtn')).toBeVisible({ timeout: 5000 });
  await page.locator('#backupBtn').click();

  await expect(page.locator('#backupStatus')).toContainText('✗', { timeout: 5000 });
});

// ── Section 3 — Restore flow (centralized / S3 mode) ─────────────────────────

test('3.1: restore section appears when /backup/list returns backups', async ({ page }) => {
  const BACKUPS = [
    {
      key: 'player123/backups/analyzer-2024-01-15T10-00-00.db',
      fileName: 'analyzer-2024-01-15T10-00-00.db',
      size: 524288,
      lastModified: '2024-01-15T10:00:00Z',
      latest: true,
    },
  ];
  await goToApp(page);
  await mockApi(page, '**/api/backup/list', BACKUPS);

  await page.evaluate(() => (window as any).showView('admin'));
  await expect(page.locator('#restoreSection')).toBeVisible({ timeout: 5000 });
  await expect(page.locator('#restoreList')).toContainText('analyzer-2024-01-15', { timeout: 5000 });
});

test('3.2: restore section hidden when /backup/list returns 409', async ({ page }) => {
  await gotoAdminView(page);
  await expect(page.locator('#restoreSection')).toBeHidden();
});
