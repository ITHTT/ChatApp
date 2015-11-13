package com.tysci.chatapp.app;

import android.app.ActivityManager;
import android.content.Context;

import com.tysci.applibrary.app.BaseApplication;
import com.tysci.applibrary.networks.HttpClientUtil;

import io.rong.imkit.RongIM;
import io.rong.imlib.ipc.RongExceptionHandler;
import io.rong.message.ImageMessage;

/**
 * Created by Administrator on 2015/11/11.
 */
public class AppApplication extends BaseApplication{

    @Override
    public void onCreate() {
        super.onCreate();
        AppExceptionHandler appExceptionHandler=AppExceptionHandler.getInstance();
        appExceptionHandler.init(this);
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

                //DemoContext.init(this);

                Thread.setDefaultUncaughtExceptionHandler(new RongExceptionHandler(this));
                try {
                    //RongIM.registerMessageType(DeAgreedFriendRequestMessage.class);
                    //RongIM.registerMessageTemplate(new ContactNotificationMessageProvider());
                    //RongIM.registerMessageTemplate(new RealTimeLocationMessageProvider());
                    RongIM.registerMessageType(ImageMessage.class);
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
