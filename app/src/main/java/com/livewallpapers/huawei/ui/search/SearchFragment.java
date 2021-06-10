package com.livewallpapers.huawei.ui.search;

import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.data.base.BaseFragment;

public class SearchFragment extends BaseFragment<SearchView, SearchPresenter> {
    @Override
    protected void onStarting() {
        presenter.initView(requireActivity(), getRootView());
    }

    @Override
    protected void onDestroyed() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected SearchPresenter initPresenter() {
        return new SearchPresenter();
    }
}
