package com.livewallpapers.huawei.ui.detail;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.benkkstudio.bsjson.BSJson;
import com.benkkstudio.bsjson.BSObject;
import com.benkkstudio.bsjson.Interface.BSJsonV2Listener;
import com.benkkstudio.bsmob.BSMob;
import com.benkkstudio.bsmob.Interface.BannerListener;
import com.benkkstudio.bsmob.Interface.RewardListener;
import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.data.adapter.AdapterWallpaper;
import com.livewallpapers.huawei.databinding.ActivityDetailBinding;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.RewardedVideoAdListener;
import com.fangxu.allangleexpandablebutton.ButtonData;
import com.fangxu.allangleexpandablebutton.ButtonEventListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;


import com.livewallpapers.huawei.data.adapter.ImagePagerAdapter;
import com.livewallpapers.huawei.data.base.BasePresenter;
import com.livewallpapers.huawei.data.downloadservice.BSDownloadFile;
import com.livewallpapers.huawei.data.downloadservice.BSDownloadListener;
import com.livewallpapers.huawei.data.utils.Constant;
import com.livewallpapers.huawei.data.utils.FacebookHelper;
import com.livewallpapers.huawei.data.utils.SharedPref;
import com.livewallpapers.huawei.data.widgets.ProgressLoader;

import com.livewallpapers.huawei.ui.zoom.ZoomActivity;

import static android.app.Activity.RESULT_OK;

public class DetailPresenter extends BasePresenter<DetailView> {
    private static final String METHOD_DOWNLOAD = "DOWNLOAD";
    private static final String METHOD_SET_WALLPAPER = "SET_WALLPAPER";
    private static final String METHOD_SET_WALLPAPER_GIF = "SET_WALLPAPER_GIF";
    private static final String METHOD_SHARE = "SHARE";
    private boolean isComplete = false;
    private AppCompatActivity activity;
    private ActivityDetailBinding binding;
    private ProgressLoader progressLoader;
    protected void initView(AppCompatActivity activity, final ActivityDetailBinding binding) {
        this.activity = activity;
        this.binding = binding;
        progressLoader = new ProgressLoader(activity);
        Constant.ads_count++;
        if(Constant.ads_count == Constant.interstitial_click){
            if(Constant.AdsOptions.IDENTIFIER.equals(Constant.AdsOptions.FACEBOOK)){
                if(FacebookHelper.getInterstitial().isAdLoaded()){
                    FacebookHelper.getInterstitial().show();
                }
            } else {
                BSMob.getInterstitial().show();
            }

            Constant.ads_count = 0;
        }
        binding.imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        checkPremiumWallpaper(activity.getIntent().getIntExtra("POS", 0));
        sendViews(activity.getIntent().getIntExtra("POS", 0));
        loadBanner();
        initClickListener();
        loadImage();
    }

