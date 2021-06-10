package com.livewallpapers.huawei.ui.splash;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.benkkstudio.bsjson.BSJson;
import com.benkkstudio.bsjson.BSObject;
import com.benkkstudio.bsjson.Interface.BSJsonV2Listener;
import com.livewallpapers.huawei.R;
import com.facebook.ads.AdSettings;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import com.livewallpapers.huawei.BuildConfig;

import com.livewallpapers.huawei.data.utils.Constant;
import com.livewallpapers.huawei.data.utils.FacebookNativeLoader;
import com.livewallpapers.huawei.data.utils.NativeLoader;
import com.livewallpapers.huawei.data.utils.NativeLoaderListener;
import com.livewallpapers.huawei.ui.main.MainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TedPermission.with(this)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        loadAbout();
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        finish();
                    }
                })
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.SET_WALLPAPER,
                        Manifest.permission.INTERNET)
                .check();
    }

    private void loadNative(){
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
            /*new NativeLoader.Builder(this)
                    .setId(Constant.native_id)
                    .setAdVariety(1)
                    .setListener(new NativeLoaderListener() {
                        @Override
                        public void onFinish() {

                        }
                    })
                    .load();*/
    }

    private void loadAbout(){
        BSObject bsObject = new BSObject();
        bsObject.addProperty("method_name", "settings");
        new BSJson.Builder(this)
                .setServer(Constant.SERVER_URL)
                .setObject(bsObject.getProperty())
                .setMethod(BSJson.METHOD_POST)
                .setListener(new BSJsonV2Listener() {
                    @Override
                    public void onLoaded(String response) {
                        Log.d("responce splash",response);
                        try {
                            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                            JsonArray jsonArray = jsonObject.getAsJsonArray(Constant.TAG_ROOT);
                            JsonObject objJson = jsonArray.get(0).getAsJsonObject();
                            if(!objJson.get("application_id").getAsString().equals(BuildConfig.VERSION_NAME)){
                                showUpdate(objJson.get("update_url").getAsString(), objJson.get("update_message").getAsString());
                            } else {
                                Constant.banner_id = objJson.get("banner_id").getAsString();
                                Constant.interstitial_id = objJson.get("interstital_id").getAsString();
                                Constant.interstitial_click = Integer.parseInt(objJson.get("interstital_click").getAsString());
                                Constant.native_id = objJson.get("native_id").getAsString();
                                Constant.reward_id = objJson.get("reward_id").getAsString();
                                Constant.privacy_police = objJson.get("privacy_police").getAsString();

                                Constant.facebook_banner_id = objJson.get("facebook_banner_id").getAsString();
                                Constant.facebook_interstitial_id = objJson.get("facebook_interstitial_id").getAsString();
                                Constant.facebook_native_id = objJson.get("facebook_native_id").getAsString();
                                Constant.facebook_reward_id = objJson.get("facebook_reward_id").getAsString();
                                Constant.AdsOptions.IDENTIFIER = objJson.get("ads_provider").getAsString();

                                if (objJson.get("ads_provider").getAsString().equals(Constant.AdsOptions.DISABLE)) {
                                    Constant.banner_id = "";
                                    Constant.interstitial_id = "";
                                    Constant.interstitial_click = 99999;
                                    Constant.native_id = "";
                                    Constant.facebook_banner_id = "";
                                    Constant.facebook_interstitial_id = "";
                                    Constant.facebook_native_id = "";
                                }
                                if (objJson.get("ads_provider").getAsString().equals(Constant.AdsOptions.FACEBOOK)) {
                                    if (BuildConfig.DEBUG) {
                                        AdSettings.setTestMode(true);
                                    }
                                    FacebookNativeLoader.load(SplashActivity.this, new NativeLoaderListener() {
                                        @Override
                                        public void onFinish() {
                                            loadNative();
                                        }
                                    });
                                } else {
                                    loadNative();
                                }
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            Log.d("ABENK : ", e.getMessage());
                            Toast.makeText(SplashActivity.this, "Server error please try again later", Toast.LENGTH_SHORT).show();
                        }

                    }
                    @Override
                    public void onError(String error) {
                        Log.d("ABENK : ", error );
                        Toast.makeText(SplashActivity.this, "Server error please try again later", Toast.LENGTH_SHORT).show();
                    }
                })

                .load();
    }

    private void showUpdate(final String update_url, final String update_message){
        final Dialog dialog = new Dialog(this, R.style.FullDialog);
        View view = getLayoutInflater().inflate(R.layout.dialog_update, null);
        dialog.setContentView(view);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        TextView updatemessage = view.findViewById(R.id.update_message);
        updatemessage.setText(update_message);
        view.findViewById(R.id.allow_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(update_url)));
            }
        });
        dialog.show();
    }
}
