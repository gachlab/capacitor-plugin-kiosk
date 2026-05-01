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
}
