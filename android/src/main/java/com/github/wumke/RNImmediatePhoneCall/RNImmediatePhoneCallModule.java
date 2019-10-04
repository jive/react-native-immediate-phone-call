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

public class RNImmediatePhoneCallModule extends ReactContextBaseJavaModule implements PermissionListener {

    private Promise promise;
    private String number = "";
    private static final int PERMISSIONS_REQUEST_ACCESS_CALL = 101;

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

        try {
            boolean hasPermission = ContextCompat.checkSelfPermission(getReactApplicationContext().getBaseContext(),
                    android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;

            if (hasPermission) {
                call(Uri.encode(number), promise, currentActivity);
            } else {
                this.promise = promise;
                this.number = Uri.encode(number);

                ActivityCompat.requestPermissions(currentActivity,
                        new String[]{android.Manifest.permission.CALL_PHONE},
                        PERMISSIONS_REQUEST_ACCESS_CALL);
            }
        } catch(Exception e) {
            promise.reject(new JSApplicationIllegalArgumentException("Could not open URL '" + number + "': " + e.getMessage()));
        }
    }

    @SuppressLint("MissingPermission")
    private void call(String number, Promise promise, Activity activity) {
        try {
            String url = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url).normalizeScheme());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            promise.resolve("success");
        } catch (Exception e) {
            promise.reject(new JSApplicationIllegalArgumentException("Could not open URL '" + number + "': " + e.getMessage()));
        }
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_CALL) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                call(number, promise, getCurrentActivity());
                return true;
            }
        }
        return false;
    }
}
