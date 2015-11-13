package com.tysci.chatapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tysci.applibrary.base.BaseFragment;
import com.tysci.chatapp.activitys.R;

/**
 * Created by Administrator on 2015/11/11.
 */
public class GameDatasFragment extends BaseFragment{
    @Override
    protected View inflaterContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_game_datas,container,false);
    }

    @Override
    protected void initViews(View view) {

    }
}
