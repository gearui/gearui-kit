// Every sample page at the narrowest phone width iOS supports (320), looking for content
// that does not fit (gate D6, narrow screens).
//
//   node scripts/acceptance/web_narrow.mjs <base-url> [width] [routes,comma,separated]
//
// Needs Playwright (PLAYWRIGHT_PATH, as web_pages.mjs) and the Web sample at <base-url>.
// Each page is scrolled to its end; at every step two things are reported:
//   - cut: an element that reaches past the side of an ancestor that clips it (a slot
//     pushed out of its card), unless that ancestor scrolls sideways;
//   - text: a text box whose text is wider than the box (a label cut mid-word);
//   - collapsed: a box squeezed to no width beside siblings of its height that have one
//     (the last slot of a row that ran out of room).
// One line per page: NARROW|route|ok or NARROW|route|<problems>; exit status 1 if any.
import fs from 'node:fs';
import path from 'node:path';
import { pathToFileURL } from 'node:url';

const { chromium } = process.env.PLAYWRIGHT_PATH
  ? await import(pathToFileURL(path.join(process.env.PLAYWRIGHT_PATH, 'index.mjs')).href)
  : await import('playwright');

const base = process.argv[2];
const width = Number(process.argv[3] ?? 320);
const only = process.argv[4]?.split(',');
const root = path.resolve(path.dirname(new URL(import.meta.url).pathname), '../..');
const stamp = new Date().toISOString().replace(/[-:]/g, '').slice(0, 15);
const out = path.join(root, 'build/acceptance', `web-narrow-${stamp}`);
fs.mkdirSync(out, { recursive: true });
const registry = fs.readFileSync(
  path.join(root, 'sample/src/commonMain/kotlin/com/gearui/sample/config/ComponentConfig.kt'), 'utf8');
// Pages whose content runs past its window by design.
const EXEMPT = {
  watermark: 'tiled marks are clipped by the area they cover',
  noticebar: 'the marquee scrolls text through its window',
};
const routes = [...registry.matchAll(/ComponentInfo\("([^"]+)",\s*"([^"]*)",\s*"([^"]+)"/g)]
  .map((m) => ({ id: m[1], titles: [m[3], m[2]] }))
  .filter((r) => (!only || only.includes(r.id)) && !EXEMPT[r.id]);

function findProblems() {
  const found = [];
  const vw = window.innerWidth, vh = window.innerHeight;
  const label = (e) => (e.innerText || e.getAttribute('aria-label') || e.tagName).trim().replace(/\s+/g, ' ').slice(0, 24);
  const scrollsSideways = (e) => {
    for (let a = e.parentElement; a && a !== document.body; a = a.parentElement) {
      const x = getComputedStyle(a).overflowX;
      if (x === 'auto' || x === 'scroll') return true;
    }
    return false;
  };
  for (const e of document.querySelectorAll('body *')) {
    if (e.tagName === 'CANVAS') continue;                    // shadows and ripples draw past their box
    const r = e.getBoundingClientRect();
    if (r.bottom < 100 || r.top > vh || r.height < 1) continue;
    // A box squeezed to nothing beside siblings of its height (climbing through wrappers).
    if (r.width < 0.5 && r.height >= 16 && (e.childElementCount > 0 || e.innerText?.trim())) {   // empty spacers are fine
      let x = e;
      while (x.parentElement && x.parentElement.childElementCount === 1) x = x.parentElement;
      const sibling = x.parentElement && [...x.parentElement.children].find((o) => {
        const q = o.getBoundingClientRect();
        return o !== x && q.width >= 16 && Math.abs(q.height - r.height) < 2;
      });
      if (sibling && !scrollsSideways(e)) found.push(`collapsed box beside "${label(sibling)}" at x=${r.left | 0}`);
    }
    if (r.width < 1) continue;
    const cs = getComputedStyle(e);
    if (e.childElementCount === 0 && e.innerText?.trim() && cs.textOverflow !== 'ellipsis' && e.scrollWidth > e.clientWidth + 1) {
      found.push(`text "${label(e)}" ${e.scrollWidth}>${e.clientWidth}`);
    }
    if (e.childElementCount !== 0 || r.width < 4 || scrollsSideways(e)) continue;
    for (let a = e.parentElement; a && a !== document.body; a = a.parentElement) {
      const as = getComputedStyle(a);
      if (as.overflowX !== 'hidden' && as.overflow !== 'hidden') continue;
      const ar = a.getBoundingClientRect();
      if (ar.width >= vw - 1) break;                                         // page-wide clip
      if (r.right > ar.right + 2 || r.left < ar.left - 2) found.push(`cut "${label(e)}" ${r.left | 0}..${r.right | 0} outside ${ar.left | 0}..${ar.right | 0}`);
      break;
    }
    if (r.right > vw + 1) found.push(`off-screen "${label(e)}" right=${r.right | 0}`);
  }
  return found;
}

const browser = await chromium.launch();
let failed = 0;
for (const { id: route, titles } of routes) {
  const page = await browser.newPage({ viewport: { width, height: 700 }, deviceScaleFactor: 2 });
  const problems = new Set();
  try {
    await page.goto(`${base}/?route=${route}&theme=light&lang=zh-Hans`, { waitUntil: 'load' });
    const ready = await page.waitForFunction((ts) => [...document.querySelectorAll('body *')]
      .some((e) => ts.includes(e.innerText?.trim()) && e.getBoundingClientRect().top < 120), titles, { timeout: 15000 })
      .then(() => true, () => false);
    if (!ready) throw new Error(`page title "${titles[0]}" never rendered`);
    await page.waitForTimeout(600);
    await page.mouse.move(width / 2, 400);
    let previous = '';
    for (let step = 0; step < 30; step++) {
      for (const p of await page.evaluate(findProblems)) problems.add(p);
      if (step === 0) await page.screenshot({ path: `${out}/${route}.png` });
      const shot = (await page.screenshot()).toString('base64');
      if (shot === previous) break;
      previous = shot;
      await page.mouse.wheel(0, 500);
      await page.waitForTimeout(350);
    }
  } catch (e) {
    problems.add(String(e.message).split('\n')[0]);
  }
  await page.close();
  if (problems.size) failed++;
  console.log(`NARROW|${route}|${problems.size ? [...problems].slice(0, 6).join(' ; ') + (problems.size > 6 ? ` ; +${problems.size - 6}` : '') : 'ok'}`);
}
await browser.close();
console.log(`NARROWTOTAL|${routes.length} pages|${failed} with problems|${width}px|${out}`);
process.exit(failed ? 1 : 0);
