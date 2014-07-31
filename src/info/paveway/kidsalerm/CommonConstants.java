package info.paveway.kidsalerm;

/**
 * キッズアラーム
 * 共通定数クラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class CommonConstants {

    public class TestDeviceId {
        public static final String NEXUS7 = "015d46d92a200e07";
    }

    /**
     * プリフェレンスキー
     *
     */
    public class PrefsKey {
        /** アプリケーションパスワード */
        public static final String APP_PASSWORD = "appPassword";

        /** メールユーザ名 */
        public static final String MAIL_USER_NAME = "mailUserName";

        /** メールパスワード */
        public static final String MAIL_PASSWORD = "mailPassword";

        /** 送信元メールアドレス */
        public static final String MAIL_FROM = "mailFrom";

        /** 送信先メールアドレス */
        public static final String MAIL_TO = "mailTo";

        /** メール設定クリア */
        public static final String MAIL_CLEAR = "mailClear";

        /** 電源ON通知 */
        public static final String NOTICE_POWER_ON = "noticePowerOn";

        /** 電源OFF通知 */
        public static final String NOTICE_POWER_OFF = "noticePowerOff";

        /** 滞在通知 */
        public static final String NOTICE_STAY = "noticeStay";

        /** 滞在通知時間(分) */
        public static final String NOTICE_STAY_TIME = "noticeStayTime";

        /** 除外場所選択 */
        public static final String EXCULSION_PLACE = "exclusionPlace";

        /** 除外場所データマップ */
        public static final String EXCLUSION_PLACE_DATA_MAP = "exclusionPlaceDataMap";

        /** ロケーション更新間隔 */
        public static final String LOCATION_INTERVAL = "locationInterval";

        /** ズーム */
        public static final String ZOOM = "zoom";

        /** チルト */
        public static final String TILT = "tilt";

        /** ベアリング */
        public static final String BEARING = "bearing";

        /** 緯度 */
        public static final String LATITUDE = "latitude";

        /** 経度 */
        public static final String LONGITUDE = "longitude";

        /** 電源ON */
        public static final String POWER_ON = "powerOn";
    }

    /**
     * メールプロパティキー
     *
     */
    public class MailPropKey {
        /** SMTPホスト */
        public static final String SMTP_HOST = "mail.smtp.host";

        /** SMTP認証 */
        public static final String SMTP_AUTH = "mail.smtp.auth";

        /** SMTPポート */
        public static final String SMTP_PORT = "mail.smtp.port";

        /** SMTP STARTTLS許可 */
        public static final String SMTP_STARTTLS_ENABLE = "mail.smtp.starttls.enable";
    }

    /**
     * アクション
     *
     */
    public static class Action {
        /** パッケージ名 */
        private static final String PACKAGE_NAME = CommonConstants.class.getPackage().getName();

        /** 電源ONメール */
        public static final String POWER_ON_MAIL = PACKAGE_NAME + ".POWER_ON_MAIL";

        /** ロケーション */
        public static final String LOCATION = PACKAGE_NAME + ".LOCATION";

        /** 滞在通知 */
        public static final String STAY = PACKAGE_NAME + ".STAY";
    }

    /**
     * Extraキー
     *
     */
    public class ExtraKey {
        /** タイトル */
        public static final String TITLE = "title";

        /** 緯度 */
        public static final String LATITUDE = "latitude";

        /** 経度 */
        public static final String LONGITUDE = "longitude";

        /** 登録済み除外場所名リスト */
        public static final String EXCLUSION_PLACE_NAME_LIST = "exclusionPlaceNameList";

        /** 進捗プログレスダイアログタイトル */
        public static final String PROGRESS_TITLE = "progressTitle";

        /** 進捗プログレスダイアログメッセージ */
        public static final String PROGRESS_MESSAGE = "progressMessage";
    }

    /**
     * リクエストコード
     *
     */
    public class RequestCode {
        /** 設定画面 */
        public static final int SETTINGS_PREFERENCE = 1;

        /** 除外場所選択画面 */
        public static final int EXCLUSION_PLACE = 2;

        /** 送信元連絡帳選択 */
        public static final int PICK_CONTACT_FROM = 3;

        /** 送信先連絡帳選択 */
        public static final int PICK_CONTACT_TO = 4;

        /** メール設定クリア */
        public static final int MAIL_CLEAR = 5;
    }

    /**
     * エンコーディング
     *
     */
    public class Encoding {
        /** UTF-8 */
        public static final String UTF_8 = "utf-8";
    }

    /**
     * 位置情報
     *
     */
    public class LocationInfo {
        /** ズームデフォルト値 */
        public static final float DEFAULT_ZOOM = 15;

        /** チルトデフォルト値 */
        public static final float DEFAULT_TILT = 0;

        /** ベアリングデフォルト値 */
        public static final float DEFAULT_BEARING = 0;

        /** 緯度デフォルト値(東京駅) */
        public static final String DEFAULT_LATITUDE  = "35.681376";

        /** 経度デフォルト値(東京駅) */
        public static final String DEFAULT_LONGITUDE = "139.766013";
    }
}
