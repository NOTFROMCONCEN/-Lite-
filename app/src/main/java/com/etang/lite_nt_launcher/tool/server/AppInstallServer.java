package com.etang.lite_nt_launcher.tool.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.etang.lite_nt_launcher.launcher.MainActivity;
import com.etang.lite_nt_launcher.tool.toast.DiyToast;

public class AppInstallServer extends BroadcastReceiver {

    /**
     * ACTION_PACKAGE_ADDED 一个新应用包已经安装在设备上，数据包括包名（最新安装的包程序不能接收到这个广播）
     * ACTION_PACKAGE_REPLACED 一个新版本的应用安装到设备，替换之前已经存在的版本
     * ACTION_PACKAGE_CHANGED 一个已存在的应用程序包已经改变，包括包名
     * ACTION_PACKAGE_REMOVED 一个已存在的应用程序包已经从设备上移除，包括包名（正在被安装的包程序不能接收到这个广播）
     * ACTION_PACKAGE_RESTARTED 用户重新开始一个包，包的所有进程将被杀死，所有与其联系的运行时间状态应该被移除，包括包名（重新开始包程序不能接收到这个广播）
     * ACTION_PACKAGE_DATA_CLEARED 用户已经清楚一个包的数据，包括包名（清除包程序不能接收到这个广播）
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        //接收安装广播
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            DiyToast.showToast(context, "安装了包名为：" + packageName + "的应用");
            Log.e("ETANG_APP", "install app packageName = " + packageName);
            MainActivity.initAppList(context);
        }
        //接收卸载广播
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.e("ETANG_APP", "uninstall app packageName = " + packageName);
            boolean isReplace = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
            Log.e("ETANG_APP", "isReplace = " + isReplace);
            DiyToast.showToast(context, "卸载了包名为：" + packageName + "的应用");
            MainActivity.initAppList(context);
        }
        //接收升级更新广播
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            Log.e("ETANG_APP", "upgrade app packageName = " + packageName);
            DiyToast.showToast(context, "更新了包名为：" + packageName + "的应用");
            MainActivity.initAppList(context);
        }
    }

    public IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addDataScheme("package");
        return intentFilter;
    }

    public void register(Context context) {
        context.registerReceiver(this, getIntentFilter());
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}