package com.tysci.chatapp.provider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sea_monster.resource.Resource;
import com.sprylab.android.widget.TextureVideoView;
import com.tysci.chatapp.activitys.R;
import com.tysci.chatapp.messages.VideoMessage;

import java.io.IOException;

import io.rong.imkit.RongContext;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;

/**
 * Created by Administrator on 2015/11/13.
 */
@ProviderTag(messageContent = VideoMessage.class,showPortrait = true, centerInHorizontal = false, showProgress = false)
public class VideoMessageItemProvider extends IContainerItemProvider.MessageProvider<VideoMessage>{
    private Context context;

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        this.context=context;
        View view= LayoutInflater.from(context).inflate(R.layout.layout_video_item,viewGroup,false);
        HolderView holderView=new HolderView();
        holderView.videoView=(TextureVideoView)view.findViewById(R.id.video_view);
        holderView.videoImageView=(AsyncImageView)view.findViewById(R.id.video_img);
        holderView.tvUploadProgress=(TextView)view.findViewById(R.id.tv_send_progress);
        view.setTag(holderView);
        return view;
    }

    @Override
    public void bindView(View view, int i, VideoMessage videoMessage, UIMessage uiMessage) {
        HolderView holderView=(HolderView)view.getTag();

        if(holderView!=null&&videoMessage!=null){
            String imgContent=videoMessage.getBase64ImgContent();
            //Log.e("ImgContent", imgContent+" ");
           // Log.e("LocalUri",videoMessage.getLocalVideoUri()+"");
            if(!TextUtils.isEmpty(imgContent)){
                Bitmap bitmap=stringtoBitmap(imgContent);
                if(bitmap!=null){
                    holderView.videoImageView.setImageBitmap(bitmap);
                }
            }else if(videoMessage.getLocalVideoUri()!=null){
                holderView.videoImageView.setResource(new Resource(videoMessage.getLocalVideoUri()));
            }

//            if(videoMessage.getRemoteVideoUri()!=null){
//                holderView.videoView.setVideoURI(videoMessage.getRemoteVideoUri());
//                holderView.videoView.start();
//            }

            int progress = uiMessage.getProgress();
            Message.SentStatus status = uiMessage.getSentStatus();
            if(status.equals(Message.SentStatus.SENDING) && progress < 100) {
                if(progress == 0) {
                    holderView.tvUploadProgress.setText("请稍等");
                } else {
                    holderView.tvUploadProgress.setText(progress + "%");
                }
                holderView.tvUploadProgress.setVisibility(View.VISIBLE);
            } else {
                holderView.tvUploadProgress.setVisibility(View.GONE);
            }

            if(status.equals(Message.SentStatus.FAILED)){
                holderView.tvUploadProgress.setText("发送失败");
                holderView.tvUploadProgress.setVisibility(View.VISIBLE);
            }
        }
    }

    public Bitmap stringtoBitmap(String string) {
// 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public Spannable getContentSummary(VideoMessage videoMessage) {
        return null;
    }

    @Override
    public void onItemClick(View view, int i, VideoMessage videoMessage, UIMessage uiMessage) {
        HolderView holderView= (HolderView) view.getTag();
        if(videoMessage.getRemoteVideoUri()!=null){
            holderView.videoImageView.setVisibility(View.GONE);
            holderView.videoView.setVideoURI(videoMessage.getRemoteVideoUri());
            holderView.videoView.start();
        }
    }

    @Override
    public void onItemLongClick(View view, int i, VideoMessage videoMessage, UIMessage uiMessage) {

    }

    public static class HolderView{
        TextureVideoView videoView;
        AsyncImageView videoImageView;
        TextView tvUploadProgress;
    }
}
