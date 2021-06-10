package com.livewallpapers.huawei.ui.bycategory;

import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.data.base.BaseActivity;
import com.livewallpapers.huawei.data.utils.SharedPref;
import com.livewallpapers.huawei.databinding.ActivityByCategoryBinding;
import com.livewallpapers.huawei.ui.main.MainPresenter;

import androidx.databinding.DataBindingUtil;


public class ByCategoryActivity extends BaseActivity<ByCategoryView, ByCategoryPresenter> {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_by_category;
    }

    @Override
    protected void beforeOnCreate() {
        if (SharedPref.getSharedPref(this).contains(MainPresenter.THEME_MODE) && SharedPref.getSharedPref(this).readBoolean(MainPresenter.THEME_MODE)) {
            setTheme(R.style.BStudioThemeDark);
        } else {
            setTheme(R.style.BStudioThemeLight);
        }
    }

    @Override
    protected void onStarting() {
        ActivityByCategoryBinding binding = DataBindingUtil.setContentView(this, getLayoutId());
        presenter.initView(this, binding);
    }

    @Override
    protected void onDestroyed() {
    }

    @Override
    public void onBackPressed(){
        finish();
        overridePendingTransition(R.anim.no_change, R.anim.slide_down);
    }

    @Override
    protected ByCategoryPresenter initPresenter() {
        return new ByCategoryPresenter();
    }
}
