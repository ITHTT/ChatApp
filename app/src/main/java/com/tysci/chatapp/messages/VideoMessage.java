package com.tysci.chatapp.messages;

import android.os.Parcel;

import io.rong.imlib.model.MessageContent;

/**
 * Created by Administrator on 2015/11/13.
 * 自定义Video消息类
 */
public class VideoMessage extends MessageContent {
    @Override
    public byte[] encode() {
        return new byte[0];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
