import { WebPlugin } from '@capacitor/core';

import type { KioskModePlugin } from './definitions';

export class KioskModeWeb extends WebPlugin implements KioskModePlugin {
  async isInKioskMode(): Promise<{ value: boolean }> {
    return { value: false };
  }

  async enterKioskMode(): Promise<void> {
    throw new Error('Kiosk mode is not supported on web');
  }

  async exitKioskMode(): Promise<void> {
    throw new Error('Kiosk mode is not supported on web');
  }

  async toggleKioskMode(): Promise<void> {
    throw new Error('Kiosk mode is not supported on web');
  }
}
