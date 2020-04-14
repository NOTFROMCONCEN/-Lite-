package com.etang.lite_nt_launcher.tool.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import com.etang.lite_nt_launcher.R;
import com.etang.lite_nt_launcher.launcher.MainActivity;
import com.etang.lite_nt_launcher.tool.sql.MyDataBaseHelper;

public class NameSettingDialog {
    public static void show_name_dialog(final Context context, final SQLiteDatabase db) {
        final AlertDialog builder = new AlertDialog.Builder(
                context).create();
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_name_show, null, false);
        builder.setView(view);
        Window window = builder.getWindow();
        builder.getWindow();
        window.setGravity(Gravity.CENTER); // 底部位置
        window.setContentView(view);
        final EditText et_name_get = (EditText) view
                .findViewById(R.id.et_title_name);
        final RadioButton ra_0 = (RadioButton) view
                .findViewById(R.id.radio0);
        final RadioButton ra_1 = (RadioButton) view
                .findViewById(R.id.radio1);
        final RadioButton ra_2 = (RadioButton) view
                .findViewById(R.id.radio2);
        final RadioButton ra_3 = (RadioButton) view
                .findViewById(R.id.radio3);
        final Button btn_con = (Button) view.findViewById(R.id.btn_dialog_rename_con);
        final Button btn_cls = (Button) view.findViewById(R.id.btn_dialog_rename_cls);
        btn_cls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        btn_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_name_get.getText().toString().isEmpty()
                        && !ra_0.isChecked() && !ra_2.isChecked()
                        && !ra_3.isChecked() && !ra_1.isChecked()) {
                    db.execSQL("update name set username = ?",
                            new String[]{""});
                } else {
                    if (ra_0.isChecked() || ra_1.isChecked()
                            || ra_2.isChecked() || ra_3.isChecked()) {
                        if (ra_0.isChecked()) {
                            db.execSQL(
                                    "update name set username = ?",
                                    new String[]{ra_0.getText()
                                            .toString() + "多看电纸书"});
                        }
                        if (ra_1.isChecked()) {
                            db.execSQL(
                                    "update name set username = ?",
                                    new String[]{ra_1.getText()
                                            .toString() + "多看电纸书"});
                        }
                        if (ra_2.isChecked()) {
                            db.execSQL(
                                    "update name set username = ?",
                                    new String[]{ra_2.getText()
                                            .toString() + "多看电纸书"});
                        }
                        if (ra_3.isChecked()) {
                            db.execSQL(
                                    "update name set username = ?",
                                    new String[]{ra_3.getText()
                                            .toString() + "多看电纸书"});
                        }
                    } else {
                        db.execSQL("update name set username = ?",
                                new String[]{et_name_get
                                        .getText().toString()});
                    }
                }
                builder.dismiss();
                MainActivity.rember_name(context, db);
            }
        });
        builder.show();
    }

}
