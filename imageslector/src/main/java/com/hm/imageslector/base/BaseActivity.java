package com.hm.imageslector.base;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * Created by p_dmweidu on 2024/2/21
 * Desc: BaseActivity
 */
public abstract class BaseActivity<V extends ViewDataBinding> extends AppCompatActivity {

    protected final String TAG = getClass().getSimpleName();
    protected V viewBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBind = DataBindingUtil.setContentView(this, bindLayout());
        initData();
        bindEvent();
    }

    /**
     * 绑定布局文件
     *
     * @return
     */
    protected abstract int bindLayout();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 绑定控件事件
     */
    protected void bindEvent() {

    }

}
