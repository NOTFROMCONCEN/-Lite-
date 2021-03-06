package com.etang.lite_nt_launcher.tool.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.etang.lite_nt_launcher.R;
import com.etang.lite_nt_launcher.launcher.MainActivity;
import com.etang.lite_nt_launcher.tool.savearrayutil.SaveArrayImageUtil;
import com.etang.lite_nt_launcher.tool.savearrayutil.SaveArrayListUtil;
import com.etang.lite_nt_launcher.tool.toast.DiyToast;

import java.util.ArrayList;

public class UnInstallDialog {
    public static void uninstall_app(final Context context, final Activity activity, final String pakename, final String app_name) {
        ArrayList<String> s = SaveArrayImageUtil.getSearchArrayList(context);
        Log.e("ArrayList", s.toString());
        try {
            final AlertDialog builder = new AlertDialog.Builder(context).create();
//            builder.setTitle("包名：" + "\n" + pakename);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_uninstall, null);
            builder.setView(view);
            builder.setTitle(app_name);
            TextView tv_uninstall_appinfo = (TextView) view.findViewById(R.id.tv_uninstall_appinfo);
            TextView tv_uninstall_packname = (TextView) view.findViewById(R.id.tv_uninstall_packname);
            Button btn_uninstall = (Button) view.findViewById(R.id.btn_uninstall_con);
            Button btn_cls = (Button) view.findViewById(R.id.btn_uninstall_cls);
            Button btn_hind = (Button) view.findViewById(R.id.btn_hind_con);
            Button btn_ico = (Button) view.findViewById(R.id.btn_load_ico);
            tv_uninstall_packname.setText(pakename);
            tv_uninstall_appinfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                        intent.setData(Uri.parse("package:" + pakename));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        String s = Build.BRAND;
                        if (s.equals("Allwinner")) {
                            DeBugDialog.debug_show_dialog(context, "此功能不适用多看电纸书，请到酷安下载多看版");
                        } else {
                            DeBugDialog.debug_show_dialog(context, e.toString());
                        }
                    }
                }
            });
            btn_hind.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        ArrayList<String> arrayList = new ArrayList<String>();
                        arrayList.clear();
                        arrayList = SaveArrayListUtil.getSearchArrayList(context);
                        arrayList.add(MainActivity.string_app_info);
                        Log.e("UnInstall_arrayList", arrayList.toString());
                        SaveArrayListUtil.saveArrayList(context, arrayList, "start");//存储在本地
                        builder.dismiss();
                        MainActivity.initAppList(context);
                    } catch (Exception e) {
                        DeBugDialog.debug_show_dialog(context, e.toString());
                    }
                }
            });
            btn_ico.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_ico_dialog(context);
                    builder.dismiss();
                }
            });
            btn_uninstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UninstallApk(context, activity, pakename);
                    builder.dismiss();
                }
            });
            btn_cls.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.dismiss();
                }
            });
            builder.show();
        } catch (Exception e) {
            DeBugDialog.debug_show_dialog(context, e.toString());
        }
    }

    /**
     * 唤起系统的卸载apk功能
     */
    private static void UninstallApk(Context context, Activity activity, String pakename) {
        try {
            Uri packageURI = Uri.parse("package:" + pakename);
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
            activity.startActivityForResult(uninstallIntent, 1);
        } catch (Exception e) {
            DeBugDialog.debug_show_dialog(context, e.toString());
        }
    }


    private static void show_ico_dialog(final Context context) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_load_ico, null, false);
            builder.setView(view);
            final EditText et_load_ico_uri = (EditText) view.findViewById(R.id.et_load_ico_uri);
            builder.setTitle("请设置图标名称");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (et_load_ico_uri.getText().toString().isEmpty()) {
                        show_ico_dialog(context);
                        DiyToast.showToast(context, "请输入文件名");
                    } else {
                        ArrayList<String> arrayList = new ArrayList<>();
                        arrayList = SaveArrayImageUtil.getSearchArrayList(context);
                        arrayList.add(MainActivity.string_app_info + "-" + et_load_ico_uri.getText().toString());
                        SaveArrayImageUtil.saveArrayList(context, arrayList, "1");
                        MainActivity.initAppList(context);
                    }
                }
            });
            builder.setNeutralButton("重置图标", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList = SaveArrayImageUtil.getSearchArrayList(context);
                    for (int i = 1; i < arrayList.size(); i++) {
                        String str = arrayList.get(i);
                        String[] all = str.split("-");
                        Log.e("TAG", all[0]);
                        if (MainActivity.string_app_info.equals(all[0])) {
                            arrayList.remove(i);
                            continue;
                        }
                    }
                    SaveArrayImageUtil.saveArrayList(context, arrayList, "1");
                    MainActivity.initAppList(context);
                }
            });
            builder.setNegativeButton("取消", null);
            builder.show();
        } catch (Exception e) {
            DeBugDialog.debug_show_dialog(context, e.toString());
        }
    }
}
