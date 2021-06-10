package com.livewallpapers.huawei.ui.gif;

import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.data.base.BaseFragment;

public class GifFragment extends BaseFragment<GifView, GifPresenter> {
    @Override
    protected void onStarting() {
        presenter.initView(requireActivity(), getRootView());
    }

    @Override
    protected void onDestroyed() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected GifPresenter initPresenter() {
        return new GifPresenter();
    }
}
