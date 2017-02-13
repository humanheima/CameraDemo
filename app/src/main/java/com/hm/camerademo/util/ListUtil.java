package com.hm.camerademo.util;

import java.util.List;

/**
 * Created by shucc on 17/1/18.
 * cc@cchao.org
 */
public class ListUtil {

    private ListUtil() {}

    public static boolean isEmpty(List<?> data) {
        if (data == null || data.size() == 0) {
            return true;
        }
        return false;
    }
}