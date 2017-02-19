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

import cn.finalteam.rxgalleryfinal.bean.MediaBean;

public class RxGalleryAdapter extends ArrayAdapter<MediaBean> {

    public static final int IMG_MAX_NUM = 9;//最多9张图片
    private List<MediaBean> mediaBeanList;
    private Context context;
    private OnAddImageListener onAddImageListener;
    private int resource;

    public RxGalleryAdapter(Context context, int resource, List<MediaBean> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.mediaBeanList = objects;

    }

    public void setOnAddImageListener(OnAddImageListener onAddImageListener) {
        this.onAddImageListener = onAddImageListener;
    }


    @Override
    public int getCount() {
        if (mediaBeanList.size() < IMG_MAX_NUM) {
            return mediaBeanList.size() + 1;
        } else {
            return mediaBeanList.size();
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

        if (position == mediaBeanList.size() && position < IMG_MAX_NUM) {
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
            ImageUtil.load(context, mediaBeanList.get(position).getOriginalPath(), holder.imageView);
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
