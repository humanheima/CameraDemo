package com.hm.camerademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityRequestPermissionBinding;
import com.hm.imageslector.base.BaseActivity;


/**
 * Created by p_dmweidu on 2024/2/21
 * Desc: 测试新的权限申请方式
 */
public class NewRequestPermissionActivity extends BaseActivity<ActivityRequestPermissionBinding> {

    public static void start(Context context) {
        Intent starter = new Intent(context, NewRequestPermissionActivity.class);
        context.startActivity(starter);
    }

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<String[]> requestMultiPermissionLauncher;

    @Override
    protected int bindLayout() {
        return R.layout.activity_request_permission;
    }

    @Override
    protected void initData() {
        viewBind.btnRequestSinglePermission.setOnClickListener(v -> {
            requestSinglePermission();
        });

        viewBind.btnRequestMultiPermission.setOnClickListener(v -> {
            requestMultiPermission();
        });
        viewBind.btnOpenAppPermissionSetting.setOnClickListener(v -> {
            openAppSettings();
        });
    }

    private void requestMultiPermission() {
        requestMultiPermissionLauncher = getActivityResultRegistry().register("activity_rq_multi_permission",
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
                        showToast("权限申请失败，部分权限未通过，请手动设置");
                    }
                });
        requestMultiPermissionLauncher.launch(new String[]{android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    private void requestSinglePermission() {
        requestPermissionLauncher = getActivityResultRegistry().register("activity_rq_camera",
                new ActivityResultContracts.RequestPermission(), result -> {
                    if (result) {
                        showToast("权限申请成功");
                    } else {
                        showToast("权限申请失败");
                    }
                });

//        requestPermissionLauncher = registerForActivityResult(new RequestPermission(
//        ), isGranted -> {
//            if (isGranted) {
//                showToast("权限申请成功");
//            } else {
//                showToast("权限申请失败");
//            }
//        });

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // 应用已经获得了 CAMERA 权限
            showToast("应用已经获得了 CAMERA 权限");
        } else {
            // 应用还没有获得 CAMERA 权限
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA);
        }
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


}