import { describe, it, expect, beforeEach } from 'vitest';
import { KioskModeWeb } from '../web';

describe('KioskModeWeb', () => {
  let plugin: KioskModeWeb;

  beforeEach(() => {
    plugin = new KioskModeWeb();
  });

  it('isInKioskMode returns false on web', async () => {
    const result = await plugin.isInKioskMode();
    expect(result).toEqual({ value: false });
  });

  it('enterKioskMode throws on web', async () => {
    await expect(plugin.enterKioskMode()).rejects.toThrow('not supported on web');
  });

  it('exitKioskMode throws on web', async () => {
    await expect(plugin.exitKioskMode()).rejects.toThrow('not supported on web');
  });

  it('toggleKioskMode throws on web', async () => {
    await expect(plugin.toggleKioskMode()).rejects.toThrow('not supported on web');
  });
});
