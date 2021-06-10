package com.livewallpapers.huawei.ui.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.benkkstudio.bsjson.BSJson;
import com.benkkstudio.bsjson.BSObject;
import com.benkkstudio.bsjson.Interface.BSJsonV2Listener;
import com.google.android.gms.common.internal.Constants;
import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.data.adapter.AdapterWallpaper;
import com.livewallpapers.huawei.ui.main.MainActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


import com.livewallpapers.huawei.data.base.BasePresenter;
import com.livewallpapers.huawei.data.base.EndlessRecyclerViewScrollListener;
import com.livewallpapers.huawei.data.model.ModelWallpaper;
import com.livewallpapers.huawei.data.utils.Constant;
import com.livewallpapers.huawei.data.utils.FacebookNativeLoader;
import com.livewallpapers.huawei.data.utils.Helpers;
import com.livewallpapers.huawei.data.widgets.ProgressLoader;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HomePresenter extends BasePresenter<HomeView> {
    public static final String FILTER_POPULAR = "FILTER_POPULAR";
    public static final String FILTER_LATEST = "FILTER_LATEST";
    public static final String FILTER_PREMIUM = "FILTER_PREMIUM";
    public static final String FILTER_FREE = "FILTER_FREE";
    public static final String FILTER_GIF = "FILTER_GIF";
    public static final String FILTER_IMAGE = "FILTER_IMAGE";
    private Activity activity;
    private View rootView;
    private int pageCount = 1, totalPost;
    private ArrayList<ModelWallpaper> arrayList;
    private AdapterWallpaper adapterWallpaper;
    public ProgressLoader progressLoader;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    private AsyncHttpClient client;
    private StringEntity stringEntity;
    private Context context;

    protected void initView(Activity activity, View rootView) {
        this.activity = activity;
        this.rootView = rootView;
        progressLoader = new ProgressLoader(activity);
        initRecyclerViewHome();
        initSwipeRefresh();
    }

    private void initSwipeRefresh() {
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pageCount = 1;
                initRecyclerViewHome();
            }
        });
        rootView.findViewById(R.id.button_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterDialog();
            }
        });
    }

    private void initRecyclerViewHome() {
        recyclerView = rootView.findViewById(R.id.recycler_view);
        final GridLayoutManager grid = new GridLayoutManager(activity, 3);
        recyclerView.setLayoutManager(grid);
        adapterWallpaper = new AdapterWallpaper(activity, new ArrayList<ModelWallpaper>());

        ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(adapterWallpaper);
        scaleInAnimationAdapter.setFirstOnly(false);
        scaleInAnimationAdapter.setDuration(2000);
        scaleInAnimationAdapter.setInterpolator(new OvershootInterpolator(.5f));
        recyclerView.setAdapter(scaleInAnimationAdapter);

        grid.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (adapterWallpaper.getItemViewType(position)) {
                    case AdapterWallpaper.VIEW_ITEM:
                        return 1;
                    case AdapterWallpaper.VIEW_PROGRESS:
                    case AdapterWallpaper.VIEW_NATIVE:
                        return 3;
                    default:
                        return -1;
                }
            }
        });
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(grid) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (adapterWallpaper.arrayList.size() <= totalPost) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapterWallpaper.removeLoading();
                            loadNow();
                        }
                    }, 1000);
                } else {
                    adapterWallpaper.removeLoading();
                }
            }

            @Override
            public void onScrolled(int dy) {
            }
        });

        if (Helpers.isConnected(activity)) {
            recyclerView.setVisibility(View.VISIBLE);
            rootView.findViewById(R.id.layout_no_connection).setVisibility(View.GONE);
            loadNow();
        } else {
            recyclerView.setVisibility(View.GONE);
            rootView.findViewById(R.id.layout_no_connection).setVisibility(View.VISIBLE);
            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                swipeRefreshLayout.setRefreshing(false);
            }
        }

    }

    private AsyncHttpClient getClient(){
        if (client == null)
        {
            client = new AsyncHttpClient();
            client.setTimeout(46000);
            client.setConnectTimeout(40000); // default is 10 seconds, minimum is 1 second
            client.setResponseTimeout(40000);
        }

        return client;
    }


    private void loadNow() {
        progressLoader.show();
        arrayList = new ArrayList<>();
   /*     JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("method_name", "wallpaper_all");
        jsonObject.addProperty("page", pageCount);
        jsonObject.addProperty("limit", Constant.WALLPAPER_LOADING_POSITION);
        jsonObject.addProperty("filter", Constant.filter_value);

        try {
            stringEntity = new StringEntity(jsonObject.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        getClient().post(context, Constant.SERVER_URL, stringEntity,
                "application/json", new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                //showNow.showLoadingDialog(EmergencyContacts.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                Log.d("RESPONSE", "onSuccess: " + json);
                try {
                    //JsonObject jsonObject = new JsonObject(json);
                    JSONObject jsonObject1 = new JSONObject(json);
                    JSONArray jsonArray = jsonObject1.getJSONArray("BENKKSTUDIO");
                    //JsonArray jsonArray = jsonObject.getAsJsonArray(Constant.TAG_ROOT);
                    Log.e("RESPONSE",responseBody.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //JsonObject objJson = jsonArray.get(i).getAsJsonObject();
                        JSONObject jsonObjectNew = jsonArray.getJSONObject(i);
                        if (Constant.native_options) {
                            if (i == (Constant.WALLPAPER_LOADING_POSITION / 2)) {
                                if(Constant.AdsOptions.IDENTIFIER.equals(Constant.AdsOptions.FACEBOOK)){
                                    arrayList.add(new ModelWallpaper(AdapterWallpaper.VIEW_NATIVE, FacebookNativeLoader.nativeAd));
                                    FacebookNativeLoader.loadNext(activity);
                                } else {
                                    arrayList.add(new ModelWallpaper(AdapterWallpaper.VIEW_NATIVE));
                                }
                            }
                        }
                      *//*  totalPost = objJson.get("post_total").getAsInt();
                        int wall_id = objJson.get("wall_id").getAsInt();
                        int cat_id = objJson.get("cat_id").getAsInt();*//*
                        totalPost = (int) jsonObjectNew.get("post_total");
                        int wall_id = (int) jsonObjectNew.get("wall_id");
                        int cat_id = (int) jsonObjectNew.get("cat_id");
                      *//*  String wallpaper_title = objJson.get("wallpaper_title").getAsString();
                        String wallpaper_image_thumb = objJson.get("wallpaper_image_thumb").getAsString();
                        String wallpaper_image_detail = objJson.get("wallpaper_image_detail").getAsString();
                        String wallpaper_image_original = objJson.get("wallpaper_image_original").getAsString();
                        String wallpaper_tags = objJson.get("wallpaper_tags").getAsString();
                        String wallpaper_type = objJson.get("wallpaper_type").getAsString();
                        String wallpaper_premium = objJson.get("wallpaper_premium").getAsString();*//*
                        String wallpaper_title = (String) jsonObjectNew.get("wallpaper_title");
                        String wallpaper_image_thumb = (String) jsonObjectNew.get("wallpaper_image_thumb");
                        String wallpaper_image_detail = (String) jsonObjectNew.get("wallpaper_image_detail");
                        String wallpaper_image_original = (String) jsonObjectNew.get("wallpaper_image_original");
                        String wallpaper_tags = (String) jsonObjectNew.get("wallpaper_tags");
                        String wallpaper_type = (String) jsonObjectNew.get("wallpaper_type");
                        String wallpaper_premium = (String) jsonObjectNew.get("wallpaper_premium");
                        arrayList.add(new ModelWallpaper(wall_id, cat_id, wallpaper_title, wallpaper_image_thumb, wallpaper_image_detail, wallpaper_image_original, wallpaper_tags, wallpaper_type, wallpaper_premium, AdapterWallpaper.VIEW_ITEM));
                    }
                    adapterWallpaper.insertData(arrayList);
                    pageCount++;
                } catch (JsonSyntaxException | JSONException e) {
                    e.printStackTrace();
                }
                swipeRefreshLayout.setRefreshing(false);
                progressLoader.stopLoader();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String json = new String(responseBody);
                Log.e("REPONSE2", "onSuccess: " + json);
                progressLoader.dismiss();
                //Toast.makeText(EmergencyContacts.this, "ERROR: "+json, Toast.LENGTH_LONG).show();
                //showNow.scheduleDismiss();
            }

            @Override
            public void onCancel() {
                super.onCancel();
                progressLoader.dismiss();
                //showNow.scheduleDismiss();
            }

        });*/

        BSObject bsObject = new BSObject();
        bsObject.addProperty("method_name", "wallpaper_all");
        bsObject.addProperty("page", pageCount);
        bsObject.addProperty("limit", Constant.WALLPAPER_LOADING_POSITION);
        bsObject.addProperty("filter", Constant.filter_value);
        new BSJson.Builder(activity)
                .setServer(Constant.SERVER_URL)
                .setObject(bsObject.getProperty())
                .setMethod(BSJson.METHOD_POST)
                .setListener(new BSJsonV2Listener() {
                    @Override
                    public void onLoaded(String response) {
                        try {
                            JsonObject jsonObject = JsonParser.parseString(response).getAsJsonObject();
                            JsonArray jsonArray = jsonObject.getAsJsonArray(Constant.TAG_ROOT);
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JsonObject objJson = jsonArray.get(i).getAsJsonObject();
                                if (Constant.native_options) {
                                    if (i == (Constant.WALLPAPER_LOADING_POSITION / 2)) {
                                        if(Constant.AdsOptions.IDENTIFIER.equals(Constant.AdsOptions.FACEBOOK)){
                                            arrayList.add(new ModelWallpaper(AdapterWallpaper.VIEW_NATIVE, FacebookNativeLoader.nativeAd));
                                            FacebookNativeLoader.loadNext(activity);
                                        } else {
                                            arrayList.add(new ModelWallpaper(AdapterWallpaper.VIEW_NATIVE));
                                        }
                                    }
                                }
                                totalPost = objJson.get("post_total").getAsInt();
                                int wall_id = objJson.get("wall_id").getAsInt();
                                int cat_id = objJson.get("cat_id").getAsInt();
                                String wallpaper_title = objJson.get("wallpaper_title").getAsString();
                                String wallpaper_image_thumb = objJson.get("wallpaper_image_thumb").getAsString();
                                String wallpaper_image_detail = objJson.get("wallpaper_image_detail").getAsString();
                                String wallpaper_image_original = objJson.get("wallpaper_image_original").getAsString();
                                String wallpaper_tags = objJson.get("wallpaper_tags").getAsString();
                                String wallpaper_type = objJson.get("wallpaper_type").getAsString();
                                String wallpaper_premium = objJson.get("wallpaper_premium").getAsString();
                                arrayList.add(new ModelWallpaper(wall_id, cat_id, wallpaper_title, wallpaper_image_thumb, wallpaper_image_detail, wallpaper_image_original, wallpaper_tags, wallpaper_type, wallpaper_premium, AdapterWallpaper.VIEW_ITEM));
                            }
                            adapterWallpaper.insertData(arrayList);
                            pageCount++;
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        progressLoader.stopLoader();
                    }

                    @Override
                    public void onError(String error) {
                        swipeRefreshLayout.setRefreshing(false);
                        progressLoader.stopLoader();
                    }
                })
                .load();
    }

    private void showFilterDialog() {
        @SuppressLint("InflateParams")
        View view = activity.getLayoutInflater().inflate(R.layout.dialog_filter, null);
        final BottomSheetDialog dialog_desc = new BottomSheetDialog(activity, R.style.SheetDialog);
        dialog_desc.setContentView(view);
        dialog_desc.show();
        final String[] filter_id = {FILTER_POPULAR, FILTER_LATEST, FILTER_PREMIUM, FILTER_FREE, FILTER_GIF, FILTER_IMAGE};
        final int[] card_view_id = {R.id.popular_button, R.id.latest_button, R.id.premium_button, R.id.free_button, R.id.gif_button, R.id.image_button};
        final int[] text_view_id = {R.id.text_popular, R.id.text_latest, R.id.text_premium, R.id.text_free, R.id.text_gif, R.id.text_image};
        for (int i = 0; i < card_view_id.length; i++) {
            CardView cardView = dialog_desc.findViewById(card_view_id[i]);
            final TextView textView = dialog_desc.findViewById(text_view_id[i]);
            assert cardView != null;
            assert textView != null;
            if(card_view_id[i] == Constant.selected_filter){
                cardView.setCardBackgroundColor(activity.getResources().getColor(R.color.colorAccent));
                textView.setTextColor(activity.getResources().getColor(android.R.color.white));
            }
            final int finalI = i;
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_desc.dismiss();
                    Constant.selected_filter = card_view_id[finalI];
                    Constant.filter_value = filter_id[finalI];
                    ((MainActivity)activity).binding.toolbarTitle.setText(textView.getText().toString());
                    pageCount = 1;
                    initRecyclerViewHome();
                }
            });
        }
    }
}
