package com.hm.imageslector.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.imageslector.R;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_preview, container, false);
        final ImageView imageView = v.findViewById(R.id.image);
        final PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView);
        // TODO: 2018/2/8 0008 不明白为什么要这样写
        Glide.with(this)
                .asBitmap()
                .load(new File(strUrl))
                .into(new SimpleTarget<Bitmap>(480, 800) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        mAttacher.update();
                    }
                });
        return v;
    }
}
