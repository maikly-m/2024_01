package com.example.u.camera.preview;

import androidx.annotation.NonNull;

/**
 *
 */
public class PreSize implements Comparable<PreSize> {
    public final int width;
    public final int height;

    public PreSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Swap width and height.
     *
     * @return a new PreSize with swapped width and height
     */
    public PreSize rotate() {
        //noinspection SuspiciousNameCombination
        return new PreSize(height, width);
    }

    /**
     * Scale by n / d.
     *
     * @param n numerator
     * @param d denominator
     * @return the scaled size
     */
    public PreSize scale(int n, int d) {
        return new PreSize(width * n / d, height * n / d);
    }

    /**
     * Scales the dimensions so that it fits entirely inside the parent.One of width or height will
     * fit exactly. Aspect ratio is preserved.
     *
     * @param into the parent to fit into
     * @return the scaled size
     */
    public PreSize scaleFit(PreSize into) {
        if (width * into.height >= into.width * height) {
            // match width
            return new PreSize(into.width, height * into.width / width);
        } else {
            // match height
            return new PreSize(width * into.height / height, into.height);
        }
    }
    /**
     * Scales the size so that both dimensions will be greater than or equal to the corresponding
     * dimension of the parent. One of width or height will fit exactly. Aspect ratio is preserved.
     *
     * @param into the parent to fit into
     * @return the scaled size
     */
    public PreSize scaleCrop(PreSize into) {
        if (width * into.height <= into.width * height) {
            // match width
            return new PreSize(into.width, height * into.width / width);
        } else {
            // match height
            return new PreSize(width * into.height / height, into.height);
        }
    }

    /**
     * Checks if both dimensions of the other size are at least as large as this size.
     *
     * @param other the size to compare with
     * @return true if this size fits into the other size
     */
    public boolean fitsIn(PreSize other) {
        return width <= other.width && height <= other.height;
    }

    /**
     * Default sort order is ascending by size.
     */
    @Override
    public int compareTo(@NonNull PreSize other) {
        int aPixels = this.height * this.width;
        int bPixels = other.height * other.width;
        if (bPixels < aPixels) {
            return 1;
        }
        if (bPixels > aPixels) {
            return -1;
        }
        return 0;
    }

    public String toString() {
        return width + "x" + height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PreSize preSize = (PreSize) o;

        return width == preSize.width && height == preSize.height;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }
}
