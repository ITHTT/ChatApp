package com.tysci.chatapp.utils;

import com.tysci.chatapp.messages.CustomConnectionStatusListener;
import com.tysci.chatapp.messages.CustomReceiveMessageListener;
import com.tysci.chatapp.messages.CustomSendMessageListener;
import com.tysci.chatapp.provider.GameBettingInputProvider;
import com.tysci.chatapp.provider.GameVideoInputProvider;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.model.Conversation;

/**
 * Created by Administrator on 2015/11/13.
 */
public class RongYunUtils {

    public static void setInputProvider(){
        InputProvider.ExtendProvider[] provider = {
                new GameBettingInputProvider(RongContext.getInstance()),
                new ImageInputProvider(RongContext.getInstance()),//图片
                new CameraInputProvider(RongContext.getInstance()),//相机
                new GameVideoInputProvider(RongContext.getInstance())
        };
        RongIM.getInstance().resetInputExtensionProvider(Conversation.ConversationType.CHATROOM, provider);
    }

    public static void setSendMessageListener(CustomSendMessageListener listener){
        RongIM.getInstance().setSendMessageListener(listener);
    }

    public static void setOnReceiveMessageListener(CustomReceiveMessageListener listener){
        RongIM.setOnReceiveMessageListener(listener);
    }

    public static void setConnectionStatusListener(CustomConnectionStatusListener listener){
        RongIM.getInstance().getRongIMClient().setConnectionStatusListener(listener);
    }
}
