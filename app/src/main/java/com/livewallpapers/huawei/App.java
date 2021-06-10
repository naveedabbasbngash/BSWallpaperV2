package com.livewallpapers.huawei;

import android.app.Application;
import android.os.StrictMode;

import com.benkkstudio.bsjson.BSJson;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(BuildConfig.DEBUG) {
            RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList(getDeviceId())).build();
            MobileAds.setRequestConfiguration(configuration);
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        new BSJson.initializing()
                .withSecret(BuildConfig.PURCHASE_CODE)
                .enableLogging(false);
        FirebaseAnalytics.getInstance(this);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    public static String getMD5(String inputText){
        String md5 = "";
        try{
            MessageDigest digester = MessageDigest.getInstance("MD5");
            digester.update(inputText.getBytes());
            md5 = new BigInteger(1, digester.digest()).toString(16);
        }
        catch(Exception e){}
        return md5;
    }



    public String getDeviceId(){
        String androidID = android.provider.Settings.Secure.getString(getContentResolver(),android.provider. Settings.Secure.ANDROID_ID);
        String deviceID = getMD5(androidID).toUpperCase();
        return deviceID;
    }
}
