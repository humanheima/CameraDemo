package com.hm.camerademo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.hm.camerademo.R;
import com.hm.camerademo.util.ImageUtil;

import java.util.List;

public class ImageAdapter extends ArrayAdapter<String> {

    public static final int IMG_MAX_NUM = 9;//最多9张图片
    private Context context;
    private int resource;
    private List<String> stringList;
    private OnAddImageListener onAddImageListener;

    public void setOnAddImageListener(OnAddImageListener onAddImageListener) {
        this.onAddImageListener = onAddImageListener;
    }

    public ImageAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.stringList = objects;
    }

    @Override
    public int getCount() {
        if (stringList.size() < IMG_MAX_NUM) {
            return stringList.size() + 1;
        } else {
            return stringList.size();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (context == null) {
            context = parent.getContext();
        }
        final OutDetailHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder = new OutDetailHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.item_img_book);
            holder.imageViewAdd = (ImageView) convertView.findViewById(R.id.item_add_img);
            convertView.setTag(holder);
        } else {
            holder = (OutDetailHolder) convertView.getTag();
        }

        if (position == stringList.size() && position < IMG_MAX_NUM) {
            holder.imageViewAdd.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.GONE);
            if (onAddImageListener != null) {
                holder.imageViewAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddImageListener.openSelect(v);
                    }
                });
            }
        } else {
            holder.imageViewAdd.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.VISIBLE);
            ImageUtil.load(context, stringList.get(position), holder.imageView);
            if (onAddImageListener != null) {
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAddImageListener.openShowImage(position);
                    }
                });
            }
        }
        return convertView;
    }

    class OutDetailHolder {
        private ImageView imageView;
        private ImageView imageViewAdd;
    }

    public interface OnAddImageListener {

        void openSelect(View view);

        void openShowImage(int position);
    }
}