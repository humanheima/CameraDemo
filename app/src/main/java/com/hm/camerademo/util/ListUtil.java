package com.hm.camerademo.util;

import java.util.List;
public class ListUtil {

    private ListUtil() {}

    public static boolean isEmpty(List<?> data) {
        if (data == null || data.size() == 0) {
            return true;
        }
        return false;
    }
}