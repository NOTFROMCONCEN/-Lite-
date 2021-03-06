package com.etang.lite_nt_launcher.launcher;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import com.etang.lite_nt_launcher.R;
import com.etang.lite_nt_launcher.launcher.diary.DiaryActivity;
import com.etang.lite_nt_launcher.launcher.settings.SettingActivity;
import com.etang.lite_nt_launcher.launcher.settings.weather.WeatherActivity;
import com.etang.lite_nt_launcher.tool.dialog.DeBugDialog;
import com.etang.lite_nt_launcher.tool.dialog.NameSettingDialog;
import com.etang.lite_nt_launcher.tool.dialog.UnInstallDialog;
import com.etang.lite_nt_launcher.tool.savearrayutil.SaveArrayListUtil;
import com.etang.lite_nt_launcher.tool.server.AppInstallServer;
import com.etang.lite_nt_launcher.tool.sql.MyDataBaseHelper;
import com.etang.lite_nt_launcher.tool.toast.DiyToast;
import com.etang.lite_nt_launcher.util.AppInfo;
import com.etang.lite_nt_launcher.util.DeskTopGridViewBaseAdapter;
import com.etang.lite_nt_launcher.util.GetApps;
import com.etang.lite_nt_launcher.util.StreamTool;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 * @year 2019
 * @Todo TODO 桌面主页
 * @package_name com.example.dklauncherdemo
 * @project_name DKLauncherDemo
 * @file_name MainActivity.java
 * @我的博客 https://naiyouhuameitang.club/
 */
