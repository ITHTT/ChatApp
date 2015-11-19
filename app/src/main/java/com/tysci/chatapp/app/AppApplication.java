package com.tysci.chatapp.app;

import android.app.ActivityManager;
import android.content.Context;

import com.tysci.applibrary.app.BaseApplication;
import com.tysci.applibrary.networks.HttpClientUtil;
import com.tysci.chatapp.messages.CustomReceiveMessageListener;
import com.tysci.chatapp.messages.MideaVideoMessage;
import com.tysci.chatapp.messages.VideoMessage;
import com.tysci.chatapp.provider.MideaVideoMessageItemProvider;
import com.tysci.chatapp.provider.VideoMessageItemProvider;
import com.tysci.chatapp.utils.RongYunUtils;

import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.ImageMessageItemProvider;
import io.rong.imlib.ipc.RongExceptionHandler;
import io.rong.message.ImageMessage;

/**
 * Created by Administrator on 2015/11/11.
 */
public class AppApplication extends BaseApplication{

    @Override
    public void onCreate() {
        super.onCreate();
        //AppExceptionHandler appExceptionHandler=AppExceptionHandler.getInstance();
        //appExceptionHandler.init(this);
        AppConfigInfo.initAppConfigInfo(this);
        HttpClientUtil.initHttpClient(this,AppConfigInfo.APP_HTTP_CACHE_PATH);
        initIMKit();
    }

    /**
     * 注意：
     *
     * IMKit SDK调用第一步 初始化
     *
     * context上下文
     *
     * 只有两个进程需要初始化，主进程和 push 进程
     */
    private void initIMKit(){
        if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext())) ||
                "io.rong.push".equals(getCurProcessName(getApplicationContext()))) {

            RongIM.init(this);

            /**
             * 融云SDK事件监听处理
             *
             * 注册相关代码，只需要在主进程里做。
             */
            if (getApplicationInfo().packageName.equals(getCurProcessName(getApplicationContext()))) {

                //Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));
                try {
                    RongYunUtils.setOnReceiveMessageListener(new CustomReceiveMessageListener());
                    RongIM.registerMessageType(VideoMessage.class);
                    RongIM.registerMessageTemplate(new VideoMessageItemProvider());
                    RongIM.registerMessageType(MideaVideoMessage.class);
                    RongIM.registerMessageTemplate(new MideaVideoMessageItemProvider());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getCurProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }
}
