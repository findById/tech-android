package org.cn.android.common;

import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by chenning on 17-9-14.
 */

public class Screen {
    static Screen SCREEN = new Screen();
    static boolean initialized = false;
    public int widthPixels;
    public int heightPixels;
    public int barHeight;
    public float scaledDensity;
    public float density;
    public int densityDpi;
    public float xdpi;
    public float ydpi;

//    public int width;
//    public int height;

    public static void init(Context ctx) {
        if (initialized) {
            return;
        }
        DisplayMetrics display = ctx.getResources().getDisplayMetrics();
        SCREEN.widthPixels = display.widthPixels;
        SCREEN.heightPixels = display.heightPixels;
        SCREEN.scaledDensity = display.scaledDensity;
        SCREEN.density = display.density;
        SCREEN.densityDpi = display.densityDpi;
        SCREEN.xdpi = display.xdpi;
        SCREEN.ydpi = display.ydpi;

        SCREEN.barHeight = getInternalDimensionSizeByKey(ctx, "status_bar_height");

//        try {
//            WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
//            SCREEN.height = wm.getDefaultDisplay().getHeight();
//            SCREEN.width = wm.getDefaultDisplay().getWidth();
//        } catch (Throwable ignored) {
//        }

        initialized = true;
    }

    public static void setStatusBarHeight(int height) {
        getInstance().barHeight = height;
    }

    public static int getInternalDimensionSizeByKey(Context ctx, String key) {
        int result = 0;
        int resId = ctx.getResources().getIdentifier(key, "dimen", "android");
        if (resId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resId);
        }
        return result;
    }

    public static Screen getInstance() {
        return SCREEN;
    }
}
