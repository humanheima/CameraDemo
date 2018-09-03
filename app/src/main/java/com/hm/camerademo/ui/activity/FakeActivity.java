package com.hm.camerademo.ui.activity;

import android.content.Context;
import android.content.Intent;

import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityFakeBinding;
import com.hm.camerademo.ui.adapter.XAShowImageAdapter;
import com.hm.imageslector.base.BaseActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FakeActivity extends BaseActivity<ActivityFakeBinding> {

    private List<String> list;
    private XAShowImageAdapter adapter;

    public static void launch(Context context, List<String> list) {
        Intent starter = new Intent(context, FakeActivity.class);
        starter.putExtra("list", (Serializable) list);
        context.startActivity(starter);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_fake;
    }

    @Override
    protected void initData() {
        list = new ArrayList<>();
        list.addAll((List<String>) getIntent().getSerializableExtra("list"));
        setAdapter();
    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new XAShowImageAdapter(this, R.layout.grid_child_item, list);
            viewBind.gridView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
