package info.paveway.util;

import info.paveway.kidsalerm.service.KidsAlermService;
import info.paveway.log.Logger;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * サービスユーティリティクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class ServiceUtil {

    /** ロガー */
    private static Logger mLogger = new Logger(ServiceUtil.class);

    /**
     * サービスが起動しているかチェックする。
     *
     * @param context コンテキスト
     * @param serviceName チェックするサービスクラス
     * @return チェック結果 true:起動している。 / false:起動していない。
     */
    public static boolean isServiceRunning(Context context, Class<?> serviceClass) {
        mLogger.d("IN");

        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);

        // 起動しているサービスのリストを取得する。
        List<RunningServiceInfo> services =
                activityManager.getRunningServices(Integer.MAX_VALUE);

        // 起動しているサービス数分繰り返す。
        for (RunningServiceInfo info : services) {
            // 位置サービスの場合
            if (serviceClass.getCanonicalName().equals(info.service.getClassName())) {
                return true;
            }
        }

        mLogger.d("OUT(OK)");
        return false;
    }

    /**
     * サービスを起動する。
     */
    public static boolean startService(Context context) {
        mLogger.d("IN");

        // コンポーネント名
        ComponentName name = null;

        // 位置取得サービスが停止してる場合
        if (!ServiceUtil.isServiceRunning(context, KidsAlermService.class)) {
            // 位置取得サービスを開始する。
            name = context.startService(new Intent(context, KidsAlermService.class));
            mLogger.d("ComponentName=[" + name + "]");
        }

        // 位置取得サービスが開始できた場合
        if (null != name) {
            mLogger.d("OUT(OK)");
            return true;

        // 位置取得サービスが開始できない場合
        } else {
            mLogger.d("OUT(NG)");
            return false;
        }
    }

    /**
     * サービスを停止する。
     */
    public static boolean stopService(Context context) {
        mLogger.d("IN");

        // 監視サービスを停止する。
        boolean result = context.stopService(new Intent(context, KidsAlermService.class));

        mLogger.d("OUT(OK) result=[" + result + "]");
        return result;
    }
}
