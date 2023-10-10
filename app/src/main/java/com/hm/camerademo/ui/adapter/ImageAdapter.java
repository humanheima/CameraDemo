package com.hm.camerademo.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.hm.camerademo.R;
import com.hm.camerademo.databinding.ItemInDetailBinding;
import com.hm.camerademo.util.ImageUtil;
import com.hm.imageslector.adapter.BindingViewHolder;
import com.hm.imageslector.localImages.ImageItem;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<BindingViewHolder> {

    private Context context;
    private List<ImageItem> data;

    public ImageAdapter(Context context, List<ImageItem> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemInDetailBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_in_detail, parent, false);
        BindingViewHolder<ItemInDetailBinding> holder = new BindingViewHolder<>(binding);
        return holder;
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        ImageItem imageItem = data.get(position);
        ImageUtil.loadLocalFile(context, ((ItemInDetailBinding) holder.getBinding()).itemImgBook,
                imageItem.getImagePath());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
