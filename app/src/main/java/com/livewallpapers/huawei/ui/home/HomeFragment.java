package com.livewallpapers.huawei.ui.home;


import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.data.base.BaseFragment;

public class HomeFragment extends BaseFragment<HomeView, HomePresenter> {

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
    protected HomePresenter initPresenter() {
        return new HomePresenter();
    }
}
