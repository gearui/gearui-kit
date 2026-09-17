# Releasing gearui-kit

[English](./RELEASING.md) | [简体中文](./RELEASING.zh-Hans.md)

Maintainer notes. Users never need this — the README covers integration.

## Candidate Gate (beta3)

Read [current readiness](BETA3_RELEASE_READINESS.md) before publishing. A local
success on uncommitted sources is not release approval.

1. Review and commit the complete candidate, including token sources, generated
   code, tests, API baselines, migration notes and resources. Never publish only
   the tracked portion while required new files remain untracked.
2. Run all checks in [CI mapping](SPEC_CI_MAPPING.md). Require remote CI on that
   exact commit. API dump generation is not an API verification step.
3. Check the supported consumers, full iOS host, Android/Web sample and the
   critical keyboard/overlay/theme paths. Record untested targets explicitly.
4. Stage all six Maven modules on macOS and inspect assets, dependency metadata,
   JS/iOS KLibs and sources. A composite source build does not test Maven consumption.
5. Review breaking changes and renderer limitations. Obtain release approval,
   then set the final version/tag and sign/upload that exact candidate.

For isolated, unsigned local packaging only (does not publish remotely):

```bash
./gradlew :gearui-kit:publishToMavenLocal \
  -Dmaven.repo.local=/tmp/gearui-beta3-staging \
  -PPOM_VERSION=1.0.0-beta3 -PsigningInMemoryKey=
```

This does not verify signing, Central upload or downstream dependency resolution.
Do not interpret a successful lifecycle task as proof of publication.

## Release to Maven Central (Central Portal)

Publishing is wired through `com.vanniktech.maven.publish` and Sonatype Central Portal.
Set credentials and signing keys as Gradle properties or env vars:

```bash
export ORG_GRADLE_PROJECT_mavenCentralUsername=<central_portal_token_name>
export ORG_GRADLE_PROJECT_mavenCentralPassword=<central_portal_token_secret>
# base64 encoded, single line: gpg --export-secret-keys <fpr> | base64 | tr -d '\n'
export ORG_GRADLE_PROJECT_signingInMemoryKey=<base64_gpg_private_key>
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=<gpg_passphrase>
```

Do **not** set `signingInMemoryKeyId` unless you mean a specific subkey; Gradle
searches subkeys only, so a master key id there makes every signing task fail
with "no configured signatory". See the notes in `gearui-kit/build.gradle.kts`.

Publish from macOS — the three iOS targets build nowhere else, and on Linux they
are silently missing from the upload rather than failing it.

```bash
./gradlew :gearui-kit:publishToMavenCentral
```

Historical beta1 note (plugin 0.30.0, not a verified behavior of the current
0.35.0 plugin): this task reported BUILD SUCCESSFUL **without
uploading anything**: it was a lifecycle task (`Skipping task ... as it has no
actions`) and the artifacts only reach `build/publish/staging/<uuid>/`. The
1.0.0-beta1 release was uploaded by posting that bundle to the Portal directly:

```bash
TOKEN=$(printf '%s:%s' "$USERNAME" "$PASSWORD" | base64)
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -F "bundle=@build/publish/staging/<uuid>.zip" \
  "https://central.sonatype.com/api/v1/publisher/upload?name=com.gearui:gearui-kit:<version>&publishingType=USER_MANAGED"
# -> prints a deployment id; then poll:
curl -X POST -H "Authorization: Bearer $TOKEN" \
  "https://central.sonatype.com/api/v1/publisher/status?id=<deployment_id>"
```

`USER_MANAGED` stops at VALIDATED so the final Publish stays a human decision.
Always verify against the Portal rather than trusting Gradle's exit code.
