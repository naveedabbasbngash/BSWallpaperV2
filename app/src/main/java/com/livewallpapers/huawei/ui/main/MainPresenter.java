package com.livewallpapers.huawei.ui.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.benkkstudio.bsmob.BSMob;
import com.benkkstudio.bsmob.Interface.BannerListener;
import com.benkkstudio.bsmob.Interface.InterstitialListener;
import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.databinding.ActivityMainBinding;
import com.livewallpapers.huawei.ui.splash.SplashActivity;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.material.appbar.AppBarLayout;
import com.yarolegovich.slidingrootnav.SlideGravity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import uc.benkkstudio.bsbottomnavigation.NavigationClickListner;

import com.livewallpapers.huawei.data.base.BasePresenter;
import com.livewallpapers.huawei.data.utils.Constant;
import com.livewallpapers.huawei.data.utils.FacebookHelper;
import com.livewallpapers.huawei.data.utils.SharedPref;

import com.livewallpapers.huawei.ui.privacy.FragmentPrivacy;

public class MainPresenter extends BasePresenter<MainView> {
    public static final String THEME_MODE = "THEME_MODE";
    private Activity activity;
    private ActivityMainBinding binding;
    private SlidingRootNav slidingRootNav;
    private FragmentManager fragmentManager;
    protected void initView(Activity activity, ActivityMainBinding binding, FragmentManager fragmentManager){
        this.activity = activity;
        this.binding = binding;
        this.fragmentManager = fragmentManager;
        loadInterstitialAndVideo();
        setClickListener();
        initBottomNavigation();
        initDrawer();
    }

    private void loadInterstitialAndVideo(){
        if(Constant.AdsOptions.IDENTIFIER.equals(Constant.AdsOptions.FACEBOOK)){
            FacebookHelper.loadInterstitial(activity);
        } else {
            new BSMob.interstitial(activity)
                    .setId(Constant.interstitial_id)
                    .setAdRequest(new AdRequest.Builder().build())
                    .setListener(new InterstitialListener() {
                        @Override
                        public void onAdLoaded(InterstitialAd interstitialAd) {

                        }

                        @Override
                        public void onAdFailed(InterstitialAd interstitialAd) {
                            interstitialAd.loadAd(new AdRequest.Builder().build());
                        }

                        @Override
                        public void onAdClosed(InterstitialAd interstitialAd) {
                            interstitialAd.loadAd(new AdRequest.Builder().build());
                        }
                    })
                    .show();
        }
    }


