package com.livewallpapers.huawei.ui.ringtones;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.data.adapter.AdapterWallpaper;
import com.livewallpapers.huawei.data.base.BasePresenter;
import com.livewallpapers.huawei.data.base.EndlessRecyclerViewScrollListener;
import com.livewallpapers.huawei.data.utils.DatabaseHandler;
import com.livewallpapers.huawei.data.widgets.AnimationHelpers;
import com.livewallpapers.huawei.data.widgets.ProgressLoader;
import com.livewallpapers.huawei.ui.favourite.FavouriteView;
import com.livewallpapers.huawei.ui.main.MainActivity;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;

public class RingtonesPresenter extends BasePresenter<RingtonesView> {
    private Activity activity;
    private View rootView;
    public ProgressLoader progressLoader;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DatabaseHandler databaseHandler;
    private AdapterWallpaper adapterWallpaper;
    private RecyclerView recyclerView;
    protected void initView(Activity activity, View rootView) {
        this.activity = activity;
        this.rootView = rootView;

    }

    private void initSwipeRefresh(){

    }

    protected void onResume(){

    }

}
