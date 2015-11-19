package com.tysci.chatapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/11/19.
 */
public class DateUtils {

    public static String getSpecialFormatDate(long times,String format){
        Date date=new Date();
        date.setTime(times);
        SimpleDateFormat dateFormat=new SimpleDateFormat(format);
        return dateFormat.format(date);
    }
}
