package org.cn.android.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by chenning on 17-9-14.
 */

public class AppUtil {

    public static boolean hasPermission(Context ctx, String permission) {
//        return ctx.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        return ActivityCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasFeature(Context ctx, String feature) {
        return ctx.getPackageManager().hasSystemFeature(feature);
    }

    public static String getCurrentProcessName(Context ctx) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        if (am == null) {
            return "";
        }
        for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
            if (pid == info.pid) {
                return info.processName;
            }
        }
        return null;
    }

    public static ApplicationInfo getApplicationInfo(Context ctx) {
        ApplicationInfo info = null;
        try {
            PackageManager pm = ctx.getPackageManager();
            info = pm.getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return info;
    }

    public static String getVersionName(Context ctx) {
        try {
            return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getVersionCode(Context ctx) {
        try {
            return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static String getDeviceId(Context ctx) {
        String androidId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (!TextUtils.isEmpty(androidId) && !"unknown".equals(androidId) && !"000000000000000".equals(androidId)) {
            return androidId;
        } else if (!TextUtils.isEmpty(Build.SERIAL)) {
            return Build.SERIAL;
        } else {
            if (hasPermission(ctx, Manifest.permission.READ_PHONE_STATE) && hasFeature(ctx, PackageManager.FEATURE_TELEPHONY)) {
                TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
                if (tm != null) {
                    @SuppressLint("MissingPermission")
                    String telephonyId = tm.getDeviceId();
                    if (!TextUtils.isEmpty(telephonyId)) {
                        return telephonyId;
                    }
                }
            }
            return null;
        }
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context ctx) {
        String imei = "000000000000000";
        if (hasPermission(ctx, Manifest.permission.READ_PHONE_STATE) && hasFeature(ctx, PackageManager.FEATURE_TELEPHONY)) {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm != null) {
                imei = tm.getDeviceId();
            }
        }
        return imei;
    }

    public static boolean isInstalled(Context ctx, String packageName) {
        if (ctx == null || TextUtils.isEmpty(packageName)) {
            return false;
        }
        PackageManager pm = ctx.getPackageManager();
        if (pm == null) {
            return false;
        }
        List<PackageInfo> infoList = pm.getInstalledPackages(0);
        if (infoList == null || infoList.isEmpty()) {
            return false;
        }
        for (PackageInfo info : infoList) {
            if (info != null && packageName.equals(info.packageName)) {
                return true;
            }
        }
        return false;
    }
}
