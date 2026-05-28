// SPDX-License-Identifier: MIT
// Copyright (c) 2026 gachlab
//
// Minimal host page exercising the kiosk plugin on Android (the plugin has no
// iOS implementation). On load, registers both event listeners and runs the
// auto-sequence enterKioskMode → exitKioskMode so the e2e harness can grep
// logcat for the round-trip events. The e2e CI step enables system screen
// pinning beforehand so startLockTask() succeeds without Device Owner.

/* global Capacitor */

document.addEventListener('DOMContentLoaded', async () => {
  const KioskMode = Capacitor.Plugins.KioskMode;

  const out = document.getElementById('log');
  const stateEl = document.querySelector('[data-testid="kiosk-state"]');
  const lastEvEl = document.querySelector('[data-testid="last-event"]');

  const log = (label, data) => {
    const line =
      `[${new Date().toISOString().slice(11, 19)}] ${label}` +
      (data === undefined ? '' : ' ' + JSON.stringify(data));
    out.textContent = line + '\n' + out.textContent;
    lastEvEl.textContent = label;
  };

  async function safe(label, fn) {
    try {
      const r = await fn();
      log(label, r);
      return r;
    } catch (e) {
      log(label + ' ERROR', { message: e?.message ?? String(e) });
    }
  }

  KioskMode.addListener('kioskEntered', (event) => {
    stateEl.textContent = 'true';
    console.log('[KIOSK-E2E] event:kioskEntered ' + JSON.stringify(event));
    log('event:kioskEntered', event);
  });
  KioskMode.addListener('kioskExited', (event) => {
    stateEl.textContent = 'false';
    console.log('[KIOSK-E2E] event:kioskExited ' + JSON.stringify(event));
    log('event:kioskExited', event);
  });

  document.getElementById('enter').onclick = () => safe('enterKioskMode', () => KioskMode.enterKioskMode());
  document.getElementById('exit').onclick = () => safe('exitKioskMode', () => KioskMode.exitKioskMode());
  document.getElementById('check').onclick = () =>
    safe('isInKioskMode', async () => {
      const r = await KioskMode.isInKioskMode();
      stateEl.textContent = String(r.value);
      return r;
    });

  // Auto-sequence so the e2e harness has deterministic round-trip log lines.
  await safe('enterKioskMode', () => KioskMode.enterKioskMode());
  await safe('exitKioskMode', () => KioskMode.exitKioskMode());

  log('ready');
});
