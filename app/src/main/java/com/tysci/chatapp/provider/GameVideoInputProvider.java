package com.tysci.chatapp.provider;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.squareup.okhttp.Request;
import com.tysci.applibrary.networks.HttpClientUtil;
import com.tysci.chatapp.activitys.R;
import com.tysci.chatapp.app.AppConfigInfo;
import com.tysci.chatapp.messages.MideaVideoMessage;
import com.tysci.chatapp.messages.VideoMessage;
import com.tysci.chatapp.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.RongIMClientWrapper;
import io.rong.imkit.widget.InputView;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;

/**
 * Created by Administrator on 2015/11/13.
 */
public class GameVideoInputProvider extends InputProvider.ExtendProvider {
    private final int requestCode=0x0001;

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
        ToastUtil.toastMsg(getContext(), "点击视频");
        InputView inputView=getInputView();
        inputView.clearFocus();
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==this.requestCode&&resultCode== Activity.RESULT_OK){
            if(data!=null){
                Uri uri=data.getData();
                if(uri!=null){
                    Conversation conversation=this.getCurrentConversation();
                    UploadVideoRunnable uploadVideoRunnable=new UploadVideoRunnable(conversation.getTargetId(),conversation.getConversationType(),uri,AppConfigInfo.getImagePath());
                    RongContext.getInstance().executorBackground(uploadVideoRunnable);
                }
            }
        }
    }

    private String getRealPath(Uri fileUri){
        String fileName = null;
        String[] proj = { MediaStore.Video.Media.DATA };
        Cursor actualimagecursor = this.getContext().getContentResolver().query(fileUri, proj, null, null, null);
        if(actualimagecursor.moveToFirst()){
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            fileName=actualimagecursor.getString(actual_image_column_index);
        }
        actualimagecursor.close();
        return fileName;
    }

    private void recycleBitmap(Bitmap bitmap){
        if(bitmap!=null&&!bitmap.isRecycled()){
            bitmap.recycle();
        }
    }

    private Uri getVideoImageUri(String videoPath,String imgPath){
        if(!TextUtils.isEmpty(videoPath)&&!TextUtils.isEmpty(imgPath)){
            Bitmap bitmap= ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);
            File file=new File(imgPath);
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    recycleBitmap(bitmap);
                    return null;
                }
            }
            try {
                FileOutputStream out=new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
                recycleBitmap(bitmap);
                return Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                recycleBitmap(bitmap);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                recycleBitmap(bitmap);
                return null;
            }
        }
        return null;
    }

//    private String getImgBase64Content(String path){
//        //String path=getRealPath(uri);
//        Log.e("VideoPath", path);
//        if(!TextUtils.isEmpty(path)){
//            Bitmap bitmap= ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
//            String string = null;
//            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
//            byte[] bytes = bStream.toByteArray();
//            string = Base64.encodeToString(bytes, Base64.DEFAULT);
//            bitmap.recycle();
//            bitmap=null;
//            return string;
//        }
//        return null;
//    }

