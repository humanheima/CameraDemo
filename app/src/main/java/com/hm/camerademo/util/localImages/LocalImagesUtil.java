package com.hm.camerademo.util.localImages;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LocalImagesUtil {

    private final String TAG = getClass().getSimpleName();

    private static LocalImagesUtil instance;
    private static ContentResolver cr;
    private Map<String, String> thumbnailList = new HashMap<>();
    private List<HashMap<String, String>> albumList = new ArrayList<>();
    private Map<String, ImageBucket> bucketList = new HashMap<>();

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

    public List<ImageItem> getLocalImagesUri() {
        List<ImageItem> mImageItem = new ArrayList<>();
        List<ImageBucket> mImageBucket = getImagesBucketList();
        for (ImageBucket imageBucket : mImageBucket) {
            mImageItem.addAll(imageBucket.imageList);
        }
        return mImageItem;
    }

    private List<ImageBucket> getImagesBucketList() {
        bucketList.clear();
        buildImagesBucketList();
        List<ImageBucket> tmpList = new ArrayList<>();
        Iterator<Map.Entry<String, ImageBucket>> itr = bucketList.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, ImageBucket> entry = itr.next();
            tmpList.add(entry.getValue());
        }
        return tmpList;
    }

    private void buildImagesBucketList() {
        getThumbnail();
        String columns[] = new String[]{MediaStore.Images.Media._ID, MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.PICASA_ID, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE, MediaStore.Images.Media.BUCKET_DISPLAY_NAME};
        Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null,
                null);
        if (cur.moveToFirst()) {
            int photoIDIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int photoPathIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int photoNameIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            int photoTitleIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.TITLE);
            int photoSizeIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
            int bucketDisplayNameIndex = cur
                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);
            int picasaIdIndex = cur.getColumnIndexOrThrow(MediaStore.Images.Media.PICASA_ID);
            int totalNum = cur.getCount();

            do {
                String _id = cur.getString(photoIDIndex);
                String name = cur.getString(photoNameIndex);
                String path = cur.getString(photoPathIndex);
                String title = cur.getString(photoTitleIndex);
                String size = cur.getString(photoSizeIndex);
                String bucketName = cur.getString(bucketDisplayNameIndex);
                String bucketId = cur.getString(bucketIdIndex);
                String picasaId = cur.getString(picasaIdIndex);

                ImageBucket bucket = bucketList.get(bucketId);
                if (bucket == null) {
                    Log.e("buildImagesBucketList", "buck==" + bucket);
                    bucket = new ImageBucket();
                    bucketList.put(bucketId, bucket);
                    bucket.imageList = new ArrayList<>();
                    bucket.bucketName = bucketName;
                }
                bucket.count++;
                ImageItem imageItem = new ImageItem();
                imageItem.setId(_id);
                imageItem.setImagePath(path);
                imageItem.setThumbnailPath(thumbnailList.get(_id));
                bucket.imageList.add(imageItem);

            } while (cur.moveToNext());
        }
    }

    private void getThumbnail() {
        String[] projection = {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA};
        Cursor cursor = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection,
                null, null, null);
        getThumbnailColumnData(cursor);
    }

    private void getThumbnailColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int _id;
            int image_id;
            String image_path;
            int _idColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails._ID);
            int image_idColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
            int dataColumn = cur.getColumnIndex(MediaStore.Images.Thumbnails.DATA);

            do {
                // Get the field values
                _id = cur.getInt(_idColumn);
                image_id = cur.getInt(image_idColumn);
                image_path = cur.getString(dataColumn);

                thumbnailList.put("" + image_id, image_path);
            } while (cur.moveToNext());
        }
    }

    private void getAlbum() {
        String[] projection = {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM, MediaStore.Audio.Albums.ALBUM_ART,
                MediaStore.Audio.Albums.ALBUM_KEY, MediaStore.Audio.Albums.ARTIST, MediaStore.Audio.Albums.NUMBER_OF_SONGS};
        Cursor cursor = cr.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, projection, null,
                null, null);
        getAlbumColumnData(cursor);

    }

    private void getAlbumColumnData(Cursor cur) {
        if (cur.moveToFirst()) {
            int _id;
            String album;
            String albumArt;
            String albumKey;
            String artist;
            int numOfSongs;

            int _idColumn = cur.getColumnIndex(MediaStore.Audio.Albums._ID);
            int albumColumn = cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int albumArtColumn = cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int albumKeyColumn = cur.getColumnIndex(MediaStore.Audio.Albums.ALBUM_KEY);
            int artistColumn = cur.getColumnIndex(MediaStore.Audio.Albums.ARTIST);
            int numOfSongsColumn = cur.getColumnIndex(MediaStore.Audio.Albums.NUMBER_OF_SONGS);

            do {
                // Get the field values
                _id = cur.getInt(_idColumn);
                album = cur.getString(albumColumn);
                albumArt = cur.getString(albumArtColumn);
                albumKey = cur.getString(albumKeyColumn);
                artist = cur.getString(artistColumn);
                numOfSongs = cur.getInt(numOfSongsColumn);

                HashMap<String, String> hash = new HashMap<>();
                hash.put("_id", _id + "");
                hash.put("album", album);
                hash.put("albumArt", albumArt);
                hash.put("albumKey", albumKey);
                hash.put("artist", artist);
                hash.put("numOfSongs", numOfSongs + "");
                albumList.add(hash);

            } while (cur.moveToNext());

        }
    }
}