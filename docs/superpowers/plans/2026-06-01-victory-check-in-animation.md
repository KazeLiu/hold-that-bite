# 守住了打卡动效实现计划

> **给 agentic workers：** 必须使用 superpowers:subagent-driven-development（推荐）或 superpowers:executing-plans 按任务执行本计划。步骤使用 checkbox（`- [ ]`）语法追踪。

**目标：** 在生产 Android Compose 应用中加入已确认的 `守住了` emoji 爆发和胜利卡片动效。

**架构：** 当前应用的页面级 Compose UI 已经集中在主流程附近，因此动效保持在首页打卡流程内实现。新增小型本地模型承载按钮 emoji 粒子和彩纸参数，替换 `守住了` 按钮渲染，并为守住打卡新增胜利卡片弹窗；`没守住` 继续沿用现有底部补充信息弹窗。

**技术栈：** Android Kotlin、Jetpack Compose Material 3、Compose animation、Gradle Android plugin。

---

### 任务 1：接入守住打卡庆祝动效

**文件：**
- 修改：`app/src/main/kotlin/com/holdthatbite/MainActivity.kt`

- [ ] **步骤 1：添加动画依赖导入**

添加 Compose 动画、手势和弹窗相关 import，例如 `Animatable`、`LinearEasing`、`LaunchedEffect`、`mutableStateListOf`、`pointerInput`、`detectTapGestures` 和 `Dialog`。

- [ ] **步骤 2：添加本地粒子模型**

在颜色常量附近添加不可变数据类，用于描述按钮 emoji 粒子和标题彩纸参数。

- [ ] **步骤 3：替换守住按钮**

将原有 `Button("守住了")` 替换为 `CelebrationCheckInButton` composable，使它能在调用 `onCheckIn(BiteStatus.KEPT)` 前触发点击爆发和长按预览粒子。

### 任务 2：新增胜利卡片弹窗

**文件：**
- 修改：`app/src/main/kotlin/com/holdthatbite/MainActivity.kt`

- [ ] **步骤 1：将守住记录路由到新弹窗**

当 `activeSheet == CHECK_IN_SUPPLEMENT` 且状态为 `BiteStatus.KEPT` 时，展示 `VictoryCheckInSupplementDialog`；其他状态继续展示 `CheckInSupplementSheet`。

- [ ] **步骤 2：实现半圈卡片入场**

使用 `Animatable` 从 `0f` 动画到 `1f`，时长约 `360ms`，缓动使用 `FastOutSlowInEasing`。将进度映射为 `rotationY = 180f * (1f - progress)`、`scale = 0.12f + 0.88f * progress` 和透明度。

- [ ] **步骤 3：添加发光标题和彩纸**

在卡片顶部居中渲染大号 `守住了` 文本。彩纸只在标题区域内下落，并在弹窗可见期间循环播放。

### 任务 3：验证

**文件：**
- 除任务 1 和任务 2 的源文件改动外，不需要额外源文件改动。

- [ ] **步骤 1：运行 Gradle 单元测试**

运行：`.\gradlew.bat testDebugUnitTest`

- [ ] **步骤 2：运行 debug 构建**

运行：`.\gradlew.bat assembleDebug`

- [ ] **步骤 3：检查模拟器安装目标**

通过 `adb devices` 检查常见 MuMu 端口；如果 MuMu 模拟器在线，则安装 debug APK。
