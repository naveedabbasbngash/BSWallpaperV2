package com.livewallpapers.huawei.data.model;


import com.facebook.ads.NativeAd;

public class ModelWallpaper{
    public int wall_id;
    public int cat_id;
    public String wallpaper_title;
    public String wallpaper_image_thumb;
    public String wallpaper_image_detail;
    public String wallpaper_image_original;
    public String wallpaper_tags;
    public String wallpaper_type;
    public String wallpaper_premium;
    public int view_type;
    public NativeAd nativeAd;
    public ModelWallpaper(int wall_id, int cat_id, String wallpaper_title, String wallpaper_image_thumb, String wallpaper_image_detail, String wallpaper_image_original, String wallpaper_tags, String wallpaper_type, String wallpaper_premium, int view_type) {
        this.wall_id = wall_id;
        this.cat_id = cat_id;
        this.wallpaper_title = wallpaper_title;
        this.wallpaper_image_thumb = wallpaper_image_thumb;
        this.wallpaper_image_detail = wallpaper_image_detail;
        this.wallpaper_image_original = wallpaper_image_original;
        this.wallpaper_tags = wallpaper_tags;
        this.wallpaper_type = wallpaper_type;
        this.wallpaper_premium = wallpaper_premium;
        this.view_type = view_type;
    }

    public ModelWallpaper(int view_type, NativeAd nativeAd) {
        this.view_type = view_type;
        this.nativeAd = nativeAd;
    }

    public ModelWallpaper(String wallpaper_premium, int view_type) {
        this.wallpaper_premium = wallpaper_premium;
        this.view_type = view_type;
    }

    public ModelWallpaper(int view_type) {
        this.view_type = view_type;
    }
}