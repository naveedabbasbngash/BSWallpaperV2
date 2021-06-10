package com.livewallpapers.huawei.ui.category;


import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.data.base.BaseFragment;

public class CategoriesFragment extends BaseFragment<CategoriesView, CategoriesPresenter> implements CategoriesView{

    @Override
    protected void onStarting() {
        presenter.initView(requireActivity(), getRootView());
    }

    @Override
    protected void onDestroyed() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_categories;
    }

    @Override
    protected CategoriesPresenter initPresenter() {
        return new CategoriesPresenter();
    }

    @Override
    public void onItemClick(int cat_id, String cat_name) {

    }
}
