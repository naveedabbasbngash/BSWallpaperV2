package com.livewallpapers.huawei.data.utils;

import android.app.Activity;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;

public class FacebookNativeLoader {
    public static NativeAd nativeAd;
    public static boolean isLoaded = false;
    public static void load(Activity activity, final NativeLoaderListener nativeLoaderListener){
        nativeAd = new NativeAd(activity, Constant.facebook_native_id);
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {
                if(!isLoaded){
                    nativeLoaderListener.onFinish();
                    isLoaded = true;
                }
            }

            @Override
            public void onAdLoaded(Ad ad) {
                nativeLoaderListener.onFinish();
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }
        });
        nativeAd.loadAd();
    }

    public static void loadNext(Activity activity){
        nativeAd = new NativeAd(activity, Constant.facebook_native_id);
        nativeAd.loadAd();
    }
}
