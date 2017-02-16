package com.hm.camerademo.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hm.camerademo.R;
import com.hm.camerademo.ui.widget.zoom.PhotoViewAttacher;

import java.io.File;

/**
 * Created by chenchao on 16/10/24.
 * cc@cchao.org
 * 单张图片查看
 */
public class ImagePreviewFragment extends Fragment {

    private static final String KEY_URL = "key_url";
    private static final String KEY_IS_LOCAL = "key_is_local";

    private String strUrl;
    private ImageView imageView;
    private PhotoViewAttacher attacher;

    private boolean isLocalImage = false;

    public static ImagePreviewFragment newInstance(String imageUrl) {
        ImagePreviewFragment f = new ImagePreviewFragment();
        Bundle args = new Bundle();
        args.putString(KEY_URL, imageUrl);
        f.setArguments(args);
        return f;
    }

    public static ImagePreviewFragment newInstance(String imageUrl, boolean isLocalImage) {
        ImagePreviewFragment f = new ImagePreviewFragment();
        Bundle args = new Bundle();
        args.putString(KEY_URL, imageUrl);
        args.putBoolean(KEY_IS_LOCAL, isLocalImage);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strUrl = getArguments() != null ? getArguments().getString(KEY_URL) : null;
        isLocalImage = getArguments() != null ? getArguments().getBoolean(KEY_IS_LOCAL) : false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_multi_image_detail, container, false);
        imageView = (ImageView) v.findViewById(R.id.image);
        attacher = new PhotoViewAttacher(imageView);

        attacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (isLocalImage) {
            Glide.with(this)
                    .load(new File(strUrl))
                    .listener(new RequestListener<File, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, File model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, File model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                    RelativeLayout.LayoutParams.MATCH_PARENT));
                            return false;
                        }
                    })
                    .dontAnimate()
                    .into(imageView);
            return;
        } else {
            if (strUrl.length() > 3 && "gif".equals(strUrl.substring(strUrl.length() - 3, strUrl.length()))) {
                Glide.with(this)
                        .load(strUrl)
                        .asGif()
                        .listener(new RequestListener<String, GifDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.MATCH_PARENT));
                                return false;
                            }
                        })
                        .dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(imageView);
            } else {
                Glide.with(this)
                        .load(strUrl)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                                        RelativeLayout.LayoutParams.MATCH_PARENT));
                                return false;
                            }
                        })
                        .dontAnimate()
                        .into(imageView);
            }
        }
    }
}
