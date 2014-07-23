package info.paveway.util;

/**
 * 文字列ユーティリティクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class StringUtil {

    /**
     * 文字列がnullか空文字列か判定する。
     *
     * @param src 判定する文字列
     * @return 判定結果 true:nullまたは空文字列 / false:null、空文字列ではない。
     */
    public static boolean isNullOrEmpty(String src) {
        return ((null == src) || "".equals(src)) ? true : false;
    }

    /**
     * 文字列がnullではなく空文字列でもないか判定する。
     *
     * @param src 判定する文字列
     * @return true:null、空文字列ではない。 / false:nullまたは空文字列
     */
    public static boolean isNotNullOrEmpty(String src) {
        return !isNullOrEmpty(src);
    }
}
