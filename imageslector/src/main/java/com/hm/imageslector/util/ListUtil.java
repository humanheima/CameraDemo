package com.hm.imageslector.util;

import java.util.List;
public class ListUtil {

    private ListUtil() {}

    public static boolean isEmpty(List<?> data) {
        if (data == null || data.isEmpty()) {
            return true;
        }
        return false;
    }
}