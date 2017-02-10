package com.hm.camerademo.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hm.camerademo.App;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2017/1/5.
 * 质量压缩不能用bitmap的形式进行压缩
 * 质量压缩不改变图片占用内存的大小
 * 拍照后，或者从本地选取图片后，应该使用采样压缩
 * 当要把一个ImageView 上的图片 存储到本地的时候，先使用质量压缩，再使用采样压缩。
 */
public class ImageUtil {

    public ImageUtil() {
    }

    private static DisplayMetrics displayMetrics = App.getInstance().getResources().getDisplayMetrics();

    /**
     * 创建图片File对象
     *
     * @return
     */
    public static File createImageFile() {
        File imageFile = null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
                path.mkdirs();
                imageFile = File.createTempFile(timeStamp, ".jpg", path);
            } else {
                imageFile = File.createTempFile(timeStamp, ".jpg", App.getInstance().getExternalFilesDir(null));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }


    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap getBitmapFromPath(String imgPath) {
        // First decode with inJustDecodeBounds=true to check dimensions
        int reqWidth = 480;
        int reqHeight = 800;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        //return compressImage(BitmapFactory.decodeFile(imgPath, options));
        return BitmapFactory.decodeFile(imgPath, options);
    }

    public static Observable<String> observableSaveImageToExternal(final Context context, final Bitmap cropBitmap) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(saveImageToExternal(context, cropBitmap));
                subscriber.onCompleted();
            }
        });
    }

    /**
     * 保存图片到本地
     *
     * @param bm
     * @return
     */
    private static String saveImageToExternal(Context context, Bitmap bm) {
        try {
            File imageFile = createImageFile();
            FileOutputStream out = new FileOutputStream(imageFile);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            //TODO 还不能马上刷新
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(imageFile);
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);
            } else {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + imageFile.getAbsoluteFile())));
            }
            if (imageFile.exists()) {
                return imageFile.getAbsolutePath();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // TODO: 2017/2/8 这个方法有问题

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 200) {  //循环判断如果压缩后图片是否大于200kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromFileDescriptor(FileDescriptor fd, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fd, null, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFileDescriptor(fd, null, options);
    }

    /**
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return inSampleSize 指示了在解析图片为Bitmap时在长宽两个方向上像素缩小的倍数
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    /**
     * 返回图片的尺寸
     *
     * @param imageView
     * @return
     */
    public static RequestImageSize getImageViewSize(ImageView imageView) {

        RequestImageSize requestImageSize = new RequestImageSize();
        ViewGroup.LayoutParams lp = imageView.getLayoutParams();
        int width = imageView.getWidth();// 获取imageview的实际宽度
        if (width <= 0) {
            width = lp.width;// 获取imageview在layout中声明的宽度
        }
        if (width <= 0) {
            width = getImageViewFieldValue(imageView, "mMaxWidth");
        }
        if (width <= 0) {
            width = displayMetrics.widthPixels;
        }

        int height = imageView.getHeight();// 获取imageview的实际高度
        if (height <= 0) {
            height = lp.height;// 获取imageview在layout中声明的宽度
        }
        if (height <= 0) {
            height = getImageViewFieldValue(imageView, "mMaxHeight");// 检查最大值
        }
        if (height <= 0) {
            height = displayMetrics.heightPixels;
        }
        requestImageSize.width = width;
        requestImageSize.height = height;
        return requestImageSize;
    }

    /**
     * 使用反射获取 imageView 的最大宽度或者高度
     *
     * @param imageView
     * @param mMaxField
     * @return imageView 的最大宽度或者高度
     */
    private static int getImageViewFieldValue(ImageView imageView, String mMaxField) {
        int requestField = 0;
        try {
            Field field = ImageView.class.getDeclaredField(mMaxField);
            field.setAccessible(true);//设置是否允许访问，因为该变量是private的，所以要手动设置允许访问，如果msg是public的就不需要这行了。
            requestField = field.getInt(imageView);
        } catch (NoSuchFieldException e) {
            Log.e("getImageViewFieldValue", "NoSuchFieldException:" + e.getMessage());
        } catch (IllegalAccessException e) {
            Log.e("getImageViewFieldValue", "IllegalAccessException:" + e.getMessage());
        }
        Log.e("getImageViewFieldValue", "requestField=" + requestField);
        return requestField;
    }

    public static class RequestImageSize {
        public int width;
        public int height;

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }

    public static Bitmap getBitMapFromLocal(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        return bitmap;
    }

    public static Bitmap compressBitMap(String path) {
        return compressBitMap(getBitMapFromLocal(path));
    }

    public static Bitmap compressBitMap(Bitmap bitmap) {
        Log.e("compressBitMap", "bitmap size=" + bitmap.getByteCount());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 60, bos);
        Log.e("compressBitMap", "bos size=" + bos.size());
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        Log.e("compressBitMap", "bis size=" + bis.available());

        return bitmap;
    }

    public static void writeImage(Bitmap bitmap) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + "dest.png";
        File file = new File(path);
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
