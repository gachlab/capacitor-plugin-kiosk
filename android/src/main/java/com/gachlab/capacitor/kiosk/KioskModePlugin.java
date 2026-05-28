package com.gachlab.capacitor.kiosk;

import android.app.ActivityManager;
import android.content.Context;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "KioskMode")
public class KioskModePlugin extends Plugin {

    private final KioskStateManager stateManager = new KioskStateManager();

    @Override
    public void load() {
        try {
            stateManager.seed(checkKioskState());
        } catch (Exception e) {
            stateManager.seed(false);
        }
    }

    /**
     * Android does not broadcast lock-task changes to normal apps, so an exit
     * performed by the user (or a re-entry) is detected when the activity next
     * comes to the foreground.
     */
    @Override
    protected void handleOnResume() {
        try {
            KioskStateManager.Event ev = stateManager.onObserved(checkKioskState());
            if (ev != null) emit(ev);
        } catch (Exception e) {
            // Activity not available; nothing to report.
        }
    }

    private void emit(KioskStateManager.Event ev) {
        JSObject data = new JSObject();
        data.put("timestamp", System.currentTimeMillis());
        if (ev.transition == KioskStateManager.Transition.ENTERED) {
            notifyListeners("kioskEntered", data);
        } else {
            data.put("reason", ev.reason);
            notifyListeners("kioskExited", data);
        }
    }

    private boolean checkKioskState() {
        ActivityManager am = (ActivityManager) getActivity().getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        return am.getLockTaskModeState() > ActivityManager.LOCK_TASK_MODE_NONE;
    }

    @PluginMethod
    public void isInKioskMode(PluginCall call) {
        try {
            JSObject ret = new JSObject();
            ret.put("value", checkKioskState());
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("Failed to check kiosk mode state", e);
        }
    }

    @PluginMethod
    public void enterKioskMode(PluginCall call) {
        try {
            getActivity().startLockTask();
            emit(stateManager.onEnterApi());
            call.resolve();
        } catch (SecurityException e) {
            call.reject("Cannot enter kiosk mode: app must be device owner or activity must be whitelisted", e);
        } catch (Exception e) {
            call.reject("Failed to enter kiosk mode", e);
        }
    }

    @PluginMethod
    public void exitKioskMode(PluginCall call) {
        try {
            getActivity().stopLockTask();
            emit(stateManager.onExitApi());
            call.resolve();
        } catch (SecurityException e) {
            call.reject("Cannot exit kiosk mode: app must be device owner", e);
        } catch (Exception e) {
            call.reject("Failed to exit kiosk mode", e);
        }
    }

    @PluginMethod
    public void toggleKioskMode(PluginCall call) {
        if (checkKioskState()) {
            exitKioskMode(call);
        } else {
            enterKioskMode(call);
        }
    }
}
