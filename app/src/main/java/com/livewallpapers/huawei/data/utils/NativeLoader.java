package com.livewallpapers.huawei.data.utils;

import android.app.Activity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.UnifiedNativeAd;

import java.util.ArrayList;
import java.util.Random;

public class NativeLoader {
    private Activity activity;
    private static boolean unifiedLoaded = false;
    private static ArrayList<UnifiedNativeAd> unifiedNativeAd = new ArrayList<>();
    private int AdsVariety = 1;
    private int LoadCount = 0;
    private String native_id;
    private NativeLoaderListener nativeLoaderListener;
    private static Random random = new Random();
    private NativeLoader(Activity activity, int adsVariety, String native_id, NativeLoaderListener nativeLoaderListener) {
        this.activity = activity;
        AdsVariety = adsVariety;
        this.native_id = native_id;
        this.nativeLoaderListener = nativeLoaderListener;
        loadNative();
    }

    private void loadNative(){
        AdLoader.Builder builder = new AdLoader.Builder(activity, native_id);
        AdLoader adLoader = builder.forUnifiedNativeAd(
                new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
                    @Override
                    public void onUnifiedNativeAdLoaded(UnifiedNativeAd unified) {
                        LoadCount++;
                        unifiedNativeAd.add(unified);
                        unifiedLoaded = true;
                        if(LoadCount == AdsVariety){
                            nativeLoaderListener.onFinish();
                        }
                    }
                }).withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                unifiedLoaded = false;
                nativeLoaderListener.onFinish();
            }
        }).build();
        adLoader.loadAds(new AdRequest.Builder().build(), AdsVariety);
    }

    public static boolean isLoaded(){
        return unifiedLoaded;
    }

    public static UnifiedNativeAd getUnifiedNativeAd(){
        return unifiedNativeAd.get(random.nextInt(unifiedNativeAd.size()));
    }

    public static class Builder {
        private Activity activity;
        private String native_id;
        private int AdsVariety = 1;
        private NativeLoaderListener nativeLoaderListener;
        public Builder(Activity activity) {
            this.activity = activity;
        }

        public Builder setId(String native_id) {
            this.native_id = native_id;
            return this;
        }

        public Builder setListener(NativeLoaderListener nativeLoaderListener) {
            this.nativeLoaderListener = nativeLoaderListener;
            return this;
        }

        public Builder setAdVariety(int AdsVariety) {
            this.AdsVariety = AdsVariety;
            return this;
        }

        public NativeLoader load() {
            return new NativeLoader(activity, AdsVariety, native_id, nativeLoaderListener);
        }
    }
}
