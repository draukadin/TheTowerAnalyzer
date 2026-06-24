import { test, expect, Page } from '@playwright/test';

// ── Helpers ───────────────────────────────────────────────────────────────────

async function mockStatus(page: Page, step: 'config' | 'complete') {
  await page.route('**/api/setup/status', route =>
    route.fulfill({ contentType: 'application/json', body: JSON.stringify({ step }) }));
}

async function mockConfig(page: Page, response: { status?: number; body: object }) {
  await page.route('**/api/setup/config', route =>
    route.fulfill({
      status: response.status ?? 200,
      contentType: 'application/json',
      body: JSON.stringify(response.body),
    }));
}

// Navigate to the app with the wizard visible (status = config).
async function gotoWizard(page: Page) {
  await mockStatus(page, 'config');
  await page.goto('/');
  await expect(page.locator('#setupWizard')).toBeVisible();
}

// ── Section 5.1 — Initial render ──────────────────────────────────────────────

test('5.1: step 1 active on initial render', async ({ page }) => {
  await gotoWizard(page);

  // Step content visibility
  await expect(page.locator('#wizardStep1')).toBeVisible();
  await expect(page.locator('#wizardStep2')).toBeHidden();

  // Dot 1 shows "1" (active, not yet done)
  await expect(page.locator('#wizDot1')).toHaveText('1');

  // Dot 2 shows "2" (pending)
  await expect(page.locator('#wizDot2')).toHaveText('2');

  // No third dot in DOM
  await expect(page.locator('#wizDot3')).toHaveCount(0);
});

// ── Section 5.2 — Step 2 active ───────────────────────────────────────────────

test('5.2: step 2 active after goToWizardStep(2)', async ({ page }) => {
  await gotoWizard(page);
  await page.evaluate(() => (window as any).goToWizardStep(2));

  await expect(page.locator('#wizardStep2')).toBeVisible();
  await expect(page.locator('#wizardStep1')).toBeHidden();

  // Dot 1 shows checkmark
  await expect(page.locator('#wizDot1')).toHaveText('✓');

  // Label 1 is green (Player Setup text colour)
  const lbl1Color = await page.locator('#wizInd1 span').evaluate(
    el => getComputedStyle(el).color
  );
  expect(lbl1Color).not.toBe('');   // colour was applied — exact value depends on theme

  // Label 2 is accent-coloured
  const lbl2Style = await page.locator('#wizLabel2').getAttribute('style');
  expect(lbl2Style).toContain('var(--accent)');
});

// ── Section 5.3 — Empty player ID ────────────────────────────────────────────

test('5.3: empty player ID shows error, stays on step 1', async ({ page }) => {
  await gotoWizard(page);

  await page.locator('#configBtn').click();

  await expect(page.locator('#configError')).toBeVisible();
  await expect(page.locator('#configError')).toContainText('Player ID is required.');
  await expect(page.locator('#wizardStep1')).toBeVisible();
  await expect(page.locator('#wizardStep2')).toBeHidden();
});

// ── Section 5.4 — Valid submit → 200 ─────────────────────────────────────────

test('5.4: valid submit advances to step 2', async ({ page }) => {
  await mockStatus(page, 'config');
  // Slow response so the "Saving…" state can be observed
  await page.route('**/api/setup/config', async route => {
    await new Promise(r => setTimeout(r, 300));
    await route.fulfill({ contentType: 'application/json', body: JSON.stringify({ step: 'complete' }) });
  });
  await page.goto('/');
  await expect(page.locator('#setupWizard')).toBeVisible();

  await page.locator('#playerId').fill('abc123');
  const clickPromise = page.locator('#configBtn').click();

  // Button disabled and shows "Saving…" while request is in flight
  await expect(page.locator('#configBtn')).toHaveText('Saving…');
  await expect(page.locator('#configBtn')).toBeDisabled();

  await clickPromise;
  await expect(page.locator('#wizardStep2')).toBeVisible({ timeout: 3000 });
});

// ── Section 5.5 — Server 400 ─────────────────────────────────────────────────

test('5.5: server 400 shows error and re-enables button', async ({ page }) => {
  await gotoWizard(page);
  await mockConfig(page, { status: 400, body: { error: 'Player ID is required.' } });

  await page.locator('#playerId').fill('abc');
  await page.locator('#configBtn').click();

  await expect(page.locator('#configError')).toBeVisible();
  await expect(page.locator('#configError')).toContainText('Player ID is required.');
  await expect(page.locator('#configBtn')).toBeEnabled();
  await expect(page.locator('#configBtn')).toHaveText('Continue →');
});

// ── Section 5.6 — Network failure ────────────────────────────────────────────

test('5.6: network failure shows "Could not reach the server."', async ({ page }) => {
  await gotoWizard(page);
  await page.route('**/api/setup/config', route => route.abort());

  await page.locator('#playerId').fill('abc');
  await page.locator('#configBtn').click();

  await expect(page.locator('#configError')).toContainText('Could not reach the server.');
  await expect(page.locator('#configBtn')).toBeEnabled();
});

// ── Section 5.7 — Region radio default ───────────────────────────────────────

test('5.7: US radio checked by default', async ({ page }) => {
  await gotoWizard(page);
  await expect(page.locator('input[name="apiGatewayRegion"][value="us"]')).toBeChecked();
  await expect(page.locator('input[name="apiGatewayRegion"][value="eu"]')).not.toBeChecked();
  await expect(page.locator('input[name="apiGatewayRegion"][value="ap"]')).not.toBeChecked();
});

// ── Sections 5.8 + 5.9 — Region POST body ────────────────────────────────────

for (const region of ['eu', 'ap'] as const) {
  test(`5.${region === 'eu' ? 8 : 9}: ${region.toUpperCase()} selection sends correct region in POST body`, async ({ page }) => {
    await gotoWizard(page);

    let capturedRegion: string | undefined;
    await page.route('**/api/setup/config', async route => {
      const body = JSON.parse(route.request().postData() ?? '{}');
      capturedRegion = body.apiGatewayRegion;
      await route.fulfill({ contentType: 'application/json', body: JSON.stringify({ step: 'complete' }) });
    });

    await page.locator(`input[name="apiGatewayRegion"][value="${region}"]`).check();
    await page.locator('#playerId').fill('abc123');
    await page.locator('#configBtn').click();

    await expect(page.locator('#wizardStep2')).toBeVisible({ timeout: 3000 });
    expect(capturedRegion).toBe(region);
  });
}

// ── Section 5.10 — Old DOM elements absent ───────────────────────────────────

test('5.10: removed wizard elements are absent from DOM', async ({ page }) => {
  await gotoWizard(page);

  const absentIds = [
    'wizDot3', 'wizInd3', 'wizLabel3', 'wizardStep3',
    'credentialsJson', 'credentialsBtn', 'credentialsError',
    'backupFolderId', 'battleReportsFolderId', 'playerTrackerSheetId',
  ];

  for (const id of absentIds) {
    await expect(page.locator(`#${id}`), `#${id} should not exist`).toHaveCount(0);
  }
});

// ── Section 7 — Removed endpoint regression ──────────────────────────────────

test('7: POST /api/setup/credentials returns 404', async ({ page }) => {
  const response = await page.request.post('/api/setup/credentials', {
    data: {},
    headers: { 'Content-Type': 'application/json' },
  });
  expect(response.status()).toBe(404);
});
