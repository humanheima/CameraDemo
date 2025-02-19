# 缩放图片

通过 Matrix 来实现。

# 裁剪图片

通过 BitmapRegionDecoder 来实现。

```java
/**
 * Decodes a rectangle region in the image specified by rect.
 *
 * @param rect The rectangle that specified the region to be decode.
 * @param options null-ok; Options that control downsampling.
 *             inPurgeable is not supported.
 * @return The decoded bitmap, or null if the image data could not be
 *         decoded.
 * @throws IllegalArgumentException if {@link BitmapFactory.Options#inPreferredConfig}
 *         is {@link android.graphics.Bitmap.Config#HARDWARE}
 *         and {@link BitmapFactory.Options#inMutable} is set, if the specified color space
 *         is not {@link ColorSpace.Model#RGB RGB}, or if the specified color space's transfer
 *         function is not an {@link ColorSpace.Rgb.TransferParameters ICC parametric curve}
 */
public Bitmap decodeRegion(Rect rect, BitmapFactory.Options options) {
    BitmapFactory.Options.validate(options);
    synchronized (mNativeLock) {
        checkRecycled("decodeRegion called on recycled region decoder");
        if (rect.right <= 0 || rect.bottom <= 0 || rect.left >= getWidth()
                || rect.top >= getHeight())
            throw new IllegalArgumentException("rectangle is outside the image");
        return nativeDecodeRegion(mNativeBitmapRegionDecoder, rect.left, rect.top,
                rect.right - rect.left, rect.bottom - rect.top, options);
    }
}
```

通过 Bitmap.createBitmap 来实现


```java
/**
     * Returns a bitmap from the specified subset of the source
     * bitmap. The new bitmap may be the same object as source, or a copy may
     * have been made. It is initialized with the same density and color space
     * as the original bitmap.
     *
     * @param source   The bitmap we are subsetting
     * @param x        The x coordinate of the first pixel in source
     * @param y        The y coordinate of the first pixel in source
     * @param width    The number of pixels in each row
     * @param height   The number of rows
     * @return A copy of a subset of the source bitmap or the source bitmap itself.
     * @throws IllegalArgumentException if the x, y, width, height values are
     *         outside of the dimensions of the source bitmap, or width is <= 0,
     *         or height is <= 0
     */
    @NonNull
    public static Bitmap createBitmap(@NonNull Bitmap source, int x, int y, int width, int height) {
        return createBitmap(source, x, y, width, height, null, false);
    }
 ```