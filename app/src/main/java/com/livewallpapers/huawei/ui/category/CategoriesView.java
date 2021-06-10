package com.livewallpapers.huawei.ui.category;

import com.livewallpapers.huawei.data.base.MvpView;

public interface CategoriesView extends MvpView {
    void onItemClick(int cat_id, String cat_name);
}
