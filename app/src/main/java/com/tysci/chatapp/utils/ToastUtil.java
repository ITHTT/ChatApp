package com.tysci.chatapp.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2015/11/11.
 */
public class ToastUtil {
    public static void toastMsg(Context context,String msg){
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
