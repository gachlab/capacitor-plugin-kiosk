package com.gachlab.capacitor.kiosk;

/**
 * Pure transition logic for kiosk (Lock Task) state. NO Android/Capacitor deps,
 * so it unit-tests on the plain JVM. The {@code Plugin} class keeps only the
 * bridge wiring: it reads {@code ActivityManager.getLockTaskModeState()} and
 * emits via {@code notifyListeners()}.
 */
public final class KioskStateManager {

    public enum Transition {
        ENTERED,
        EXITED
    }

    /** A transition observed between two polls (or driven by an API call). */
    public static final class Event {

        public final Transition transition;
        /** {@code 'user' | 'system' | 'api'} for {@link Transition#EXITED}; {@code null} for {@link Transition#ENTERED}. */
        public final String reason;

        public Event(Transition transition, String reason) {
            this.transition = transition;
            this.reason = reason;
        }
    }

    private boolean lastKioskState;

    /** Seed the baseline (e.g. in {@code Plugin.load()}). */
    public void seed(boolean isInKioskMode) {
        lastKioskState = isInKioskMode;
    }

    /**
     * Process a state reading from the OS (e.g. {@code handleOnResume()} polling).
     * Returns the transition event when the state changed, {@code null} otherwise.
     * A lifecycle-detected exit is reported with {@code reason='user'} (a user
     * gesture is the typical cause; firm detection of system exits needs Device Owner).
     */
    public Event onObserved(boolean isInKioskMode) {
        if (isInKioskMode == lastKioskState) return null;
        lastKioskState = isInKioskMode;
        return new Event(isInKioskMode ? Transition.ENTERED : Transition.EXITED, isInKioskMode ? null : "user");
    }

    /** API-driven enter — emit after a successful {@code enterKioskMode()}. */
    public Event onEnterApi() {
        lastKioskState = true;
        return new Event(Transition.ENTERED, null);
    }

    /** API-driven exit — emit after a successful {@code exitKioskMode()}. */
    public Event onExitApi() {
        lastKioskState = false;
        return new Event(Transition.EXITED, "api");
    }
}
