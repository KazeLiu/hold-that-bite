# Color Tokens

This project keeps user-facing color choices in named tokens so future Codex work can reuse them without asking for raw values again.

## Required Tokens

| Token | Hex | Usage |
| --- | --- | --- |
| `ThemeBlue` | `#23ADE5` | Main brand color, primary actions, selected navigation, chart line. |
| `WeightIncreaseSoft` | `#FBE4E6` | Soft red background for weight increase, highest weight, and related inline/chart emphasis. |
| `WeightDecreaseSoft` | `#DDF4E5` | Soft green background for weight decrease, lowest weight, and related inline/chart emphasis. |
| `FastingEase` | `#42A66A` | Green foreground for easier fasting plans such as `12+12` and `14+10`. |
| `FastingEaseSoft` | `#E1F5E8` | Green soft background for easier fasting-plan chips. |
| `FastingModerate` | `#D39B23` | Yellow foreground for the standard `16+8` fasting plan. |
| `FastingModerateSoft` | `#FFF2CD` | Yellow soft background for the standard fasting-plan chip. |
| `FastingHard` | `#E36F78` | Red foreground for harder fasting plans such as `18+6` and `20+4`. |
| `FastingHardSoft` | `#FBE1E4` | Red soft background for harder fasting-plan chips. |
| `CelebrationBlueDeep` | `#1489BC` | Deeper blue for celebratory primary button gradients and victory-card depth. |
| `CelebrationGold` | `#FFC83D` | Warm confetti accent for check-in success celebrations. |
| `CelebrationPink` | `#FF8AB3` | Playful confetti accent for check-in success celebrations. |
| `CelebrationMint` | `#8FE0B2` | Fresh confetti accent for check-in success celebrations. |
| `CelebrationSky` | `#A7DFFF` | Light blue confetti accent for check-in success celebrations. |
| `DarkBackground` | `#10181D` | Night-mode app background. |
| `DarkSurface` | `#172329` | Night-mode cards, sheets, bottom navigation, and dialog surfaces. |
| `DarkSurfaceSubtle` | `#1E2D34` | Night-mode card gradients and subtle grouped rows. |
| `DarkTextPrimary` | `#EAF7FB` | Night-mode primary text. |
| `DarkTextSecondary` | `#A7BDC6` | Night-mode secondary text and subdued icons. |
| `DarkBorder` | `#31464F` | Night-mode borders, dividers, chart grid lines, and empty calendar markers. |
| `DarkNeutral` | `#22323A` | Night-mode neutral fills and unselected controls. |
| `DarkWeightIncreaseSoft` | `#4A343A` | Night-mode opaque soft red for highest-weight and missed-state emphasis. |
| `DarkWeightDecreaseSoft` | `#2F4C3E` | Night-mode opaque soft green for lowest-weight and kept-state emphasis. |
| `DarkFastingEaseSoft` | `#234638` | Night-mode soft green background for easier fasting-plan chips. |
| `DarkFastingModerateSoft` | `#4A4025` | Night-mode soft yellow background for the standard fasting-plan chip. |
| `DarkFastingHardSoft` | `#4A3034` | Night-mode soft red background for harder fasting-plan chips. |

## Implementation

- Compose code should import `com.holdthatbite.ui.AppColors` and use the named token.
- Avoid new hardcoded `Color(0x...)` values in feature code unless a new token is first added here and in `AppColors`.
- If a color has a domain meaning, name it by meaning instead of appearance. For example, use `WeightIncreaseSoft` instead of `LightRed`.
