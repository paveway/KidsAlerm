package info.paveway.util;

import android.location.Location;

/**
 * 監視ユーティリティクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class MonitorUtil {

    /**
     * 距離を求める。
     *
     * @param prevLocation 前回のロケーション
     * @param currLocation 現在のロケーション
     * @param digit 小数点以下の桁数
     * @return 距離(km)
     */
    public static double getDistance(Location prevLocation, Location currLocation, int digit) {
        return getDistance(
                prevLocation.getLatitude(), prevLocation.getLongitude(),
                currLocation.getLatitude(), currLocation.getLongitude(),
                digit);
    }

    /**
     * 距離を求める。
     *
     * @param latitude1 1点目の緯度
     * @param longitude1 1点目の経度
     * @param latitude2 2点目の緯度
     * @param longitude2 2点目の経度
     * @param digit 小数点以下の桁数
     * @return 距離(km)
     */
    public static double getDistance(double latitude1, double longitude1, double latitude2, double longitude2, int digit) {
        double distance = 0.0F;
        if ((Math.abs(latitude1 - latitude2) < 0.00001) && (Math.abs(longitude1 - longitude2) < 0.00001)) {
            distance = 0.0F;

        } else {
            latitude1  = latitude1  * Math.PI / 180;
            longitude1 = longitude1 * Math.PI / 180;
            latitude2  = latitude2  * Math.PI / 180;
            longitude2 = longitude2 * Math.PI / 180;

            double a = 6378140.0F; // 地球赤道半径
            double b = 6356755.0F; // 地球極半径
            double f = (a - b) / a; // 化成経度
            double p1 = Math.atan((b / a) * Math.tan(latitude1));
            double p2 = Math.atan((b / a) * Math.tan(latitude2));

            // 球面上の距離
            double x = Math.acos(Math.sin(p1) * Math.sin(p2) + Math.cos(p1) * Math.cos(p2) * Math.cos(longitude1 - longitude2) );
            double l = (f / 8) *
                    ((Math.sin(x) - x) * Math.pow((Math.sin(p1) + Math.sin(p2)), 2) / Math.pow(Math.cos(x / 2), 2) -
                     (Math.sin(x) - x) * Math.pow( Math.sin(p1) - Math.sin(p2),  2) / Math.pow(Math.sin(x),     2));

            distance = a * (x + l);
            double decimalNo = Math.pow(10, digit);
            distance = Math.round(decimalNo * distance / 1000) / decimalNo;
        }

        return distance;
    }
}
