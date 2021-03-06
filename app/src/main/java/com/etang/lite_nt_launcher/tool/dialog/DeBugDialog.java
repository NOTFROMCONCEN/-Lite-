package com.etang.lite_nt_launcher.tool.dialog;

import android.app.AlertDialog;
import android.content.Context;

public class DeBugDialog {
    public static void debug_show_dialog(Context context, String e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("出现错误");
        builder.setMessage("请截图或者拍照反馈在软件评论区并@开发者" + "\n" + "错误信息：" + "\n" + e.toString());
        builder.setNeutralButton("关闭", null);
        builder.show();
    }
}
