# gearui-kit 发布流程

[English](./RELEASING.md) | 简体中文

维护者用。使用者不需要看这个——接入方式见 README。

## 发布到 Maven Central（Central Portal）

发布通过 `com.vanniktech.maven.publish` 接入 Sonatype Central Portal。
凭证和签名密钥通过环境变量或 Gradle property 注入：

```bash
export ORG_GRADLE_PROJECT_mavenCentralUsername=<Central_Portal_Token_名称>
export ORG_GRADLE_PROJECT_mavenCentralPassword=<Central_Portal_Token_密码>
# base64 单行：gpg --export-secret-keys <指纹> | base64 | tr -d '\n'
export ORG_GRADLE_PROJECT_signingInMemoryKey=<base64_GPG_私钥>
export ORG_GRADLE_PROJECT_signingInMemoryKeyPassword=<GPG_口令>
```

**不要**设置 `signingInMemoryKeyId`，除非你要指定某个 subkey；Gradle 只在 subkey
里查找，填主密钥 id 会让所有签名任务报 "no configured signatory"。
详见 `gearui-kit/build.gradle.kts` 里的说明。

**必须在 macOS 上发布**——三个 iOS target 只有 macOS 能编，在 Linux 上它们会从
上传内容里静默消失，而不是让构建失败。

```bash
./gradlew :gearui-kit:publishToMavenCentral
```

⚠️ 插件 0.30.0 下这条命令会**报 BUILD SUCCESSFUL 但什么都没上传**：它是个
lifecycle task（日志 `Skipping task ... as it has no actions`），产物只落到
`build/publish/staging/<uuid>/`。1.0.0-beta1 是直接把该 bundle POST 到 Portal 发的：

```bash
TOKEN=$(printf '%s:%s' "$USERNAME" "$PASSWORD" | base64)
curl -X POST -H "Authorization: Bearer $TOKEN" \
  -F "bundle=@build/publish/staging/<uuid>.zip" \
  "https://central.sonatype.com/api/v1/publisher/upload?name=com.gearui:gearui-kit:<版本>&publishingType=USER_MANAGED"
# -> 返回 deployment id，然后轮询：
curl -X POST -H "Authorization: Bearer $TOKEN" \
  "https://central.sonatype.com/api/v1/publisher/status?id=<deployment_id>"
```

`USER_MANAGED` 会停在 VALIDATED，最后那下 Publish 保持由人来点。
永远以 Portal 的状态为准，不要相信 Gradle 的退出码。
