package com.etang.lite_nt_launcher.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.etang.lite_nt_launcher.R;
import com.etang.lite_nt_launcher.launcher.diary.DiaryActivity;

import java.util.ArrayList;
import java.util.List;


public class GetApps {
    public static List<AppInfo> GetAppList1(Context context) {
        List<AppInfo> list = new ArrayList<AppInfo>();
        PackageManager pm = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> activities = pm.queryIntentActivities(mainIntent, 0);
        for (ResolveInfo info : activities) {
            String packName = info.activityInfo.packageName;
            if (packName.equals(context.getPackageName())) {
                continue;
            }
            AppInfo mInfo = new AppInfo();
            mInfo.setIco(drawableToBitmap(info.activityInfo.applicationInfo.loadIcon(pm)));
            mInfo.setName(info.activityInfo.applicationInfo.loadLabel(pm)
                    .toString());
            mInfo.setPackageName(packName);
            Intent launchIntent = new Intent();
            launchIntent.setComponent(new ComponentName(packName,
                    info.activityInfo.name));
            mInfo.setIntent(launchIntent);
            list.add(mInfo);
        }
        /**
         * 全部APP添加完成后，添加“日记”
         */
        add_diary(context, list);
        Log.e("GetApps", "装载完成");
        return list;
    }

    private static void add_diary(Context context, List<AppInfo> list) {
        /**
         * 添加“日记”
         */
        AppInfo mInfo_diary = new AppInfo();
        mInfo_diary.setName("日记");
        mInfo_diary.setPackageName(context.getPackageName() + ".diary");
        Resources r_diary = context.getResources();
        Bitmap bmp_diary = BitmapFactory.decodeResource(r_diary, R.drawable.ic_diary);
        mInfo_diary.setIco(Bitmap.createBitmap(bmp_diary));
        list.add(mInfo_diary);
        /**
         * 添加“天气”
         */
        AppInfo mInfo_weather = new AppInfo();
        mInfo_weather.setName("天气");
        mInfo_weather.setPackageName(context.getPackageName() + ".weather");
        Resources r_weather = context.getResources();
        Bitmap bmp_weather = BitmapFactory.decodeResource(r_weather, R.drawable.ic_weather);
        mInfo_weather.setIco(Bitmap.createBitmap(bmp_weather));
        list.add(mInfo_weather);
    }

    /**
     * drawable强转bitmap
     *
     * @param drawable
     * @return
     */
    public static final Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
