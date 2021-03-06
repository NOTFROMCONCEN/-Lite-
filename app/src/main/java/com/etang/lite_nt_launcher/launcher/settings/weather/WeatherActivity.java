package com.etang.lite_nt_launcher.launcher.settings.weather;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.etang.lite_nt_launcher.R;
import com.etang.lite_nt_launcher.tool.sql.MyDataBaseHelper;
import com.etang.lite_nt_launcher.tool.toast.DiyToast;


public class WeatherActivity extends Activity {
    private Button btn_wather_con, btn_wather_cls;
    private EditText et_city_get;
    private MyDataBaseHelper dbHelper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting_wather);
        initView();
        btn_wather_con.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (et_city_get.getText().toString().isEmpty()) {
                    DiyToast.showToast(getApplicationContext(), "请输入城市");
                } else {
                    db.execSQL("update wather_city set city = ? ",
                            new String[]{et_city_get.getText().toString()});
                    finish();
                }
            }
        });
        btn_wather_cls.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        // TODO Auto-generated method stub
        btn_wather_con = (Button) findViewById(R.id.btn_wather_con);
        et_city_get = (EditText) findViewById(R.id.et_city_get);
        btn_wather_cls = (Button) findViewById(R.id.btn_wather_cls);
        dbHelper = new MyDataBaseHelper(getApplicationContext(), "info.db",
                null, 2);
        db = dbHelper.getWritableDatabase();
    }
}
