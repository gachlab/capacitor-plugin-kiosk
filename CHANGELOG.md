# Changelog

## 2.0.0

### Breaking Changes

- **Migrated from Capacitor 5 to Capacitor 8**
- **Package moved** from `cc.elysium.capacitor.plugin.kiosk` to `com.gachlab.capacitor.kiosk`
- **Web stub** now returns `{ value: false }` (was `true`) and throws on enter/exit/toggle
- Requires Android SDK 36, minSdk 24, Java 21

### Bug Fixes

- **Removed `@PluginMethod` from private `isInKioskMode()` helper** — was causing reflection issues
- **Fixed double `call.resolve()` in `toggleKioskMode()`** — enter/exit already resolve, removed duplicate
- **Added try-catch for `SecurityException`** on `startLockTask()`/`stopLockTask()` — now rejects with error message instead of crashing
- **Made `TAG` static final** (was instance field)
- **Removed SDK version check** for `isInLockTaskMode()` — minSdk is now 24, `getLockTaskModeState()` is always available

### Improvements

- Migrated build from Rollup to Vite + vite-plugin-dts
- Upgraded to TypeScript 6, AGP 8.13.0, Gradle 8.14.3
- Removed `jcenter()` repository (deprecated)
- Removed iOS podspec (Android-only plugin)
- Added JSDoc comments to TypeScript interface
- Added tests: 4 web (Vitest), 1 Android (JUnit)
- Added GitHub Actions CI and publish workflow
