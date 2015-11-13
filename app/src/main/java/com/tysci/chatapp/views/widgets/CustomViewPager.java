package com.tysci.chatapp.views.widgets;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2015/11/13.
 */
public class CustomViewPager extends ViewPager{
    private boolean noScroll=false;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        /* return false;//super.onTouchEvent(arg0); */
        if(noScroll){
            return false;
        }
        if(getCurrentItem()==0&&arg0.getAction()==MotionEvent.ACTION_UP){
            noScroll=true;
        }
       return super.onTouchEvent(arg0);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if(noScroll){
            return false;
        }
       return super.onInterceptTouchEvent(arg0);
    }

    public void isNoScroll(boolean isScroll){
        this.noScroll=isScroll;
    }

}
