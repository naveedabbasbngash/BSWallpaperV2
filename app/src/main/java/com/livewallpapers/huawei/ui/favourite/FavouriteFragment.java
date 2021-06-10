package com.livewallpapers.huawei.ui.favourite;

import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.data.base.BaseFragment;


public class FavouriteFragment extends BaseFragment<FavouriteView, FavouritePresenter> {
    @Override
    protected void onStarting() {
        presenter.initView(requireActivity(), getRootView());
    }

    @Override
    protected void onDestroyed() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_favourite;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected FavouritePresenter initPresenter() {
        return new FavouritePresenter();
    }
}
