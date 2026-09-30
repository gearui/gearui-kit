// Smoke run of every sample page in a browser, light and dark (gate D6).
//
//   node scripts/acceptance/web_pages.mjs <base-url> [out-dir]
//
// Needs Playwright (from this repo's node_modules, or the package directory given in
// PLAYWRIGHT_PATH) and the Web sample served at <base-url>
// (see sample/jsApp/README.md). For each route and theme it opens the page at a
// 390x844 phone viewport and waits for the route's NavBar title (a page that never
// shows it fails), scrolls, taps the first tappable element, then resizes to a
// 1280-wide desktop and back, recording page errors, console errors, horizontal
// overflow and a screenshot per step. It proves pages load, scroll and resize
// cleanly; it does not check what a tap did — component behaviour is asserted by
// the per-component scripts (web_overlay_resize.mjs). One line per page:
// WEB|route|theme|ok or WEB|route|theme|<problems>; exit status 1 if any page has one.
import fs from 'node:fs';
import path from 'node:path';
import { pathToFileURL } from 'node:url';

const { chromium } = process.env.PLAYWRIGHT_PATH
  ? await import(pathToFileURL(path.join(process.env.PLAYWRIGHT_PATH, 'index.mjs')).href)
  : await import('playwright');

const base = process.argv[2];
const root = path.resolve(path.dirname(new URL(import.meta.url).pathname), '../..');
const stamp = new Date().toISOString().replace(/[-:]/g, '').slice(0, 15);
const out = process.argv[3] ?? path.join(root, 'build/acceptance', `web-${stamp}`);
fs.mkdirSync(out, { recursive: true });
const registry = fs.readFileSync(
  path.join(root, 'sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt'), 'utf8');
// A page shows its English name in the NavBar, or (pages without one) its Chinese name.
const routes = [...registry.matchAll(/ComponentInfo\("([^"]+)",\s*"([^"]*)",\s*"([^"]+)"/g)]
  .map((m) => ({ id: m[1], titles: [m[3], m[2]] }));

const browser = await chromium.launch();
let failed = 0;
for (const { id: route, titles } of routes) for (const theme of ['light', 'dark']) {
  const page = await browser.newPage({ viewport: { width: 390, height: 844 }, deviceScaleFactor: 2 });
  const problems = [];
  page.on('pageerror', (e) => problems.push(`pageerror: ${e.message.split('\n')[0]}`));
  page.on('console', (m) => {
    // Resource failures are reported with their URL by `requestfailed` below.
    if (m.type() === 'error' && !m.text().startsWith('Failed to load resource')) problems.push(`console: ${m.text().slice(0, 160)}`);
  });
  // `invalid.example` is the demos' deliberately broken image (Avatar's fallback).
  page.on('requestfailed', (r) => { if (!r.url().includes('invalid.example')) problems.push(`request failed: ${r.url().slice(0, 120)} ${r.failure()?.errorText ?? ''}`); });
  try {
    await page.goto(`${base}/?route=${route}&theme=${theme}&lang=zh-Hans`, { waitUntil: 'load' });
    const ready = await page.waitForFunction((ts) => [...document.querySelectorAll('body *')]
      .some((e) => ts.includes(e.innerText?.trim()) && e.getBoundingClientRect().top < 120), titles, { timeout: 15000 })
      .then(() => true, () => false);
    if (!ready) throw new Error(`page title "${titles[0]}" never rendered`);
    await page.waitForTimeout(500);
    await page.screenshot({ path: `${out}/${route}-${theme}-phone.png` });
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
    await page.screenshot({ path: `${out}/${route}-${theme}-desktop.png` });
    await page.setViewportSize({ width: 390, height: 844 });
    await page.waitForTimeout(500);
    const overflow = await page.evaluate(() => document.documentElement.scrollWidth > window.innerWidth + 1);
    if (overflow) problems.push('horizontal overflow after resize');
    await page.screenshot({ path: `${out}/${route}-${theme}-resized.png` });
  } catch (e) {
    problems.push(`driver: ${e.message.split('\n')[0]}`);
  }
  if (problems.length) failed++;
  console.log(`WEB|${route}|${theme}|${problems.length ? [...new Set(problems)].join(' ; ') : 'ok'}`);
  await page.close();
}
await browser.close();
console.log(`WEBTOTAL|${routes.length * 2} pages|${failed} with problems|${out}`);
process.exit(failed ? 1 : 0);
