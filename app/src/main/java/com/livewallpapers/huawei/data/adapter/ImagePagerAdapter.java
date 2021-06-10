package com.livewallpapers.huawei.data.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.livewallpapers.huawei.R;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.google.android.ads.nativetemplates.BSNative;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import uc.benkkstudio.bscarouselviewpager.BSCarouselViewPager;
import uc.benkkstudio.bscarouselviewpager.BSCarouselViewPagerAdapter;

import com.livewallpapers.huawei.data.model.ModelWallpaper;
import com.livewallpapers.huawei.data.utils.Constant;
import com.livewallpapers.huawei.data.utils.FacebookNativeLoader;
import com.livewallpapers.huawei.data.utils.NativeLoader;

public class ImagePagerAdapter extends BSCarouselViewPagerAdapter {

    private LayoutInflater inflater;
    private Activity activity;
    private ArrayList<ModelWallpaper> arrayList;
    public ImagePagerAdapter(Activity activity, ArrayList<ModelWallpaper> arrayList) {
        super(arrayList);
        this.activity = activity;
        this.arrayList = arrayList;
        inflater = activity.getLayoutInflater();
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View imageLayout = null;
        if(arrayList.get(position).view_type == AdapterWallpaper.VIEW_NATIVE){
            if(Constant.AdsOptions.IDENTIFIER.equals(Constant.AdsOptions.FACEBOOK)){
                imageLayout = inflater.inflate(R.layout.view_pager_native_fb, container, false);
                AdIconView native_ad_icon = imageLayout.findViewById(R.id.native_ad_icon);
                TextView native_ad_title = imageLayout.findViewById(R.id.native_ad_title);
                TextView native_ad_sponsored_label = imageLayout.findViewById(R.id.native_ad_sponsored_label);
                LinearLayout ad_choices_container = imageLayout.findViewById(R.id.ad_choices_container);
                MediaView native_ad_media = imageLayout.findViewById(R.id.native_ad_media);
                TextView native_ad_social_context = imageLayout.findViewById(R.id.native_ad_social_context);
                TextView native_ad_body = imageLayout.findViewById(R.id.native_ad_body);
                Button native_ad_call_to_action = imageLayout.findViewById(R.id.native_ad_call_to_action);

                ad_choices_container.removeAllViews();
                AdChoicesView adChoicesView = new AdChoicesView(activity, FacebookNativeLoader.nativeAd, true);
                ad_choices_container.addView(adChoicesView);
                native_ad_title.setText(FacebookNativeLoader.nativeAd.getAdvertiserName());
                native_ad_body.setText(FacebookNativeLoader.nativeAd.getAdBodyText());
                native_ad_social_context.setText(FacebookNativeLoader.nativeAd.getAdSocialContext());

                native_ad_call_to_action.setVisibility(FacebookNativeLoader.nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                native_ad_call_to_action.setText(FacebookNativeLoader.nativeAd.getAdCallToAction());
                native_ad_sponsored_label.setText(FacebookNativeLoader.nativeAd.getSponsoredTranslation());

                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(native_ad_call_to_action);
                clickableViews.add(native_ad_media);
                clickableViews.add(native_ad_icon);
                FacebookNativeLoader.nativeAd.registerViewForInteraction(
                        native_ad_call_to_action,
                        native_ad_media,
                        native_ad_icon,
                        clickableViews);
            } else {
                imageLayout = inflater.inflate(R.layout.view_pager_native, container, false);
                BSNative native_view = imageLayout.findViewById(R.id.native_view);
                if(NativeLoader.isLoaded()){
                    native_view.setNativeAd(NativeLoader.getUnifiedNativeAd());
                }
            }
        } else {
            imageLayout = inflater.inflate(R.layout.view_pager_wallpaper, container, false);
            ImageView imageView = imageLayout.findViewById(R.id.image);
            final LottieAnimationView progressBar = imageLayout.findViewById(R.id.progressBar);
            Picasso.get()
                    .load(arrayList.get(position).wallpaper_image_detail)
                    .placeholder(R.drawable.placeholder)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });
        }


        imageLayout.setTag(BSCarouselViewPager.ENCHANTED_VIEWPAGER_POSITION + position);
        container.addView(imageLayout);
        return imageLayout;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
