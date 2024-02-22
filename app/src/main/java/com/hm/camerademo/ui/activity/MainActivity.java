package com.hm.camerademo.ui.activity;

import android.Manifest;
import android.Manifest.permission;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityMainBinding;
import com.hm.camerademo.network.HttpResult;
import com.hm.camerademo.network.NetWork;
import com.hm.camerademo.util.ImageUtil;
import com.hm.imageslector.base.BaseActivity;
import com.hm.imageslector.localImages.ImageItem;
import com.yongchun.library.view.ImageSelectorActivity;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by p_dmweidu on 2024/2/21
 * Desc:
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

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
            case R.id.btn_new_request_permission:
                NewRequestPermissionActivity.start(MainActivity.this);
                break;
            case R.id.btn_take_photo:
                takePhoto();
                break;
            case R.id.btn_choose_photo:
                chooseFromAlbum();
                break;
            case R.id.btn_choose_multi_photo:
                MultiPhotoActivity.launch(MainActivity.this, 3);
                break;
            case R.id.btn_xiaanming:
                XAActivity.launch(MainActivity.this);
                break;
            case R.id.btn_compress:
                CompressActivity.launch(MainActivity.this);
                break;
            case R.id.btn_other:
                ImageSelectorActivity.start(MainActivity.this,
                        9, ImageSelectorActivity.MODE_MULTIPLE, false, true, false);
                break;

            case R.id.btn_test_content_resolver:
                getAllImages(MainActivity.this);
                break;
            default:
                break;
        }
    }

    public void getAllImages(Context context) {
        Observable.create(new OnSubscribe<List<ImageItem>>() {
                    @Override
                    public void call(Subscriber<? super List<ImageItem>> subscriber) {
                        List<ImageItem> images = new ArrayList<>();
                        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
                        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                        if (cursor != null) {
                            while (cursor.moveToNext()) {
                                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
                                String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                                ImageItem imageItem = new ImageItem();
                                imageItem.setId(String.valueOf(id));
                                imageItem.setImagePath(path);
                                images.add(imageItem);
                            }
                            cursor.close();
                        }
                        subscriber.onNext(images);
                        subscriber.onCompleted();
                    }
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ImageItem>>() {
                    @Override
                    public void call(List<ImageItem> uris) {
                        for (ImageItem uri : uris) {
                            Log.e(TAG, "call: " + uri.getImagePath());
                        }
                    }
                });

    }

    private void requestPermission() {
        String[] prems = {Manifest.permission.WRITE_EXTERNAL_STORAGE, permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};
        if (ContextCompat.checkSelfPermission(this, prems[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, prems[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, prems[2]) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "have got permission");
        } else {
            ActivityResultLauncher<String[]> requestPermissionLauncher = getActivityResultRegistry().register(
                    "activity_rq_multi_permission",
                    new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                        if (result == null) {
                            showToast("权限申请失败");
                            return;
                        }
                        if (Boolean.TRUE.equals(result.get(android.Manifest.permission.CAMERA)) && Boolean.TRUE.equals(
                                result.get(
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                            showToast("权限申请成功");
                        } else {
                            showSettingDialog("权限申请失败，部分权限未通过，请手动设置");
                        }
                    });

            requestPermissionLauncher.launch(prems);
        }
    }

    private void showSettingDialog(String tips) {
        new AlertDialog.Builder(this).setTitle(tips).setMessage(tips)
                .setPositiveButton("去设置", (dialog, which) -> {
                    openAppSettings();
                }).setNegativeButton("取消", null).show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
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

    private void chooseFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, CHOOSE_FROM_ALBUM);
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
                if (resultCode == RESULT_OK) {
                    Log.e(TAG, "SYSTEM_CROP");
                }
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
        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, cropUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivityForResult(intent, SYSTEM_CROP);
    }

    /**
     * 处理拍照后的图片
     */
    private void processTakePhoto(final String imgPath) {
        //图片被旋转,则旋转为正常角度并保存
        Observable.just(imgPath)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        try {
                            //返回压缩的图片的路径，要记得上传完成后，把这个压缩的图片给删除了。
                            return ImageUtil.compressImage(MainActivity.this, imgPath, 70);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String imgPath) {
                        ImageUtil.load(MainActivity.this, imgPath, viewBind.imgPreview);
                        Toast.makeText(MainActivity.this, "压缩成功", Toast.LENGTH_SHORT).show();
                        // TODO: 2017/2/10 在这里上传图片
                        //uploadAvatar(imgPath);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(MainActivity.this, "压缩失败" + throwable.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });
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

    /**
     * 上传头像,删除压缩后的图片
     *
     * @param imgPath
     */
    private void uploadAvatar(final String imgPath) {
        File file = new File(imgPath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        final MultipartBody.Part part = MultipartBody.Part.createFormData("file[]", file.getName(), requestBody);
        NetWork.getApi().uploadAvatar(part)
                .subscribeOn(Schedulers.io())
                .flatMap(new Func1<HttpResult<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(HttpResult<String> result) {
                        return NetWork.flatResponse(result);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        //上传成功，删除压缩图片
                        File deleteFile = new File(imgPath);
                        if (deleteFile.exists()) {
                            deleteFile.delete();
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {

                    }
                });
    }


    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
