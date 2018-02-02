package com.hm.camerademo.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;

import com.hm.camerademo.App;

import java.lang.reflect.Field;

/**
 * Created by chenchao on 16/9/29.
 * cc@cchao.org
 */
public class ScreenUtil {

    public static class Data {
        public int px;
        public float dp;

        public Data(int px, float density) {
            this.px = px;
            this.dp = px / density;
        }
    }

    /**
     * 获取屏幕
     */
    public static Display getDisplay(Context context) {
        Display d = ((Activity) context).getWindowManager().getDefaultDisplay();
        return d;
    }

    public static int dipToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int spTopx(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 屏幕宽度
     */
    public static Data width(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        getDisplay(context).getMetrics(dm);
        return new Data(dm.widthPixels, dm.density);
    }

    /**
     * 屏幕高度
     */
    public static Data height(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        getDisplay(context).getMetrics(dm);
        return new Data(dm.heightPixels, dm.density);
    }

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            return App.getInstance().getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
            return 75;
        }
    }
}
