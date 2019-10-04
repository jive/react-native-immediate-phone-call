package com.github.wumke.RNImmediatePhoneCall;

import android.app.Activity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.JSApplicationIllegalArgumentException;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.PermissionListener;

public class RNImmediatePhoneCallModule extends ReactContextBaseJavaModule {

    public RNImmediatePhoneCallModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "RNImmediatePhoneCall";
    }

    @ReactMethod
    public void immediatePhoneCall(String number, Promise promise) {

        if (number == null || number.isEmpty()) {
            promise.reject(new JSApplicationIllegalArgumentException("Invalid number: " + number));
            return;
        }

        Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            promise.reject(new JSApplicationIllegalArgumentException("Current Activity is null: " + number));
            return;
        }

        String url = "tel:" + number;
        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url).normalizeScheme());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            currentActivity.startActivity(intent);
            promise.resolve("success");
        } catch(Exception e) {
            promise.reject(new JSApplicationIllegalArgumentException("Could not open URL '" + url + "': " + e.getMessage()));
        }
    }
}