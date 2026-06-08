# Hold That Bite Agent Notes

## Color Tokens

- Before adding or changing UI colors, read `docs/design/colors.md`.
- Prefer `AppColors` from `app/src/main/kotlin/com/holdthatbite/ui/AppColors.kt` instead of hardcoded `Color(0x...)` values.
- Current required tokens:
  - `ThemeBlue`: main brand/action blue.
  - `WeightIncreaseSoft`: soft red for weight increase and highest-weight emphasis.
  - `WeightDecreaseSoft`: soft green for weight decrease and lowest-weight emphasis.

## Lessons Learned

### Compose `pointerInput` Stable Keys

- 触发信号：按下自定义 Compose 按钮后，点击提交、长按预览或动画协程突然失效。
- 根因 / 约束：`pointerInput` 的 key 若包含每次重组都会变化的 lambda / 状态对象，按下后触发的状态更新会取消当前手势协程。
- 正确做法：`pointerInput(Unit)` 保持手势协程稳定，通过 `rememberUpdatedState` 读取最新回调或状态。
- 验证方式：用 logcat 确认短按完整输出 `tap action commit`，长按完整输出 `long preview start` 和 release ticks。
- 适用范围：所有自定义 Compose 手势组件，尤其是按下时会触发动画状态变化的按钮。

### App 前台恢复刷新

- 触发信号：App 从后台回到前台后，页面仍停留在旧日期、旧数据或旧状态。
- 根因 / 约束：只靠进入时初始化不够，跨天或后台期间的本地数据变化不会自动刷新。
- 正确做法：在 Activity/Compose 生命周期 `ON_RESUME` 时重读本地数据，并把当前日期视图同步到今天。
- 验证方式：切到后台修改或跨天后再回前台，确认首页日期和记录同步刷新。
- 适用范围：依赖本地缓存、日期驱动界面、可能跨天失真的主页或日历页。
