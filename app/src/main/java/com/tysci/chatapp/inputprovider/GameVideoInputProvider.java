package com.tysci.chatapp.inputprovider;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.tysci.chatapp.activitys.R;

import io.rong.imkit.RongContext;
import io.rong.imkit.widget.provider.InputProvider;

/**
 * Created by Administrator on 2015/11/13.
 */
public class GameVideoInputProvider extends InputProvider.ExtendProvider {

    public GameVideoInputProvider(RongContext context) {
        super(context);
    }

    @Override
    public Drawable obtainPluginDrawable(Context context) {
        return context.getResources().getDrawable(R.mipmap.rc_ic_camera);
    }

    @Override
    public CharSequence obtainPluginTitle(Context context) {
        return context.getResources().getString(R.string.game_video);
    }

    @Override
    public void onPluginClick(View view) {

    }
}
