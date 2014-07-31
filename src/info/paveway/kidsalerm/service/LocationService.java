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
import android.content.SharedPreferences.Editor;
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
 * ロケーションサービスクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class LocationService extends Service {

    /** ロガー */
    private Logger mLogger = new Logger(LocationService.class);

    /** 秒 */
    private static final long SEC = 1000;

    /** 分 */
    private static final long MINUTE = 60 * SEC;

    /** 更新間隔デフォルト値(5分) */
    private static final long DEFAULT_INTERVAL = 5 * MINUTE;

    /** カウントダウンタイマーインターバル(5分) */
    private static final long COUNT_DOWN_INTERVAL = 5 * MINUTE;

    /** 滞在通知時間(分) */
    private static final String DEFAULT_STAY_TIME = "120";

    /** 距離の小数点以下桁数(2桁) */
    private static final int DISTANCE_DIGIT = 2;

    /** 滞在としない距離(100m) */
    private static final double DISTANCE_IGNORE = 0.1D;

    /** プリフェレンス */
    private SharedPreferences mPrefs;

    /** ロケーションクライアント */
    private LocationClient mLocationClient;

    /** ロケーションリクエスト */
    private LocationRequest mLocationRequest;

    /** ロケーションリスナー */
    private LocationListener mLocationListener;

    /** 滞在通知カウントダウンタイマー */
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

        // プリフェレンスを取得する。
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        // ロケーションリクエストを生成する。
//        long interval = mPrefs.getLong(PrefsKey.LOCATION_INTERVAL, DEFAULT_INTERVAL);
        long interval = DEFAULT_INTERVAL;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
        mLocationRequest.setInterval(interval * SEC);
        mLocationRequest.setFastestInterval(DEFAULT_INTERVAL * SEC);

        // ロケーションリスナーを生成する。
        mLocationListener = new UserLocationListener();

        // ロケーションクライアントを生成する。
        mLocationClient =
                new LocationClient(
                        LocationService.this,
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

        // 滞在通知カウントダウンタイマーを停止する。
        stopStayCountDownTimer();

        // ロケーションクライアントが有効な場合
        if (null != mLocationClient) {
            // 切断する。
            mLocationClient.disconnect();
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * 滞在通知カウントダウンタイマーを再開始する。
     */
    private void restartStayCountDownTimer() {
        mLogger.d("IN");

        // 滞在通知カウントダウンタイマーを停止する。
        stopStayCountDownTimer();

        // 滞在時間を取得する。
        long stayTime = getStayTime();

        // カウントダウンタイマーを開始する。
        mStayCountDownTimer = new StayCountDownTimer(stayTime * MINUTE, COUNT_DOWN_INTERVAL);
        mStayCountDownTimer.start();

        mLogger.d("OUT(OK)");
    }

    /**
     * 滞在時間を取得する。
     *
     * @return 滞在時間
     */
    private long getStayTime() {
        mLogger.d("IN");

        // 滞在通知時間を取得する。
        String stayTimeStr = mPrefs.getString(PrefsKey.NOTICE_STAY_TIME, DEFAULT_STAY_TIME);
        // 未設定の場合
        if (StringUtil.isNullOrEmpty(stayTimeStr)) {
            // デフォルト値を設定する。
            stayTimeStr = DEFAULT_STAY_TIME;
        }

        long stayTime = Long.parseLong(stayTimeStr);
        mLogger.d("OUT(OK) result=[" + stayTime + "]");
        return stayTime;
    }

    /**
     * 滞在通知カウントダウンタイマーを停止する。
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

            // 滞在通知カウントダウンタイマーを開始する。
            restartStayCountDownTimer();

            mLogger.d("OUT(OK)");
        }

        /**
         * 切断した時に呼び出される。
         */
        @Override
        public void onDisconnected() {
            mLogger.d("IN");

            // 滞在通知カウントダウンタイマーを停止する。
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

            // 接続状態を取得する。
            boolean isConnected = mLocationClient.isConnected();
            mLogger.d("isConnected=[" + isConnected + "]");

            // 接続済みの場合
            if (isConnected) {
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

            // ロケーション情報をブロードキャスト送信する。
              sendLocationBroadcast(location);

            // 電源ONメール処理を行う。
              powerOnMail();

            // 前回ロケーションが設定済みの場合
            if (null != mPrevLocation) {
                // 除外場所でないかチェックする。
                boolean check = isNotExclusionPlace(location);

                // 除外場所ではない場合
                if (check) {
                    // 滞在をチェックする。
                    checkStay(location);

                // 除外場所の場合
                } else {
                    // 滞在通知カウントダウンタイマーを再開始する。
                    restartStayCountDownTimer();
                }
            }

            // 前回ロケーションを更新する。
            mPrevLocation = location;

            mLogger.d("OUT(OK)");
        }

        /**
         * ロケーションブロードキャストを送信する。
         *
         * @param location ロケーション
         */
        private void sendLocationBroadcast(Location location) {
            mLogger.d("IN");

            Intent intent = new Intent();
            intent.setAction(Action.LOCATION);
            intent.putExtra(ExtraKey.LATITUDE,  location.getLatitude());
            intent.putExtra(ExtraKey.LONGITUDE, location.getLongitude());
            sendBroadcast(intent);

            mLogger.d("OUT(OK)");
        }

        /**
         * 電源ONメール送信処理
         */
        private void powerOnMail() {
            mLogger.d("IN");

            // 電源ONメールを送信する場合
            boolean powerOn = mPrefs.getBoolean(PrefsKey.POWER_ON, false);
            mLogger.d("powerOn=[" + powerOn + "]");
            if (powerOn) {
                // 電源ONメール設定をクリアする。
                Editor editor = mPrefs.edit();
                editor.putBoolean(PrefsKey.POWER_ON, false);
                editor.commit();

                // 電源ONメール送信を通知する。
                Intent intent = new Intent();
                intent.setAction(Action.POWER_ON_MAIL);
                sendBroadcast(intent);
            }

            mLogger.d("OUT(OK)");
        }

        /**
         * 滞在をチェックする。
         *
         * @param location ロケーション
         */
        private void checkStay(Location location) {
            mLogger.d("IN");

            // 前回ロケーションからの距離を取得する。
            double distance = MonitorUtil.getDistance(mPrevLocation, location, DISTANCE_DIGIT);
            mLogger.d("distance=[" + distance + "]");

            // 移動した場合
            if (DISTANCE_IGNORE < distance) {
                // 滞在通知カウントダウンタイマーを再開始する。
                restartStayCountDownTimer();
            }

            mLogger.d("OUT(OK)");
        }
    }

    /**
     * 除外場所ではないかチェックする。
     *
     * @param location ロケーション
     * @return チェック結果
     */
    private boolean isNotExclusionPlace(Location location) {
        mLogger.d("IN");

        // 除外場所のデータを取得する。
        String json = mPrefs.getString(PrefsKey.EXCLUSION_PLACE_DATA_MAP, "");
        mLogger.d("ExclusionPlaceData(JSON)=[" + json + "]");

        // 除外場所のデータがある場合
        boolean check = true;
        if (StringUtil.isNotNullOrEmpty(json)) {
            // 除外場所マップを取得する。
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, ExclusionPlaceData>>(){}.getType();
            Map<String, ExclusionPlaceData> stayExclusionDataMap = gson.fromJson(json, mapType);

            // 除外場所データ数分繰り返す。
            Iterator<String> itr = stayExclusionDataMap.keySet().iterator();
            while (itr.hasNext()) {
                // 除外場所からの距離を取得する。
                ExclusionPlaceData data = stayExclusionDataMap.get(itr.next());
                double distance =
                        MonitorUtil.getDistance(
                                location.getLatitude(), location.getLongitude(),
                                Double.parseDouble(data.getLatitude()), Double.parseDouble(data.getLongitude()),
                                DISTANCE_DIGIT);
                mLogger.d("distance=[" + distance + "]");

                // 除外範囲の場合
                if (DISTANCE_IGNORE > distance) {
                    // 滞在通知のチェックは行わない。
                    check = false;

                    // ループを終了する。
                    break;
                }
            }
        }

        mLogger.d("OUT(OK) result=[" + check + "]");
        return check;
    }

    /**************************************************************************/
    /**
     * 滞在通知カウントダウンタイマークラス
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

            mLogger.d("IN millisInFuture=[" + millisInFuture + "] countDownInterval=[" + countDownInterval + "]");
            mLogger.d("OUT(OK)");
        }

        /**
         * インターバルごとに呼び出される。
         *
         * @param millisUntilFinished カウントダウンするまでの時間
         */
        @Override
        public void onTick(long millisUntilFinished) {
            mLogger.d("IN millisUntilFinished=[" + millisUntilFinished + "]");

            // 何もしない。

            mLogger.d("OUT(OK)");
        }

        /**
         * カウントダウンした時に呼び出される。
         */
        @Override
        public void onFinish() {
            mLogger.d("IN");

            // 最新のロケーションを取得する。
            Location location = mLocationClient.getLastLocation();
            mLogger.d("location=[" + location + "]");

            // ロケーションが取得でき、かつ除外場所ではない場合
            if ((null != location) && isNotExclusionPlace(location)) {
                // ブロードキャストを送信する。
                Intent intent = new Intent();
                intent.setAction(Action.STAY);
                intent.putExtra(ExtraKey.LATITUDE,  location.getLatitude());
                intent.putExtra(ExtraKey.LONGITUDE, location.getLongitude());
                sendBroadcast(intent);

            // 上記以外
            } else {
                // カウントダウンタイマーを再起動する。
                restartStayCountDownTimer();
            }

            mLogger.d("OUT(OK)");
        }
    }
}
