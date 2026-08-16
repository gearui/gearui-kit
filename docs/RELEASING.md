# Releasing gearui-kit

[English](./RELEASING.md) | [简体中文](./RELEASING.zh-Hans.md)

Maintainer notes. Users never need this — the README covers integration.

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

⚠️ With the plugin at 0.30.0 this task reports BUILD SUCCESSFUL **without
uploading anything**: it is a lifecycle task (`Skipping task ... as it has no
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
