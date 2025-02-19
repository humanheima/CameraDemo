package com.hm.imageslector.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.imageslector.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;

/**
 * Created by p_dmweidu on 2024/2/23
 * Desc: 图片预览的Fragment
 */
public class PreviewFragment extends Fragment {

    private final String TAG = getClass().getName();
    private static final String KEY_URL = "key_url";

    private String strUrl;

    public static PreviewFragment newInstance(String imageUrl) {
        PreviewFragment f = new PreviewFragment();
        Bundle args = new Bundle();
        args.putString(KEY_URL, imageUrl);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strUrl = getArguments().getString(KEY_URL);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: strUrl = " + strUrl);
        final View v = inflater.inflate(R.layout.fragment_preview, container, false);
        final PhotoView imageView = v.findViewById(R.id.photo_view);

        //Note: 在ViewPager中使用PhotoView时，滑动有点冲突，后面换一个库
        Glide.with(this).load(new File(strUrl)).into(imageView);

        //final PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);

//        Glide.with(this)
//                .asBitmap()
//                .load(new File(strUrl))
//                .into(new CustomTarget<Bitmap>() {
//                    @Override
//                    public void onResourceReady(@NonNull Bitmap resource,
//                            @Nullable Transition<? super Bitmap> transition) {
//                        imageView.setImageBitmap(resource);
//                        mAttacher.update();
//                    }
//
//                    @Override
//                    public void onLoadCleared(@Nullable Drawable placeholder) {
//
//                    }
//                });
        return v;
    }
}
