package com.etang.lite_nt_launcher.tool.dialog;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.etang.lite_nt_launcher.BuildConfig;
import com.etang.lite_nt_launcher.tool.toast.DiyToast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;


public class CheckUpdateDialog {
    public static void check_update(final Context context, final ProgressDialog progressDialog) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String val = data.getString("weblink_state");
                switch (val) {
                    case "1":
                        DiyToast.showToast(context, "正在连接，请稍后");
                        break;
                    case "2":
                        DiyToast.showToast(context, "出现错误，请重试！");
                        Bundle data_error = msg.getData();
                        String error_message = data_error.getString("error_message");
                        DeBugDialog.debug_show_dialog(context, error_message);
                        progressDialog.dismiss();
                        break;
                    case "3":
                        DiyToast.showToast(context, "连接成功，解析中");
                        progressDialog.setMessage("连接成功，解析中");
                        break;
                    case "4":
                        DiyToast.showToast(context, "");
                        progressDialog.setMessage("");
                        break;
                    case "5":
                        DiyToast.showToast(context, "加载完成");
                        progressDialog.dismiss();
                        Bundle data_version = msg.getData();
                        String version_message = data_version.getString("version_message");
                        String new_version_message = version_message.replace("<li>", ""); //得到新的字符串
                        new_version_message = new_version_message.replace("</li>", "");
                        version_update(context, new_version_message);
                        break;
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg_start = new Message();
                Bundle data = new Bundle();
                data.putString("weblink_state", "1");
                msg_start.setData(data);
                handler.sendMessage(msg_start);
                try {
                    Log.e("TAG111111111111111", "开始链接");
                    Document doc = Jsoup.connect(" https://naiyouhuameitang.club/index.php/24.html").get();
                    /**
                     * 连接成功
                     * */
                    Message msg_link_ok = new Message();
                    Bundle data_link_ok = new Bundle();
                    data_link_ok.putString("weblink_state", "3");
                    msg_link_ok.setData(data_link_ok);
                    handler.sendMessage(msg_link_ok);
                    /**
                     * 开始解析
                     * */
                    final Elements titleAndPic = doc.select("div.post-body");
                    Log.e("HTML", String.valueOf(titleAndPic.get(0).select("ul").select("li")));
                    /**
                     * 发送结果
                     * */
                    Message msg_version = new Message();
                    Bundle data_version = new Bundle();
                    data_version.putString("weblink_state", "5");
                    data_version.putString("version_message", String.valueOf(titleAndPic.get(0).select("ul").select("li")));
                    msg_version.setData(data_version);
                    handler.sendMessage(msg_version);
                } catch (Exception e) {
                    Log.e("TAG111111111111111", e.toString());
                    Message msg_link_error = new Message();
                    Bundle data_error = new Bundle();
                    data_error.putString("weblink_state", "2");
                    data_error.putString("error_message", e.toString());
                    msg_link_error.setData(data_error);
                    handler.sendMessage(msg_link_error);
                }
            }
        }).start();
    }

    private static void version_update(final Context context, String s) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        String userId = "21321321?u=2132132132";
//        String userIdJiequ = userId.substring(userId.indexOf("?u="));
        String version_web_1 = s.substring(s.indexOf("(com.etang.nt_launcher)"));
        Log.e("version1", version_web_1);
        String version_web_2 = version_web_1.substring(0, version_web_1.indexOf("(" + context.getPackageName() + ")"));
        Log.e("version2", version_web_2);
        String version_web_3 = version_web_2.replace("(com.moan.nt_launcher_dk)\n", ""); //得到新的字符串
        Log.e("version3", version_web_3);
        String version_web_4 = version_web_3.replace("(com.etang.nt_launcher)\n", ""); //得到新的字符串
        Log.e("version4", version_web_4);
        String version = BuildConfig.VERSION_NAME;
        if (!version.equals(version_web_4)) {
            builder.setMessage("当前版本：" + "\n" + version + "\n" + "现有版本：" + "\n" + version_web_4 + "\n" + "有更新，请到“酷安”进行更新，或登录博客在电脑端或者浏览器内下载后自行安装，软件内更新功能即将上线");
            DiyToast.showToast(context, "有更新，请到“酷安”进行更新，或登录博客在电脑端或者浏览器内下载后自行安装，软件内更新功能即将上线");
        } else {
            builder.setMessage("当前版本：" + "\n" + version + "\n" + "现有版本：" + "\n" + version_web_4 + "\n" + "你已经是最新版本了");
            DiyToast.showToast(context, "你已经是最新版本了");
        }
        builder.setPositiveButton("博客地址", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                DiyToast.showToast(context, "https://naiyouhuameitang.club/nt_launcher.html");
                web_html(context);
            }
        });
        builder.setNeutralButton("关闭", null);
        builder.show();
    }

    private static void web_html(Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("在浏览器内输入：\n https://naiyouhuameitang.club/nt_launcher.html");
        builder.setPositiveButton("关闭", null);
        builder.show();
    }
}