package com.slide.verify;

import android.content.Context;

public class Utils {

    /**
     * dp转px
     * */
    public static int dp2px(Context ctx, float dip) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return (int) (dip * density);
    }
}
