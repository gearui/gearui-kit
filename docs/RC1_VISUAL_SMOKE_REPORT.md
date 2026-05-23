# RC-1 Visual Smoke Report

Status: **进行中 — 自动化部分已覆盖，交互/iOS 部分待人工。**
Mode: release validation（只记录，不现场改）。Severity: **P0 = release blocker / P1 = visible regression / P2 = post-1.0 polish**。
Build: HEAD `9404389`，sample = `com.gearui.kit.sample`，设备 Android（1440×3200），版本号显示 **1.0.0**。

## 工具限制（必须说明）

通过 adb 截图 + 坐标点击驱动 smoke 有两个硬限制，导致**无法可靠地脚本化逐项核对**：
1. **主题在进程重启后重置为「跟随系统」**——切到浅色/暗紫后若 app 被重启即丢失，必须在同一会话内连续操作。
2. **从首页按返回直接退出 app**——难以在同一会话内稳定地逐页进出。
3. 弹层打开 / 输入聚焦 / 键盘 / 滚动阻断 / 导航 badge 溢出等**交互项需要真实触摸序列**，盲点击不可靠。
4. **iOS Simulator** 仅做了编译验证，未做交互式 UI smoke。

→ 因此本报告只对"能可靠自动验证的渲染项"下结论；交互项与 iOS 标注 **PENDING（待人工）**，不伪造绿灯。

## 已验证（自动化，Android）

| 组件/页面 | 主题 | 结果 | 备注 |
|---|---|---|---|
| 设置页（语言/主题/关于） | Light | **PASS** | 白底、边框、单选选中态、文字层级正常；版本 1.0.0 |
| 设置页 | Dark | **PASS** | 主题切换器含 浅色/深色/暗紫/跟随系统 |
| Badge（红点/数字/自定义/圆形/方形/气泡） | Dark | **PASS** | 红底**白字**（destructiveForeground），本轮 P0/P1 修复点无回归 |
| 首页 / 组件列表（NavBar/SearchBar/SectionHeader/Cell） | Dark | **PASS** | 安全区、间距、分割线、文字层级正常 |
| ActionSheet（弹出 + scrim） | Dark | **PASS** | scrim 纯黑 55% 明显压暗，sheet 浮起（scrim 修复点） |
| Dialog（带标题对话框） | Dark | **PASS**（早前会话验证） | 模态遮罩正常 |
| 主题切换 Light↔Dark↔DarkPurple | — | **PASS** | 切换生效（同会话内） |

编译层闸门（全绿，见 RC_READINESS_SNAPSHOT）：deprecation=0 / token_compat / hardcoded-color / apiCheck / metadata / iOS compile / sample Android。

## 待人工 smoke（PENDING）

以下需在**同一会话内连续操作**或**真实触摸**，建议人工过一遍；本轮改动点已标注重点看什么。

### Theme / Contrast（重点：本轮 feedback foreground 改动）
- [ ] Button filled states（**warning/success/destructive** 三色实底文字）— Light + Dark + DarkPurple；**重点：warning 实底文字应为深色、不再白字不可读**
- [ ] Tag DARK variants（success/warning/danger）— 三主题
- [ ] Toast / Snackbar / Notification — 三主题；Toast 三态实底文字
- [ ] Progress inside label（>50% 进度条内文字）— 三主题
- [ ] Badge — Light + DarkPurple（Dark 已 PASS）

### Overlay Family（重点：scrim/radius/border/dark contrast/safe area/背景滚动阻断）
- [ ] Dialog / Popup / BottomSheet / ActionSheet / Select / Popover / ContextMenu — Dark + DarkPurple；**重点：scrim 一致压暗、背景列表不可滚动、安全区正确**

### Navigation / Layout
- [ ] NavBar / BottomNavBar / Page — 安全区、icon/文字对齐、**badge 溢出**、暗色 nav 对比；edge-to-edge / keyboard inset

### Input Family
- [ ] Input / SearchBar / Textarea / Form validation — **focus ring、placeholder 对比、键盘 dismiss、disabled/readOnly**（注：Input 状态模型为 1.1 known debt，此处只验证无明显断裂）

### Primitives
- [ ] Card / Cell / Avatar / ListItem / SectionHeader — **radius 一致性**（Radius 已对齐 Shapes）、spacing 一致性、暗色 surface 层次

### iOS
- [ ] 以上全部在 iOS Simulator 过一遍，重点找平台差异（安全区、字体、圆角、scrim、键盘）

## 当前结论

- **已验证项 P0 = 0、P1 = 0**（无回归）；本轮两个报告 bug（Badge 暗色黑字、ActionSheet scrim）确认已修。
- 交互项与 iOS 为 **PENDING**，需人工完成后才能判定整体 P0/P1。
- **建议**：人工按上方 checklist 过一遍 Android（三主题）+ iOS；若 P0=0、P1 可接受，则进入 **RC-2 publish dry-run**。不在 smoke 阶段现场改代码——发现问题按 severity 记录，P0 单独修。
