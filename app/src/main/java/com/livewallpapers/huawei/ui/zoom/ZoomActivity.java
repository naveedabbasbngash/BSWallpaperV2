package com.livewallpapers.huawei.ui.zoom;

import androidx.databinding.DataBindingUtil;



import android.graphics.Color;
import android.view.View;

import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.data.base.BaseActivity;
import com.livewallpapers.huawei.databinding.ActivityZoomBinding;

public class ZoomActivity extends BaseActivity<ZoomView, ZoomPresenter> {
    private ActivityZoomBinding mBinding;

    @Override
    protected void beforeOnCreate() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_zoom;
    }

    @Override
    protected void onStarting() {
        mBinding = DataBindingUtil.setContentView(this, getLayoutId());
        presenter.initView(this, mBinding, getIntent().getStringExtra("str_image"));
    }

    @Override
    protected void onDestroyed() {

    }

    @Override
    protected ZoomPresenter initPresenter() {
        return new ZoomPresenter();
    }
}
