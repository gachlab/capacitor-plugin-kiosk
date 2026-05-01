# @gachlab/capacitor-kiosk

A Capacitor plugin for Android kiosk mode using the [Lock Task API](https://developer.android.com/work/dpc/dedicated-devices/lock-task-mode). Pin your app to the screen and prevent users from leaving.

**Android only.** Web stub returns `false` / throws errors.

## Installation

```bash
npm install @gachlab/capacitor-kiosk
npx cap sync
```

## Prerequisites

For kiosk mode to work, your app must be set as a **device owner** or the activity must be **whitelisted for lock task mode**. Without this, `enterKioskMode()` will reject with a `SecurityException`.

To set your app as device owner (on a factory-reset device):

```bash
adb shell dpm set-device-owner com.your.package/.MainActivity
```

## Usage

```typescript
import { KioskMode } from '@gachlab/capacitor-kiosk';

// Check if currently in kiosk mode
const { value } = await KioskMode.isInKioskMode();

// Enter kiosk mode
await KioskMode.enterKioskMode();

// Exit kiosk mode
await KioskMode.exitKioskMode();

// Toggle kiosk mode
await KioskMode.toggleKioskMode();
```

## API

### isInKioskMode()

```typescript
isInKioskMode() => Promise<{ value: boolean }>
```

Returns whether the device is currently in lock task (kiosk) mode.

---

### enterKioskMode()

```typescript
enterKioskMode() => Promise<void>
```

Enters kiosk mode. Rejects with an error if the app is not a device owner or the activity is not whitelisted.

---

### exitKioskMode()

```typescript
exitKioskMode() => Promise<void>
```

Exits kiosk mode. Rejects with an error if the app is not a device owner.

---

### toggleKioskMode()

```typescript
toggleKioskMode() => Promise<void>
```

Toggles kiosk mode based on the current state.

## Migration from v1

v2 is a breaking change due to the Capacitor 5 → 8 upgrade:

- Requires Capacitor 8+ (`@capacitor/core ^8.0.1`)
- Requires Android SDK 36, minSdk 24, Java 21
- Package moved from `cc.elysium.capacitor.plugin.kiosk` to `com.gachlab.capacitor.kiosk`
- Web stub now returns `{ value: false }` instead of `{ value: true }`
- Web stub now throws errors for enter/exit/toggle instead of silently succeeding
- `SecurityException` is now caught and returned as a rejected promise instead of crashing
