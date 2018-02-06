package com.hm.camerademo.util.localImages;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class ImageItem implements Comparable<ImageItem>, Serializable {

    private String id;
    private String imagePath;
    private boolean selected;
    private long time;
    //是否是正在被预览的图片
    private boolean preview;
    //在预览界面是否被取消选择了
    private boolean canceled;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isPreview() {
        return preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }

    /**
     * {@link java.util.Collections#sort(List)}这个方法排序是 ASC的
     * 因为我们想倒序，所以 compareTo 方法，大的时候我们返回 -1 而小的时候我们返回1
     */
    @Override
    public int compareTo(@NonNull ImageItem o) {
        return this.time >= o.time ? -1 : 1;
    }
}
