# gearui-kit 发布流程

[English](./RELEASING.md) | 简体中文

维护者用。使用者不需要看这个——接入方式见 README。

## beta3 候选发布门槛

先读 [当前发布检查](BETA3_RELEASE_READINESS.md)。未提交工作区的本地通过不是发布批准。

1. 提交完整候选，包括新增 Token、生成代码、测试、API 基线、迁移说明与资源。
2. 运行 [CI 对照表](SPEC_CI_MAPPING.md) 全部检查，远端 CI 必须对应同一提交；
   生成 API dump 不能代替检查。
3. 验证消费方、iOS 宿主、Android/Web 示例及键盘/弹层/主题关键路径；标明未验平台。
4. 在 macOS 隔离暂存六个 Maven 模块，检查资源、依赖元数据、JS/iOS KLib 和源码。
   源码 composite build 通过不能替代 Maven 制品消费验证。
5. 审核破坏性变更与渲染限制，获得发布批准后再设置版本、打标签、签名上传。

仅本地、不签名、不上传的打包检查：

```bash
./gradlew :gearui-kit:publishToMavenLocal \
  -Dmaven.repo.local=/tmp/gearui-beta3-staging \
  -PPOM_VERSION=1.0.0-beta3 -PsigningInMemoryKey=
```

这不验证签名、Central 上传或消费方依赖解析。不能仅凭生命周期任务返回成功认定发布成功。

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

历史 beta1 记录（插件 0.30.0，不代表当前 0.35.0 的行为已经验证）：当时这条命令
**报 BUILD SUCCESSFUL 但什么都没上传**：它是个
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
