// Drives every sample page in a browser (gate D6).
//
//   node scripts/acceptance/web_pages.mjs <base-url> [out-dir]
//
// Needs Playwright (from this repo's node_modules, or the package directory given in
// PLAYWRIGHT_PATH) and the Web sample served at <base-url>
// (see sample/jsApp/README.md). For each route it opens the page at a 390x844 phone
// viewport, scrolls, taps the first tappable element, then resizes from a 1280-wide
// desktop back to the phone width, recording page errors, console errors and a
// screenshot per step. One line per route: WEB|route|ok or WEB|route|<problems>.
import fs from 'node:fs';
import path from 'node:path';
import { pathToFileURL } from 'node:url';

const { chromium } = process.env.PLAYWRIGHT_PATH
  ? await import(pathToFileURL(path.join(process.env.PLAYWRIGHT_PATH, 'index.mjs')).href)
  : await import('playwright');

const base = process.argv[2];
const root = path.resolve(path.dirname(new URL(import.meta.url).pathname), '../..');
const out = process.argv[3] ?? path.join(root, 'build/beta7-acceptance/web');
fs.mkdirSync(out, { recursive: true });
const registry = fs.readFileSync(
  path.join(root, 'sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt'), 'utf8');
const routes = [...registry.matchAll(/ComponentInfo\("([^"]+)"/g)].map((m) => m[1]);

const browser = await chromium.launch();
let failed = 0;
for (const route of routes) {
  const page = await browser.newPage({ viewport: { width: 390, height: 844 }, deviceScaleFactor: 2 });
  const problems = [];
  page.on('pageerror', (e) => problems.push(`pageerror: ${e.message.split('\n')[0]}`));
  page.on('console', (m) => { if (m.type() === 'error') problems.push(`console: ${m.text().slice(0, 160)}`); });
  try {
    await page.goto(`${base}/?route=${route}&theme=light&lang=zh-Hans`, { waitUntil: 'load' });
    await page.waitForTimeout(1500);
    const blank = await page.evaluate(() => document.body.innerText.trim().length === 0);
    if (blank) problems.push('blank page');
    await page.screenshot({ path: `${out}/${route}-phone.png` });
    await page.mouse.move(195, 500);
    await page.mouse.wheel(0, 600);
    await page.waitForTimeout(400);
    // The first element with a click role or cursor, below the navigation bar.
    const target = await page.evaluate(() => {
      const els = [...document.querySelectorAll('div,span,input,button')];
      for (const el of els) {
        const r = el.getBoundingClientRect();
        if (r.top < 120 || r.bottom > 840 || r.width < 20 || r.height < 20) continue;
        if (getComputedStyle(el).cursor === 'pointer' || el.getAttribute('role') === 'button' || el.tagName === 'INPUT') {
          return { x: r.left + r.width / 2, y: r.top + r.height / 2 };
        }
      }
      return null;
    });
    if (target) {
      await page.mouse.click(target.x, target.y);
      await page.waitForTimeout(600);
      await page.keyboard.press('Escape');
    }
    await page.setViewportSize({ width: 1280, height: 800 });
    await page.waitForTimeout(500);
    await page.screenshot({ path: `${out}/${route}-desktop.png` });
    await page.setViewportSize({ width: 390, height: 844 });
    await page.waitForTimeout(500);
    const overflow = await page.evaluate(() => document.documentElement.scrollWidth > window.innerWidth + 1);
    if (overflow) problems.push('horizontal overflow after resize');
    await page.screenshot({ path: `${out}/${route}-resized.png` });
  } catch (e) {
    problems.push(`driver: ${e.message.split('\n')[0]}`);
  }
  if (problems.length) failed++;
  console.log(`WEB|${route}|${problems.length ? [...new Set(problems)].join(' ; ') : 'ok'}`);
  await page.close();
}
await browser.close();
console.log(`WEBTOTAL|${routes.length} routes|${failed} with problems`);
