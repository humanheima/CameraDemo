package com.hm.camerademo.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.hm.camerademo.R;
import com.hm.camerademo.network.HttpResult;
import com.hm.camerademo.network.NetWork;
import com.hm.camerademo.ui.fragment.MyDialog;
import com.hm.camerademo.util.ImageUtil;
import com.hm.camerademo.util.SpUtil;

import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private static final int TAKE_PHOTO = 1000;
    public static final int REQUEST_TAKE_PHOTO_PERMISSION = 1001;
    public static final int CHOOSE_FROM_ALBUM = 1004;
    @BindView(R.id.img_preview)
    ImageView imgPreview;
    @BindView(R.id.btn_take_photo)
    Button btnTakePhoto;
    @BindView(R.id.btn_choose_photo)
    Button btnChoosePhoto;
    @BindView(R.id.btn_save_bitmap)
    Button btnCompressBitmap;
    @BindView(R.id.btn_choose_multi_photo)
    Button btnChooseMultiPhoto;
    //拍照图片旋转角度
    private int photoDegree = 0;
    private File photoFile;
    private Uri photoURI;
    private MyDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_take_photo, R.id.btn_choose_photo, R.id.btn_save_bitmap, R.id.btn_choose_multi_photo, R.id.btn_xiaanming, R.id.btn_final})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_take_photo:
                takePhotoRequestPermission();
                break;
            case R.id.btn_choose_photo:
                chooseFromAlbum();
                break;
            case R.id.btn_save_bitmap:
                saveBitmap();
                break;
            case R.id.btn_choose_multi_photo:
                MultiPhotoActivity.launch(MainActivity.this);
                break;
            case R.id.btn_xiaanming:
                XAActivity.launch(MainActivity.this);
                break;
            case R.id.btn_final:
                RxGalleryActivity.launch(MainActivity.this);
                break;
            default:
                break;
        }
    }

    private void takePhotoRequestPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                if (!SpUtil.getInstance().getNotAskAgain()) {
                    if (dialog == null) {
                        dialog = MyDialog.newInstance("提示", "需要读写数据权限");
                        dialog.setOnAllowClickListener(new MyDialog.OnAllowClickListener() {
                            @Override
                            public void onClick() {
                                startAppSetting();
                            }
                        });
                    }
                    dialog.show(getSupportFragmentManager(), "dialog");
                } else {
                    Toast.makeText(this, "请设置权限后再使用", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "takePhotoRequestPermission shouldShowRequestPermissionRationale==false");
                //直接申请权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_TAKE_PHOTO_PERMISSION);
            }
        } else {
            takePhoto();
        }
    }

    public void startAppSetting() {
        Intent in = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        in.setData(uri);
        startActivityForResult(in, REQUEST_TAKE_PHOTO_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_TAKE_PHOTO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                Toast.makeText(this, "CAMERA PERMISSION DENIED", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void takePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            photoFile = ImageUtil.createImageFile();
            if (photoFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Log.e(TAG, "Build.VERSION.SDK_INT >= Build.VERSION_CODES.N");
                    photoURI = FileProvider.getUriForFile(this, "com.hm.camerademo.fileprovider", photoFile);
                    takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                } else {
                    photoURI = Uri.fromFile(photoFile);
                }
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

    /**
     * 把imageView上的bitmap保存到本地
     */
    private void saveBitmap() {
        String destination = null;
        imgPreview.setDrawingCacheEnabled(true);
        Bitmap bitmap = imgPreview.getDrawingCache();
        try {
            destination = ImageUtil.compressImage(MainActivity.this, bitmap, 70);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(destination)) {
            Toast.makeText(MainActivity.this, "保存失败", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "压缩成功", Toast.LENGTH_SHORT).show();
        }
        imgPreview.setDrawingCacheEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // processTakePhoto();
                    processTakePhoto(photoFile.getPath());
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
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_TAKE_PHOTO_PERMISSION);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 拍照后展示，压缩上传
     */
    private void processTakePhoto() {
        photoDegree = ImageUtil.getBitmapDegree(photoURI.getPath());
        //图片被旋转,则旋转为正常角度并保存
        Observable.just(photoURI.getPath())
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        try {
                            return ImageUtil.getBitmapFormUri(MainActivity.this, photoURI);
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
                .map(new Func1<Bitmap, Bitmap>() {
                    @Override
                    public Bitmap call(Bitmap bitmap) {
                        if (photoDegree == 0) {
                            return bitmap;
                        }
                        return ImageUtil.rotateBitmapByDegree(bitmap, photoDegree);
                    }
                })
                .flatMap(new Func1<Bitmap, Observable<String>>() {
                    @Override
                    public Observable<String> call(Bitmap bitmap) {
                        return ImageUtil.observableSaveImageToExternal(MainActivity.this, bitmap);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String imgPath) {
                        ImageUtil.load(MainActivity.this, imgPath, imgPreview);
                        Toast.makeText(MainActivity.this, "压缩成功", Toast.LENGTH_SHORT).show();
                        // TODO: 2017/2/10 在这里上传图片
                        //uploadAvatar(imgPath);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(MainActivity.this, "压缩失败" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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
                        ImageUtil.load(MainActivity.this, imgPath, imgPreview);
                        Toast.makeText(MainActivity.this, "压缩成功", Toast.LENGTH_SHORT).show();
                        // TODO: 2017/2/10 在这里上传图片
                        //uploadAvatar(imgPath);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Toast.makeText(MainActivity.this, "压缩失败" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 从相册选取后展示，压缩上传
     *
     * @param picturePath
     */
    private void processChoosePicture(final String picturePath) {
        ImageUtil.load(MainActivity.this, picturePath, imgPreview);
        Observable.just(picturePath)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
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
}
