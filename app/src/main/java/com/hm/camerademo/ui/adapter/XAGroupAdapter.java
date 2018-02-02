package com.hm.camerademo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.hm.camerademo.R;
import com.hm.camerademo.util.ImageUtil;
import com.hm.camerademo.util.xiananmingdemo.ImageBean;
import com.hm.camerademo.util.xiananmingdemo.MyImageView;

import java.util.List;

/**
 * Created by dumingwei on 2017/2/19.
 */
public class XAGroupAdapter extends ArrayAdapter<ImageBean> {

    private int resource;
    private Context context;
    private List<ImageBean> list;

    public XAGroupAdapter(Context context, int resource, List<ImageBean> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        GroupVH holder;
        ImageBean bean = list.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            holder = new GroupVH();
            holder.myImageView = (MyImageView) convertView.findViewById(R.id.group_image);
            holder.textTitle = (TextView) convertView.findViewById(R.id.group_title);
            holder.textCounts = (TextView) convertView.findViewById(R.id.group_count);
            convertView.setTag(holder);
        } else {
            holder = (GroupVH) convertView.getTag();
        }
        holder.textTitle.setText(bean.getFolderName());
        holder.textCounts.setText(String.valueOf(bean.getImageCounts()));
        ImageUtil.load(context, bean.getTopImagePath(), holder.myImageView);
        return convertView;
    }

    static class GroupVH {
        private MyImageView myImageView;
        private TextView textTitle;
        private TextView textCounts;
    }
}
