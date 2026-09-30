// Anchored overlays on the Web sample survive a window resize and a scroll (gate D6).
//
//   node scripts/acceptance/web_overlay_resize.mjs <base-url>
//
// For ComboBox, Select and Popover: open the panel, resize 1280 → 390 → 1280, and at
// every width assert the panel still sits against its trigger (below it, overlapping
// it horizontally); then pick an option and assert the value was written back. A
// second pass scrolls the page with the panel open and asserts the panel either
// follows the trigger or closes — never left floating where the trigger was.
// Exit status 0 when every check passes, 1 otherwise. Needs Playwright (this repo's
// node_modules, or PLAYWRIGHT_PATH) and the Web sample served at <base-url>.
import path from 'node:path';
import { pathToFileURL } from 'node:url';

const { chromium } = process.env.PLAYWRIGHT_PATH
  ? await import(pathToFileURL(path.join(process.env.PLAYWRIGHT_PATH, 'index.mjs')).href)
  : await import('playwright');

const base = process.argv[2];
if (!base) { console.error('usage: web_overlay_resize.mjs <base-url>'); process.exit(2); }

// The smallest visible element whose own text is exactly `text`.
async function box(page, text) {
  return page.evaluate((t) => {
    let best = null;
    for (const el of document.querySelectorAll('body *')) {
      const own = el.tagName === 'INPUT' ? (el.value || el.placeholder) : el.innerText?.trim();
      if (own !== t) continue;
      const r = el.getBoundingClientRect();
      if (r.width === 0 || r.height === 0 || r.bottom < 0 || r.top > innerHeight) continue;
      if (!best || r.width * r.height < best.width * best.height) {
        best = { x: r.left, y: r.top, width: r.width, height: r.height, bottom: r.bottom, right: r.right };
      }
    }
    return best;
  }, text);
}

// Widest ancestor of the element with text `text` that stays inside a band: for the
// trigger, ancestors under 80px tall (the field); for a panel, ancestors that start
// below `minTop` (the panel surface, not the full-screen overlay layer).
async function extent(page, text, { maxHeight = Infinity, minTop = -Infinity } = {}) {
  return page.evaluate(([t, maxH, minT]) => {
    let el = null;
    for (const e of document.querySelectorAll('body *')) {
      const own = e.tagName === 'INPUT' ? (e.value || e.placeholder) : e.innerText?.trim();
      const r = e.getBoundingClientRect();
      if (own === t && r.width > 0 && r.height > 0 && r.top < innerHeight && (!el || r.width * r.height < el.getBoundingClientRect().width * el.getBoundingClientRect().height)) el = e;
    }
    let best = null;
    for (let a = el; a && a !== document.body; a = a.parentElement) {
      const r = a.getBoundingClientRect();
      if (r.height >= maxH || r.top < minT) break;
      best = { x: r.left, width: r.width, top: r.top, bottom: r.bottom };
    }
    return best;
  }, [text, maxHeight, minTop]);
}

async function inputValue(page, placeholder) {
  return page.evaluate((p) => [...document.querySelectorAll('input,textarea')]
    .find((e) => e.placeholder === p)?.value ?? null, placeholder);
}

// Panel against trigger: `side` is where the panel opens relative to the trigger.
function attached(trigger, panel, side = 'below') {
  if (!trigger || !panel) return false;
  const overlapX = panel.x < trigger.right + 8 && panel.right > trigger.x - 8;
  const gap = side === 'below' ? panel.y - trigger.bottom : trigger.y - panel.bottom;
  return overlapX && gap > -4 && gap < 160;
}

const CASES = [
  {
    name: 'combo-box', route: 'combo-box', trigger: '输入城市名', open: 'click-input', option: '上海', matchWidth: true,
    written: async (p) => (await inputValue(p, '城市')) === '上海' && !!(await box(p, '已选择:上海')),
  },
  {
    name: 'select', route: 'select', trigger: '请选择城市', open: 'click', option: '上海', matchWidth: true,
    // Once chosen the trigger shows the value instead of the placeholder.
    written: async (p) => !(await box(p, '请选择城市')) && !!(await box(p, '上海')),
  },
  {
    name: 'popover', route: 'popover', trigger: '带箭头', open: 'click', option: null, panel: '这是带箭头的气泡',
    side: 'below', written: null,
  },
];

const browser = await chromium.launch();
let failures = 0;
const fail = (c, what) => { failures++; console.log(`FAIL|${c.name}|${what}`); };

for (const c of CASES) {
  for (const pass of ['resize', 'scroll']) {
    const page = await browser.newPage({ viewport: { width: 390, height: 844 } });
    const errors = [];
    page.on('pageerror', (e) => errors.push(e.message.split('\n')[0]));
    await page.goto(`${base}/?route=${c.route}&theme=light&lang=zh-Hans`, { waitUntil: 'load' });
    await page.waitForTimeout(1500);
    const t0 = await box(page, c.trigger);
    if (!t0) { fail(c, `${pass}: trigger "${c.trigger}" not found`); await page.close(); continue; }
    await page.mouse.click(t0.x + t0.width / 2, t0.y + t0.height / 2);
    await page.waitForTimeout(700);
    const panelText = c.panel ?? c.option;
    const check = async (label) => {
      const trig = await box(page, c.trigger);
      const pan = await box(page, panelText);
      if (!attached(trig, pan, c.side)) {
        fail(c, `${pass} ${label}: panel ${JSON.stringify(pan)} not attached to trigger ${JSON.stringify(trig)}`);
        return false;
      }
      if (c.matchWidth) {
        // The panel is as wide as its field, whatever the window did since it opened.
        const field = await extent(page, c.trigger, { maxHeight: 80 });
        const panel = await extent(page, panelText, { minTop: trig.bottom - 4 });
        if (!field || !panel || Math.abs(panel.width - field.width) > Math.max(24, field.width * 0.06)) {
          fail(c, `${pass} ${label}: panel width ${panel?.width} vs field ${field?.width}`);
          return false;
        }
      }
      return true;
    };
    if (!(await check('after open'))) { await page.close(); continue; }

    if (pass === 'resize') {
      for (const width of [1280, 390, 1280]) {
        await page.setViewportSize({ width, height: 844 });
        await page.waitForTimeout(700);
        await check(`at ${width}`);
      }
      if (c.option) {
        const o = await box(page, c.option);
        if (o) {
          await page.mouse.click(o.x + o.width / 2, o.y + o.height / 2);
          await page.waitForTimeout(700);
        }
        if (!o || !(await c.written(page))) fail(c, 'resize: choosing the option did not write the value back');
      }
    } else {
      await page.mouse.move(195, 700);
      await page.mouse.wheel(0, 240);
      await page.waitForTimeout(900);
      const pan = await box(page, panelText);
      const trig = await box(page, c.trigger);
      // Closed is fine (a dropdown may close on scroll); open must still be attached.
      const optionStillShown = c.option ? pan && !(await c.written(page).catch(() => false)) : pan;
      if (optionStillShown && !attached(trig, pan, c.side)) {
        fail(c, `scroll: panel left behind at ${JSON.stringify(pan)}, trigger now ${JSON.stringify(trig)}`);
      }
    }
    if (errors.length) fail(c, `${pass}: page errors: ${[...new Set(errors)].join(' ; ')}`);
    await page.close();
  }
  console.log(`CASE|${c.name}|done`);
}
await browser.close();
console.log(`OVERLAY|${failures === 0 ? 'ok' : `${failures} failures`}`);
process.exit(failures === 0 ? 0 : 1);
