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

    private boolean checkKioskState() {
        ActivityManager am = (ActivityManager) getActivity()
                .getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
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
