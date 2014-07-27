package info.paveway.kidsalerm;

import info.paveway.kidsalerm.CommonConstants.LocationInfo;
import info.paveway.kidsalerm.CommonConstants.PrefsKey;
import info.paveway.kidsalerm.dialog.DeleteExclusionPlaceDialog.OnDeleteListener;
import info.paveway.kidsalerm.dialog.DetailExclusionPlaceDialog;
import info.paveway.kidsalerm.dialog.RegistExclusionPlaceNameDialog;
import info.paveway.kidsalerm.dialog.RegistExclusionPlaceNameDialog.OnRegistListener;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * キッズアラーム
 * 除外場所選択画面
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class SelectExclusionPlaceActivity extends ActionBarActivity implements OnRegistListener, OnDeleteListener {

    /** ロガー */
    private Logger mLogger = new Logger(SelectExclusionPlaceActivity.class);

    /** プリフェレンス */
    private SharedPreferences mPrefs;

    /** Googleマップ */
    private GoogleMap mGoogleMap;

    /** カメラポジション */
    private CameraPosition mCameraPosition;

    /** 除外場所データマップ */
    private Map<String, ExclusionPlaceData> mExclusionPlaceDataMap;

    /** マーカー */
    private Marker mDeleteMarker;

    /**
     * 生成した時に呼び出される。
     *
     * @param savendInstanceState 保存した時のインスタンスの状態
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // レイアウトを設定する。
        setContentView(R.layout.activity_map);

        // プリフェレンスを取得する。
        mPrefs = PreferenceManager.getDefaultSharedPreferences(SelectExclusionPlaceActivity.this);

        // Google Play servicesが利用できるかチェックする。
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // 利用できない場合
        if (ConnectionResult.SUCCESS != result) {
            // リカバリー可能なエラーの場合
            if (GooglePlayServicesUtil.isUserRecoverableError(result)) {
                // リカバリー可能を示すダイアログを表示する。
                GooglePlayServicesUtil.getErrorDialog(result, this, 1, new DialogInterface.OnCancelListener() {

                    /**
                     * キャンセルされた時に呼び出される。
                     *
                     * @param dialog ダイアログ
                     */
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // 終了する。
                        Toast.makeText(
                                SelectExclusionPlaceActivity.this,
                                getResources().getString(R.string.stay_exclusion_error_google_play_service),
                                Toast.LENGTH_SHORT).show();
                        finish();
                        mLogger.w("OUT(NG)");
                    }
                }).show();
                // 続きはonActivityResult()で行う

            // リカバリー不可能なエラーの場合
            } else {
                // 終了する。
                Toast.makeText(
                        SelectExclusionPlaceActivity.this,
                        getResources().getString(R.string.stay_exclusion_error_google_play_service),
                        Toast.LENGTH_SHORT).show();
                finish();
                mLogger.w("OUT(NG)");
                return;
            }
        }

        // マップフラグメントを取得する。
        MapFragment mapFragment = (MapFragment)(getFragmentManager().findFragmentById(R.id.map));
        try {
            // マップオブジェクトを取得する。
            mGoogleMap = mapFragment.getMap();

            // Activityが初めて生成された場合
            if (null == savedInstanceState) {
                // フラグメントを保存する。
                mapFragment.setRetainInstance(true);

                // 地図の初期設定を行う。
                initMap();
            }
        } catch (Exception e) {
            // 終了する。
            mLogger.e(e);
            Toast.makeText(
                    SelectExclusionPlaceActivity.this,
                    getResources().getString(R.string.stay_exclusion_error_map),
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    /**************************************************************************/
    /*** 内部メソッド                                                       ***/
    /**************************************************************************/
    /**
     * 地図の初期化を行う。
     */
    private void initMap() {
        mLogger.d("IN");

        // 地図タイプを設定する。
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // 現在位置ボタンの表示する。
        mGoogleMap.setMyLocationEnabled(true);

        // マップカメラチェンジリスナーを設定する。
        mGoogleMap.setOnCameraChangeListener(new MapOnCameraChangeListener());

        // マップクリックリスナーを設定する。
        mGoogleMap.setOnMapClickListener(new MapOnMapClickListener());

        // マーカークリックリスナーを設定する。
        mGoogleMap.setOnMarkerClickListener(new MapOnMarkerClickListener());

        // カメラポジションの初期値を設定する。
        float zoom       = mPrefs.getFloat(PrefsKey.ZOOM,    LocationInfo.DEFAULT_ZOOM);
        float tilt       = mPrefs.getFloat(PrefsKey.TILT,    LocationInfo.DEFAULT_TILT);
        float bearing    = mPrefs.getFloat(PrefsKey.BEARING, LocationInfo.DEFAULT_BEARING);
        double latitude  = Double.parseDouble(mPrefs.getString(PrefsKey.LATITUDE,  LocationInfo.DEFAULT_LATITUDE));
        double longitude = Double.parseDouble(mPrefs.getString(PrefsKey.LONGITUDE, LocationInfo.DEFAULT_LONGITUDE));
        CameraPosition cameraPosition = new CameraPosition(new LatLng(latitude, longitude), zoom, tilt, bearing);
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // 除外場所のデータを取得する。
        String json = mPrefs.getString(PrefsKey.EXCLUSION_PLACE_DATA_MAP, "");
        mLogger.d("ExclusionPlaceData(JSON)=[" + json + "]");

        // 除外場所のデータがある場合
        if (StringUtil.isNotNullOrEmpty(json)) {
            // 除外場所マップを設定する。
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, ExclusionPlaceData>>(){}.getType();
            mExclusionPlaceDataMap = gson.fromJson(json, mapType);

        // 除外場所のデータがない場合
        } else {
            // 除外場所マップを生成する。
            mExclusionPlaceDataMap = new LinkedHashMap<String, ExclusionPlaceData>();
        }

        // 除外場所データからマーカーを設定する。
        Iterator<String> itr = mExclusionPlaceDataMap.keySet().iterator();
        while (itr.hasNext()) {
            ExclusionPlaceData data = mExclusionPlaceDataMap.get(itr.next());
            setMarker(data);
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * マーカーを設定する。
     *
     * @param data 除外場所データ
     */
    private void setMarker(ExclusionPlaceData data) {
        mLogger.d("IN");

        // マーカーを生成する。
        MarkerOptions options = new MarkerOptions();
        options.position(
                new LatLng(
                        Double.parseDouble(data.getLatitude()),
                        Double.parseDouble(data.getLongitude())));
        options.title(data.getTitle());
        mGoogleMap.addMarker(options);

        mLogger.d("OUT(OK)");
    }

    /**
     * 除外場所名を登録した時に呼び出される。
     *
     * @param exclusionPlaceName 除外場所名
     * @param latitude 緯度
     * @param longitude 経度
     */
    @Override
    public void onRegist(String exclusionPlaceName, double latitude, double longitude) {
        mLogger.d("IN exclusionPlaceName=[" + exclusionPlaceName + "] latitude=[" + latitude + "] longitude=[" + longitude + "]");

        // 除外場所名がある場合
        if (StringUtil.isNotNullOrEmpty(exclusionPlaceName)) {
            // マーカーを生成する。
            MarkerOptions options = new MarkerOptions();
            options.position(new LatLng(latitude, longitude));
            options.title(exclusionPlaceName);
            Marker marker = mGoogleMap.addMarker(options);

            // 除外場所マップにデータを追加する。
            mExclusionPlaceDataMap.put(
                    exclusionPlaceName,
                    new ExclusionPlaceData(
                            exclusionPlaceName,
                            String.valueOf(marker.getPosition().latitude),
                            String.valueOf(marker.getPosition().longitude)));

            // 除外場所のJSONデータを生成する。
            Gson gson = new Gson();
            String json = gson.toJson(mExclusionPlaceDataMap);
            mLogger.d("ExclusionPlaceData(JSON)=[" + json + "]");

            // 除外場所データをプリフェレンスに保存する。
            Editor editor = mPrefs.edit();
            editor.putString(PrefsKey.EXCLUSION_PLACE_DATA_MAP, json);
            editor.commit();
        }

        mLogger.d("OUT(OK)");
    }

    @Override
    public void onDelete() {

        String title = mDeleteMarker.getTitle();

        // 除外場所マップから対象のデータを取得する。
        ExclusionPlaceData data = mExclusionPlaceDataMap.get(title);

        // データがある場合
        if (null != data) {
            // マーカーを削除する。
            mDeleteMarker.remove();

            // 除外場所マップから削除する。
            mExclusionPlaceDataMap.remove(title);

            // 除外場所のJSONデータを生成する。
            Gson gson = new Gson();
            String json = gson.toJson(mExclusionPlaceDataMap);
            mLogger.d("ExclusionPlaceData(JSON)=[" + json + "]");

            // 除外場所データをプリフェレンスに保存する。
            SharedPreferences prefs =
                    PreferenceManager.getDefaultSharedPreferences(SelectExclusionPlaceActivity.this);
            Editor editor = prefs.edit();
            editor.putString(PrefsKey.EXCLUSION_PLACE_DATA_MAP, json);
            editor.commit();
        }

    }

    /**************************************************************************/
    /**
     * カメラチェンジリスナークラス
     *
     */
    private class MapOnCameraChangeListener implements OnCameraChangeListener {

        /** ロガー */
        private Logger mLogger = new Logger(MapOnCameraChangeListener.class);

        /**
         * カメラポジションが変更された時に呼び出される。
         *
         * @param カメラポジション
         */
        @Override
        public void onCameraChange(CameraPosition cameraPosition) {
            mLogger.d("IN bearing=[" + cameraPosition.bearing + "] tilt=[" + cameraPosition.tilt + "] zoom=[" + cameraPosition.zoom + "]");

            // カメラポジションを保存する。
            mCameraPosition = cameraPosition;

            // 次回起動時用に保存する。
            Editor editor = mPrefs.edit();
            editor.putFloat( PrefsKey.ZOOM,      mCameraPosition.zoom);
            editor.putFloat( PrefsKey.TILT,      mCameraPosition.tilt);
            editor.putFloat( PrefsKey.BEARING,   mCameraPosition.bearing);
            editor.putString(PrefsKey.LATITUDE,  String.valueOf(mCameraPosition.target.latitude));
            editor.putString(PrefsKey.LONGITUDE, String.valueOf(mCameraPosition.target.longitude));
            editor.commit();

            // カメラポジションを再設定する。
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(mCameraPosition));

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * マップクリックリスナークラス
     *
     */
    private class MapOnMapClickListener implements OnMapClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(MapOnMarkerClickListener.class);

        /**
         * マップがクリックされた時に呼び出される。
         *
         * @param latlng 緯度経度データ
         */
        @Override
        public void onMapClick(LatLng latlng) {
            mLogger.d("IN");

            ArrayList<String> exclusionPlaceNameList = new ArrayList<String>();
            Iterator<String> itr = mExclusionPlaceDataMap.keySet().iterator();
            while (itr.hasNext()) {
                exclusionPlaceNameList.add(itr.next());
            }

            // 除外場所名登録ダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            RegistExclusionPlaceNameDialog dialog =
                    RegistExclusionPlaceNameDialog.newInstance(exclusionPlaceNameList, latlng.latitude, latlng.longitude);
            dialog.setCancelable(false);
            dialog.show(manager, dialog.getClass().getSimpleName());

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * マップマーカークリックリスナークラス
     *
     */
    private class MapOnMarkerClickListener implements OnMarkerClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(MapOnMarkerClickListener.class);

        /**
         * マーカーをクリックした時に呼び出される。
         *
         * @param marker マーカー
         */
        @Override
        public boolean onMarkerClick(Marker marker) {
            mLogger.d("IN");

            mDeleteMarker = marker;

            // 除外場所詳細ダイアログを表示する。
            FragmentManager manager = getSupportFragmentManager();
            DetailExclusionPlaceDialog dialog =
                    DetailExclusionPlaceDialog.newInstance(marker);
            dialog.setCancelable(false);
            dialog.show(manager, dialog.getClass().getSimpleName());

            mLogger.d("OUT(OK)");
            return false;
        }
    }
}
