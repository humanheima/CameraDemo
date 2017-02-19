package com.hm.camerademo.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import android.widget.TextView;

import com.hm.camerademo.R;
import com.hm.camerademo.ui.adapter.XAShowImageAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.hm.camerademo.R.id.grid_view;

public class FuckActivity extends AppCompatActivity {

    @BindView(R.id.textView)
    TextView textView;
    @BindView(grid_view)
    GridView gridView;
    private List<String> list;
    private XAShowImageAdapter adapter;

    public static void launch(Context context) {
        Intent starter = new Intent(context, FuckActivity.class);
        context.startActivity(starter);
    }

    public static void launch(Context context, List<String> list) {
        Intent starter = new Intent(context, FuckActivity.class);
        starter.putExtra("list", (Serializable) list);
        context.startActivity(starter);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuck);
        ButterKnife.bind(this);
        list = new ArrayList<>();
        list.addAll((List<String>) getIntent().getSerializableExtra("list"));
        setAdapter();
    }

    private void setAdapter() {
        if (adapter == null) {
            adapter = new XAShowImageAdapter(this, R.layout.grid_child_item, list);
            gridView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
