package com.hm.imageslector.localImages;

import androidx.annotation.NonNull;
import java.io.Serializable;

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

    @Override
    public int compareTo(@NonNull ImageItem o) {
        if (this.time > o.time) {
            return 1;
        } else if (this.time == o.time) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        return "ImageItem{" +
                "id='" + id + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
