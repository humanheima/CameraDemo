package com.hm.camerademo.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityMainBinding;
import com.hm.camerademo.util.ImageUtil;
import com.hm.imageslector.base.BaseActivity;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by dumingwei on 2020/9/24
 * <p>
 * Desc:
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> implements EasyPermissions.PermissionCallbacks {

    public static final int REQUEST_TAKE_PHOTO_PERMISSION = 1001;
    public static final int CHOOSE_FROM_ALBUM = 1002;
    //系统剪裁
    public static final int SYSTEM_CROP = 1003;
    private static final int TAKE_PHOTO = 1000;
    private final String TAG = getClass().getSimpleName();
    private Uri photoURI;
    private File cropFile;

    @Override
    protected int bindLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        requestPermission();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_take_photo:
                takePhoto();
                break;
            case R.id.btn_test_path:
                logcatPath();

                break;
            default:
                break;
        }
    }

    /**
     * Environment.getExternalStorageDirectory().getAbsolutePath() =/storage/emulated/0
     * Context.getFilesDir() =/data/user/0/com.hm.camerademo/files
     * Context.getCacheDir() =/data/user/0/com.hm.camerademo/cache
     * Context.getExternalFilesDir() =/storage/emulated/0/Android/data/com.hm.camerademo/files
     * Context.externalCacheDir() =/storage/emulated/0/Android/data/com.hm.camerademo/cache
     */
    private void logcatPath() {
        StringBuilder stringBuilder = new StringBuilder();

        String externalStorageDirectory = Environment.getExternalStorageDirectory().getAbsolutePath();

        stringBuilder.append("Environment.getExternalStorageDirectory().getAbsolutePath() =")
                .append(externalStorageDirectory)
                .append("\n");


        String filesDir = getFilesDir().getAbsolutePath();

        stringBuilder.append("Context.getFilesDir() =").append(filesDir).append("\n");

        String cacheDir = getCacheDir().getAbsolutePath();

        stringBuilder.append("Context.getCacheDir() =").append(cacheDir).append("\n");

        String externalFilesDir = getExternalFilesDir(null).getAbsolutePath();

        stringBuilder.append("Context.getExternalFilesDir() =").append(externalFilesDir).append("\n");

        String externalCacheDir = getExternalCacheDir().getAbsolutePath();

        stringBuilder.append("Context.externalCacheDir() =").append(externalCacheDir).append("\n");

        Log.i(TAG, "\n" + stringBuilder.toString());
    }

    private void requestPermission() {
        String[] prems = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};
        if (EasyPermissions.hasPermissions(this, prems)) {
            Log.d(TAG, "have got permission");
        } else {
            EasyPermissions.requestPermissions(MainActivity.this, "request WRITE_EXTERNAL_STORAGE permission", REQUEST_TAKE_PHOTO_PERMISSION, prems);
        }
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = ImageUtil.createImageFile();
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this, "com.hm.camerademo.fileprovider", photoFile);
                Log.e(TAG, "takePhoto" + photoURI.getPath());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //processTakePhoto(photoFile.getPath());
                    cropPhoto();
                }
                break;
            case CHOOSE_FROM_ALBUM:
                if (resultCode == RESULT_OK) {
                    String imgPath;
                    if (data.getData().toString().startsWith("content")) {
                        String[] proj = {MediaStore.Images.Media.DATA};
                        Cursor cursor = getContentResolver().query(data.getData(), proj, null, null, null);
                        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                        cursor.moveToFirst();
                        imgPath = cursor.getString(column_index);
                        cursor.close();
                    } else {
                        imgPath = data.getData().getPath();
                    }
                    processChoosePicture(imgPath);
                }
                break;
            case REQUEST_TAKE_PHOTO_PERMISSION:
                break;
            case SYSTEM_CROP:
                if (resultCode == RESULT_OK)
                    Log.e(TAG, "SYSTEM_CROP");
                ImageUtil.load(MainActivity.this, cropFile.getPath(), viewBind.imgPreview);
                break;
            default:
                break;
        }
    }

    private void cropPhoto() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction("com.android.camera.action.CROP");
        intent.setDataAndType(photoURI, "image/*");
        cropFile = ImageUtil.createImageFile();
        Uri cropUri = FileProvider.getUriForFile(this, "com.hm.camerademo.fileprovider", cropFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        //剪裁后的图片尺寸,可以指定，也可以不指定
        //intent.putExtra("outputX", 400);
        //intent.putExtra("outputY", 400);
        //intent.putExtra("scale", true);  //是否保持比例
        //intent.putExtra("return-data", false);  //是否返回bitmap
        //将存储图片的uri读写权限授权给剪裁工具应用
        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, cropUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivityForResult(intent, SYSTEM_CROP);
    }

    /**
     * 从相册选取后展示，压缩上传
     *
     * @param picturePath
     */
    private void processChoosePicture(final String picturePath) {
        ImageUtil.load(MainActivity.this, picturePath, viewBind.imgPreview);
        Observable.just(picturePath)
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        try {
                            return ImageUtil.getBitmapFromPath(picturePath);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .filter(new Func1<Bitmap, Boolean>() {
                    @Override
                    public Boolean call(Bitmap bitmap) {
                        return bitmap != null;
                    }
                })
                .flatMap(new Func1<Bitmap, Observable<String>>() {
                    @Override
                    public Observable<String> call(Bitmap bitmap) {
                        return ImageUtil.observableSaveImageToExternal(MainActivity.this, bitmap);
                    }
                })
                .subscribeOn(Schedulers.io())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String imgPath) {
                        Toast.makeText(MainActivity.this, "压缩成功", Toast.LENGTH_SHORT).show();
                        // TODO: 2017/2/10 在这里上传图片
                        //uploadAvatar(imgPath);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "call: " + throwable);
                    }
                });
    }


    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO_PERMISSION:
                Toast.makeText(MainActivity.this, "have got permission", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.e(TAG, "onPermissionsDenied");
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this)
                    .setRequestCode(REQUEST_TAKE_PHOTO_PERMISSION)
                    .setRationale("请在应用中开启所需的权限")
                    .build().show();
        }
    }

}
