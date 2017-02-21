package com.hm.camerademo.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.hm.camerademo.App;

/**
 * Created by dumingwei on 2017/2/21.
 */
public class SpUtil {

    private static SharedPreferences hmSpref;
    private static SharedPreferences.Editor editor;
    private static SpUtil spUtil;
    private final String FLAG = "flag";

    private SpUtil() {
        hmSpref = App.getInstance().getSharedPreferences("hmSpref", Context.MODE_PRIVATE);
        editor = hmSpref.edit();
    }

    public static SpUtil getInstance() {
        if (spUtil == null) {
            synchronized (SpUtil.class) {
                if (spUtil == null) {
                    spUtil = new SpUtil();
                }
            }
        }
        return spUtil;
    }

    public void putFlag(boolean flag) {
        editor.putBoolean(FLAG, flag);
        editor.commit();
    }

    public boolean getFlag() {
        return hmSpref.getBoolean(FLAG, false);
    }
}
