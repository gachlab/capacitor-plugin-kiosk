// SPDX-License-Identifier: MIT
// Copyright (c) 2026 gachlab

import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.gachlab.capacitor.kiosk.example',
  appName: 'Kiosk Example',
  webDir: 'www',
  server: {
    androidScheme: 'https',
    cleartext: true,
  },
};

export default config;
