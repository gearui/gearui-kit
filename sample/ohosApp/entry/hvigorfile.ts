import { hapTasks } from '@ohos/hvigor-ohos-plugin';
import * as fs from 'fs';
import * as path from 'path';

export default {
  system: hapTasks, /* Built-in plugin of Hvigor. It cannot be modified. */
  plugins: [requireKotlinArtifacts(), copySampleAssets()]
}

/**
 * Fails the build when the Kotlin/Native artifacts are missing, and says how to
 * produce them.
 *
 * The KuiklyUI template this was adapted from downloads a prebuilt libshared.so
 * from Tencent's CDN when the file is absent. That is right for their demo and
 * dangerous here: the download is *their* demo binary, so a missing local build
 * would silently produce an app that launches, renders, and is not GearUI's
 * sample at all. Failing loudly is the only safe behaviour.
 *
 * Both files come from the ohos configuration of the Gradle build, which is
 * separate because the KuiklyUI artifacts carrying ohosArm64 are published
 * against Kotlin 2.0.21-KBA-010 rather than the 2.1.21 the rest of the build
 * uses.
 */
function requireKotlinArtifacts(): HvigorPlugin {
  const BUILD_CMD =
    './gradlew -c settings.ohos.gradle.kts :sample:linkSharedDebugSharedOhosArm64';

  return {
    pluginId: 'gearuiRequireKotlinArtifacts',
    apply(node: HvigorNode) {
      node.registerTask({
        name: 'gearui_require_kotlin_artifacts',
        run: () => {
          const entryDir = node.getNodePath();
          const so = path.join(entryDir, 'libs', 'arm64-v8a', 'libshared.so');
          const header = path.join(
            entryDir, 'src', 'main', 'cpp', 'thirdparty', 'biz_entry', 'libshared_api.h');

          const missing = [so, header].filter((f) => !fs.existsSync(f));
          if (missing.length === 0) {
            return;
          }

          const built = path.join(
            entryDir, '..', '..', 'build', 'bin', 'ohosArm64', 'sharedDebugShared');
          throw new Error(
            `Kotlin artifacts missing:\n` +
            missing.map((f) => `  ${f}`).join('\n') +
            `\n\nBuild them from the repository root:\n` +
            `  ${BUILD_CMD}\n\n` +
            `then copy the results out of ${built}:\n` +
            `  libshared.so     -> entry/libs/arm64-v8a/\n` +
            `  libshared_api.h  -> entry/src/main/cpp/thirdparty/biz_entry/\n`
          );
        },
        postDependencies: ['default@PreBuild']
      })
    }
  }
}

/**
 * Stages the image assets the sample loads at runtime.
 *
 * Two source directories, matching what the other hosts do — the iOS pod
 * resources sync and the web bundle both take gearui-kit's icons plus the
 * sample's own files. Icons are loaded through coil3 from
 * `assets://icons/<name>.png`, so a missing directory shows as correctly sized
 * blank boxes rather than an error.
 */
function copySampleAssets(): HvigorPlugin {
  return {
    pluginId: 'gearuiCopySampleAssets',
    apply(node: HvigorNode) {
      node.registerTask({
        name: 'gearui_copy_assets',
        run: () => {
          const entryDir = node.getNodePath();
          const repoRoot = path.join(entryDir, '..', '..', '..');
          const sources = [
            path.join(repoRoot, 'gearui-kit', 'src', 'commonMain', 'assets'),
            path.join(repoRoot, 'sample', 'src', 'commonMain', 'assets'),
          ];
          const destDir = path.join(
            entryDir, 'build', 'default', 'intermediates', 'res', 'default', 'resources', 'resfile');

          fs.mkdirSync(destDir, { recursive: true });
          for (const sourceDir of sources) {
            if (!fs.existsSync(sourceDir)) {
              throw new Error(`assets directory not found: ${sourceDir}`);
            }
            fs.cpSync(sourceDir, destDir, { recursive: true, force: true });
            console.log(`assets copied: ${sourceDir} -> ${destDir}`);
          }
        },
        dependencies: [`default@CompileResource`],
        postDependencies: [`default@CompileArkTS`]
      })
    }
  }
}
