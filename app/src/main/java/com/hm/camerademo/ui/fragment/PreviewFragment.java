package com.hm.camerademo.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.hm.camerademo.R;
import com.hm.camerademo.util.ImageUtil;

/**
 * Created by chenchao on 16/10/24.
 * cc@cchao.org
 * 单张图片查看
 */
public class PreviewFragment extends Fragment {

    private static final String KEY_URL = "key_url";
    private static final String KEY_IS_LOCAL = "key_is_local";

    private String strUrl;
    private PhotoView imageView;

    private boolean isLocalImage = false;

    public static PreviewFragment newInstance(String imageUrl) {
        PreviewFragment f = new PreviewFragment();
        Bundle args = new Bundle();
        args.putString(KEY_URL, imageUrl);
        f.setArguments(args);
        return f;
    }

    public static PreviewFragment newInstance(String imageUrl, boolean isLocalImage) {
        PreviewFragment f = new PreviewFragment();
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
        final View v = inflater.inflate(R.layout.fragment_preview, container, false);
        imageView = v.findViewById(R.id.image);
        ImageUtil.loadLocalFile(getContext(), imageView, strUrl);
        return v;
    }

}