    private void loadBanner() {
        if(Constant.AdsOptions.IDENTIFIER.equals(Constant.AdsOptions.FACEBOOK)){
            AdView adView = new AdView(activity, Constant.facebook_banner_id, AdSize.BANNER_HEIGHT_50);
            binding.adsView.addView(adView);
            adView.loadAd();
        } else {
            new BSMob.banner(activity)
                    .setSize(BSMob.adaptiveSize(activity))
                    .setLayout(binding.adsView)
                    .setId(Constant.banner_id)
                    .setAdRequest(new AdRequest.Builder().build())
                    .setListener(new BannerListener() {
                        @Override
                        public void onAdFailedToLoad(int error) {
                            binding.adsView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAdLoaded() {
                            binding.adsView.setVisibility(View.VISIBLE);
                        }
                    })
                    .show();
        }
    }

    private void initClickListener(){
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.ic_detail_plus, R.drawable.ic_detail_zoom, R.drawable.ic_detail_download, R.drawable.ic_detail_crop, R.drawable.ic_detail_share};
        for (int value : drawable) {
            ButtonData buttonData = ButtonData.buildIconButton(activity, value, 12);
            buttonData.setBackgroundColorId(activity, R.color.colorButtonDetail);
            buttonDatas.add(buttonData);
        }
        binding.buttonExpandable.setButtonDatas(buttonDatas);
        binding.buttonExpandable.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {
                switch (index){
                    case 1:
                        Intent intent = new Intent(activity, ZoomActivity.class);
                        intent.putExtra("str_image", Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_image_original);
                        activity.startActivity(intent);
                        break;
                    case 2:
                        wallpaperTask(METHOD_DOWNLOAD);
                        break;
                    case 3:
                        wallpaperTask(Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_type.equals("gif") ? METHOD_SET_WALLPAPER_GIF : METHOD_SET_WALLPAPER);
                        break;
                    case 4:
                        wallpaperTask(METHOD_SHARE);
                        break;
                }
            }

            @Override
            public void onExpand() {

            }

            @Override
            public void onCollapse() {

            }
        });
    }

    private void checkPremiumWallpaper(int position){
        binding.buttonSubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadRewardVideo();
            }
        });
        binding.layoutLocked.setVisibility(Constant.arrayListDetail.get(position).wallpaper_premium.equals("premium") && !SharedPref.getSharedPref(activity).read("premium_checker").contains(Constant.arrayListDetail.get(position).wallpaper_image_original) ? View.VISIBLE : View.GONE);
        binding.buttonExpandable.setVisibility(Constant.arrayListDetail.get(position).wallpaper_premium.equals("premium") && !SharedPref.getSharedPref(activity).read("premium_checker").contains(Constant.arrayListDetail.get(position).wallpaper_image_original) ? View.GONE : View.VISIBLE);
        if(!Constant.arrayListDetail.get(position).wallpaper_premium.equals("premium")){
            binding.buttonExpandable.setVisibility(Constant.arrayListDetail.get(position).view_type == AdapterWallpaper.VIEW_NATIVE ? View.GONE : View.VISIBLE);
        }
    }

    private void loadImage() {
        ImagePagerAdapter imagePagerAdapter = new ImagePagerAdapter(activity, Constant.arrayListDetail);
        binding.viewPager.setAdapter(imagePagerAdapter);
        binding.viewPager.useScale();
        binding.viewPager.useAlpha();
        imagePagerAdapter.enableCarrousel();
        binding.viewPager.disableScroll(false);
        binding.viewPager.setCurrentItem(activity.getIntent().getIntExtra("POS", 0));
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                checkPremiumWallpaper(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void loadRewardVideo(){
        progressLoader.show();
        if(Constant.AdsOptions.IDENTIFIER.equals(Constant.AdsOptions.FACEBOOK)){
            final com.facebook.ads.RewardedVideoAd rewardedVideoAd = new com.facebook.ads.RewardedVideoAd(activity, Constant.facebook_reward_id);
            rewardedVideoAd.setAdListener(new RewardedVideoAdListener() {
                @Override
                public void onError(Ad ad, AdError error) {
                    SharedPref.getSharedPref(activity).write("premium_checker", SharedPref.getSharedPref(activity).contains("premium_checker")
                            ? SharedPref.getSharedPref(activity).read("premium_checker") + ", " + Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_image_original
                            : Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_image_original);
                    checkPremiumWallpaper(binding.viewPager.getCurrentItem());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    progressLoader.dismiss();
                    if(rewardedVideoAd.isAdLoaded()){
                        rewardedVideoAd.show();
                    }
                }

                @Override
                public void onAdClicked(Ad ad) {
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                }

                @Override
                public void onRewardedVideoCompleted() {
                    isComplete = true;

                }

                @Override
                public void onRewardedVideoClosed() {
                    if(isComplete){
                        isComplete = false;
                        SharedPref.getSharedPref(activity).write("premium_checker", SharedPref.getSharedPref(activity).contains("premium_checker")
                                ? SharedPref.getSharedPref(activity).read("premium_checker") + ", " + Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_image_original
                                : Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_image_original);
                        checkPremiumWallpaper(binding.viewPager.getCurrentItem());
                    }
                }
            });
            rewardedVideoAd.loadAd();
        } else {
            new BSMob.reward(activity)
                    .setId(Constant.reward_id)
                    .setAdRequest(new AdRequest.Builder().build())
                    .setListener(new RewardListener() {
                        @Override
                        public void onRewardedVideoAdLoaded(RewardedVideoAd rewardedVideoAd) {
                            progressLoader.dismiss();
                            if(rewardedVideoAd.isLoaded()){
                                rewardedVideoAd.show();
                            }
                        }

                        @Override
                        public void onRewardedVideoAdClosed(RewardedVideoAd rewardedVideoAd) {
                            if(isComplete){
                                isComplete = false;
                                SharedPref.getSharedPref(activity).write("premium_checker", SharedPref.getSharedPref(activity).contains("premium_checker")
                                        ? SharedPref.getSharedPref(activity).read("premium_checker") + ", " + Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_image_original
                                        : Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_image_original);
                                checkPremiumWallpaper(binding.viewPager.getCurrentItem());
                            }
                        }

                        @Override
                        public void onRewardedVideoAdFailedToLoad(int error, RewardedVideoAd rewardedVideoAd) {
                            SharedPref.getSharedPref(activity).write("premium_checker", SharedPref.getSharedPref(activity).contains("premium_checker")
                                    ? SharedPref.getSharedPref(activity).read("premium_checker") + ", " + Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_image_original
                                    : Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_image_original);
                            checkPremiumWallpaper(binding.viewPager.getCurrentItem());
                        }

                        @Override
                        public void onRewardedVideoCompleted() {
                            isComplete = true;

                        }
                    })
                    .show();
        }

    }

    private void wallpaperTask(String method){
        switch (method){
            case METHOD_DOWNLOAD:
                shareImage("download");
                break;
            case METHOD_SHARE:
                shareImage("share");
                break;
            case METHOD_SET_WALLPAPER:
                cropWallpaper();
                break;
            case METHOD_SET_WALLPAPER_GIF:
                new BSDownloadFile.Builder(activity)
                        .setUrl(Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_image_original)
                        .setDirectory(activity.getExternalCacheDir() + File.separator + "set_wallpaper" + File.separator)
                        .setListener(new BSDownloadListener() {
                            @Override
                            public void onFinish(String directory, String filename) {
                                SharedPref.getSharedPref(activity).write("set_wallpaper", directory + filename);
                                Intent intent;
                                try {
                                    WallpaperManager.getInstance(activity).clear();
                                }catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
                                    intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(activity, activity.getPackageName() + ".data.services.GifWallpaperService"));
                                    intent.putExtra("SET_LOCKSCREEN_WALLPAPER", true);
                                    activity.startActivity(intent);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(activity, "Your phone not support live wallpaper", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .download();
                break;
        }
    }

    private void cropWallpaper(){
        Uri source = Uri.parse(Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_image_original);
        File dest = new File(activity.getApplicationContext().getCacheDir(), "cropped");
        //noinspection ResultOfMethodCallIgnored
        dest.mkdirs();
        Uri destination = Uri.fromFile(new File(activity.getApplicationContext().getCacheDir(), "cropped/image.png"));
        UCrop uCrop = UCrop.of(source, destination);
        uCrop = basisConfig(uCrop);
        uCrop = advancedConfig(uCrop);
        uCrop.start(activity);
    }

    private UCrop basisConfig(@NonNull UCrop uCrop) {
        return uCrop.useSourceImageAspectRatio();
    }

    private UCrop advancedConfig(@NonNull UCrop uCrop) {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        return uCrop.withOptions(options);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), resultUri);
                if(Constant.AdsOptions.IDENTIFIER.equals(Constant.AdsOptions.FACEBOOK)){
                    if (FacebookHelper.getInterstitial().isAdLoaded()) {
                        FacebookHelper.getInterstitial().show();
                    }
                } else {
                    BSMob.getInterstitial().show();
                }
                showBottomSheetDialog(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showBottomSheetDialog(final Bitmap bitmap) {
        @SuppressLint("InflateParams")
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_set_wallpaper, null);
        final BottomSheetDialog dialog_desc = new BottomSheetDialog(activity, R.style.SheetDialog);
        dialog_desc.setCancelable(false);
        dialog_desc.setContentView(view);
        dialog_desc.show();

        TextView home_screen = dialog_desc.findViewById(R.id.home_screen);
        TextView lock_screen = dialog_desc.findViewById(R.id.lock_screen);
        TextView home_lock_screen = dialog_desc.findViewById(R.id.home_lock_screen);

        Objects.requireNonNull(home_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_desc.dismiss();
                new SetWallpaperTask(bitmap, "HOME").execute();
            }
        });

        Objects.requireNonNull(lock_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_desc.dismiss();
                new SetWallpaperTask(bitmap, "LOCK").execute();
            }
        });

        Objects.requireNonNull(home_lock_screen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_desc.dismiss();
                new SetWallpaperTask(bitmap, "BOTH").execute();
            }
        });
    }

    private void shareImage(final String type){
        final String shareImage = Constant.arrayListDetail.get(binding.viewPager.getCurrentItem()).wallpaper_image_original;
        Picasso.get()
                .load(shareImage)
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        String root = null;
                        switch (type){
                            case "share":
                                root = activity.getExternalCacheDir() + File.separator;
                                break;
                            case "download":
                                root = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + activity.getString(R.string.download_folder) + File.separator;
                                File rootFile = new File(root);
                                if(!rootFile.exists()){
                                    rootFile.mkdir();
                                }
                                break;
                        }
                        final File directory = new File(root ,  shareImage.substring(shareImage.lastIndexOf("/")));
                        try {
                            if(!directory.exists()){
                                FileOutputStream out = new FileOutputStream(directory);
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                                out.flush();
                                out.close();
                            }
                            switch (type){
                                case "share":
                                    Intent share = new Intent(Intent.ACTION_SEND);
                                    share.setType("image/*");
                                    share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + directory));
                                    share.putExtra(Intent.EXTRA_TEXT, activity.getString(R.string.get_more_wall) + "\n" + activity.getString(R.string.app_name) + " - " + "https://play.google.com/store/apps/details?id=" + activity.getPackageName());
                                    activity.startActivity(Intent.createChooser(share, "Share Wallpaper"));
                                    break;
                                case "download":
                                    MediaScannerConnection.scanFile(activity, new String[] { directory.getAbsolutePath()},
                                            null,
                                            new MediaScannerConnection.OnScanCompletedListener() {
                                                @Override
                                                public void onScanCompleted(String path, Uri uri) {

                                                }
                                            });
                                    Toast.makeText(activity,  "Downloaded at: " + directory, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "an error occurred, please try again later", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
    }
    @SuppressLint("StaticFieldLeak")
    public class SetWallpaperTask extends AsyncTask<String, String, String> {
        Bitmap bmImg;
        String options;
        private SweetAlertDialog pDialog;

        private SetWallpaperTask(Bitmap bmImg, String options) {
            this.bmImg = bmImg;
            this.options = options;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressLoader.show();
        }

        @Override
        protected String doInBackground(String... args) {
            return null;
        }

        @Override
        protected void onPostExecute(String args) {
            progressLoader.dismiss();
            WallpaperManager wpm = WallpaperManager.getInstance(activity.getApplicationContext());
            try {
                wpm.setWallpaperOffsetSteps(0, 0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    switch (options) {
                        case "HOME":
                            wpm.setBitmap(bmImg, null, true, WallpaperManager.FLAG_SYSTEM);
                            break;
                        case "LOCK":
                            wpm.setBitmap(bmImg, null, true, WallpaperManager.FLAG_LOCK);
                            break;
                        case "BOTH":
                            wpm.setBitmap(bmImg);
                            break;
                    }
                } else {
                    wpm.setBitmap(bmImg);
                }
                new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
                        .setTitleText("Success!")
                        .setContentText("Wallpaper set successfully!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                activity.finish();
                            }
                        })
                        .show();
            } catch (Exception e) {
                e.printStackTrace();
                new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Error!")
                        .setContentText("Wallpaper set error!")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                activity.finish();
                            }
                        })
                        .show();
            }

        }
    }

    private void sendViews(int position) {
        BSObject bsObject = new BSObject();
        bsObject.addProperty("method_name", "send_view");
        bsObject.addProperty("id", Constant.arrayListDetail.get(position).wall_id);
        new BSJson.Builder(activity)
                .setServer(Constant.SERVER_URL)
                .setObject(bsObject.getProperty())
                .setMethod(BSJson.METHOD_POST)
                .setListener(new BSJsonV2Listener() {
                    @Override
                    public void onLoaded(String response) {

                    }

                    @Override
                    public void onError(String error) {

                    }
                })
                .load();
    }

    protected void onBackPressed(){
        activity.finish();
        activity.overridePendingTransition(R.anim.no_change, R.anim.slide_down);
    }
}
