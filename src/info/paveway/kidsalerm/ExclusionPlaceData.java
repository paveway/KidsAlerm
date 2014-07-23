package info.paveway.kidsalerm;

/**
 * キッズアラーム
 * 滞在監視除外場所データ
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class ExclusionPlaceData {

    /** タイトル */
    private String mTitle;

    /** 緯度 */
    private String mLatitude;

    /** 経度 */
    private String mLongitude;

    /**
     * コンストラクタ
     *
     * @param title タイトル
     * @param latitude 経度
     * @param longitude 緯度
     */
    public ExclusionPlaceData(String title, String latitude, String longitude) {
        mTitle = title;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    /**
     * タイトルを設定する。
     *
     * @param title タイトル
     */
    public void setTitle(String title) {
        mTitle = title;
    }

    /**
     * タイトルを返却する。
     *
     * @return タイトル
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * 緯度を設定する。
     *
     * @param latitude 緯度
     */
    public void setLatitude(String latitude) {
        mLatitude = latitude;
    }

    /**
     * 緯度を返却する。
     *
     * @return 緯度
     */
    public String getLatitude() {
        return mLatitude;
    }

    /**
     * 経度を設定する。
     *
     * @param longitude 経度
     */
    public void setLongitude(String longitude) {
        mLongitude = longitude;
    }

    /**
     * 経度を返却する。
     *
     * @return 経度
     */
    public String getLongitude() {
        return mLongitude;
    }
}
