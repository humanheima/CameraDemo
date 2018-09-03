package com.hm.imageslector.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.imageslector.R;
import com.example.imageslector.databinding.ItemPreviewBinding;
import com.hm.imageslector.listener.OnItemClickListener;
import com.hm.imageslector.localImages.ImageItem;
import com.hm.imageslector.util.ImageUtil;

import java.util.List;

/**
 * Created by dumingwei on 2018/2/4 0004.
 */

public class PreviewRvAdapter extends RecyclerView.Adapter<PreviewRvAdapter.PreviewViewHolder> {

    private final String TAG = getClass().getName();

    private List<ImageItem> imageItemList;
    private Context context;
    private OnItemClickListener onItemClickListener;


    public PreviewRvAdapter(Context context, List<ImageItem> imageItemList) {
        this.context = context;
        this.imageItemList = imageItemList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public PreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemPreviewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_preview, parent, false);
        final PreviewViewHolder holder = new PreviewViewHolder(binding.getRoot());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            }
        });
        holder.binding = binding;
        return holder;
    }

    @Override
    public void onBindViewHolder(PreviewViewHolder holder, int position) {
        ImageItem imageItem = imageItemList.get(position);
        ImageUtil.loadLocalFile(context, holder.binding.imgPreview, imageItem.getImagePath());
        if (imageItem.isPreview()) {
            holder.binding.viewSquareLine.setVisibility(View.VISIBLE);
        } else {
            holder.binding.viewSquareLine.setVisibility(View.GONE);
        }
        if (imageItem.isSelected()) {
            holder.binding.viewMask.setVisibility(View.GONE);
        } else {
            holder.binding.viewMask.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return null == imageItemList ? 0 : imageItemList.size();
    }

    public static class PreviewViewHolder extends RecyclerView.ViewHolder {

        private ItemPreviewBinding binding;

        public PreviewViewHolder(View itemView) {
            super(itemView);
        }
    }
}
