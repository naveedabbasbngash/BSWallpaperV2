package com.livewallpapers.huawei.ui.main;

import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.ViewPager;

import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.Settings;
import com.livewallpapers.huawei.data.adapter.ViewPagerAdapter;
import com.livewallpapers.huawei.data.base.BaseActivity;
import com.livewallpapers.huawei.data.utils.SharedPref;
import com.livewallpapers.huawei.databinding.ActivityMainBinding;


import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends BaseActivity<MainView, MainPresenter> implements MainView{
    public ActivityMainBinding binding;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void beforeOnCreate() {
        if (SharedPref.getSharedPref(this).contains(MainPresenter.THEME_MODE) &&
                SharedPref.getSharedPref(this).readBoolean(MainPresenter.THEME_MODE)) {
            setTheme(R.style.BStudioThemeDark);
        } else {
            setTheme(R.style.BStudioThemeLight);
        }
    }

    @Override
    protected void onStarting() {
        if(!Settings.premium_unlocked_premanent){
            SharedPref.getSharedPref(this).write("premium_checker", "");
        }
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        presenter.initView(this, binding, getSupportFragmentManager());
        initViewPager();
    }

    private void initViewPager(){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.viewPager.setAdapter(viewPagerAdapter);
        binding.viewPager.setCurrentItem(2);
        binding.viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroyed() {
    }

    @Override
    protected MainPresenter initPresenter() {
        return new MainPresenter();
    }

    @Override
    public void BottomNavigationCenter() {

    }

    @Override
    public void BottomNavigationHome() {

    }

    @Override
    public void BottomNavigationCategory() {

    }

    public void exitCollapsed(){
        presenter.exitCollapsed();
    }


}
