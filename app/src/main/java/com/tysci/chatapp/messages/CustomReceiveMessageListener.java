package com.tysci.chatapp.messages;

import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;

/**
 * Created by Administrator on 2015/11/12.
 */
public class CustomReceiveMessageListener implements RongIMClient.OnReceiveMessageListener{
    @Override
    public boolean onReceived(Message message, int i) {

        return false;
    }
}
