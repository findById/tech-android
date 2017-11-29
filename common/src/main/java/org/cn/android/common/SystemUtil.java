package org.cn.android.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

/**
 * Created by chenning on 17-11-29.
 */

public class SystemUtil {
    private static final String TAG = "system";

    public static String getCpuInfo() {
        String command = "more /proc/cpufreq";

        BufferedReader br = null;
        String result = "";

        ProcessBuilder cmd = new ProcessBuilder(command);
        Process process = null;
        try {
            process = cmd.start();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String temp;
            while ((temp = br.readLine()) != null) {
                result += temp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ignored) {
                }
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    public static String[] getHardwareInfo() {
        String[] information = new String[6];
        String path = "/proc/version";
        String temp;
        FileReader fr = null;
        BufferedReader br = null;
        try { // 内核版本
            fr = new FileReader(path);
            br = new BufferedReader(fr, 8192);
            temp = br.readLine();
            information[0] = temp.split("\\s+")[2];//KernelVersion
        } catch (IOException e) {
            information[0] = "unknown";
        } finally {
            closeQuietly(fr, br);
        }

        try { // cpu信息
            fr = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fr);
            temp = br.readLine();
            information[1] = temp.split(":\\s+", 2)[1];
        } catch (Throwable e) {
            information[1] = "unknown";
        } finally {
            closeQuietly(fr, br);
        }

        try { // 单核cpu主频
            String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            ProcessBuilder cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            temp = br.readLine();
            information[2] = String.format("%.2fGHz", (Integer.parseInt(temp) / 1024. / 1024));
        } catch (Throwable e) {
            information[2] = "unknown";
        } finally {
            closeQuietly(br);
        }

        try { // cpu核心数
//            File dir = new File("/sys/devices/system/cpu/");
//            File[] files = dir.listFiles(new FileFilter() {
//                @Override
//                public boolean accept(File pathname) {
//                    return Pattern.matches("cpu[0-9]", pathname.getName());
//                }
//            });
//            information[3] = String.valueOf(files.length);

            path = "/sys/devices/system/cpu/possible";
            fr = new FileReader(path);
            br = new BufferedReader(fr, 8192);
            temp = br.readLine();
            temp = temp.split("-", 2)[1];
            information[3] = String.valueOf(Integer.parseInt(temp) + 1);
        } catch (Throwable e) {
            information[3] = "1";
        } finally {
            closeQuietly(fr, br);
        }

        try {
            path = "/proc/meminfo";
            fr = new FileReader(path);
            br = new BufferedReader(fr, 8192);
            temp = br.readLine();
            temp = temp.split(":\\s+", 2)[1];
            temp = temp.replaceAll("[a-zA-Z]", "").trim();
            long total = Integer.parseInt(temp);
            information[4] = String.format("%.2fGB", (total / 1024. / 1024));
            temp = br.readLine();
            temp = temp.split(":\\s+", 2)[1];
            temp = temp.replaceAll("[a-zA-Z]", "").trim();
            long active = Integer.parseInt(temp);
            information[5] = String.format("%.2fGB", ((total - active) / 1024. / 1024));
        } catch (Throwable e) {
            information[4] = "unknown";
            information[5] = "unknown";
        } finally {
            closeQuietly(fr);
        }
        return information;
    }

    public static String[] getSystemInfo() {
        String[] information = new String[6];
        String path = "/proc/version";
        String temp;
        FileReader fr = null;
        BufferedReader br = null;
        try { // 内核版本
            fr = new FileReader(path);
            br = new BufferedReader(fr, 8192);
            temp = br.readLine();
            information[0] = temp.split("\\s+")[2];//KernelVersion
        } catch (IOException e) {
            information[0] = "unknown";
        } finally {
            closeQuietly(fr, br);
        }

        try { // cpu信息
            fr = new FileReader("/proc/cpuinfo");
            br = new BufferedReader(fr);
            temp = br.readLine();
            information[1] = temp.split(":\\s+", 2)[1];
        } catch (Throwable e) {
            information[1] = "unknown";
        } finally {
            closeQuietly(fr, br);
        }

        try { // 单核cpu主频
            String[] args = {"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"};
            ProcessBuilder cmd = new ProcessBuilder(args);
            Process process = cmd.start();
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            temp = br.readLine();
            information[2] = String.format("%.2fGHz", (Integer.parseInt(temp) / 1024. / 1024));
        } catch (Throwable e) {
            information[2] = "unknown";
        } finally {
            closeQuietly(br);
        }

        try { // cpu核心数
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return Pattern.matches("cpu[0-9]", pathname.getName());
                }
            });
            information[3] = String.valueOf(files.length);
        } catch (Throwable e) {
            information[3] = "1";
        } finally {
        }

        try {
            path = "/proc/meminfo";
            fr = new FileReader(path);
            br = new BufferedReader(fr, 8192);
            temp = br.readLine();
            temp = temp.split(":\\s+", 2)[1];
            temp = temp.replaceAll("[a-zA-Z]", "").trim();
            information[4] = String.format("%.2fGB", (Integer.valueOf(temp) / 1024. / 1024));
        } catch (Throwable e) {
            information[4] = "unknown";
        } finally {
            closeQuietly(fr);
        }
        // 是否ROOT
        information[5] = String.valueOf((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists()));


        return information;
    }

    public static void getBatteryInfo(final Context ctx, final OnResultListener listener) {
        ctx.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {

                    int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                    String technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);

                    if (listener != null) {
                        listener.onResult(new String[]{level + "%", technology});
                    }
                    context.unregisterReceiver(this);
                }
            }
        }, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Deprecated
    public static String[] getNetworkInfo(Context ctx) {
        String[] result = new String[5];
        String type = "unknown";
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            result[0] = "unknown";
            result[1] = type;
            return result;
        }
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null || !info.isConnected()) {
            result[0] = "unconnected";
        } else {
            result[0] = "connected";
            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                type = "WIFI" + wifiInfo.getSSID();
            } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                String subType = info.getSubtypeName();
                int networkType = info.getSubtype();
                switch (networkType) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
                        type = "2G " + info.getSubtypeName();
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
                        type = "3G " + info.getSubtypeName();
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
                        type = "4G " + info.getSubtypeName();
                        break;
                    default:
                        // http://baike.baidu.com/item/TD-SCDMA 中国移动 联通 电信 三种3G制式
                        if ("TD-SCDMA".equalsIgnoreCase(subType) || "WCDMA".equalsIgnoreCase(subType) || "CDMA2000".equalsIgnoreCase(subType)) {
                            type = "3G " + info.getSubtypeName();
                        } else {
                            type = subType;
                        }
                        break;
                }

                result[2] = info.getExtraInfo(); // cmnet
                result[3] = info.getTypeName(); // mobile
            }
        }
        result[1] = type;
        return result;
    }

    public interface OnResultListener {
        void onResult(String[] results);
    }

    public static void closeQuietly(Closeable... closeables) {
        if (closeables != null && closeables.length > 0) {
            for (Closeable c : closeables) {
                if (c != null) {
                    try {
                        c.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        }
    }
}
