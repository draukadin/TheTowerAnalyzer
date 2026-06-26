import { Page } from '@playwright/test';

export async function mockApi(page: Page, pattern: string, body: unknown, status = 200) {
  await page.route(pattern, route =>
    route.fulfill({ status, contentType: 'application/json', body: JSON.stringify(body) }));
}

/** Navigate to the app with setup complete and auth authenticated, no reports loaded. */
export async function goToApp(page: Page) {
  await mockApi(page, '**/api/setup/status', { step: 'complete' });
  await mockApi(page, '**/api/auth/status', { status: 'authenticated' });
  await mockApi(page, '**/api/modules/substats', {});
  await mockApi(page, '**/api/reports', []);
  await mockApi(page, '**/api/reports/duplicates', []);
  await page.goto('/');
}
