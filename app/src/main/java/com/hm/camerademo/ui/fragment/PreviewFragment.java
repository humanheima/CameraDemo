package com.hm.camerademo.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.chrisbanes.photoview.PhotoView;
import com.hm.camerademo.R;
import com.hm.camerademo.util.ImageUtil;

public class PreviewFragment extends Fragment {

    private final String TAG = getClass().getName();
    private static final String KEY_URL = "key_url";

    private String strUrl;
    private PhotoView imageView;

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
        imageView = v.findViewById(R.id.image);
        ImageUtil.loadLocalFile(getContext(), imageView, strUrl);
        return v;
    }
}
