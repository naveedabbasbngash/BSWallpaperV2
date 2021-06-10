package com.livewallpapers.huawei.data.base;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;


@SuppressWarnings("unchecked")
public abstract class BaseActivity<V extends MvpView, P extends BasePresenter<V>> extends AppCompatActivity implements MvpView{
    protected P presenter;
    protected ViewDataBinding dataBinding;
    protected abstract int getLayoutId();
    protected abstract void beforeOnCreate();
    protected abstract void onStarting();
    protected abstract void onDestroyed();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        beforeOnCreate();
        super.onCreate(savedInstanceState);
        try {
            dataBinding = DataBindingUtil.setContentView(this, getLayoutId());
        } catch (Exception e) {
            if (dataBinding == null) {
                setContentView(getLayoutId());
            }
        }
        BaseViewModel<V, P> baseViewModel = ViewModelProviders.of(this).get(BaseViewModel.class);
        boolean isPresenterCreated = false;
        if (baseViewModel.getPresenter() == null) {
            baseViewModel.setPresenter(initPresenter());
            isPresenterCreated = true;
        }
        presenter = baseViewModel.getPresenter();
        presenter.attachLifecycle(getLifecycle());
        presenter.attachView((V) this);
        if (isPresenterCreated) {
            presenter.onPresenterCreated();
        }
        onStarting();
    }

    protected static void runCurrentActivity(Activity activity, Class nextClass) {
        activity.startActivity(new Intent(activity, nextClass));
    }

    @Override
    protected void onDestroy() {
        onDestroyed();
        super.onDestroy();
        presenter.detachLifecycle(getLifecycle());
        presenter.detachView();
    }

    protected abstract P initPresenter();

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    protected BaseFragment mBaseCurrentFragment;

    protected void callFragment(int layoutId, BaseFragment baseFragment) {
//        getSupportFragmentManager()
//                .beginTransaction()
//                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
//                .replace(layoutId, baseFragment, baseFragment.getClass().getName())
//                .commit();
//
//        setCurrentFragment(baseFragment);
        getSupportFragmentManager().beginTransaction().replace(layoutId, baseFragment, baseFragment.getClass().getName()).commit();
    }

    public void replaceFragment(int layoutId, BaseFragment fragment, String name) {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getSupportFragmentManager().popBackStack();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(layoutId, fragment, name);
        fragmentTransaction.commit();
    }

    public void addFragment(int layoutId, BaseFragment fragment, String name) {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getSupportFragmentManager().popBackStack();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.hide(getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount()));
        fragmentTransaction.add(layoutId, fragment, name);
        fragmentTransaction.addToBackStack(name);
        fragmentTransaction.commit();
    }
    public void addFragment2(int layoutId, BaseFragment fragment, String name) {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); ++i) {
            getSupportFragmentManager().popBackStack();
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        //fragmentTransaction.hide(getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount()));
        fragmentTransaction.add(layoutId, fragment, name);
        fragmentTransaction.addToBackStack(name);
        fragmentTransaction.commit();
    }

    protected void setCurrentFragment(BaseFragment baseFragment) {
        this.mBaseCurrentFragment = baseFragment;
    }

    protected BaseFragment getBaseCurrentFragment() {
        return mBaseCurrentFragment;
    }

}