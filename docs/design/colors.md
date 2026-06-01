# Color Tokens

This project keeps user-facing color choices in named tokens so future Codex work can reuse them without asking for raw values again.

## Required Tokens

| Token | Hex | Usage |
| --- | --- | --- |
| `ThemeBlue` | `#23ADE5` | Main brand color, primary actions, selected navigation, chart line. |
| `WeightIncreaseSoft` | `#FBE4E6` | Soft red background for weight increase, highest weight, and related inline/chart emphasis. |
| `WeightDecreaseSoft` | `#DDF4E5` | Soft green background for weight decrease, lowest weight, and related inline/chart emphasis. |
| `CelebrationBlueDeep` | `#1489BC` | Deeper blue for celebratory primary button gradients and victory-card depth. |
| `CelebrationGold` | `#FFC83D` | Warm confetti accent for check-in success celebrations. |
| `CelebrationPink` | `#FF8AB3` | Playful confetti accent for check-in success celebrations. |
| `CelebrationMint` | `#8FE0B2` | Fresh confetti accent for check-in success celebrations. |
| `CelebrationSky` | `#A7DFFF` | Light blue confetti accent for check-in success celebrations. |

## Implementation

- Compose code should import `com.holdthatbite.ui.AppColors` and use the named token.
- Avoid new hardcoded `Color(0x...)` values in feature code unless a new token is first added here and in `AppColors`.
- If a color has a domain meaning, name it by meaning instead of appearance. For example, use `WeightIncreaseSoft` instead of `LightRed`.
