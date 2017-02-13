package com.hm.camerademo.util.localImages;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchao on 16/12/12.
 * cc@cchao.org
 */
public class LocalImagesUri {

    private static ImagesHelper mImagesHelper;
    private static List<ImageBucket> mImageBucket = new ArrayList<ImageBucket>();
    private static List<ImageItem> mImageItem = new ArrayList<ImageItem>();

    public static List<ImageItem> getLocalImagesUri(Context context) {
        mImagesHelper = ImagesHelper.getHelper();
        mImagesHelper.init(context);
        mImageBucket = mImagesHelper.getImagesBucketList(false);
        if (mImageBucket.isEmpty()) {
            mImageBucket.clear();
        }
        if (mImageItem.isEmpty()) {
            mImageItem.clear();
        }
        for (ImageBucket imageBucket : mImageBucket) {
            mImageItem.addAll(imageBucket.imageList);
        }
        return mImageItem;
    }
}