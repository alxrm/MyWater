package com.rm.mywater.util;

import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by alex on 01/05/15.
 */
public final class ViewHelper {

    public static void clear(View v) {
        ViewCompat.setAlpha(v, 0);
        ViewCompat.setScaleY(v, 1);
        ViewCompat.setScaleX(v, 0);
        ViewCompat.setTranslationY(v, 0);
        ViewCompat.setTranslationX(v, 0);
        ViewCompat.setRotation(v, 0);
        ViewCompat.setRotationY(v, 0);
        ViewCompat.setRotationX(v, 0);
        // @TODO https://code.google.com/p/android/issues/detail?id=80863
//        ViewCompat.setPivotY(v, v.getMeasuredHeight() / 2);
        v.setPivotY(v.getMeasuredHeight() / 2);
        ViewCompat.setPivotX(v, 0);
        ViewCompat.animate(v).setInterpolator(null);
    }
}
