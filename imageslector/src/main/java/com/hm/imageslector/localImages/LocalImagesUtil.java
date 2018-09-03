package com.hm.imageslector.localImages;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalImagesUtil {

    private final String TAG = getClass().getSimpleName();

    private static LocalImagesUtil instance;
    private static ContentResolver cr;
    private Map<String, ImageBucket> bucketMap = new HashMap<>();

    private LocalImagesUtil() {
    }

    public static LocalImagesUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (LocalImagesUtil.class) {
                instance = new LocalImagesUtil();
                cr = context.getApplicationContext().getContentResolver();
            }
        }
        return instance;
    }

    /**
     * 获取所有图片
     *
     * @return
     */
    public ImageBucket getLocalImages() {
        List<ImageItem> imageItems = new ArrayList<>();
        String columns[] = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
        Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC ");
        if (null != cur) {
            while (cur.moveToNext()) {
                String _id = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                String path = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                ImageItem imageItem = new ImageItem();
                imageItem.setId(_id);
                imageItem.setImagePath(path);
                imageItems.add(imageItem);
            }
            cur.close();
        }
        ImageBucket bucket = new ImageBucket();
        bucket.setCount(imageItems.size());
        bucket.setBucketName("所有图片");
        bucket.setImageList(imageItems);
        return bucket;
    }

    /**
     * 获取所有的包含图片的文件夹
     *
     * @return
     */
    public List<ImageBucket> getBucketList() {
        bucketMap.clear();
        String columns[] = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
                MediaStore.Images.Media.DATE_ADDED + " DESC");

        if (null != cur) {
            Log.e(TAG, "buildImagesBucketList: totalNum is" + cur.getCount());
            while (cur.moveToNext()) {
                String _id = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                String path = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                long time = cur.getLong(cur.getColumnIndex(MediaStore.Images.Media.DATE_ADDED));
                String bucketId = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
                String bucketName = cur.getString(cur.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                ImageBucket bucket = bucketMap.get(bucketId);
                if (bucket == null) {
                    bucket = new ImageBucket();
                    bucket.setSelected(false);
                    bucketMap.put(bucketId, bucket);
                    bucket.setImageList(new ArrayList<ImageItem>());
                    bucket.setBucketName(bucketName);
                }
                ImageItem imageItem = new ImageItem();
                imageItem.setId(_id);
                imageItem.setTime(time);
                imageItem.setImagePath(path);
                bucket.getImageList().add(imageItem);
                bucket.setCount(bucket.getImageList().size());
            }
            cur.close();
            List<ImageBucket> bucketList = new ArrayList<>();
            //所有图片
            ImageBucket allBucket = new ImageBucket();
            allBucket.setSelected(true);
            allBucket.setBucketName("所有图片");
            int allCount;
            List<ImageItem> allList = new ArrayList<>();
            for (Map.Entry<String, ImageBucket> entry : bucketMap.entrySet()) {
                ImageBucket bucket = entry.getValue();
                Collections.sort(bucket.getImageList());
                allList.addAll(bucket.getImageList());
                bucketList.add(bucket);
            }
            allCount = allList.size();
            allBucket.setCount(allCount);
            Collections.sort(allList);
            Collections.reverse(allList);
            allBucket.setImageList(allList);
            bucketList.add(0, allBucket);
            return bucketList;
        } else {
            Log.e(TAG, "buildImagesBucketList: not any image");
            return null;
        }
    }
}