    private void setClickListener(){
        binding.imageMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingRootNav.openMenu(true);
            }
        });
    }


    protected void exitCollapsed(){
        CoordinatorLayout.LayoutParams appBarLayoutLayoutParams = (CoordinatorLayout.LayoutParams)
                binding.appBarLayout.getLayoutParams();
        appBarLayoutLayoutParams.setBehavior(new AppBarLayout.Behavior() {});
    }

    private void initBottomNavigation() {
        binding.bottomNavigation.addButton("", R.drawable.ic_nav_category);
        binding.bottomNavigation.addButton("", R.drawable.ic_nav_search);
        binding.bottomNavigation.addButton("", R.drawable.ic_gif_tag);
        binding.bottomNavigation.addButton("", R.drawable.ic_nav_favourtie);
        binding.bottomNavigation.showIconOnly();
        binding.bottomNavigation.setSelectedItem(-1);
        binding.bottomNavigation.setButtonClickListener(new NavigationClickListner() {
            @Override
            public void onCentreButtonClick() {
                binding.viewPager.setCurrentItem(2);
                binding.bottomNavigation.setSelectedItem(-1);
                binding.toolbarTitle.setText("LATEST WALLPAPER");
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onItemClick(int itemIndex, String itemName) {
                switch (itemIndex) {
                    case 0:
                        binding.viewPager.setCurrentItem(0);
                        binding.toolbarTitle.setText("CATEGORY");
                        break;
                    case 1:
                        binding.viewPager.setCurrentItem(1);
                        binding.toolbarTitle.setText("SEARCH WALLPAPER");
                        break;
                    case 2:
                        binding.viewPager.setCurrentItem(3);
                        binding.toolbarTitle.setText("GIF WALLPAPER");
                        break;
                    case 3:
                        binding.viewPager.setCurrentItem(4);
                        binding.toolbarTitle.setText("FAVOURITE WALLPAPER");
                        break;
                }
                exitCollapsed();
            }
        });
    }


    private void initDrawer() {
        slidingRootNav = new SlidingRootNavBuilder(activity)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withGravity(SlideGravity.LEFT)
                .withMenuLayout(R.layout.drawer_layout)
                .inject();
        TextView headerTitle = slidingRootNav.getLayout().findViewById(R.id.headerTitle);
        TextView headerEmail = slidingRootNav.getLayout().findViewById(R.id.headerEmail);
        headerTitle.setText(Constant.app_name);
        headerEmail.setText(Constant.app_email);
        final LinearLayout adsview = slidingRootNav.getLayout().findViewById(R.id.adsview);
        if(Constant.AdsOptions.IDENTIFIER.equals(Constant.AdsOptions.FACEBOOK)){
            AdView adView = new AdView(activity, Constant.facebook_banner_id, AdSize.BANNER_HEIGHT_50);
            adsview.addView(adView);
            adView.loadAd();
        } else {
            new BSMob.banner(activity)
                    .setAdRequest(new AdRequest.Builder().build())
                    .setId(Constant.banner_id)
                    .setLayout(adsview)
                    .setListener(new BannerListener() {
                        @Override
                        public void onAdFailedToLoad(int error) {
                            adsview.setVisibility(View.GONE);
                        }

                        @Override
                        public void onAdLoaded() {
                            adsview.setVisibility(View.VISIBLE);
                        }
                    })
                    .setSize(BSMob.adaptiveSize(activity))
                    .show();
        }


        if (SharedPref.getSharedPref(activity).contains(THEME_MODE)) {
            if(!SharedPref.getSharedPref(activity).readBoolean(THEME_MODE)){
                binding.themeMode.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_light_mode));
            } else {
                binding.themeMode.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_dark_mode));
            }
        }
        binding.themeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SharedPref.getSharedPref(activity).contains(THEME_MODE)) {
                    if(!SharedPref.getSharedPref(activity).readBoolean(THEME_MODE)){
                        SharedPref.getSharedPref(activity).write(THEME_MODE, true);
                        binding.themeMode.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_dark_mode));
                    } else {
                        SharedPref.getSharedPref(activity).write(THEME_MODE, false);
                        binding.themeMode.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_light_mode));
                    }
                } else {
                    SharedPref.getSharedPref(activity).write(THEME_MODE, true);
                }
                activity.startActivity(new Intent(activity, SplashActivity.class));
                activity.finish();
                Toast.makeText(activity, "Please restart app to apply new theme", Toast.LENGTH_LONG).show();
            }
        });

        slidingRootNav.getLayout().findViewById(R.id.menuHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu(true);
                binding.viewPager.setCurrentItem(2);
            }
        });

        slidingRootNav.getLayout().findViewById(R.id.menuLatest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu(true);
                binding.viewPager.setCurrentItem(2);
            }
        });

        slidingRootNav.getLayout().findViewById(R.id.menuCategory).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu(true);
                binding.viewPager.setCurrentItem(0);
            }
        });

        slidingRootNav.getLayout().findViewById(R.id.menuFavourite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu(true);
                binding.viewPager.setCurrentItem(4);
            }
        });

        slidingRootNav.getLayout().findViewById(R.id.menuRingtone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slidingRootNav.closeMenu(true);
                binding.viewPager.setCurrentItem(3);
                Toast.makeText(activity, "DFVGDGF", Toast.LENGTH_SHORT).show();
            }
        });

        slidingRootNav.getLayout().findViewById(R.id.menuShare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu(true);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.get_more_wall) + "\n" + "https://play.google.com/store/apps/details?id=" + activity.getPackageName());
                sendIntent.setType("text/plain");
                activity.startActivity(sendIntent);
            }
        });

        slidingRootNav.getLayout().findViewById(R.id.menuRate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu(true);
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            }
        });

        slidingRootNav.getLayout().findViewById(R.id.menuMore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu(true);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.play_developer_id)));
                activity.startActivity(intent);
            }
        });

        slidingRootNav.getLayout().findViewById(R.id.menuPrivacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingRootNav.closeMenu(true);
                FragmentPrivacy fragmentPrivacy = new FragmentPrivacy();
                fragmentPrivacy.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                fragmentPrivacy.show(fragmentManager, "fragmentPrivacy");
            }
        });
    }


}
