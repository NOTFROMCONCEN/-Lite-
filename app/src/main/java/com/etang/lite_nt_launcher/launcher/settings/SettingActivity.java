package com.etang.lite_nt_launcher.launcher.settings;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etang.lite_nt_launcher.R;
import com.etang.lite_nt_launcher.launcher.MainActivity;
import com.etang.lite_nt_launcher.launcher.settings.desktopsetting.DeskTopSettingActivity;
import com.etang.lite_nt_launcher.launcher.settings.hindapp.HindAppSetting;
import com.etang.lite_nt_launcher.launcher.settings.textsizesetting.TextSizeSetting;
import com.etang.lite_nt_launcher.launcher.settings.weather.WeatherSettingActivity;
import com.etang.lite_nt_launcher.tool.dialog.CheckUpdateDialog;
import com.etang.lite_nt_launcher.tool.dialog.UnInstallDialog;
import com.etang.lite_nt_launcher.tool.toast.DiyToast;

public class SettingActivity extends Activity {

    TextView tv_back_text;
    LinearLayout lv_weather_gone_setting, lv_textsize_setting, lv_applist_setting, lv_hindapp_setting, lv_uninstall_ntlauncherlite;
    private TextView tv_title_text, tv_title_imagetext;
    private CheckBox cb_hind_setting_ico;
    private ImageView iv_title_back, iv_title_imagebutton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.
                FLAG_KEEP_SCREEN_ON);//应用运行时，保持屏幕高亮，不锁屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 无Title
        setContentView(R.layout.activity_setting);
        initView();
        tv_title_text.setText("检查更新");
        tv_back_text.setText("< 桌面");
        iv_title_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //隐藏暂时无用的选项
        if (!Build.BRAND.toString().equals("Allwinner")) {
        }
        //桌面应用列表设置
        lv_applist_setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, DeskTopSettingActivity.class));
            }
        });
        //天气设置
        lv_weather_gone_setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, WeatherSettingActivity.class));
            }
        });
        //文本大小设置
        lv_textsize_setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, TextSizeSetting.class));
            }
        });
        //已隐藏应用管理
        lv_hindapp_setting.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, HindAppSetting.class));
            }
        });
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        String ico_info = sharedPreferences.getString("setting_ico_hind", null);
        try {
            if (ico_info.equals("true")) {
                cb_hind_setting_ico.setChecked(true);
                MainActivity.check_view_hind(SettingActivity.this, sharedPreferences);
            } else {
                cb_hind_setting_ico.setChecked(false);
                MainActivity.check_view_hind(SettingActivity.this, sharedPreferences);
            }
        } catch (Exception e) {
            DiyToast.showToast(SettingActivity.this, "出现错误，设置参数已重置");
            SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
            editor.putString("setting_ico_hind", "false");//日期文本大小
            editor.apply();
        }
        cb_hind_setting_ico.setText("隐藏所有“底栏”内容\n你还可以通过长按时间的“小时”部分打开桌面设置，推荐将桌面设置为“仅显示应用列表”后再隐藏");
        cb_hind_setting_ico.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                    editor.putString("setting_ico_hind", "true");//日期文本大小
                    editor.apply();
                } else {
                    SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                    editor.putString("setting_ico_hind", "false");//日期文本大小
                    editor.apply();
                }
            }
        });
        tv_title_imagetext.setText("检查更新");
        tv_title_imagetext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(SettingActivity.this);
                progressDialog.setMessage("正在加载");
                progressDialog.show();
                CheckUpdateDialog.check_update(SettingActivity.this, progressDialog);
            }
        });
        iv_title_imagebutton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(SettingActivity.this);
                progressDialog.setMessage("正在加载");
                progressDialog.show();
                CheckUpdateDialog.check_update(SettingActivity.this, progressDialog);
            }
        });
        lv_uninstall_ntlauncherlite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                UnInstallDialog.uninstall_app(SettingActivity.this, SettingActivity.this, getPackageName(), "奶糖桌面Lite");
            }
        });
    }

    private void initView() {
        // TODO Auto-generated method stub
        cb_hind_setting_ico = (CheckBox) findViewById(R.id.cb_hind_setting_ico);
        lv_hindapp_setting = (LinearLayout) findViewById(R.id.lv_hindapp_setting);
        lv_uninstall_ntlauncherlite = (LinearLayout) findViewById(R.id.lv_uninstall_ntlauncherlite);
        tv_back_text = (TextView) findViewById(R.id.tv_back_text);
        tv_title_imagetext = (TextView) findViewById(R.id.tv_title_imagetext);
        tv_title_text = (TextView) findViewById(R.id.tv_title_text);
        iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
        iv_title_imagebutton = (ImageView) findViewById(R.id.iv_title_imagebutton);
        lv_textsize_setting = (LinearLayout) findViewById(R.id.lv_textsize_setting);
        lv_applist_setting = (LinearLayout) findViewById(R.id.lv_applist_setting);
        lv_weather_gone_setting = (LinearLayout) findViewById(R.id.lv_weather_gone_setting);
    }
}