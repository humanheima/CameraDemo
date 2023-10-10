package com.hm.camerademo.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.hm.camerademo.R;

/**
 * Created by dumingwei on 2017/2/21.
 */
public class MyDialog extends DialogFragment {

    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private TextView textTitle;
    private TextView textContent;
    private Button btnDeny;
    private Button btnAllow;

    private String title;
    private String content;
    private OnAllowClickListener onAllowClickListener;

    public MyDialog() {
    }

    public static MyDialog newInstance(String title, String content) {
        MyDialog dialog = new MyDialog();
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(CONTENT, content);
        dialog.setArguments(args);
        return dialog;
    }

    public void setOnAllowClickListener(OnAllowClickListener onAllowClickListener) {
        this.onAllowClickListener = onAllowClickListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(TITLE);
            content = getArguments().getString(CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment, container);
        textTitle = view.findViewById(R.id.text_title);
        textContent = view.findViewById(R.id.text_content);
        textTitle.setText(title);
        textContent.setText(content);
        btnDeny = view.findViewById(R.id.btn_deny);
        btnAllow = view.findViewById(R.id.btn_allow);
        btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btnAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //这时候应该让用户去应用里面设置权限
                dismiss();
                if (onAllowClickListener != null) {
                    onAllowClickListener.onClick();
                }
            }
        });
        return view;
    }

    public interface OnAllowClickListener {

        void onClick();
    }
}
