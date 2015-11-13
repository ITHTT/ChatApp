package com.tysci.applibrary.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/11/10.
 */
abstract public class BaseFragment extends Fragment{
    protected View contentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView=inflaterContentView(inflater,container,savedInstanceState);
        ButterKnife.bind(this,contentView);
        initViews(contentView);
        return contentView;
    }

    abstract protected View inflaterContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    abstract protected void initViews(View view);

    protected void releaseMemory(){

    }

    @Override
    public void onDestroy() {
        ButterKnife.unbind(this);
        contentView=null;
        releaseMemory();
        super.onDestroy();
    }
}
