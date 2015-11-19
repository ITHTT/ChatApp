package com.tysci.chatapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.tysci.applibrary.base.BaseFragment;
import com.tysci.chatapp.activitys.MainActivity;
import com.tysci.chatapp.activitys.R;

import java.util.Locale;

import butterknife.Bind;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.model.Conversation;

/**
 * Created by Administrator on 2015/11/11.
 */
public class GameChatRoomFragment extends BaseFragment{
    @Bind(R.id.layout_top)
    protected RelativeLayout layoutTop;

    private String targetId;
    private String targetIds;
    private Conversation.ConversationType mConversationType;
    private ConversationFragment conversationFragment=null;


    @Override
    protected View inflaterContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat_room,container,false);
    }

    @Override
    protected void initViews(View view) {

       Intent intent= this.getActivity().getIntent();
        if(intent!=null){
            targetId = intent.getData().getQueryParameter("targetId");
            targetIds = intent.getData().getQueryParameter("targetIds");
            mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

            //intent.getData().getLastPathSegment();//获得当前会话类型
//            conversationFragment=(ConversationFragment)this.getActivity().getSupportFragmentManager().findFragmentById(R.id.conversation);
//            Uri uri = Uri.parse("rong://" + this.getActivity().getApplicationInfo().packageName).buildUpon()
//                    .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
//                    .appendQueryParameter("targetId", targetId).build();
//            conversationFragment.setUri(uri);

            ConversationFragment fragment = new ConversationFragment();
            Uri uri = Uri.parse("rong://" + this.getActivity().getApplicationInfo().packageName).buildUpon()
                    .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                    .appendQueryParameter("targetId", targetId).build();
            fragment.setUri(uri);

            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            //xxx 为你要加载的 id
            transaction.add(R.id.layout_chat, fragment);
            transaction.commit();

            layoutTop.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch(event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                        //case MotionEvent.ACTION_MOVE:
                            Log.e("MotionEvent:","触摸屏幕...");
                            ((MainActivity)getActivity()).setViewPagerNoScroll(false);
                            break;
                        case MotionEvent.ACTION_UP:
                            Log.e("MotionEvent:","离开屏幕...");
                            ((MainActivity)getActivity()).setViewPagerNoScroll(true);
                            break;
                    }
                    return false;
                }
            });
        }
    }
}
