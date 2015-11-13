package com.tysci.chatapp.utils;

import com.tysci.chatapp.inputprovider.GameBettingInputProvider;
import com.tysci.chatapp.inputprovider.GameVideoInputProvider;

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
}
