package info.paveway.kidsalerm.service;

import info.paveway.kidsalerm.CommonConstants.Action;
import info.paveway.kidsalerm.CommonConstants.ExtraKey;
import info.paveway.kidsalerm.CommonConstants.PrefsKey;
import info.paveway.kidsalerm.ExclusionPlaceData;
import info.paveway.log.Logger;
import info.paveway.util.MonitorUtil;
import info.paveway.util.StringUtil;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * キッズアラーム
 * サービスクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class KidsAlermService extends Service {

    /** ロガー */
    private Logger mLogger = new Logger(KidsAlermService.class);

    /** 秒 */
    private static final long SEC = 1000;

    /** 分 */
    private static final long MINUTE = 60 * SEC;

    /** 更新間隔デフォルト値(30秒) */
    private static final long DEFAULT_INTERVAL = 30 * SEC;

    /** カウントダウンタイマーインターバル(5分) */
    private static final long COUNT_DOWN_INTERVAL = 5 * MINUTE;

    /** 滞在監視時間(分) */
    private static final String DEFAULT_STAY_TIME = "60";

    /** 距離の小数点以下桁数(2桁) */
    private static final int DISTANCE_DIGIT = 2;

    /** 滞在としない距離(100m) */
    private static final double DISTANCE_IGNORE = 0.1D;

    /** ロケーションクライアント */
    private LocationClient mLocationClient;

    /** ロケーションリクエスト */
    private LocationRequest mLocationRequest;

    /** ロケーションリスナー */
    private LocationListener mLocationListener;

    /** 滞在監視カウントダウンタイマー */
    private StayCountDownTimer mStayCountDownTimer;

    /** 前回ロケーション */
    private Location mPrevLocation;

    /**
     * バインドした時に呼び出される。
     *
     * @param intent インテント
     */
    @Override
    public IBinder onBind(Intent intent) {
        mLogger.d("IN");

        // 使用しない。

        mLogger.d("OUT(OK)");
        return null;
    }

    /**
     * 生成された時に呼び出される。
     */
    @Override
    public void onCreate() {
        mLogger.d("IN");

        // ロケーションリクエストを生成する。
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        long interval = prefs.getLong(PrefsKey.LOCATION_INTERVAL, DEFAULT_INTERVAL);
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(interval * SEC);
        mLocationRequest.setFastestInterval(DEFAULT_INTERVAL * SEC);

        // ロケーションリスナーを生成する。
        mLocationListener = new UserLocationListener();

        // ロケーションクライアントを生成する。
        mLocationClient =
                new LocationClient(
                        KidsAlermService.this,
                        new LocationConnectionCallbacks(),
                        new LocationOnConnectionFailedListener());

        // 接続する。
        mLocationClient.connect();

        mLogger.d("OUT(OK)");
    }

    /**
     * 終了する時に呼び出される。
     */
    @Override
    public void onDestroy() {
        mLogger.d("IN");

        // 滞在監視カウントダウンタイマーを停止する。
        stopStayCountDownTimer();

        // ロケーションクライアントが有効な場合
        if (null != mLocationClient) {
            // 切断する。
            mLocationClient.disconnect();
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * 滞在監視カウントダウンタイマーを再開始する。
     */
    private void restartStayCountDownTimer() {
        mLogger.d("IN");

        // 滞在監視カウントダウンタイマーを停止する。
        stopStayCountDownTimer();

        // 滞在監視時間を取得する。
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        long stayTime = Long.parseLong(prefs.getString(PrefsKey.MONITOR_STAY_TIME, DEFAULT_STAY_TIME));

        // カウントダウンタイマーを開始する。
        mStayCountDownTimer = new StayCountDownTimer(stayTime * MINUTE, COUNT_DOWN_INTERVAL);
        mStayCountDownTimer.start();

        mLogger.d("OUT(OK)");
    }

    /**
     * 滞在監視カウントダウンタイマーを停止する。
     */
    private void stopStayCountDownTimer() {
        mLogger.d("IN");

        // カウントダウンタイマーが有効な場合
        if (null != mStayCountDownTimer) {
            // カウントダウンタイマーを停止する。
            mStayCountDownTimer.cancel();
            mStayCountDownTimer = null;
        }

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /**
     * ロケーション接続コールバッククラス
     *
     */
    private class LocationConnectionCallbacks implements ConnectionCallbacks {

        /** ロガー */
        private Logger mLogger = new Logger(LocationConnectionCallbacks.class);

        /**
         * 接続した時に呼び出される。
         *
         * @param bundle バンドル
         */
        @Override
        public void onConnected(Bundle bundle) {
            mLogger.d("IN");

            // ロケーション更新を開始する。
            startLocationUpdates();

            // 滞在監視カウントダウンタイマーを開始する。
            restartStayCountDownTimer();

            mLogger.d("OUT(OK)");
        }

        /**
         * 切断した時に呼び出される。
         */
        @Override
        public void onDisconnected() {
            mLogger.d("IN");

            // 滞在監視カウントダウンタイマーを停止する。
            stopStayCountDownTimer();

            // ロケーション更新を停止する。
            stopLocationUpdates();

            mLogger.d("OUT(OK)");
        }

        /**
         * ロケーション更新を開始する。
         */
        private void startLocationUpdates() {
            mLogger.d("IN");

            // 接続済みの場合
            if (mLocationClient.isConnected()) {
                // ロケーション更新を要求する。
                mLocationClient.requestLocationUpdates(mLocationRequest, mLocationListener);
            }

            mLogger.d("OUT(OK)");
        }

        /**
         * ロケーション更新を停止する。
         */
        private void stopLocationUpdates() {
            mLogger.d("IN");

            // ロケーション更新を解除する。
            mLocationClient.removeLocationUpdates(mLocationListener);

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * ロケーション接続失敗リスナークラス
     *
     */
    private class LocationOnConnectionFailedListener implements OnConnectionFailedListener {

        /** ロガー */
        private Logger mLogger = new Logger(LocationOnConnectionFailedListener.class);

        /**
         * 接続が失敗した時に呼び出される。
         *
         * @param connectionResult 接続結果
         */
        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            mLogger.d("IN");

//            // 解決策がある場合
//            if (connectionResult.hasResolution()) {
//                try {
//                    // エラーを解決してくれるインテントを投げる。
//                    connectionResult.startResolutionForResult(
//                        LocationService.this, RequestCode.CONNECTION_FAILURE_RESOLUTION_REQUEST);
//                } catch (IntentSender.SendIntentException e) {
//                    mLogger.e(e);
//                }
//
//            // 解決策がない場合
//            } else {
//                // 解決策がない場合はエラーダイアログを出します
//                showErrorDialog(
//                    connectionResult.getErrorCode(),
//                    RequestCode.CONNECTION_FAILURE_RESOLUTION_REQUEST);
//            }

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * ユーザロケーションリスナークラス
     *
     */
    private class UserLocationListener implements LocationListener {

        /** ロガー */
        private Logger mLogger = new Logger(UserLocationListener.class);

        /**
         * ロケーションが変更された時に呼び出される。
         *
         * @param location ロケーションオブジェクト
         */
        @Override
        public void onLocationChanged(Location location) {
            mLogger.d("IN");

            // 前回ロケーションが未設定の場合
            if (null == mPrevLocation) {
                // 前回ロケーションを設定する。
                mPrevLocation = location;

            // 前回ロケーションが設定済みの場合
            } else {
                // 滞在監視除外場所のデータを取得する。
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(KidsAlermService.this);
                String json = prefs.getString(PrefsKey.EXCLUSION_PLACE_DATA_MAP, "");
                mLogger.d("ExclusionPlaceData(JSON)=[" + json + "]");

                // 滞在監視除外場所のデータがある場合
                boolean check = true;
                if (StringUtil.isNotNullOrEmpty(json)) {
                    // 滞在監視除外場所マップを取得する。
                    Gson gson = new Gson();
                    Type mapType = new TypeToken<Map<String, ExclusionPlaceData>>(){}.getType();
                    Map<String, ExclusionPlaceData> stayExclusionDataMap = gson.fromJson(json, mapType);

                    // 滞在監視除外場所データ数分繰り返す。
                    Iterator<String> itr = stayExclusionDataMap.keySet().iterator();
                    while (itr.hasNext()) {
                        // 滞在監視除外場所からの距離を取得する。
                        ExclusionPlaceData data = stayExclusionDataMap.get(itr.next());
                        double distance =
                                MonitorUtil.getDistance(
                                        location.getLatitude(), location.getLongitude(),
                                        Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude()),
                                        DISTANCE_DIGIT);
                        mLogger.d("distance=[" + distance + "]");

                        // 滞在監視除外場所の場合
                        if (DISTANCE_IGNORE > distance) {
                            // 滞在監視のチェックは行わない。
                            check = false;

                            // 滞在監視カウントダウンタイマーを再開始する。
                            restartStayCountDownTimer();

                            // ループを終了する。
                            break;
                        }
                    }
                }

                // 滞在監視のチェックを行う場合
                if (check) {
                    // 前回ロケーションからの距離を取得する。
                    double distance = MonitorUtil.getDistance(mPrevLocation, location, DISTANCE_DIGIT);
                    mLogger.d("distance=[" + distance + "]");

                    // 移動した場合
                    if (DISTANCE_IGNORE < distance) {
                        // 滞在監視カウントダウンタイマーを再開始する。
                        restartStayCountDownTimer();
                    }
                }

                // 前回ロケーションを更新する。
                mPrevLocation = location;
            }

            // ロケーション情報をブロードキャスト送信する。
            Intent intent = new Intent();
            intent.setAction(Action.LOCATION);
            intent.putExtra(ExtraKey.LATITUDE, location.getLatitude());
            intent.putExtra(ExtraKey.LONGITUDE, location.getLongitude());
            sendBroadcast(intent);

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * 滞在監視カウントダウンタイマークラス
     *
     */
    private class StayCountDownTimer extends CountDownTimer {

        /** ロガー */
        private Logger mLogger = new Logger(StayCountDownTimer.class);

        /**
         * コンストラクタ
         *
         * @param millisInFuture カウントダウン時間
         * @param countDownInterval カウントダウンインターバル
         */
        public StayCountDownTimer(long millisInFuture, long countDownInterval) {
            // スーパークラスのコンストラクタを呼び出す。
            super(millisInFuture, countDownInterval);

            mLogger.d("IN");
            mLogger.d("OUT(OK)");
        }

        /**
         * インターバルごとに呼び出される。
         *
         * @param millisUntilFinished カウントダウンするまでの時間
         */
        @Override
        public void onTick(long millisUntilFinished) {
            mLogger.d("IN");

            // 何もしない。

            mLogger.d("OUT(OK)");
        }

        /**
         * カウントダウンした時に呼び出される。
         */
        @Override
        public void onFinish() {
            mLogger.d("IN");

            // ブロードキャストを送信する。
            Intent intent = new Intent();
            intent.setAction(Action.STAY);
            Location location = mLocationClient.getLastLocation();
            intent.putExtra(ExtraKey.LATITUDE,  location.getLatitude());
            intent.putExtra(ExtraKey.LONGITUDE, location.getLongitude());
            sendBroadcast(intent);

            mLogger.d("OUT(OK)");
        }
    }
}
