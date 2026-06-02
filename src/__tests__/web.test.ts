import { describe, it, beforeEach } from 'node:test';
import assert from 'node:assert/strict';
import { KioskModeWeb } from '../web';

describe('KioskModeWeb', () => {
  let plugin: KioskModeWeb;

  beforeEach(() => {
    plugin = new KioskModeWeb();
  });

  it('isInKioskMode returns false on web', async () => {
    const result = await plugin.isInKioskMode();
    assert.deepStrictEqual(result, { value: false });
  });

  it('enterKioskMode throws on web', async () => {
    await assert.rejects(() => plugin.enterKioskMode(), /not supported on web/);
  });

  it('exitKioskMode throws on web', async () => {
    await assert.rejects(() => plugin.exitKioskMode(), /not supported on web/);
  });

  it('toggleKioskMode throws on web', async () => {
    await assert.rejects(() => plugin.toggleKioskMode(), /not supported on web/);
  });
});
