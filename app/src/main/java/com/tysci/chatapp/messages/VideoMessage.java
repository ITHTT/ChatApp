package com.tysci.chatapp.messages;

import android.database.Cursor;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Parcel;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.rong.common.ParcelUtils;
import io.rong.imlib.MessageTag;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;

/**
 * Created by Administrator on 2015/11/13.
 * 自定义Video消息类
 */
@MessageTag(value="app:video",flag=MessageTag.ISCOUNTED | MessageTag.ISPERSISTED,messageHandler = VideoMessageHandler.class)
public class VideoMessage extends MessageContent {
    /**上传视频的url*/
    private Uri remoteVideoUri;
    /**上传的视频的本地uri*/
    private Uri localVideoUri;
    /**是否上传成功*/
    private boolean isUploadExp=false;
    /**视频图片的base64编码*/
    private String base64ImgContent;

    public static final Creator<VideoMessage> CREATOR = new Creator() {
        public VideoMessage createFromParcel(Parcel source) {
            return new VideoMessage(source);
        }

        public VideoMessage[] newArray(int size) {
            return new VideoMessage[size];
        }
    };

    public VideoMessage(byte[] data) {
        String jsonStr = new String(data);
        if(!TextUtils.isEmpty(jsonStr)){
            JSONObject jsonObject= JSON.parseObject(jsonStr);
            if(!jsonObject.isEmpty()){
                String videoUrl=jsonObject.getString("videoUrl");
                if(!TextUtils.isEmpty(videoUrl)){
                    this.remoteVideoUri=Uri.parse(videoUrl);
                }
                if(this.getRemoteVideoUri() != null && this.getRemoteVideoUri().getScheme() != null && !this.getRemoteVideoUri().getScheme().equals("http")){
                    this.localVideoUri=this.remoteVideoUri;
                }
                base64ImgContent=jsonObject.getString("imgContent");
                this.isUploadExp=true;
                this.setUserInfo(jsonObject.getObject("user",UserInfo.class));
            }
        }
    }

    public VideoMessage(Parcel parcel){
        setRemoteVideoUri((Uri)ParcelUtils.readFromParcel(parcel,Uri.class));
        setLocalVideoUri((Uri) ParcelUtils.readFromParcel(parcel, Uri.class));
        setBase64ImgContent(ParcelUtils.readFromParcel(parcel));
        setUserInfo(ParcelUtils.readFromParcel(parcel,UserInfo.class));
    }

    public VideoMessage(Uri localVideoUri,String imgContent){
        this.localVideoUri=localVideoUri;
        //ThumbnailUtils.createVideoThumbnail(localVideoUri.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
        this.base64ImgContent=imgContent;
    }

    @Override
    public byte[] encode() {
        JSONObject jsonObject=new JSONObject();

        if(!TextUtils.isEmpty(base64ImgContent)){
            jsonObject.put("imgContent",this.base64ImgContent);
        }

        if(remoteVideoUri!=null&&!TextUtils.isEmpty(remoteVideoUri.toString())){
            jsonObject.put("videoUrl",this.remoteVideoUri.toString());
        }else if(localVideoUri!=null&&!TextUtils.isEmpty(localVideoUri.toString())){
            jsonObject.put("videoUrl",this.localVideoUri.toString());
        }

        if(isUploadExp){
            jsonObject.put("exp",true);
        }

        if(this.getJSONUserInfo()!=null){
            jsonObject.put("user", this.getJSONUserInfo());
        }
        this.base64ImgContent=null;
        return jsonObject.toString().getBytes();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        ParcelUtils.writeToParcel(dest,this.remoteVideoUri);
        ParcelUtils.writeToParcel(dest,this.localVideoUri);
        ParcelUtils.writeToParcel(dest,this.base64ImgContent);
        ParcelUtils.writeToParcel(dest,this.getUserInfo());
    }

    public Uri getRemoteVideoUri() {
        return remoteVideoUri;
    }

    public void setRemoteVideoUri(Uri remoteVideoUri) {
        this.remoteVideoUri = remoteVideoUri;
    }

    public Uri getLocalVideoUri() {
        return localVideoUri;
    }

    public void setLocalVideoUri(Uri localVideoUri) {
        this.localVideoUri = localVideoUri;
    }

    public boolean isUploadExp() {
        return isUploadExp;
    }

    public void setIsUploadExp(boolean isUploadExp) {
        this.isUploadExp = isUploadExp;
    }

    public String getBase64ImgContent() {
        return base64ImgContent;
    }

    public void setBase64ImgContent(String base64ImgContent) {
        this.base64ImgContent = base64ImgContent;
    }
}
