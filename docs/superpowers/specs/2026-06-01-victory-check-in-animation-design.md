# Victory Check-In Animation Design

## Goal

Make the `守住了` check-in feel like a small win before the user fills note and optional weight fields.

## Interaction

- Tapping `守住了` triggers a victory emoji burst from the button center.
- Long pressing `守住了` continuously emits single emoji particles until release and is only a preview of the effect.
- Releasing after a long press does not record the check-in and does not open the victory card.
- After tap or release, the note and weight form appears as a victory card rather than a bottom sheet.
- The victory card rotates once around its vertical center axis while scaling from far to near, then settles in place.
- The card title uses large glowing `守住了` text with lightweight confetti falling in the title area.
- Emoji and confetti effects stay away from input fields so the form remains readable.
- `没守住` keeps the existing supplement bottom sheet and does not use celebration effects.

## Timing

- Tap burst: about 18 emojis, cleared before the victory card appears so it does not look like repeated taps.
- Long press stream: starts after about 260ms, emits one particle about every 90ms with a low active-particle cap to prevent buildup.
- Card delay: about 360ms after tap, about 300ms after long-press release.
- Card intro: one 360 degree axis rotation in about 780ms.

## Constraints

- Use existing `AppColors` tokens for app-level colors.
- Keep touch target size at least 48dp.
- Use transform and alpha animation; avoid layout-shifting animation.
- Keep changes local to the home check-in flow unless a small helper type is required.
