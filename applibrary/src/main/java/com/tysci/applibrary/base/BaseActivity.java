package com.tysci.applibrary.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tysci.applibrary.app.ActivityStacksManager;
import com.tysci.applibrary.networks.HttpClientUtil;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2015/11/10.
 */
abstract public class BaseActivity extends AppCompatActivity{
    protected final String Tag=this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HttpClientUtil.getHttpClientUtil().setRequestTag(Tag);
        ActivityStacksManager.getActivityStacksManager().addActivity(this);
        setRootContentView();
        ButterKnife.bind(this);
        initViews();
    }

    /**
     * 设置Activity的内容
     */
    abstract protected void setRootContentView();

    /**
     * 初始化控件
     */
    abstract protected void initViews();

    /**
     * 释放内存
     */
    protected void releaseMemeory(){

    }

    @Override
    protected void onDestroy() {
        HttpClientUtil.getHttpClientUtil().cancelRequest(Tag);
        ButterKnife.unbind(this);
        releaseMemeory();
        ActivityStacksManager.getActivityStacksManager().finishActivity(this);
        super.onDestroy();
    }
}
