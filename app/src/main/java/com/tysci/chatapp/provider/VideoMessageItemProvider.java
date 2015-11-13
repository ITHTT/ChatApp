package com.tysci.chatapp.provider;

import android.content.Context;
import android.os.Parcelable;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tysci.chatapp.activitys.R;
import com.tysci.chatapp.messages.VideoMessage;
import com.yqritc.scalablevideoview.ScalableVideoView;

import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.MessageContent;

/**
 * Created by Administrator on 2015/11/13.
 */
@ProviderTag(messageContent = VideoMessage.class,showPortrait = false, centerInHorizontal = true, showProgress = true)
public class VideoMessageItemProvider extends IContainerItemProvider.MessageProvider<VideoMessage>{

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_video_item,null);
        HolderView holderView=new HolderView();
        holderView.videoView=(ScalableVideoView)view.findViewById(R.id.video_view);
        view.setTag(holderView);
        return view;
    }

    @Override
    public void bindView(View view, int i, VideoMessage videoMessage, UIMessage uiMessage) {
        HolderView holderView=(HolderView)view.getTag();
        if(holderView!=null){

        }
    }

    @Override
    public Spannable getContentSummary(VideoMessage videoMessage) {
        return null;
    }

    @Override
    public void onItemClick(View view, int i, VideoMessage videoMessage, UIMessage uiMessage) {

    }

    @Override
    public void onItemLongClick(View view, int i, VideoMessage videoMessage, UIMessage uiMessage) {

    }

    public static class HolderView{
        ScalableVideoView videoView;
    }
}
