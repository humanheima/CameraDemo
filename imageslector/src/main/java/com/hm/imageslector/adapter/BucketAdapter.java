package com.hm.imageslector.adapter;

import android.content.Context;
import androidx.databinding.DataBindingUtil;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import com.example.imageslector.R;
import com.example.imageslector.databinding.MyItemBucketBinding;
import com.hm.imageslector.listener.OnItemClickListener;
import com.hm.imageslector.localImages.ImageBucket;
import com.hm.imageslector.localImages.ImageItem;
import com.hm.imageslector.util.ImageUtil;

import java.util.List;

/**
 * Created by dumingwei on 2018/2/4 0004.
 */

public class BucketAdapter extends RecyclerView.Adapter<BucketAdapter.BucketViewHolder> {

    private final String TAG = getClass().getName();

    private List<ImageBucket> bucketList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public BucketAdapter(Context context, List<ImageBucket> bucketList) {
        this.context = context;
        this.bucketList = bucketList;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public BucketViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyItemBucketBinding binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_bucket, parent, false);
        final BucketViewHolder holder = new BucketViewHolder(binding.getRoot());
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
    public void onBindViewHolder(BucketViewHolder holder, int position) {
        ImageBucket bucket = bucketList.get(position);
        Log.e(TAG, "onBindViewHolder: position=" + position + "," + bucket.getBucketName());
        List<ImageItem> imageItems = bucket.getImageList();
        ImageUtil.load(context, imageItems.get(0).getImagePath(), holder.binding.imgBucket);
        holder.binding.tvBucketTitle.setText(bucket.getBucketName());
        holder.binding.tvBucketNumber.setText(String.valueOf(bucket.getCount()));
        if (bucket.isSelected()) {
            holder.binding.ivBucketSelected.setImageResource(R.drawable.ic_pictures_selected);
        } else {
            holder.binding.ivBucketSelected.setImageResource(R.drawable.ic_picture_unselected);
        }
    }

    @Override
    public int getItemCount() {
        return null == bucketList ? 0 : bucketList.size();
    }

    public static class BucketViewHolder extends RecyclerView.ViewHolder {

        private MyItemBucketBinding binding;

        public BucketViewHolder(View itemView) {
            super(itemView);
        }

    }
}