public class MainActivity extends Activity implements OnClickListener {
    private BroadcastReceiver batteryLevelRcvr;
    private IntentFilter batteryLevelFilter;
    private Handler handler;
    private Runnable runnable;
    private MyDataBaseHelper dbHelper_name_sql;
    private SQLiteDatabase db;
    public static ImageView iv_setting_button;
    public static TextView tv_user_id, tv_time_hour, tv_time_min,
            tv_main_batterystate, tv_city, tv_wind, tv_temp_state,
            tv_last_updatetime, tv_date;
    public static View view_buttom;
    public static LinearLayout line_wather;
    public static String string_app_info = "";
    public static GridView mListView;
    public static List<AppInfo> appInfos = new ArrayList<AppInfo>();
    private AppInstallServer appinstallserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.
                FLAG_KEEP_SCREEN_ON);//应用运行时，保持屏幕高亮，不锁屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 无Title
        setContentView(R.layout.activity_main);
        //绑定各类
        initView();// 绑定控件
        new_time_Thread();// 启用更新时间进程
        rember_name(MainActivity.this, db);// 记住昵称
        initAppList(this);// 获取应用列表
        monitorBatteryState();// 监听电池信息
        mListView.setNumColumns(GridView.AUTO_FIT);
        /**
         * 更新天气信息
         */
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        update_wathers(sharedPreferences);
        /**
         * 判断是不是第一次使用
         */
        if (isFirstStart(MainActivity.this)) {//第一次
            /**
             * 填充预设数据
             */
            SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
            editor.putString("images_info", "applist");
            editor.putString("images_app_listifo", "true");
            editor.putString("appname_state", "nope");
            editor.putString("applist_number", "5");
            editor.putString("setting_ico_hind", "false");//隐藏底栏
            /**
             * 设定文本大小预填充
             */
            editor.putString("timetext_min_size", "50");//一言文本大小
            editor.putString("timetext_hour_size", "70");//一言文本大小
            editor.putString("nametext_size", "16");//昵称文本大小
            editor.putString("dianchitext_size", "16");//电池文本大小
            editor.putString("datetext_size", "16");//日期文本大小
            editor.apply();//应用
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add("frist");
            SaveArrayListUtil.saveArrayList(MainActivity.this, arrayList, "start");//存储在本地
            initAppList(MainActivity.this);
            /**
             * 申请存储权限
             */
            DiyToast.showToast(MainActivity.this, "如果您不需要自定义图标功能，存储权限可以拒绝");
            checkPermission();//存取权限
        }
        get_applist_number();//获取设定的应用列表列数
        check_text_size(MainActivity.this);//获取文本大小
        check_view_hind(MainActivity.this, sharedPreferences);
        tv_time_hour.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                return true;
            }
        });
        // 长按弹出APP信息
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                try {
                    string_app_info = appInfos.get(position).getPackageName();
                    UnInstallDialog.uninstall_app(MainActivity.this, MainActivity.this, string_app_info,appInfos.get(position).getName());
//                    Intent intent = new Intent(MainActivity.this, UnInstallActivity.class);
//                    startActivity(intent);
                } catch (Exception e) {
                    DeBugDialog.debug_show_dialog(MainActivity.this, e.toString());
                }
                return true;
            }
        });
        // 当点击GridView时，获取ID和应用包名并启动
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                try {
                    // Intent intent=appInfos.get(position).getIntent();
                    // startActivity(intent);
                    Intent intent = getPackageManager().getLaunchIntentForPackage(
                            appInfos.get(position).getPackageName());
                    if (intent != null) {//点击的APP无异常
                        intent.putExtra("type", "110");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else if (appInfos.get(position).getPackageName().equals(getPackageName() + ".diary")) {//点击了“日记”
                        intent = new Intent(MainActivity.this, DiaryActivity.class);
                        startActivity(intent);
                    } else if (appInfos.get(position).getPackageName().equals(getPackageName() + ".weather")) {//点击了“天气”
                        intent = new Intent(MainActivity.this, WeatherActivity.class);
                        startActivity(intent);
                    } else {//出现异常
                        DeBugDialog.debug_show_dialog(MainActivity.this, "启动APP时出现“Intent”为空的情况");
                    }
                } catch (Exception e) {
                    DeBugDialog.debug_show_dialog(MainActivity.this, e.toString());
                }
            }
        });
        //长按弹出昵称设置
        tv_user_id.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                NameSettingDialog.show_name_dialog(MainActivity.this, db);
                return true;
            }
        });
        /**
         * 开启常驻通知
         */
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setNotification();
            }
        }, 2000);
    }

    public static void check_view_hind(Context context, SharedPreferences sharedPreferences) {
        String ico_info = sharedPreferences.getString("setting_ico_hind", null);
        try {
            if (ico_info.equals("true")) {
                MainActivity.iv_setting_button.setVisibility(View.GONE);
                MainActivity.tv_main_batterystate.setVisibility(View.GONE);
                MainActivity.view_buttom.setVisibility(View.INVISIBLE);
            } else {
                MainActivity.iv_setting_button.setVisibility(View.VISIBLE);
                MainActivity.tv_main_batterystate.setVisibility(View.VISIBLE);
                MainActivity.view_buttom.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            DiyToast.showToast(context, "出现错误，设置参数已重置");
            SharedPreferences.Editor editor = context.getSharedPreferences("info", context.MODE_PRIVATE).edit();
            editor.putString("setting_ico_hind", "false");//日期文本大小
            editor.apply();
        }
    }

    private void get_applist_number() {
        try {
            SharedPreferences sharedPreferences;
            sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
            String applist_number = sharedPreferences.getString("applist_number", null);
            if (applist_number.equals("auto")) {
                mListView.setNumColumns(GridView.AUTO_FIT);
            }
            if (!applist_number.equals("auto")) {
                mListView.setNumColumns(Integer.valueOf(applist_number));
            }
        } catch (Exception e) {
            SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
            editor.putString("applist_number", "auto");
            editor.apply();
            mListView.setNumColumns(GridView.AUTO_FIT);
        }
    }

    public boolean isFirstStart(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                "SHARE_APP_TAG", 0);
        Boolean isFirst = preferences.getBoolean("FIRSTStart", true);
        if (isFirst) {// 第一次
            preferences.edit().putBoolean("FIRSTStart", false).commit();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Cursor cursor = db.rawQuery("select * from wather_city", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            update_wather(MainActivity.this,
                    cursor.getString(cursor.getColumnIndex("city")));
        }
        /**
         * 更新天气信息
         */
        SharedPreferences sharedPreferences;
        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
        update_wathers(sharedPreferences);
        /**
         * 更新文本大小数据
         */
        check_text_size(MainActivity.this);//获取文本大小
    }

    /**
     * +获取应用列表、隐藏应用
     *
     * @param context
     */
    public static void initAppList(Context context) {
        try {
            appInfos = GetApps.GetAppList1(context);
            ArrayList<String> hind_apparrayList = new ArrayList<String>();
            hind_apparrayList.clear();
            hind_apparrayList = SaveArrayListUtil.getSearchArrayList(context);
            for (int j = 0; j < hind_apparrayList.size(); j++) {
                for (int i = 0; i < appInfos.size(); i++) {
                    if (hind_apparrayList.get(j).equals(appInfos.get(i).getPackageName())) {
                        Log.e("APPDATA-------", appInfos.get(i).getPackageName());
                        appInfos.remove(i);
                        continue;
                    }
                }
            }
            DeskTopGridViewBaseAdapter deskTopGridViewBaseAdapter = new DeskTopGridViewBaseAdapter(appInfos,
                    context);
            mListView.setAdapter(deskTopGridViewBaseAdapter);
        } catch (Exception e) {
            DeBugDialog.debug_show_dialog(context, e.toString());
        }
    }

    /**
     * 读取昵称
     * <p>
     * SQLite
     */
    public static void rember_name(Context context, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from name", null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            MainActivity.tv_user_id.setText(cursor.getString(cursor
                    .getColumnIndex("username")));
            if (MainActivity.tv_user_id.getText().toString().isEmpty()) {
                MainActivity.tv_user_id.setText("请设置昵称（长按此处）");
            }
        }
    }

    /**
     * 更新时间
     */
    private void new_time_Thread() {
        handler = new Handler();
        runnable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                SimpleDateFormat simpleDateFormat_hour = new SimpleDateFormat(
                        "HH");
                SimpleDateFormat simpleDateFormat_min = new SimpleDateFormat(
                        "mm");
                SimpleDateFormat simpleDateFormat_date = new SimpleDateFormat(
                        "yyyy年MM月dd日");
                tv_date.setText(simpleDateFormat_date.format
                        (new java.util.Date()));//日期
                tv_time_hour.setText(simpleDateFormat_hour
                        .format(new java.util.Date()));//小时
                tv_time_min.setText(simpleDateFormat_min
                        .format(new java.util.Date()));//分钟
                handler.postDelayed(runnable, 1000);
            }
        };
        handler.post(runnable);
    }

    /**
     * 绑定控件
     */
    private void initView() {
        view_buttom = (View) findViewById(R.id.view_buttom);
        mListView = (GridView) findViewById(R.id.mListView);
        iv_setting_button = (ImageView) findViewById(R.id.iv_setting_button);
        tv_time_hour = (TextView) findViewById(R.id.tv_time_hour);
        tv_time_min = (TextView) findViewById(R.id.tv_time_min);
        tv_user_id = (TextView) findViewById(R.id.tv_user_id);
        tv_main_batterystate = (TextView) findViewById(R.id.tv_main_batterystate);
        line_wather = (LinearLayout) findViewById(R.id.line_wather);
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_wind = (TextView) findViewById(R.id.tv_wind);
        tv_temp_state = (TextView) findViewById(R.id.tv_temp_state);
        tv_last_updatetime = (TextView) findViewById(R.id.tv_last_updatetime);
        iv_setting_button.setOnClickListener(this);
        line_wather.setOnClickListener(this);
        dbHelper_name_sql = new MyDataBaseHelper(getApplicationContext(), "info.db",
                null, 2);
        db = dbHelper_name_sql.getWritableDatabase();
        //动态注册
        appinstallserver = new AppInstallServer();
        appinstallserver.register(this);
    }

    private void update_wathers(SharedPreferences sharedPreferences) {
        tv_wind.setText(sharedPreferences.getString("wather_info_wind", null));
        tv_temp_state.setText(sharedPreferences.getString("wather_info_temp", null));
        tv_last_updatetime.setText(sharedPreferences.getString("wather_info_updatetime", null));
        /**
         * 判断设置是不是隐藏天气布局
         */
        check_weather_view(sharedPreferences);
    }

    private void check_weather_view(SharedPreferences sharedPreferences) {
        if (sharedPreferences.getBoolean("isHind_weather", false) == true) {
            line_wather.setVisibility(View.INVISIBLE);
        } else if (sharedPreferences.getBoolean("isHind_weather", false) == false) {
            line_wather.setVisibility(View.VISIBLE);
        } else {
            line_wather.setVisibility(View.VISIBLE);
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String fengxiang = "";
                    String fengli = "";
                    String high = "";
                    String type = "";
                    String low = "";
                    String date = "";
                    JSONArray dataArray = (JSONArray) msg.obj;
                    try {
                        String json_today = dataArray.getString(0);
                        JSONObject jsonObject = dataArray.getJSONObject(0);
                        System.out.println(jsonObject);
                        if (jsonObject != null) {
                            fengxiang = jsonObject.optString("fengxiang");
                            fengli = jsonObject.optString("fengli");
                            high = jsonObject.optString("high");
                            type = jsonObject.optString("type");
                            low = jsonObject.optString("low");
                            date = jsonObject.optString("date");
                        }
                        Cursor cursor = db.rawQuery("select * from wather_city",
                                null);
                        if (cursor.getCount() != 0) {
                            cursor.moveToFirst();
                            tv_city.setText(cursor.getString(cursor
                                    .getColumnIndex("city")) + "  " + type);
                        } else {
                            DiyToast.showToast(getApplicationContext(), "请选择天气城市");
                        }
                        SharedPreferences.Editor editor = getSharedPreferences("info", MODE_PRIVATE).edit();
                        editor.putString("wather_info_wind", fengxiang);
                        editor.putString("wather_info_temp", high + "  " + low);
                        editor.putString("wather_info_updatetime", "于"
                                + tv_time_hour.getText().toString() + ":"
                                + tv_time_min.getText().toString() + "更新");
                        editor.apply();
                        /**
                         * 更新天气信息
                         */
                        SharedPreferences sharedPreferences;
                        sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                        update_wathers(sharedPreferences);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                    break;
                case 1:
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }
    };

    public void update_wather(Context context, final String city) {
        if (TextUtils.isEmpty(city)) {
            DiyToast.showToast(context, "城市错误，不在数据库中");
            return;
        }
        new Thread() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                try {
                    URL url = new URL(
                            "http://wthrcdn.etouch.cn/weather_mini?city="
                                    + URLEncoder.encode(city, "UTF-8"));
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();
                    if (code == 200) {
                        // 连接网络成功
                        InputStream in = conn.getInputStream();
                        String data = StreamTool.decodeStream(in);
                        // 解析json格式的数据
                        JSONObject jsonObj = new JSONObject(data);
                        // 获得desc的值
                        String result = jsonObj.getString("desc");
                        if ("OK".equals(result)) {
                            // 城市有效，返回了需要的数据
                            JSONObject dataObj = jsonObj.getJSONObject("data");
                            JSONArray jsonArray = dataObj
                                    .getJSONArray("forecast");
                            // 通知更新ui
                            Message msg = Message.obtain();
                            msg.obj = jsonArray;
                            msg.what = 0;
                            mHandler.sendMessage(msg);
                        } else {
                            // 城市无效
                            Message msg = Message.obtain();
                            msg.what = 1;
                            mHandler.sendMessage(msg);
                        }
                    }
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                    Log.e("weather", "异常：" + e.toString());
                    Message msg = Message.obtain();
                    msg.what = 2;
                    mHandler.sendMessage(msg);
                }
            }

            ;
        }.start();
    }

    /**
     * 拦截返回键、Home键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Activity被销毁的同时销毁广播
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(batteryLevelRcvr);
        appinstallserver.unregister(this);
    }

    /**
     * 充电状态显示
     * <p>
     * Code Copy from http://blog.sina.com.cn/s/blog_c79c5e3c0102uyun.html
     */
    private void monitorBatteryState() {
        batteryLevelRcvr = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                StringBuilder sb = new StringBuilder();
                int rawlevel = intent.getIntExtra("level", -1);
                int scale = intent.getIntExtra("scale", -1);
                int status = intent.getIntExtra("status", -1);
                int health = intent.getIntExtra("health", -1);
                int level = -1; // percentage, or -1 for unknown
                if (rawlevel >= 0 && scale > 0) {
                    level = (rawlevel * 100) / scale;
                }
                if (BatteryManager.BATTERY_HEALTH_OVERHEAT == health) {
                    sb.append("'s battery feels very hot!");
                } else {
                    if (status == BatteryManager.BATTERY_STATUS_FULL) {//充电完成
                        sb.append(String.valueOf(level) + "% " + " 充电完成 ");
                        tv_main_batterystate.setText(sb.toString());
                    }
                    if (status == BatteryManager.BATTERY_STATUS_CHARGING) {//充电
                        sb.append(String.valueOf(level) + "% " + " 正在充电 ");
                        tv_main_batterystate.setText(sb.toString());
                    }
                    if (status == BatteryManager.BATTERY_STATUS_DISCHARGING) {//放电
                        sb.append(String.valueOf(level) + "% " + " 使用中 ");
                        tv_main_batterystate.setText(sb.toString());
                    }
                    if (status == BatteryManager.BATTERY_STATUS_NOT_CHARGING) {//未在充电
                        sb.append(String.valueOf(level) + "% " + " 使用中 ");
                        tv_main_batterystate.setText(sb.toString());
                    }
                }
                sb.append(' ');
            }
        };
        batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelRcvr, batteryLevelFilter);
    }

    /**
     * 桌面左下角设置 点击事件监听
     */
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.iv_setting_button:
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
                break;
            case R.id.line_wather:
                Cursor cursor = db.rawQuery("select * from wather_city", null);
                if (cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    update_wather(MainActivity.this,
                            cursor.getString(cursor.getColumnIndex("city")));
                    /**
                     * 更新天气信息
                     */
                    SharedPreferences sharedPreferences;
                    sharedPreferences = getSharedPreferences("info", MODE_PRIVATE);
                    update_wathers(sharedPreferences);
                }
                break;
            default:
                break;
        }
    }

    // 添加常驻通知
    private void setNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        int channelId = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {    //Android 8.0以上适配
            NotificationChannel channel = new NotificationChannel(String.valueOf(channelId), "channel_name",
                    NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
            builder = new NotificationCompat.Builder(this, String.valueOf(channelId));
        } else {
            builder = new NotificationCompat.Builder(this);
        }
//        Intent intent = new Intent(this, MainActivity.class);
        Intent intent = new Intent();// 创建Intent对象
        intent.setAction(Intent.ACTION_MAIN);// 设置Intent动作
        intent.addCategory(Intent.CATEGORY_HOME);// 设置Intent种类
//        startActivity(intent);// 将Intent传递给Activity
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentTitle("点击回到桌面")//指定通知栏的标题内容
                .setContentText("后台运行中")//通知的正文内容
                .setWhen(0)//通知创建的时间
                .setAutoCancel(false)//点击通知后，自动取消
                .setStyle(new NotificationCompat.BigTextStyle().bigText(""))
                .setSmallIcon(R.drawable.title_back_alpha)//通知显示的小图标，只能用alpha图层的图片进行设置
                .setPriority(NotificationCompat.PRIORITY_MAX)//通知重要程度
                .setContentIntent(pi)//点击跳转
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        manager.notify(channelId, notification);
    }

    public void checkPermission() {
        boolean isGranted = true;
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false;
            }
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            Log.i("cbs", "isGranted == " + isGranted);
            if (!isGranted) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                                .ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        102);
            }
        }
    }

    /**
     * 检查文本大小设置
     *
     * @param context
     */
    public static void check_text_size(Context context) {
        try {
            SharedPreferences sharedPreferences = context.getSharedPreferences("info", MODE_PRIVATE);
            MainActivity.tv_time_hour.setTextSize(Integer.valueOf(sharedPreferences.getString("timetext_hour_size", null)));
            MainActivity.tv_time_min.setTextSize(Integer.valueOf(sharedPreferences.getString("timetext_min_size", null)));
            MainActivity.tv_user_id.setTextSize(Integer.valueOf(sharedPreferences.getString("nametext_size", null)));
            MainActivity.tv_main_batterystate.setTextSize(Integer.valueOf(sharedPreferences.getString("dianchitext_size", null)));
            MainActivity.tv_date.setTextSize(Integer.valueOf(sharedPreferences.getString("datetext_size", null)));
        } catch (Exception e) {
            SharedPreferences.Editor editor = context.getSharedPreferences("info", MODE_PRIVATE).edit();
            /**
             * 设定文本大小预填充
             */
            editor.putString("timetext_min_size", "50");
            editor.putString("timetext_hour_size", "70");
            editor.putString("nametext_size", "16");//昵称文本大小
            editor.putString("dianchitext_size", "16");//电池文本大小
            editor.putString("datetext_size", "16");//日期文本大小
            editor.apply();
            check_text_size(context);
        }
    }
}