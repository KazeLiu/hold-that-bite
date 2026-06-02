# Umeng Analytics Consent Design

## Goal

给 `守住这口` 接入友盟+移动统计，在用户授权隐私政策后再初始化 SDK，并只上报匿名功能事件。

## Scope

- 首次打开展示隐私政策与用户协议提示。
- 用户同意后开启匿名统计；用户暂不启用时，App 继续作为本地记录工具运行。
- 设置页提供“匿名使用统计”开关和“隐私政策 / 用户协议”查看入口。
- 接入友盟统计 SDK 基础统计能力，AppKey 为 `6a1e645b9a7f376488eda983`，默认渠道为 `official`。
- 不接入友盟高级运营分析 `uyumao`，不申请定位权限，不读取应用列表。
- 不上传体重数值、饮食内容、备注文本、日期等个人记录内容。

## Events

- `app_open`: 统计开启后的应用打开事件。
- `bite_kept`: 点了“守住了”。
- `bite_missed`: 点了“没守住”。
- `snack_refusal_added`: 点了“拒绝零食 +1”。
- `weight_trend_enabled`: 打开体重趋势开关。
- `weight_trend_disabled`: 关闭体重趋势开关。
- `weight_trend_opened`: 进入体重趋势页。
- `weight_record_created`: 新增体重记录，只记录动作。
- `settings_opened`: 进入设置页。
- `privacy_policy_opened`: 查看隐私政策 / 用户协议。

## Compliance Notes

隐私政策需告知使用友盟 SDK，服务类型为统计分析，可能收集设备信息、网络信息、OAID、IP 地址等用于匿名统计。位置只允许基于网络信息做城市级粗略统计；本 App 不申请 GPS / 精确定位权限，不读取应用列表。SDK 必须延迟初始化：用户同意并开启匿名统计前不调用友盟初始化入口。
