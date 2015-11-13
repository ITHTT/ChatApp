package com.tysci.applibrary.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * 页面跳转工具
 * Created by Administrator on 2015/10/8.
 */
public class ActivitySkipUtils {
    public static void skipActivity(Activity activity, Class<?> cla) {
        // TODO Auto-generated method stub
        Intent intent=new Intent();
        intent.setClass(activity, cla);
        activity.startActivity(intent);
    }

    public static void skipActivity(Fragment fragment,Class<?>cla){
        Intent intent=new Intent();
        intent.setClass(fragment.getActivity(), cla);
        fragment.startActivity(intent);
    }

    public static void skipActivity(Activity activity, Class<?> cla, Bundle data) {
        // TODO Auto-generated method stub
        Intent intent=new Intent();
        intent.setClass(activity, cla);
        intent.putExtras(data);
        activity.startActivity(intent);
    }

    public static void skipActivity(Fragment fragment,Class<?>cla,Bundle data){
        Intent intent=new Intent();
        intent.setClass(fragment.getActivity(), cla);
        intent.putExtras(data);
        fragment.startActivity(intent);
    }

    public static void skipActivityForResult(Activity activity, Class<?> cla,
                                             int requestCode, Bundle data) {
        // TODO Auto-generated method stub
        Intent intent=new Intent();
        intent.setClass(activity,cla);
        if(data!=null)
            intent.putExtras(data);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void skipActivityForResult(Fragment fragment,Class<?>cla,int requestCode,Bundle data){
        Intent intent=new Intent();
        intent.setClass(fragment.getActivity(),cla);
        if(data!=null)
            intent.putExtras(data);
        fragment.startActivityForResult(intent, requestCode);
    }

}
