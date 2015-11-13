package com.tysci.chatapp.messages;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Message;

/**
 * Created by Administrator on 2015/11/12.
 */
public class CustomSendMessageListener implements RongIM.OnSendMessageListener{

    @Override
    public Message onSend(Message message) {
        return null;
    }

    @Override
    public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
        return false;
    }
}
