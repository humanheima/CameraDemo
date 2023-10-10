package com.hm.imageslector.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.imageslector.R;
import com.example.imageslector.databinding.MyItemPictureSelectBinding;
import com.hm.imageslector.listener.OnItemClickListener;
import com.hm.imageslector.localImages.ImageItem;
import com.hm.imageslector.util.ImageUtil;
import java.util.List;

/**
 * Created by dumingwei on 2017/2/4.
 */
public class PictureSelectAdapter extends RecyclerView.Adapter<BindingViewHolder<MyItemPictureSelectBinding>> {

    private final int SELECTED_COLOR_FILTER = 0x77000000;

    private Context context;
    private List<ImageItem> imageLocal;
    private OnItemClickListener onItemClickListener;
    private OnPreviewListener onPreviewListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnPreviewListener(OnPreviewListener onPreviewListener) {
        this.onPreviewListener = onPreviewListener;
    }

    public PictureSelectAdapter(Context context, List<ImageItem> imageLocal) {
        this.context = context;
        this.imageLocal = imageLocal;
    }

    @Override
    public BindingViewHolder<MyItemPictureSelectBinding> onCreateViewHolder(ViewGroup parent, int viewType) {
        MyItemPictureSelectBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_picture_select, parent, false);
        BindingViewHolder<MyItemPictureSelectBinding> holder = new BindingViewHolder<>(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(final BindingViewHolder<MyItemPictureSelectBinding> holder, int position) {
        ImageItem item = imageLocal.get(position);
        if (item.getImagePath().endsWith(".gif")) {
            holder.getBinding().tvGif.setVisibility(View.VISIBLE);
        } else {
            holder.getBinding().tvGif.setVisibility(View.GONE);
        }
        if (onItemClickListener != null) {
            holder.getBinding().imgSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            });
        }
        if (onPreviewListener != null) {
            holder.getBinding().imgLocal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPreviewListener.onPreview(holder.getAdapterPosition());
                }
            });
        }
        if (item.isSelected()) {
            holder.getBinding().imgSelect.setImageDrawable(
                    context.getResources().getDrawable(R.drawable.ic_selected_green));
            holder.getBinding().imgLocal.setColorFilter(SELECTED_COLOR_FILTER);
        } else {
            holder.getBinding().imgSelect.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_unselect));
            holder.getBinding().imgLocal.setColorFilter(null);
        }
        ImageUtil.loadSmallFile(context, holder.getBinding().imgLocal, item.getImagePath());
    }

    @Override
    public int getItemCount() {
        return imageLocal.size();
    }

    public interface OnPreviewListener {

        void onPreview(int position);
    }

}
