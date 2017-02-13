package com.hm.camerademo.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hm.camerademo.R;
import com.hm.camerademo.listener.OnItemClickListener;
import com.hm.camerademo.util.ImageUtil;
import com.hm.camerademo.util.localImages.ImageItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by dumingwei on 2017/2/4.
 */
public class PictureSelectAdapter extends RecyclerView.Adapter<PictureSelectAdapter.PictureSelectHolder> {

    private final int SELECTED_COLOR_FILTER = 0x77000000;

    private Context context;
    private List<ImageItem> imageLocal;
    private List<Boolean> isSelect;
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public PictureSelectAdapter(List<ImageItem> imageLocal, List<Boolean> isSelect) {
        this.imageLocal = imageLocal;
        this.isSelect = isSelect;
    }

    @Override
    public PictureSelectHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View convertView = LayoutInflater.from(context).inflate(R.layout.item_picture_select, parent, false);
        return new PictureSelectHolder(convertView);
    }

    @Override
    public void onBindViewHolder(final PictureSelectHolder holder, int position) {

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, holder.getAdapterPosition());
                }
            });
        }
        if (isSelect.get(position)) {
            holder.imgSelected.setImageResource(R.drawable.ic_pictures_selected);
            holder.imgLocal.setColorFilter(SELECTED_COLOR_FILTER);
        } else {
            holder.imgSelected.setImageResource(R.drawable.ic_picture_unselected);
            holder.imgLocal.setColorFilter(null);
        }
        ImageUtil.loadLocalFile(context, holder.imgLocal, imageLocal.get(position).getImagePath());
    }

    @Override
    public int getItemCount() {
        return imageLocal.size();
    }

    public class PictureSelectHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_local)
        ImageView imgLocal;
        @BindView(R.id.img_select)
        ImageView imgSelected;

        public PictureSelectHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
