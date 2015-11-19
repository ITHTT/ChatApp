package com.tysci.chatapp.provider;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sea_monster.resource.Resource;
import com.sprylab.android.widget.TextureVideoView;
import com.squareup.okhttp.Request;
import com.tysci.applibrary.networks.HttpClientUtil;
import com.tysci.chatapp.activitys.R;
import com.tysci.chatapp.app.AppConfigInfo;
import com.tysci.chatapp.messages.MideaVideoMessage;
import com.tysci.chatapp.messages.VideoMessage;
import com.tysci.chatapp.utils.MD5;

import io.rong.imkit.RongContext;
import io.rong.imkit.model.ProviderTag;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.widget.AsyncImageView;
import io.rong.imkit.widget.provider.IContainerItemProvider;
import io.rong.imlib.model.Message;

/**
 * Created by Administrator on 2015/11/18.
 */
@ProviderTag(messageContent = MideaVideoMessage.class,showPortrait = true, centerInHorizontal = false, showProgress = false)
public class MideaVideoMessageItemProvider extends IContainerItemProvider.MessageProvider<MideaVideoMessage>{

    @Override
    public void bindView(View view, int i, MideaVideoMessage mideaVideoMessage, UIMessage uiMessage) {
        ViewHolder viewHolder=(ViewHolder)view.getTag();

        if(viewHolder!=null&&mideaVideoMessage!=null){
            if(mideaVideoMessage.getThumUri()!=null){
                viewHolder.videoImageView.setResource(new Resource(mideaVideoMessage.getThumUri()));
            }

            if(viewHolder.videoImageView.getVisibility()!=View.VISIBLE&&!viewHolder.videoView.isPlaying()){
                viewHolder.videoImageView.setVisibility(View.VISIBLE);
            }

            Uri remoteUri=mideaVideoMessage.getRemoteUri();
            if(remoteUri!=null){
                Log.e("RemoteUri", remoteUri.getPath());
            }

            int progress = uiMessage.getProgress();
            Message.SentStatus status = uiMessage.getSentStatus();
            if(status.equals(Message.SentStatus.SENDING) && progress < 100) {
                if(progress == 0) {
                    viewHolder.tvUploadProgress.setText("请稍等");
                } else{
                    viewHolder.tvUploadProgress.setText(progress + "%");
                }
                viewHolder.tvUploadProgress.setVisibility(View.VISIBLE);
            } else {
                Boolean isLoad= (Boolean) viewHolder.tvUploadProgress.getTag();
                if(isLoad!=null&&isLoad){
                    viewHolder.tvUploadProgress.setVisibility(View.VISIBLE);
                }else {
                    viewHolder.tvUploadProgress.setVisibility(View.GONE);
                }
            }

            if(status.equals(Message.SentStatus.FAILED)){
                viewHolder.tvUploadProgress.setText("发送失败");
                viewHolder.tvUploadProgress.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public Spannable getContentSummary(MideaVideoMessage mideaVideoMessage) {
        return new SpannableString(RongContext.getInstance().getResources().getString(R.string.rc_message_content_video));
    }

    @Override
    public void onItemClick(View view, int i, MideaVideoMessage mideaVideoMessage, UIMessage uiMessage) {
        final ViewHolder holderView= (ViewHolder) view.getTag();
        Boolean isLoad= (Boolean) holderView.tvUploadProgress.getTag();
        if((isLoad==null||!isLoad)&&!holderView.videoView.isPlaying()){
            Uri remoteUri=mideaVideoMessage.getRemoteUri();
            if(remoteUri!=null){
                String url="http://"+remoteUri.getHost()+":"+remoteUri.getPort()+remoteUri.getPath();
                Log.e("RemoteUrl",url);
                if(!TextUtils.isEmpty(url)&&url.contains("http://")){
                    String videoName= MD5.GetMD5Code(url);
                    if(AppConfigInfo.isExistFile(videoName+".mp4")){
                        holderView.videoView.setVideoPath(AppConfigInfo.getVideoPath(videoName));
                        holderView.videoView.start();
                        holderView.tvUploadProgress.setVisibility(View.GONE);
                        holderView.videoImageView.setVisibility(View.GONE);
                    }else{
                        holderView.tvUploadProgress.setVisibility(View.VISIBLE);
                        holderView.tvUploadProgress.setTag(true);
                        holderView.videoImageView.setVisibility(View.VISIBLE);
                        holderView.tvUploadProgress.setText("加载中...");
                        HttpClientUtil.getHttpClientUtil().downloadFiles(url, "downVideo", null, null, AppConfigInfo.getVideoPath(videoName), new HttpClientUtil.HttpResultCallback() {
                            @Override
                            public void loadingProgress(int progress) {
                                holderView.tvUploadProgress.setText(progress+"%");
                            }

                            @Override
                            public void onError(Request request, Exception error) {
                                holderView.tvUploadProgress.setText("加载视频失败...");
                                holderView.tvUploadProgress.setTag(false);
                            }

                            @Override
                            public void onSuccess(String response) {
                                holderView.tvUploadProgress.setTag(false);
                                if(!TextUtils.isEmpty(response)){
                                    holderView.videoView.setVideoPath(response);
                                    holderView.videoView.start();
                                    holderView.tvUploadProgress.setVisibility(View.GONE);
                                    holderView.videoImageView.setVisibility(View.GONE);
                                }else{
                                    holderView.tvUploadProgress.setText("加载视频失败...");
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void onItemLongClick(View view, int i, MideaVideoMessage mideaVideoMessage, UIMessage uiMessage) {

    }

    @Override
    public View newView(Context context, ViewGroup viewGroup) {
        View view= LayoutInflater.from(context).inflate(R.layout.layout_video_item,viewGroup,false);
        ViewHolder holderView=new ViewHolder();
        holderView.videoView=(TextureVideoView)view.findViewById(R.id.video_view);
        holderView.videoImageView=(AsyncImageView)view.findViewById(R.id.video_img);
        holderView.tvUploadProgress=(TextView)view.findViewById(R.id.tv_send_progress);
        view.setTag(holderView);
        return view;
    }

    public static final class ViewHolder{
        TextureVideoView videoView;
        AsyncImageView videoImageView;
        TextView tvUploadProgress;
    }
}
