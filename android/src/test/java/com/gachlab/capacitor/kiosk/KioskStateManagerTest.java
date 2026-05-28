package com.gachlab.capacitor.kiosk;

import static org.junit.Assert.*;

import org.junit.Test;

/** Pure-JVM unit tests for the kiosk transition logic (no Android, no device). */
public class KioskStateManagerTest {

    @Test
    public void onObserved_sameState_returnsNull() {
        KioskStateManager m = new KioskStateManager();
        m.seed(false);
        assertNull(m.onObserved(false));
        m.seed(true);
        assertNull(m.onObserved(true));
    }

    @Test
    public void onObserved_falseToTrue_returnsEntered_withNullReason() {
        KioskStateManager m = new KioskStateManager();
        m.seed(false);
        KioskStateManager.Event ev = m.onObserved(true);
        assertNotNull(ev);
        assertEquals(KioskStateManager.Transition.ENTERED, ev.transition);
        assertNull(ev.reason);
    }

    @Test
    public void onObserved_trueToFalse_returnsExited_withUserReason() {
        KioskStateManager m = new KioskStateManager();
        m.seed(true);
        KioskStateManager.Event ev = m.onObserved(false);
        assertNotNull(ev);
        assertEquals(KioskStateManager.Transition.EXITED, ev.transition);
        assertEquals("user", ev.reason);
    }

    @Test
    public void onEnterApi_returnsEntered() {
        KioskStateManager m = new KioskStateManager();
        m.seed(false);
        KioskStateManager.Event ev = m.onEnterApi();
        assertEquals(KioskStateManager.Transition.ENTERED, ev.transition);
        assertNull(ev.reason);
        // baseline updated → next same observation is a no-op
        assertNull(m.onObserved(true));
    }

    @Test
    public void onExitApi_returnsExited_withApiReason_andUpdatesBaseline() {
        KioskStateManager m = new KioskStateManager();
        m.seed(true);
        KioskStateManager.Event ev = m.onExitApi();
        assertEquals(KioskStateManager.Transition.EXITED, ev.transition);
        assertEquals("api", ev.reason);
        assertNull(m.onObserved(false));
    }

    @Test
    public void apiEnterThenObservedFalse_isUserExit() {
        KioskStateManager m = new KioskStateManager();
        m.seed(false);
        m.onEnterApi();
        KioskStateManager.Event ev = m.onObserved(false);
        assertNotNull(ev);
        assertEquals(KioskStateManager.Transition.EXITED, ev.transition);
        assertEquals("user", ev.reason);
    }
}
