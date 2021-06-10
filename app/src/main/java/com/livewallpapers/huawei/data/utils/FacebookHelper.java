package com.livewallpapers.huawei.data.utils;

import android.app.Activity;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;



public class FacebookHelper {
    private static InterstitialAd interstitialAd;
    public static void loadInterstitial(Activity activity){
        interstitialAd = new InterstitialAd(activity, Constant.facebook_interstitial_id);
            interstitialAd.loadAd();

        interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                interstitialAd.loadAd();
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                interstitialAd.loadAd();
            }

            @Override
            public void onAdLoaded(Ad ad) {
            }

            @Override
            public void onAdClicked(Ad ad) {
            }

            @Override
            public void onLoggingImpression(Ad ad) {
            }
        });
    }

    public static InterstitialAd getInterstitial(){
        return interstitialAd;
    }
}
