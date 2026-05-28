# Testing — @gachlab/capacitor-kiosk

This plugin follows the gachlab **test pyramid: unit + integration + e2e**.
The e2e layer reuses the harness pattern proven in `capacitor-background-geolocation`
(example host app + emulator driven by scripts — **no Appium**). CI runs all
layers in `.github/workflows/build.yml`.

**Android-only** (no iOS implementation), so the CI has no iOS job.

## Layers

| Layer | Web | Android |
|---|---|---|
| **Unit** | vitest (`src/__tests__`) | `KioskStateManager` pure JVM JUnit (`android/src/test`) |
| **Integration** (real OS) | — | instrumented (`android/src/androidTest`) — real `ActivityManager` |
| **E2E** (round-trip to JS) | — | `example-app` + auto enter/exit + logcat (`scripts/e2e-kiosk.sh`) |

The transition logic lives in a pure `KioskStateManager` (no Android/Capacitor
deps) so it unit-tests on the plain JVM; the `Plugin` keeps only
`startLockTask()` / `stopLockTask()` / `getLockTaskModeState()` calls and
`notifyListeners()`.

## Run locally

```bash
npm test                                              # web unit
cd android && ./gradlew test                          # JVM unit (KioskStateManager)
cd android && ./gradlew connectedDebugAndroidTest     # integration (needs emulator)
npm run build && (cd example-app && npm install && npx cap sync android && cd android && ./gradlew assembleDebug)
./.github/scripts/e2e-kiosk.sh                        # Android e2e (needs emulator)
```

## How the e2e harness works

`example-app/www/main.js` registers both listeners and auto-runs
`enterKioskMode → exitKioskMode` on load, logging each event with the
`[KIOSK-E2E]` tag. The script enables system screen-pinning via
`adb settings put global lock_to_app_enabled 1` so `startLockTask()` succeeds
without Device Owner, then greps logcat for the round-trip events
(`kioskEntered`, `kioskExited` with `reason: 'api'`, both with timestamps).

## Manual checklist (not automatable)

- **`kioskExited` with reason `'user'`** (lifecycle-detected exit): requires the
  user to actually exit screen pinning by gesture; verify by hand on a device.
- **Device Owner / firm lock task** (`LOCK_TASK_MODE_LOCKED`): requires device
  enrollment (factory reset + DPM); document for fleet deployments.
