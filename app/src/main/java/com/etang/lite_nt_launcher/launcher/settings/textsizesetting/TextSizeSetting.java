package com.etang.lite_nt_launcher.launcher.settings.textsizesetting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.etang.lite_nt_launcher.R;
import com.etang.lite_nt_launcher.launcher.MainActivity;
import com.etang.lite_nt_launcher.tool.toast.DiyToast;

public class TextSizeSetting extends AppCompatActivity {
    private TextView tv_title_text;
    private ImageView iv_title_back;
    private Button btn_timetext_hour_size, btn_timetext_min_size, btn_datetext_size, btn_nametext_size, btn_dianchitext_size, btn_textsize_resert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.
                FLAG_KEEP_SCREEN_ON);//应用运行时，保持屏幕高亮，不锁屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 无Title
        setContentView(R.layout.setting_textsize);
        initView();
        tv_title_text.setText("文本设置");
        iv_title_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_timetext_min_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_size_dialog("timetext_min_size");
            }
        });
        btn_timetext_hour_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_size_dialog("timetext_hour_size");
            }
        });
        btn_nametext_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_size_dialog("nametext_size");
            }
        });
        btn_dianchitext_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_size_dialog("dianchitext_size");
            }
        });
        btn_datetext_size.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_size_dialog("datetext_size");
            }
        });
        btn_textsize_resert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                /**
                 * 设定文本大小预填充
                 */
                editor.putString("timetext_min_size", "50");
                editor.putString("timetext_hour_size", "70");
                editor.putString("nametext_size", "16");//昵称文本大小
                editor.putString("dianchitext_size", "16");//电池文本大小
                editor.putString("datetext_size", "16");//日期文本大小
                editor.apply();
                DiyToast.showToast(TextSizeSetting.this, "重置成功");
            }
        });
    }

    private void show_size_dialog(final String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(TextSizeSetting.this);
        View view = LayoutInflater.from(TextSizeSetting.this).inflate(R.layout.dialog_seekbar_and_edittext, null);
        switch (name) {
            case "timetext_min_size":
                builder.setTitle("时间文本大小设置（分钟）");
                break;
            case "timetext_hour_size":
                builder.setTitle("时间文本大小设置（小时）");
                break;
            case "nametext_size":
                builder.setTitle("昵称文本大小设置");
                break;
            case "dianchitext_size":
                builder.setTitle("电池文本大小设置");
                break;
            case "datetext_size":
                builder.setTitle("日期文本大小设置");
                break;
        }
        builder.setView(view);
        final int[] number = {0};
        final SeekBar dialog_seekbar = (SeekBar) view.findViewById(R.id.dialog_seekbar);
        final TextView dialog_textview = (TextView) view.findViewById(R.id.dialog_textview);
        dialog_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                dialog_textview.setText(String.valueOf(seekBar.getProgress()));
                number[0] = seekBar.getProgress();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                editor.putString(name, String.valueOf(number[0]));
                editor.apply();
                MainActivity.check_text_size(TextSizeSetting.this);
            }
        });
        builder.setNeutralButton("关闭", null);
        builder.show();
    }

    private void initView() {
        // TODO Auto-generated method stub
        btn_textsize_resert = (Button) findViewById(R.id.btn_textsize_resert);
        btn_timetext_min_size = (Button) findViewById(R.id.btn_timetext_min_size);
        btn_timetext_hour_size = (Button) findViewById(R.id.btn_timetext_hour_size);
        btn_dianchitext_size = (Button) findViewById(R.id.btn_dianchitext_size);
        btn_nametext_size = (Button) findViewById(R.id.btn_nametext_size);
        btn_datetext_size = (Button) findViewById(R.id.btn_datetext_size);
        tv_title_text = (TextView) findViewById(R.id.tv_title_text);
        iv_title_back = (ImageView) findViewById(R.id.iv_title_back);
    }
}