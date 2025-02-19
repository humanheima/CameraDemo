package com.hm.camerademo.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.hm.camerademo.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rx.Observable;
import rx.Subscriber;


/**
 * Created by dumingwei on 2017/1/5.
 * 质量压缩不能用bitmap的形式进行压缩
 * 质量压缩不改变图片占用内存的大小
 * 拍照后，或者从本地选取图片后，应该使用采样压缩
 * 当要把一个ImageView 上的图片 存储到本地的时候，先使用质量压缩，再使用采样压缩。
 */
public class ImageUtil {

    private static final String TAG = "ImageUtil";
    //private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd-HH:mm:ss", Locale.CHINA);
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HH_mm_ss", Locale.CHINA);

    private static RequestOptions options = new RequestOptions()
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .dontAnimate();
    private static RequestOptions smallOptions = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.ic_placeholder)
            .error(R.drawable.ic_placeholder)
            .dontAnimate();

    private ImageUtil() {
    }

    public static void load(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .apply(options)
                .into(imageView);
    }

    public static void load(Context context, int resId, ImageView imageView) {
        Glide.with(context)
                .load(resId)
                .apply(options)
                .into(imageView);
    }

    public static void loadLocalFile(Context context, ImageView imageView, String url) {
        if (url.endsWith(".gif")) {
            Glide.with(context)
                    .asGif()
                    .load(new File(url))
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(new File(url))
                    .apply(options)
                    .into(imageView);
        }

    }

    public static void loadSmallFile(Context context, ImageView imageView, String url) {
        Glide.with(context)
                .load(new File(url))
                .apply(smallOptions)
                .into(imageView);
    }


    /**
     * 创建图片File对象
     *
     * @return
     */
    public static File createImageFile() {
        File imageFile = null;
        String storagePath;
        File storageDir;
        String timeStamp = dateFormat.format(new Date());
        try {
            storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "camerademo";
            //storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + File.separator + "camerademo";
            //storagePath = App.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
            //storagePath = App.getInstance().getExternalCacheDir().getAbsolutePath() + File.separator + "camerademo";
            //storagePath = App.getInstance().getFilesDir().getAbsolutePath() + File.separator + "images";
            storageDir = new File(storagePath);
            storageDir.mkdirs();
            //yyyyMMdd-HH:mm:ss，这种格式会报错，errorOperation not permitted
            imageFile = File.createTempFile(timeStamp, ".jpg", storageDir);
            Log.e(TAG, imageFile.getAbsolutePath());
        } catch (IOException e) {
            Log.e(TAG, "error" + e.getMessage());
            e.printStackTrace();
        }
        return imageFile;
    }


    public static Bitmap getSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static Bitmap getBitmapFromResource(Resources res, int resId) {
        return BitmapFactory.decodeResource(res, resId);
    }

    /**
     * 通过imgPath获取图片并采样压缩，降低bitmap 占用的内存
     *
     * @param imgPath 图片地址
     * @return The decoded bitmap, or null
     */
    public static Bitmap getBitmapFromPath(String imgPath) {
        int reqWidth = 480;
        int reqHeight = 800;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgPath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imgPath, options);
    }

    /**
     * 通过uri获取图片并采样压缩，降低bitmap 占用的内存
     *
     * @param uri 图片uri
     * @return The decoded bitmap, or null
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        if (input == null) {
            return null;
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, options);
            input.close();
            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;
            if ((originalWidth == -1) || (originalHeight == -1))
                return null;
            options.inSampleSize = calculateInSampleSize(options, 480, 800);//设置缩放比例
            options.inJustDecodeBounds = false;
            input = ac.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
            if (input != null) {
                input.close();
            }
            return bitmap;
        }
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
     * @param options   options
     * @param reqWidth  目标宽度
     * @param reqHeight 目标高度
     * @return inSampleSize 指示了在解析图片为Bitmap时在长宽两个方向上像素缩小的倍数
     */
    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d(TAG, "options.outWidth=" + options.outWidth + ",options.outHeight=" + options.outHeight);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            //计算压缩比，计算出的inSampleSize保证图片的宽和高都比要求的宽高大一点。
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
     * 质量压缩方法,并不能减小图片在内存中的占用大小，只能改变以文件形式存储时的文件大小
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
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 压缩图片处理某些手机拍照角度旋转的问题。
     *
     * @param context  上下文对象
     * @param filePath 图片地址
     * @param quantity 压缩质量
     * @return
     * @throws FileNotFoundException
     */
    public static String compressImage(Context context, String filePath, int quantity) throws IOException {
        Bitmap bitmap = getBitmapFromPath(filePath);
        int degree = getBitmapDegree(filePath);
        if (degree != 0) {
            bitmap = rotateBitmapByDegree(bitmap, degree);
        }
        File imageFile = createImageFile();
        FileOutputStream out = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, quantity, out);
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
        return imageFile.getPath();
    }

    /**
     * @param context  上下文对象
     * @param bitmap   要存在
     * @param quantity 压缩质量
     * @return 存储的压缩后的路径
     * @throws FileNotFoundException
     */
    public static String compressImage(Context context, Bitmap bitmap, int quantity) throws IOException {
        File imageFile = createImageFile();
        FileOutputStream out = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, quantity, out);
        out.flush();
        out.close();
        //通知媒体扫描器扫描文件
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(imageFile);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
        } else {
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + imageFile.getAbsoluteFile())));
        }
        return imageFile.getPath();
    }


    /**
     * 读取图片的旋转的角度
     *
     * @param path 图片绝对路径
     * @return 图片的旋转角度
     */
    private static int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 将图片按照某个角度进行旋转
     *
     * @param bm     需要旋转的图片
     * @param degree 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            Log.e("rotateBitmapByDegree", "恭喜你 OOM了");
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
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
            bm.compress(Bitmap.CompressFormat.JPEG, 80, out);
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

}
