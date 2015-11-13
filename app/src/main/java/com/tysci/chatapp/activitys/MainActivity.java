package com.tysci.chatapp.activitys;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.android.volley.VolleyError;
import com.tysci.applibrary.base.BaseActivity;
import com.tysci.applibrary.networks.HttpClientUtil;
import com.tysci.applibrary.networks.HttpResponseHandler;
import com.tysci.chatapp.app.AppApplication;
import com.tysci.chatapp.fragments.GameBettingFragment;
import com.tysci.chatapp.fragments.GameChatRoomFragment;
import com.tysci.chatapp.fragments.GameDatasFragment;
import com.tysci.chatapp.fragments.GameInfosFragment;
import com.tysci.chatapp.networks.HttpUrls;
import com.tysci.chatapp.utils.RongYunUtils;
import com.tysci.chatapp.utils.ToastUtil;
import com.tysci.chatapp.views.widgets.CustomViewPager;

import butterknife.Bind;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class MainActivity extends BaseActivity implements ViewPager.OnPageChangeListener{
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.tabLayout)
    protected TabLayout tabLayout;
    @Bind(R.id.viewPager)
    protected CustomViewPager viewPager;

    protected String[] titles={"球聊","指数","投注明细","赛况"};

    protected GameChatRoomFragment gameChatRoomFragment;
    protected GameDatasFragment gameDatasFragment;
    protected GameBettingFragment gameBettingFragment;
    protected GameInfosFragment gameInfosFragment;

    @Override
    protected void setRootContentView() {
        RongYunUtils.setInputProvider();
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initViews() {
        setSupportActionBar(toolbar);
        this.getSupportActionBar().setTitle("聊天室");
        viewPager.requestDisallowInterceptTouchEvent(true);

        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        for(int i=0;i<titles.length;i++){
            addTab(titles[i],i);
        }

        GameFragmentPagerAdapter adapter=new GameFragmentPagerAdapter(this.getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setTabsFromPagerAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOnPageChangeListener(this);
        viewPager.isNoScroll(true);

    }

    protected void addTab(String title,int index){
        View view= LayoutInflater.from(this).inflate(R.layout.layout_tab,null);
        TextView textTitle=(TextView)view.findViewById(R.id.tv_tab_title);
        TextView textMsg=(TextView)view.findViewById(R.id.tv_tab_msg_count);
        if(textMsg!=null){
            textMsg.setVisibility(View.GONE);
        }
        if(textTitle!=null){
          textTitle.setText(title);
         }
        tabLayout.addTab(tabLayout.newTab().setCustomView(view).setText(title), index, true);
    }

    public void setViewPagerNoScroll(boolean isScroll){
        viewPager.isNoScroll(isScroll);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(position==0){
            viewPager.isNoScroll(true);
        }else{
            viewPager.isNoScroll(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class GameFragmentPagerAdapter extends FragmentStatePagerAdapter{

        public GameFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    if(gameChatRoomFragment==null){
                        gameChatRoomFragment=new GameChatRoomFragment();
                    }
                    return gameChatRoomFragment;
                case 1:
                    if(gameDatasFragment==null){
                        gameDatasFragment=new GameDatasFragment();
                    }
                    return gameDatasFragment;
                case 2:
                    if(gameBettingFragment==null){
                        gameBettingFragment=new GameBettingFragment();
                    }
                    return gameBettingFragment;
                case 3:
                    if(gameInfosFragment==null){
                        gameInfosFragment=new GameInfosFragment();
                    }
                    return gameInfosFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}
