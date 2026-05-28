package com.gachlab.capacitor.kiosk;

import static org.junit.Assert.*;

import android.app.ActivityManager;
import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Integration tests — read REAL lock-task state from the device's
 * {@link ActivityManager} and drive the real {@link KioskStateManager} with it.
 * Runs on an emulator/device, no mocks. (Actually entering lock task here is
 * brittle without Device Owner, so the e2e harness covers the full enter/exit
 * round-trip; this layer asserts the OS-read path the plugin depends on.)
 */
@RunWith(AndroidJUnit4.class)
public class KioskIntegrationTest {

    private boolean isInKioskNow() {
        Context ctx = ApplicationProvider.getApplicationContext();
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getLockTaskModeState() != ActivityManager.LOCK_TASK_MODE_NONE;
    }

    @Test
    public void getLockTaskModeState_isReadable_fromRealOs() {
        Context ctx = ApplicationProvider.getApplicationContext();
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        int state = am.getLockTaskModeState();
        assertTrue(
            state == ActivityManager.LOCK_TASK_MODE_NONE ||
                state == ActivityManager.LOCK_TASK_MODE_LOCKED ||
                state == ActivityManager.LOCK_TASK_MODE_PINNED
        );
    }

    @Test
    public void stateManager_baselineFromRealOs_detectsAFlip() {
        boolean inKiosk = isInKioskNow();
        KioskStateManager manager = new KioskStateManager();
        manager.seed(inKiosk);
        assertNull("same state must produce no transition", manager.onObserved(inKiosk));
        KioskStateManager.Event ev = manager.onObserved(!inKiosk);
        assertNotNull("flipped state must produce a transition", ev);
    }
}
