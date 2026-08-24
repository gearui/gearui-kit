#!/usr/bin/env bash
set -uo pipefail

# Escape hatch for `ohpm install --all` failing with ECONNRESET.
#
# The ohpm registry serves metadata from ohpm.openharmony.cn but redirects the
# actual .har downloads to Huawei's CDN (contentcenter-drcn.dbankcdn.cn, itself
# CloudFront-backed). A local proxy running in fake-IP mode answers DNS for that
# host with something like 198.18.0.x and then kills the TLS handshake, so the
# metadata fetch succeeds and every package download fails:
#
#   ohpm WARN: ECONNRESET fetch package @kuikly-open/render ... failed,
#   errMsg: Client network socket disconnected before secure TLS connection was established
#
# That reads like a registry outage and is not one — the connection is fine once
# DNS is. This script resolves the CDN host over DoH and pins the real address
# with curl --resolve, then installs from the downloaded files.
#
# The proper fix is in the proxy: route ohpm.openharmony.cn and *.dbankcdn.cn
# direct, or turn fake-IP off. Use this when you cannot change that.
#
# Usage:  ./fetch-deps.sh && ohpm install --all

HERE="$(cd "$(dirname "$0")" && pwd)"
OHPM="/Applications/DevEco-Studio.app/Contents/tools/ohpm/bin/ohpm"
LIBS="$HERE/libs"

PACKAGES=(
  "@kuikly-open/render:2.25.0"
  "@tencent/libpag:4.4.25"
  "@kuiklybase/knoi:0.0.4"
  "@ohos/hypium:1.0.16"
  "@ohos/hamock:1.0.0"
)

mkdir -p "$LIBS"

fetch() {
  local pkg="$1" ver="$2" out="$3"
  local url final host ip
  url=$(curl -s "https://ohpm.openharmony.cn/ohpm/${pkg}" | python3 -c "
import sys, json
d = json.load(sys.stdin)
v = '${ver}'
versions = d.get('versions', {})
if v not in versions:
    sys.exit('version ${ver} not published')
dist = versions[v].get('dist', {})
print(dist.get('tarball') or dist.get('url', ''))
") || return 1
  [ -z "$url" ] && { echo "no tarball url"; return 1; }

  final=$(curl -s -o /dev/null -w '%{redirect_url}' "$url")
  [ -z "$final" ] && final="$url"
  host=$(printf '%s' "$final" | sed -E 's#https?://([^/]+)/.*#\1#')

  ip=$(curl -s "https://1.1.1.1/dns-query?name=${host}&type=A" -H 'accept: application/dns-json' \
       | python3 -c "import sys,json;print(next((a['data'] for a in json.load(sys.stdin).get('Answer',[]) if a.get('type')==1),''))")
  [ -z "$ip" ] && { echo "cannot resolve ${host} over DoH"; return 1; }

  curl -s --resolve "${host}:443:${ip}" --noproxy '*' --max-time 300 -o "$out" \
       -w '%{http_code}' "$final"
}

status=0
for spec in "${PACKAGES[@]}"; do
  pkg="${spec%:*}"; ver="${spec##*:}"
  out="$LIBS/$(printf '%s' "$pkg" | tr '/@' '__')-${ver}.har"
  printf '%-24s %-10s ' "$pkg" "$ver"
  if [ -s "$out" ]; then
    echo "cached"
    continue
  fi
  code=$(fetch "$pkg" "$ver" "$out") || { echo "FAILED"; status=1; continue; }
  if [ "$code" = "200" ] && [ -s "$out" ]; then
    echo "ok ($(du -h "$out" | cut -f1))"
  else
    echo "FAILED (http ${code:-none})"
    rm -f "$out"
    status=1
  fi
done

[ $status -ne 0 ] && { echo; echo "Some packages could not be fetched."; exit 1; }

echo
echo "Installing from $LIBS"
cd "$HERE" || exit 1

# Point the manifests at the downloaded files, install, then put them back, so
# the committed manifests keep their registry versions.
cp entry/oh-package.json5 /tmp/gearui-entry-oh-package.bak
cp oh-package.json5 /tmp/gearui-root-oh-package.bak
trap 'mv /tmp/gearui-entry-oh-package.bak entry/oh-package.json5; mv /tmp/gearui-root-oh-package.bak oh-package.json5' EXIT

python3 - "$LIBS" <<'PY'
import sys, os, re
libs = sys.argv[1]
def local(pkg, ver):
    return os.path.join(libs, pkg.replace('/', '_').replace('@', '_') + f'-{ver}.har')

for path, deps in (
    ('entry/oh-package.json5', [('@kuikly-open/render', '2.25.0'), ('@tencent/libpag', '4.4.25'), ('@kuiklybase/knoi', '0.0.4')]),
    ('oh-package.json5', [('@ohos/hypium', '1.0.16'), ('@ohos/hamock', '1.0.0')]),
):
    s = open(path, encoding='utf-8').read()
    for pkg, ver in deps:
        s = re.sub(rf'"{re.escape(pkg)}":\s*"[^"]*"', f'"{pkg}": "file:{local(pkg, ver)}"', s)
    open(path, 'w', encoding='utf-8').write(s)
PY

"$OHPM" install --all
