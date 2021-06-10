package com.livewallpapers.huawei.data.utils;

import com.livewallpapers.huawei.BuildConfig;
import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.ui.home.HomePresenter;

import java.io.Serializable;
import java.util.ArrayList;


import com.livewallpapers.huawei.data.model.ModelWallpaper;

public class Constant implements Serializable {
    public static final String SERVER_URL = BuildConfig.SERVER_URL + "api";
    public static final String TAG_ROOT = "BENKKSTUDIO";
    public static final int WALLPAPER_LOADING_POSITION = 30;
    public static final int CATEGORY_LOADING_POSITION = 10;
    public static final int VIEW_PAGER_LIMIT = 20;
    public static int cat_id;
    public static String cat_name;
    public static ArrayList<ModelWallpaper> arrayListDetail;
    public static String app_name;
    public static String app_email;
    public static boolean banner_options  = true;
    public static String banner_id;
    public static boolean interstitial_options  = true;
    public static String interstitial_id;
    public static int interstitial_click;
    public static boolean native_options = true;
    public static String native_id;
    public static String reward_id;
    public static String privacy_police;

    public static String facebook_banner_id;
    public static String facebook_interstitial_id;
    public static String facebook_reward_id;
    public static String facebook_native_id;

    public static boolean native_loaded;
    public static int ads_count = 0;
    public static int selected_filter = R.id.latest_button;
    public static String filter_value = HomePresenter.FILTER_LATEST;

    public static class AdsOptions{
        public static String IDENTIFIER;
        public static final String ADMOB = "ADMOB";
        public static final String FACEBOOK = "FACEBOOK";
        public static final String DISABLE = "DISABLE";
    }
}
