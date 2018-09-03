package com.hm.camerademo.ui.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ActivityXaBinding;
import com.hm.camerademo.ui.adapter.XAGroupAdapter;
import com.hm.imageslector.base.BaseActivity;
import com.hm.camerademo.util.xiananmingdemo.ImageBean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class XAActivity extends BaseActivity<ActivityXaBinding> {

    private Map<String, List<String>> mGroupMap;
    private List<ImageBean> imageBeanList;
    private XAGroupAdapter adapter;

    public static void launch(Context context) {
        Intent starter = new Intent(context, XAActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int bindLayout() {
        return R.layout.activity_xa;
    }

    @Override
    protected void initData() {
        mGroupMap = new HashMap<>();
        imageBeanList = new ArrayList<>();
        getImages();
    }

    private void getImages() {
        Log.e(TAG, "getImages");
        Observable.create(new Observable.OnSubscribe<Map<String, List<String>>>() {
            @Override
            public void call(Subscriber<? super Map<String, List<String>>> subscriber) {
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver
                        .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
                                MediaStore.Images.Media.MIME_TYPE + "= ? or " + MediaStore.Images.Media.MIME_TYPE + "= ? or " + MediaStore.Images.Media.MIME_TYPE + "= ?",
                                new String[]{"image/jpeg", "image/png", "image/gif"},
                                MediaStore.Images.Media.DATE_MODIFIED
                        );
                while (cursor.moveToNext()) {
                    //获取图片的路径
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    //获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();
                    Log.e(TAG, "parentName==" + parentName);
                    if (!mGroupMap.containsKey(parentName)) {
                        List<String> childList = new ArrayList<>();
                        childList.add(path);
                        mGroupMap.put(parentName, childList);
                    } else {
                        mGroupMap.get(parentName).add(path);
                    }

                }
                subscriber.onNext(mGroupMap);
                subscriber.onCompleted();
                cursor.close();
            }
        })
                .subscribeOn(AndroidSchedulers.mainThread())
                .map(new Func1<Map<String, List<String>>, List<ImageBean>>() {
                    @Override
                    public List<ImageBean> call(Map<String, List<String>> stringListMap) {
                        if (mGroupMap == null) {
                            return null;
                        }
                        List<ImageBean> list = new ArrayList<>();
                        Iterator<Map.Entry<String, List<String>>> it = mGroupMap.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, List<String>> entry = it.next();
                            ImageBean imageBean = new ImageBean();
                            String key = entry.getKey();
                            List<String> value = entry.getValue();
                            imageBean.setFolderName(key);
                            imageBean.setImageCounts(value.size());
                            imageBean.setTopImagePath(value.get(0));
                            list.add(imageBean);
                        }
                        return list;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<ImageBean>>() {
                    @Override
                    public void call(List<ImageBean> imageBeen) {
                        Log.e(TAG, "call imageBeen size=" + imageBeen.size());
                        imageBeanList.addAll(imageBeen);
                        setAdapter();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "getImages error " + throwable.getMessage());
                    }
                });
    }

    private void setAdapter() {
        if (adapter == null) {
            Log.e(TAG, "setAdapter imageBeanList size=" + imageBeanList.size());
            adapter = new XAGroupAdapter(XAActivity.this, R.layout.item_grid_view, imageBeanList);
            viewBind.gridViewGroup.setAdapter(adapter);
            viewBind.gridViewGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    List<String> list = mGroupMap.get(imageBeanList.get(position).getFolderName());
                    Log.e(TAG, "onItemClick list size=" + list.size());
                    FakeActivity.launch(XAActivity.this, list);
                }
            });
        } else {
            adapter.notifyDataSetChanged();
        }
    }
}
