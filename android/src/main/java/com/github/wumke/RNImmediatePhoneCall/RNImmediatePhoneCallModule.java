package com.github.wumke.RNImmediatePhoneCall;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class RNImmediatePhoneCallModule extends ReactContextBaseJavaModule {

    private static RNImmediatePhoneCallModule rnImmediatePhoneCallModule;

    private ReactApplicationContext reactContext;
    private Promise promise;
    private static String number = "";
    private static final int PERMISSIONS_REQUEST_ACCESS_CALL = 101;

    public RNImmediatePhoneCallModule(ReactApplicationContext reactContext) {
        super(reactContext);
        if (rnImmediatePhoneCallModule == null) {
            rnImmediatePhoneCallModule = this;
        }
        rnImmediatePhoneCallModule.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNImmediatePhoneCall";
    }

    @ReactMethod
    public void immediatePhoneCall(String number, Promise promise) {

        if (number == null || number.isEmpty()) {
            promise.reject(new JSApplicationIllegalArgumentException("Invalid number: " + url));
            return;
        }

        try {
            Bool hasPermission = ContextCompat.checkSelfPermission(reactContext.getApplicationContext(),
            android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED

            Activity currentActivity = getCurrentActivity();
            if (currentActivity == null) {
                currentActivity = getReactApplicationContext();
            }

            if (hasPermission) {
                call(Uri.encode(number), promise, currentActivity);
            } else {
                RNImmediatePhoneCallModule.promise = promise;
                RNImmediatePhoneCallModule.number = Uri.encode(number);

                ActivityCompat.requestPermissions(currentActivity,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    PERMISSIONS_REQUEST_ACCESS_CALL);
            }
        } catch(Exception e) {
            promise.reject(new JSApplicationIllegalArgumentException("Could not open URL '" + url + "': " + e.getMessage()));
        }
    }
	
	@SuppressLint("MissingPermission")
    private static void call(String number, Promise promise, Activity activity) {
        try {
            String url = "tel:" + number;
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url).normalizeScheme());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
            promise.reject(new JSApplicationIllegalArgumentException("Could not open URL '" + url + "': " + e.getMessage()));
        }
    }

    public static void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_CALL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Activity currentActivity = getCurrentActivity();
                    if (currentActivity == null) {
                        currentActivity = getReactApplicationContext();  
                    }
                    call(RNImmediatePhoneCallModule.number, RNImmediatePhoneCallModule.promise, currentActivity);
                }
            }
        }
    }
}
