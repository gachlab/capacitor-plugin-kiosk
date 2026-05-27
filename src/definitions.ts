import type { PluginListenerHandle } from '@capacitor/core';

export interface KioskModePlugin {
  /**
   * Returns whether the device is currently in kiosk (lock task) mode.
   */
  isInKioskMode(): Promise<{ value: boolean }>;

  /**
   * Enters kiosk mode using Android's Lock Task API.
   * Requires the app to be a device owner or the activity to be whitelisted.
   */
  enterKioskMode(): Promise<void>;

  /**
   * Exits kiosk mode.
   */
  exitKioskMode(): Promise<void>;

  /**
   * Toggles kiosk mode on or off based on the current state.
   */
  toggleKioskMode(): Promise<void>;

  /**
   * Emitted when the app enters kiosk (lock task) mode. `timestamp` is the
   * epoch time in milliseconds when it was observed.
   */
  addListener(
    eventName: 'kioskEntered',
    listenerFunc: (event: { timestamp: number }) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Emitted when the app leaves kiosk mode.
   *
   * `reason` is `'api'` when triggered by `exitKioskMode()`, or `'user'` when an
   * exit is detected on the next foreground (Android does not push lock-task
   * changes to normal apps, so user/system exits are detected on resume —
   * `'system'` is reserved for a future Device Owner break detector).
   * `timestamp` is the epoch time in milliseconds when it was observed.
   */
  addListener(
    eventName: 'kioskExited',
    listenerFunc: (event: { reason: 'user' | 'system' | 'api'; timestamp: number }) => void,
  ): Promise<PluginListenerHandle>;

  /**
   * Removes all listeners for this plugin.
   */
  removeAllListeners(): Promise<void>;
}
