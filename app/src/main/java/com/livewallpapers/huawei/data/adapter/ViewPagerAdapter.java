package com.livewallpapers.huawei.data.adapter;

import com.livewallpapers.huawei.ui.category.CategoriesFragment;
import com.livewallpapers.huawei.ui.gif.GifFragment;
import com.livewallpapers.huawei.ui.home.HomeFragment;
import com.livewallpapers.huawei.ui.ringtones.RingtonesFragment;
import com.livewallpapers.huawei.ui.search.SearchFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.livewallpapers.huawei.ui.favourite.FavouriteFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new CategoriesFragment();
            case 1:
                return new SearchFragment();
            case 2:
                return new HomeFragment();
            case 3:
                return new GifFragment();
            case 4:
                return new FavouriteFragment();
            case 5:
                return new RingtonesFragment();
        }
        return new HomeFragment();
    }

    @Override
    public int getCount() {
        return 5;
    }
}