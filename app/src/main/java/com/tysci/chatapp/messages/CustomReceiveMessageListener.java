package com.tysci.chatapp.messages;

import android.util.Log;

import com.tysci.chatapp.utils.DateUtils;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * Created by Administrator on 2015/11/12.
 */
public class CustomReceiveMessageListener implements RongIMClient.OnReceiveMessageListener{
    @Override
    public boolean onReceived(Message message, int i) {
        Log.e("MessageReceived", "接收时间:" + DateUtils.getSpecialFormatDate(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss"));
        return false;
    }
}
