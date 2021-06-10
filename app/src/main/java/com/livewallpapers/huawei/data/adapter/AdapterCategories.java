package com.livewallpapers.huawei.data.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.data.base.BaseRecyclerViewClickListener;
import com.livewallpapers.huawei.data.model.ModelCategories;
import com.livewallpapers.huawei.data.utils.Constant;
import com.livewallpapers.huawei.ui.bycategory.ByCategoryActivity;


import com.airbnb.lottie.LottieAnimationView;
import com.benkkstudio.bsmob.BSMob;
import com.benkkstudio.bsmob.Interface.BannerListener;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AdapterCategories extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_PROGRESS = 0;
    public static final int VIEW_ITEM = 1;
    public static final int VIEW_BANNER = 2;

    private Activity activity;
    public ArrayList<ModelCategories> arrayList;
    private BaseRecyclerViewClickListener<ModelCategories> baseRecyclerViewClickListener;

    public AdapterCategories(Activity activity, ArrayList<ModelCategories> arrayList) {
        this.activity = activity;
        this.arrayList = arrayList;
    }

    public void setListener(BaseRecyclerViewClickListener<ModelCategories> baseRecyclerViewClickListener){
        this.baseRecyclerViewClickListener = baseRecyclerViewClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == VIEW_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_category, parent, false);
            viewHolder = new OriginalViewHolder(view);
        } else if(viewType == VIEW_BANNER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_banner, parent, false);
            viewHolder = new BannerAdViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lsv_item_progressbar, parent, false);
            viewHolder = new ProgressViewHolder(view);
        }
        return viewHolder;
    }

    class OriginalViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView image;
        LottieAnimationView progressBar;

        OriginalViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            progressBar = itemView.findViewById(R.id.progressBar);

        }
    }

    class ProgressViewHolder extends RecyclerView.ViewHolder {
        LottieAnimationView progressBar;
        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public class BannerAdViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llAds;
        BannerAdViewHolder(View view) {
            super(view);
            llAds = view.findViewById(R.id.llAds);
            if(Constant.banner_options){
                if(Constant.AdsOptions.IDENTIFIER.equals(Constant.AdsOptions.FACEBOOK)){
                    AdView adView = new AdView(activity, Constant.facebook_banner_id, AdSize.BANNER_HEIGHT_50);
                    llAds.addView(adView);
                    adView.loadAd();
                } else {
                    new BSMob.banner(activity)
                            .setAdRequest(new AdRequest.Builder().build())
                            .setId(Constant.banner_id)
                            .setLayout(llAds)
                            .setListener(new BannerListener() {
                                @Override
                                public void onAdFailedToLoad(int error) {
                                    llAds.setVisibility(View.GONE);
                                }

                                @Override
                                public void onAdLoaded() {
                                    llAds.setVisibility(View.VISIBLE);
                                }
                            })
                            .setSize(BSMob.adaptiveSize(activity))
                            .show();
                }
            }
        }
    }



    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holderPrent, final int position) {
        final ModelCategories modelCategories = arrayList.get(position);
        if (getItemViewType(position) == VIEW_ITEM) {
            final OriginalViewHolder holder = (OriginalViewHolder) holderPrent;
            Picasso.get()
                    .load(modelCategories.category_image_thumb)
                    .placeholder(R.drawable.placeholder)
                    .into(holder.image, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.title.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

            holder.title.setText(String.format("%s ~ %s Images", modelCategories.category_name, modelCategories.total_wallpaper));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Constant.cat_id = modelCategories.id;
                    Constant.cat_name = modelCategories.category_name;
                    activity.startActivity( new Intent(activity, ByCategoryActivity.class));
                    activity.overridePendingTransition(R.anim.slide_up, R.anim.no_change);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return arrayList.get(position).viewType;
    }

    public void insertData(ArrayList<ModelCategories> arrayList) {
        this.arrayList.addAll(arrayList);
        notifyItemRangeInserted(getItemCount(), arrayList.size());
        if(arrayList.size() >= Constant.WALLPAPER_LOADING_POSITION){
            this.arrayList.add(new ModelCategories(VIEW_PROGRESS));
            notifyItemRangeInserted(getItemCount(), 1);
        }
    }

    public void removeLoading() {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).viewType == VIEW_PROGRESS) {
                arrayList.remove(i);
                notifyItemRemoved(i);
            }
        }
    }
}