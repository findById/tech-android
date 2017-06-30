package org.cn.android.platform;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by chenning on 2017/6/30.
 */

public class PermissionManager {

    private static AtomicInteger mAtomicInteger = new AtomicInteger(0);
    private static HashMap<Integer, PermissionTask> mPermissionMap = new HashMap<>();

    /**
     * Open application settings
     *
     * @param ctx
     */
    public static void startAppSettings(Context ctx) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", ctx.getPackageName(), null);
        intent.setData(uri);
        if (!(ctx instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        ctx.startActivity(intent);
    }

    /**
     * @param ctx
     * @param listener
     * @param permissions
     */
    public static void requestPermissions(Activity ctx, OnPermissionListener listener, String... permissions) {
        if (checkSelfPermission(ctx, permissions).size() > 0) {
            PermissionTask item = new PermissionTask(listener, permissions);
            mPermissionMap.put(item.requestCode, item);
            item.requestPermission(ctx);
            return;
        }
        if (listener != null) {
            boolean[] showRationales = new boolean[permissions.length];
            Arrays.fill(showRationales, false);
            int[] grantResults = new int[permissions.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            listener.onRequestPermissionsResult(true, permissions, grantResults, showRationales);
        }
    }

    /**
     * @param ctx
     * @param listener
     * @param permissions
     */
    public static void requestPermissions(Fragment ctx, OnPermissionListener listener, String... permissions) {
        if (checkSelfPermission(ctx.getActivity(), permissions).size() > 0) {
            PermissionTask item = new PermissionTask(listener, permissions);
            mPermissionMap.put(item.requestCode, item);
            item.requestPermission(ctx);
            return;
        }
        if (listener != null) {
            boolean[] showRationales = new boolean[permissions.length];
            Arrays.fill(showRationales, false);
            int[] grantResults = new int[permissions.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            listener.onRequestPermissionsResult(true, permissions, grantResults, showRationales);
        }
    }

    /**
     * @param ctx
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return
     */
    public static boolean onRequestPermissionsResult(Activity ctx, int requestCode, String[] permissions, int[] grantResults) {
        PermissionTask item = mPermissionMap.get(requestCode);
        if (item == null) {
            return false;
        }
        boolean success = true;
        boolean[] showRationales = new boolean[permissions.length];

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i]; //本次权限
            int grantResult = (i >= grantResults.length) ? PackageManager.PERMISSION_DENIED : grantResults[i]; //当前权限的状态
            boolean showRequestRationale = false; //是否需要解释 授权理由

            if (grantResult != PackageManager.PERMISSION_GRANTED) { //没有授权
                success = false;
                showRequestRationale = !ActivityCompat.shouldShowRequestPermissionRationale(ctx, permission);
            }
            showRationales[i] = showRequestRationale;
        }

        if (item.listener != null) {
            item.listener.onRequestPermissionsResult(success, permissions, grantResults, showRationales);
        }
        mPermissionMap.remove(requestCode);
        return true;
    }

    /**
     * @param ctx
     * @param requestCode
     * @param permissions
     * @param grantResults
     * @return
     */
    public static boolean onRequestPermissionsResult(Fragment ctx, int requestCode, String[] permissions, int[] grantResults) {
        PermissionTask item = mPermissionMap.get(requestCode);
        if (item == null) {
            return false;
        }
        boolean success = true;
        boolean[] showRationales = new boolean[permissions.length];

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i]; //本次权限
            int grantResult = (i >= grantResults.length) ? PackageManager.PERMISSION_DENIED : grantResults[i]; //当前权限的状态
            boolean showRequestRationale = false; //是否需要解释 授权理由

            if (grantResult != PackageManager.PERMISSION_GRANTED) { //没有授权
                success = false;
                showRequestRationale = !ActivityCompat.shouldShowRequestPermissionRationale(ctx.getActivity(), permission);
            }
            showRationales[i] = showRequestRationale;
        }

        if (item.listener != null) {
            item.listener.onRequestPermissionsResult(success, permissions, grantResults, showRationales);
        }
        mPermissionMap.remove(requestCode);
        return true;
    }

    /**
     * @param ctx
     * @param permissions
     * @return
     */
    private static ArrayList<String> checkSelfPermission(Activity ctx, String[] permissions) {
        ArrayList<String> list = new ArrayList<>(3);
        int i = 0;
        int len = permissions.length;
        while (i < len) {
            if (ContextCompat.checkSelfPermission(ctx, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                list.add(permissions[i]);
            }
            i++;
        }
        return list;
    }

    /**
     *
     */
    public interface OnPermissionListener {
        void onRequestPermissionsResult(boolean success, String[] permissions, int[] grantResults, boolean[] showRationale);
    }

    /**
     *
     */
    private static class PermissionTask {
        public int requestCode;
        public String[] permissions;
        public OnPermissionListener listener;

        /**
         * @param listener
         * @param permissions
         */
        public PermissionTask(OnPermissionListener listener, String... permissions) {
            this.listener = listener;
            this.permissions = permissions;
            this.requestCode = mAtomicInteger.getAndIncrement();
        }

        /**
         * @param ctx
         */
        public void requestPermission(Activity ctx) {
            if (permissions == null || permissions.length <= 0) {
                mPermissionMap.remove(requestCode);
                return;
            }
            ActivityCompat.requestPermissions(ctx, permissions, requestCode);
        }

        /**
         * @param ctx
         */
        public void requestPermission(Fragment ctx) {
            if (permissions == null || permissions.length <= 0) {
                mPermissionMap.remove(requestCode);
                return;
            }
            ctx.requestPermissions(permissions, requestCode);
        }
    }
}
