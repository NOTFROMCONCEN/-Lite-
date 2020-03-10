package com.etang.lite_nt_launcher.launcher.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.etang.lite_nt_launcher.R;
import com.etang.lite_nt_launcher.launcher.settings.desktopsetting.DeskTopSettingActivity;
import com.etang.lite_nt_launcher.launcher.settings.hindapp.HindAppSetting;
import com.etang.lite_nt_launcher.launcher.settings.textsizesetting.TextSizeSetting;
import com.etang.lite_nt_launcher.launcher.settings.wather.WeatherSettingActivity;

public class SettingActivity extends Activity {

    TextView tv_back_text;
    LinearLayout lv_weather_gone_setting, lv_textsize_setting, lv_applist_setting, lv_hindapp_setting;
    private TextView tv_title_text;
    private ImageView iv_title_back;


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
        tv_title_text.setText("桌面设置（v1.5）");
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
    }

    private void initView() {
        // TODO Auto-generated method stub
        lv_hindapp_setting = (LinearLayout) findViewById(R.id.lv_hindapp_setting);
        tv_back_text = (TextView) findViewById(R.id.tv_back_text);
        tv_title_text = (TextView) findViewById(R.id.tv_title_text);
        iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
        lv_textsize_setting = (LinearLayout) findViewById(R.id.lv_textsize_setting);
        lv_applist_setting = (LinearLayout) findViewById(R.id.lv_applist_setting);
        lv_weather_gone_setting = (LinearLayout) findViewById(R.id.lv_weather_gone_setting);
    }
}