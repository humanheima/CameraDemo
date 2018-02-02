package com.hm.camerademo.ui.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.hm.camerademo.R;
import com.hm.camerademo.util.ImageUtil;
import com.hm.camerademo.util.xiananmingdemo.MyImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dumingwei on 2017/2/19.
 */
public class XAShowImageAdapter extends ArrayAdapter<String> {

    private List<String> list;
    private Context context;
    private int resource;
    private List<Boolean> checkedList;

    public XAShowImageAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        checkedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            checkedList.add(false);
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ShowImageVH holder;
        String path = list.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, null);
            holder = new ShowImageVH();
            holder.imageView = convertView.findViewById(R.id.child_image);
            holder.checkBox = convertView.findViewById(R.id.child_checkbox);
            convertView.setTag(holder);
        } else {
            holder = (ShowImageVH) convertView.getTag();
        }
        final int pos = position;
        holder.checkBox.setChecked(checkedList.get(pos));
        ImageUtil.load(context, path, holder.imageView);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("onclick", "onclick");
                CheckBox checkBox = (CheckBox) v;
                checkedList.set(pos, checkBox.isChecked());
            }
        });
        return convertView;
    }

    private static class ShowImageVH {
        MyImageView imageView;
        CheckBox checkBox;
    }
}
