package com.livewallpapers.huawei.ui.ringtones;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.livewallpapers.huawei.R;
import com.livewallpapers.huawei.data.base.BaseFragment;


public class RingtonesFragment extends BaseFragment<RingtonesView, RingtonesPresenter> {

    @Override
    protected void onStarting() {
        presenter.initView(requireActivity(), getRootView());

    }

    @Override
    protected void onDestroyed() {

    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ringtones;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected RingtonesPresenter initPresenter() {
        return new RingtonesPresenter();
    }
}