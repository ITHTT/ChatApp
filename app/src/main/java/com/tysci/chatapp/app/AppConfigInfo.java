package com.tysci.chatapp.app;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Administrator on 2015/11/11.
 */
public class AppConfigInfo {
    public static final String APP_DIR_NAME="ChatApp";
    public static String APP_PATH=null;
    public static final String APP_HTTP_CACHE_DIR_NAME="httpcache";
    public static String APP_HTTP_CACHE_PATH=null;
    public static final String APP_DATABASE_DIR_NAME="database";
    public static final String APP_DATABASE_NAME="chat.db";
    public static String APP_DATABASE_PATH=null;
    public static final String APP_LOG_DIR_NAME="log";
    public static String APP_LOG_PATH=null;
    public static final String APP_IMAGE_DIR_NAME="image";
    public static String APP_IMAGE_PATH=null;

    public static final String APP_VIDEO_DIR_NAME="videos";
    public static String APP_VIDEO_PATH=null;

    public static String COOKIE_KEY="cookie_key";
    public static String TOKEN_KEY="token_key";
    public static String USER_ACCOUNT="user_account";
    public static String USER_PASSWORD="user_password";

    public static int REQUEST_CODE_USER_REGISTER=0x0001;

    public static void initAppConfigInfo(Context context){
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        File dir=null;
        if(sdCardExist){
            dir=new File(Environment.getExternalStorageDirectory()+ File.separator+APP_DIR_NAME);
        }else{
            dir=new File(context.getFilesDir()+File.separator+APP_DIR_NAME);
        }
        if(!dir.exists()){
            dir.mkdirs();
        }
        APP_PATH=dir.getAbsolutePath();

        File DBDir=new File(APP_PATH+File.separator+APP_DATABASE_DIR_NAME);
        if(!DBDir.exists()){
            DBDir.mkdirs();
        }
        APP_DATABASE_PATH=DBDir.getAbsolutePath();

        File logDir=new File(APP_PATH+File.separator+APP_LOG_DIR_NAME);
        if(!logDir.exists()){
            logDir.mkdirs();
        }
        APP_LOG_PATH=logDir.getAbsolutePath();

        File httpCacheDir=new File(APP_PATH+File.separator+APP_HTTP_CACHE_DIR_NAME);
        if(!httpCacheDir.exists()){
            httpCacheDir.mkdirs();
        }
        APP_HTTP_CACHE_PATH=httpCacheDir.getAbsolutePath();

        File imageDir=new File(APP_PATH+File.separator+APP_IMAGE_DIR_NAME);
        if(!imageDir.exists()){
            imageDir.mkdirs();
        }
        APP_IMAGE_PATH=imageDir.getAbsolutePath();

        File videoDir=new File(APP_PATH+File.separator+APP_VIDEO_DIR_NAME);
        if(!videoDir.exists()){
            videoDir.mkdirs();
        }
        APP_VIDEO_PATH=videoDir.getAbsolutePath();
    }

    public static String getAppException(){
        return APP_LOG_PATH+File.separator+System.currentTimeMillis()+".log";
    }

    public static String getImagePath(){
        return APP_IMAGE_PATH+File.separator+System.currentTimeMillis()+".jpg";
    }

    public static String getVideoPath(String name){
        return APP_VIDEO_PATH+File.separator+name+".mp4";
    }

    public static boolean isExistFile(String fileName){
        File fileDir=new File(APP_VIDEO_PATH);
        if(fileDir.exists()){
            String[] fileNames=fileDir.list();
            for(int i=0;i<fileNames.length;i++){
                if(fileNames[i].equals(fileName)){
                    return true;
                }
            }
        }
        return false;
    }

}
