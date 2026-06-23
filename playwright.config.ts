import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './src/test/playwright',
  use: {
    baseURL: 'http://localhost:8080',
    slowMo: process.env.SLOWMO ? parseInt(process.env.SLOWMO) : 0,
  },
});
