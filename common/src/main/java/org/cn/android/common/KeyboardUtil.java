package org.cn.android.common;

import android.app.Activity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by chenning on 17-9-14.
 */

public class KeyboardUtil {

    public static void show(final Activity ctx, final EditText view) {
        if (ctx == null || view == null) {
            return;
        }
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(view, 0);
                    timer.cancel();
                } catch (Throwable ignored) {
                }
            }
        }, 500);
    }

    public static void hide(Activity ctx) {
        if (ctx == null) {
            return;
        }
        try {
            InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm == null) {
                return;
            }
            if (ctx.getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(ctx.getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Throwable ignored) {
        }
    }

}
