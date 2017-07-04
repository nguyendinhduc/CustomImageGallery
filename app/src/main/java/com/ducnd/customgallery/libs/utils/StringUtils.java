package com.ducnd.customgallery.libs.utils;

/**
 * Created by ducnd on 7/5/17.
 */

public class StringUtils {

    public static boolean isEmpty(String content) {
        if ( content == null || "".equals(content)) {
            return true;
        }
        return false;
    }
}
