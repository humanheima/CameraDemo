package com.hm.camerademo.ui.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ItemPictureSelectBinding;
import com.hm.camerademo.listener.OnItemClickListener;
import com.hm.camerademo.util.ImageUtil;
import com.hm.camerademo.util.localImages.ImageItem;

import java.util.List;

/**
 * Created by dumingwei on 2017/2/4.
 */
public class PictureSelectAdapter extends RecyclerView.Adapter<PictureSelectAdapter.PictureSelectHolder> {

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

    public PictureSelectAdapter(List<ImageItem> imageLocal) {
        this.imageLocal = imageLocal;
    }

    @Override
    public PictureSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        ItemPictureSelectBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_picture_select, parent, false);
        PictureSelectHolder holder = new PictureSelectHolder(binding.getRoot());
        holder.setBinding(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PictureSelectHolder holder, int position) {
        ImageItem item = imageLocal.get(position);
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
            holder.getBinding().imgSelect.setImageResource(R.drawable.ic_pictures_selected);
            holder.getBinding().imgLocal.setColorFilter(SELECTED_COLOR_FILTER);
        } else {
            holder.getBinding().imgSelect.setImageResource(R.drawable.ic_picture_unselected);
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

    public class PictureSelectHolder extends RecyclerView.ViewHolder {

        private ItemPictureSelectBinding binding;

        public PictureSelectHolder(View itemView) {
            super(itemView);
        }

        public ItemPictureSelectBinding getBinding() {
            return binding;
        }

        public void setBinding(ItemPictureSelectBinding binding) {
            this.binding = binding;
        }

    }
}