//    private void sendVideoMessage(final VideoMessage videoMessage){
//        Conversation conversation=this.getCurrentConversation();
//        RongIM.getInstance().getRongIMClient()
//                .sendImageMessage(Message.obtain(conversation.getTargetId(), conversation.getConversationType(), videoMessage), null, null, new RongIMClient.SendImageMessageWithUploadListenerCallback() {
//                    @Override
//                    public void onAttached(Message message, RongIMClient.uploadImageStatusListener uploadImageStatusListener) {
//                        VideoMessage video = (VideoMessage) message.getContent();
//                        Log.e("ImgContent...", video.getBase64ImgContent() + "");
//                        Log.e("SendMsg", "上传文件中...");
//                        //uploadVideoFile(videoMessage, uploadImageStatusListener);
//                    }
//
//                    @Override
//                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {
//                        Log.e("SendMsg", "发送消息失败...");
//                        message.setSentStatus(Message.SentStatus.FAILED);
//                    }
//
//                    @Override
//                    public void onSuccess(Message message) {
//                        Log.e("SendMsg", "发送消息成功...");
//                    }
//
//                    @Override
//                    public void onProgress(Message message, int i) {
//                        //Log.e("SendMsg","进度"+i);
//                    }
//                });
//
//        //RongIM.getInstance().getRongIMClient().sendMessage();
//    }

    class UploadVideoRunnable implements Runnable{
        private Uri dataUri;
        private String imgPath;
        private String targetId;
        private Conversation.ConversationType type;

        public UploadVideoRunnable(String targetId,Conversation.ConversationType type,Uri uri,String imgPath){
            this.dataUri=uri;
            this.imgPath=imgPath;
            this.targetId=targetId;
            this.type=type;
        }

        @Override
        public void run() {
            if(dataUri!=null){
                String path=getRealPath(dataUri);
                Log.e("VideoPath",path);
                //imagePath= AppConfigInfo.getImagePath();
                Uri imageUri=getVideoImageUri(path,imgPath);
                if(imageUri!=null){
                    Log.e("VideoImagePath",imageUri.getPath());
                    MideaVideoMessage message= MideaVideoMessage.obtain(imageUri,imageUri);
                    sendVideoMessage(message,path);
                }
            }
        }

        private void sendVideoMessage(final MideaVideoMessage videoMessage,final String file){
            RongIM.getInstance().getRongIMClient()
                    .sendImageMessage(Message.obtain(this.targetId, type, videoMessage), null, null, new RongIMClient.SendImageMessageWithUploadListenerCallback() {
                        @Override
                        public void onAttached(Message message, RongIMClient.uploadImageStatusListener uploadImageStatusListener) {
                            MideaVideoMessage video = (MideaVideoMessage) message.getContent();
                            Log.e("ImgContent...", video.getBase64() + "");
                            Log.e("SendMsg", "上传文件中...");
                            uploadVideoFile(video, file, uploadImageStatusListener);
                        }

                        @Override
                        public void onError(Message message, RongIMClient.ErrorCode errorCode) {
                            Log.e("SendMsg", "发送消息失败...");
                            message.setSentStatus(Message.SentStatus.FAILED);
                        }

                        @Override
                        public void onSuccess(Message message) {
                            Log.e("SendMsg", "发送消息成功...");
                        }

                        @Override
                        public void onProgress(Message message, int i) {
                            //Log.e("SendMsg","进度"+i);
                        }
                    });

            //RongIM.getInstance().getRongIMClient().sendMessage();
        }


        private void uploadVideoFile(final MideaVideoMessage videoMessage,String localUrl,final RongIMClient.uploadImageStatusListener watcher){
            String url="http://192.168.1.176:8080/chat-server/servlet/fileupload";
            Map<String,String> params=new HashMap<String,String>(2);
            params.put("filepathdir", this.targetId);
            params.put("upload", localUrl);
            //Log.e("ImgContent...", videoMessage.getBase64ImgContent() + " ");
            //final String imgContent=videoMessage.getBase64ImgContent();
            HttpClientUtil.getHttpClientUtil().uploadFiles(url, "videoTag", null, params, new File[]{new File(localUrl)}, new HttpClientUtil.HttpResultCallback() {
                @Override
                public void loadingProgress(int progress) {
                    Log.e("uploadProgress",progress+"");
                    watcher.update(progress);
                }

                @Override
                public void onError(Request request, Exception error) {
                    Log.e("uploadProgress","上传文件失败");
                    if(error!=null){
                        Log.e("uploadError",error.getMessage()+"---"+error.getCause());
                    }
                    watcher.error();
                }

                @Override
                public void onSuccess(String response) {
                    System.out.println("response:"+response);
                    Log.e("ImgContent", videoMessage.getBase64() + " ");
                    if(!TextUtils.isEmpty(response)){
                        JSONObject jsonObject= JSON.parseObject(response);
                        if(jsonObject!=null&&!jsonObject.isEmpty()){
                            JSONObject obj=jsonObject.getJSONObject("map");
                            if(obj!=null&&!obj.isEmpty()){
                                String url=obj.getString("file-url");
                                if(!TextUtils.isEmpty(url)){
                                    videoMessage.setRemoteUri(Uri.parse(url));
                                    //videoMessage.setBase64ImgContent(imgContent);
                                    watcher.success();
                                    return;
                                }
                            }
                        }
                    }
                    watcher.error();
                }
            });
        }
    }

}
