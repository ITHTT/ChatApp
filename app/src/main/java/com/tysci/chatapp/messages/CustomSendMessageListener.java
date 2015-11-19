package com.tysci.chatapp.messages;

import android.util.Log;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Message;

/**
 * Created by Administrator on 2015/11/12.
 */
public class CustomSendMessageListener implements RongIM.OnSendMessageListener{

    @Override
    public Message onSend(Message message) {
        return message;
    }

    @Override
    public boolean onSent(Message message, RongIM.SentMessageErrorCode sentMessageErrorCode) {
        if(message.getSentStatus().equals(Message.SentStatus.FAILED)){
            Log.e("MessageSend", "发送失败");
        }
        return false;
    }
}
