# SolarEase Design System

## Visual world

Energy field guide: a calm, trustworthy Android-native interface inspired by a household energy notebook. It combines warm mineral surfaces, deep solar green, and a limited sunrise accent to turn bills, estimates, and installation progress into a readable journey.

## Rules

- Use the solar gradient only for a hero, selected progress, or a single high-value summary—not dense copy or ordinary cards.
- Prefer editorial sections, metric strips, list rows, timelines, and dividers over repeated elevated cards.
- Green signals the next productive action; amber signals energy/cost attention; red remains reserved for errors and due states.
- Keep text on tinted surfaces at Material color-role contrast; all controls remain at least 48 dp.
- On compact widths stack information; on medium and expanded widths retain the rail and use two-column metric/option groups where existing layouts permit.
- Every empty/loading/error state explains the current status and the next recovery action.

## Component vocabulary

- `StitchPrimaryButton`: single decisive action per task step.
- `StitchStepProgressBar`: visible journey progress for assessment and installation.
- `StitchSelectableCard`: choice row with a clear selected state, not a generic card grid.
- Tonal surfaces: quiet content grouping; white is reserved for form controls and high-focus content.

## Screen migration

- Home: energy command centre with a state-led hero, primary next step, concise tools, and expandable secondary detail.
- Assessment/order: a consistent guided journey with progress, visual choice rows, estimate summaries, payment states, and tracking timeline.
- Analytics/dashboard/system detail: data-first hierarchy with charts, metric bands, and status summaries.
- Profile/legal/auth: simplified reading hierarchy, grouped rows, and clear confirmation/error states.
