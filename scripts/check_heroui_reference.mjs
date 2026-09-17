import { readFileSync } from 'node:fs';
import { createHash } from 'node:crypto';
import { execFileSync } from 'node:child_process';
import { resolve } from 'node:path';

// Local reference verification, not a CI dependency on a sibling checkout.
const lock = JSON.parse(readFileSync(new URL('../tokens/reference/heroui-native.lock.json', import.meta.url)));
const root = resolve(process.argv[2] ?? '../heroui-native');
const commit = execFileSync('git', ['-C', root, 'rev-parse', 'HEAD'], { encoding: 'utf8' }).trim();
if (commit !== lock.commit) throw new Error(`Reference commit differs: ${commit}`);
for (const [path, expected] of Object.entries(lock.files)) {
  const actual = createHash('sha256').update(readFileSync(resolve(root, path))).digest('hex');
  if (actual !== expected) throw new Error(`Reference content differs: ${path}`);
}
console.log(`HeroUI Native ${lock.version}: ${Object.keys(lock.files).length} reference files verified.`);
