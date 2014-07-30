package info.paveway.kidsalerm;

import info.paveway.kidsalerm.CommonConstants.PrefsKey;
import info.paveway.kidsalerm.CommonConstants.RequestCode;
import info.paveway.kidsalerm.dialog.EditDialogPreference;
import info.paveway.kidsalerm.dialog.StayTimeDialogPreference;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.provider.ContactsContract;
import android.widget.Toast;

/**
 * キッズアラーム
 * 設定画面
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class SettingsPreferenceActivity extends PreferenceActivity {

    /** ロガー */
    private Logger mLogger = new Logger(SettingsPreferenceActivity.class);

    /** Gmailアドレス接尾語 */
    private static final String GMAIL_ADDR_SUFFIX = "@gmail.com";

    /** プリフェレンス */
    private SharedPreferences mPrefs;

    /** リソース */
    private Resources mResources;

    /** アプリケーションパスワード */
    private EditDialogPreference mAppPassword;

    /** メールユーザ名 */
    private EditDialogPreference mMailUserName;

    /** メールパスワード */
    private EditDialogPreference mMailPassword;

    /** 送信元メールアドレス */
    private PreferenceScreen mMailFrom;

    /** 送信先メールアドレス */
    private PreferenceScreen mMailTo;

    /** メール設定クリア */
    private PreferenceScreen mMailClear;

    /** 電源ON通知 */
    private CheckBoxPreference mNoticePowerOn;

    /** 電源OFF通知 */
    private CheckBoxPreference mNoticePowerOff;

    /** 滞在通知 */
    private CheckBoxPreference mNoticeStay;

    /** 滞在通知時間 */
    private StayTimeDialogPreference mNoticeStayTime;

    /** 滞在通知除外場所選択 */
    private PreferenceScreen mExclusionPlace;

    /**
     * 生成した時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        mLogger.d("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // 設定リソースを追加する。
        addPreferencesFromResource(R.xml.preference_settings);

        // プリフェレンスを取得する。
        mPrefs = PreferenceManager.getDefaultSharedPreferences(SettingsPreferenceActivity.this);

        // リソースを取得する。
        mResources = getResources();

        // 各入力項目を取得する。
        mAppPassword     = (EditDialogPreference)findPreference(PrefsKey.APP_PASSWORD);
        mMailUserName    = (EditDialogPreference)findPreference(PrefsKey.MAIL_USER_NAME);
        mMailPassword    = (EditDialogPreference)findPreference(PrefsKey.MAIL_PASSWORD);
        mMailFrom        = (PreferenceScreen    )findPreference(PrefsKey.MAIL_FROM);
        mMailTo          = (PreferenceScreen    )findPreference(PrefsKey.MAIL_TO);
        mMailClear       = (PreferenceScreen    )findPreference(PrefsKey.MAIL_CLEAR);
        mNoticePowerOn   = (CheckBoxPreference  )findPreference(PrefsKey.NOTICE_POWER_ON);
        mNoticePowerOff  = (CheckBoxPreference  )findPreference(PrefsKey.NOTICE_POWER_OFF);
        mNoticeStay      = (CheckBoxPreference  )findPreference(PrefsKey.NOTICE_STAY);
        mNoticeStayTime  = (StayTimeDialogPreference)findPreference(PrefsKey.NOTICE_STAY_TIME);
        mExclusionPlace  = (PreferenceScreen    )findPreference(PrefsKey.EXCULSION_PLACE);

        // アプリケーションパスワードが入力済みの場合
        String appPassword = mPrefs.getString(PrefsKey.APP_PASSWORD, "");
        if (StringUtil.isNotNullOrEmpty(appPassword)) {
            // サマリーを設定する。
            mAppPassword.setSummary(getResourceString(R.string.summary_password));
        }

        // メールユーザ名が入力済みの場合
        String mailUserName = mPrefs.getString(PrefsKey.MAIL_USER_NAME, "");
        if (StringUtil.isNotNullOrEmpty(mailUserName)) {
            // サマリーを設定する。
            mMailUserName.setSummary(getResourceString(R.string.summary_prefix) + mailUserName);
        }

        // メールパスワードが入力済みの場合
        String mailPassword = mPrefs.getString(PrefsKey.MAIL_PASSWORD, "");
        if (StringUtil.isNotNullOrEmpty(mailPassword)) {
            // サマリーを設定する。
            mMailPassword.setSummary(getResourceString(R.string.summary_password));
        }

        // 送信元メールアドレスが入力済みの場合
        String mailFrom = mPrefs.getString(PrefsKey.MAIL_FROM, "");
        if (StringUtil.isNotNullOrEmpty(mailFrom)) {
            // サマリーを設定する。
            mMailFrom.setSummary(getResourceString(R.string.summary_prefix) + mailFrom);
        }

        // 送信先メールアドレスが入力済みの場合
        String mailTo = mPrefs.getString(PrefsKey.MAIL_TO, "");
        if (StringUtil.isNotNullOrEmpty(mailTo)) {
            // サマリーを設定する。
            mMailTo.setSummary(getResourceString(R.string.summary_prefix) + mailTo);
        }

        // 滞在通知時間が入力済みの場合
        String noticeStatyTime = mPrefs.getString(PrefsKey.NOTICE_STAY_TIME, "");
        if (StringUtil.isNotNullOrEmpty(noticeStatyTime)) {
            // サマリーを設定する。
            mNoticeStayTime.setSummary(getResourceString(R.string.summary_prefix) + noticeStatyTime + "分");
        }

        // クリックリスナーを設定する。
        mMailFrom.setOnPreferenceClickListener(      new MailFromOnPreferenceClickListener());
        mMailTo.setOnPreferenceClickListener(        new MailToOnPreferenceClickListener());
        mMailClear.setOnPreferenceClickListener(     new MailClearOnPreferenceClickListener(SettingsPreferenceActivity.this));
        mExclusionPlace.setOnPreferenceClickListener(new ExclusionPlaceOnPreferenceClickListener());

        // ビューの表示可否を設定する。
        enableViews();

        mLogger.d("OUT(OK)");
    }

    /**
     * 呼び出した画面から戻った時に呼び出される。
     *
     * @param requestCode リクエストコード
     * @param resultCode 結果コード
     * @param data データ
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLogger.d("IN");

        boolean result = false;

        // 送信元メールアドレス選択かつ正常終了かつデータがある場合
        if ((RequestCode.PICK_CONTACT_FROM == requestCode) && (RESULT_OK == resultCode) && (null != data)) {
            result = pickContactFromResult(data);

        // 送信先メールアドレス選択かつ正常終了かつデータがある場合
        } else if ((RequestCode.PICK_CONTACT_TO == requestCode) && (RESULT_OK == resultCode) && (null != data)) {
            result = pickContactToResult(data);

        // メール設定クリアかつ正常終了の場合
        } else if ((RequestCode.MAIL_CLEAR == requestCode) && (RESULT_OK == resultCode)) {
            result = mailClearResult();
        }

        if (result) { mLogger.d("OUT(OK)"); } else { mLogger.d("OUT(NG)"); }
    }

    /**
     * 送信元メールアドレス選択結果の処理を行う。
     *
     * @param data データ
     * @return 処理結果
     */
    private boolean pickContactFromResult(Intent data) {
        mLogger.d("IN");

        // メールアドレスを取得する。
        String emailAddress = getEmailAddressFrom(data.getData());

        // メールアドレスが取得できた場合
        if (StringUtil.isNotNullOrEmpty(emailAddress)) {
            // メールアドレスを保存する。
            Editor editor = mPrefs.edit();
            editor.putString(PrefsKey.MAIL_FROM, emailAddress);
            editor.commit();
            mMailFrom.setSummary(getResourceString(R.string.summary_prefix) + emailAddress);

            // ビューの表示可否を設定する。
            enableViews();

            mLogger.d("OUT(OK)");
            return true;

        // メールアドレスが取得できない場合
        } else {
            Toast.makeText(this, getResourceString(R.string.mail_from_error), Toast.LENGTH_SHORT).show();

            // ビューの表示可否を設定する。
            enableViews();

            mLogger.d("OUT(NG)");
            return false;
        }
    }

    /**
     * 送信先メールアドレス選択結果の処理を行う。
     *
     * @param data データ
     * @return 処理結果
     */
    private boolean pickContactToResult(Intent data) {
        mLogger.d("IN");

        // メールアドレスを取得する。
        String emailAddress = getEmailAddressTo(data.getData());
        // メールアドレスが取得できた場合
        if (StringUtil.isNotNullOrEmpty(emailAddress)) {
            // メールアドレスを保存する。
            Editor editor = mPrefs.edit();
            editor.putString(PrefsKey.MAIL_TO, emailAddress);
            editor.commit();
            mMailTo.setSummary(getResourceString(R.string.summary_prefix) + emailAddress);

            // ビューの表示可否を設定する。
            enableViews();
        }

        mLogger.d("OUT(OK)");
        return true;
    }

    /**
     * メール設定クリア結果の処理を行う。
     *
     * @param data データ
     * @return 処理結果
     */
    private boolean mailClearResult() {
        mLogger.d("IN");

        // 設定値をクリアする。
        Editor editor = mPrefs.edit();
        editor.putString(PrefsKey.MAIL_USER_NAME, "");
        editor.putString(PrefsKey.MAIL_PASSWORD,  "");
        editor.putString(PrefsKey.MAIL_FROM,      "");
        editor.putString(PrefsKey.MAIL_TO,        "");
        editor.commit();

        // ビューの表示可否を設定する。
        enableViews();

        mLogger.d("OUT(OK)");
        return true;
    }


    /**
     * 戻るボタンが押された時に呼び出される。
     */
    @Override
    public void onBackPressed() {
        mLogger.d("IN");

        // 開始画面を表示する。
        Intent intent = new Intent(SettingsPreferenceActivity.this, StartupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        // 終了する。
        finish();

        mLogger.d("OUT(OK)");
    }

    /**
     * リソース文字列を取得する。
     *
     * @param id リソース文字列のID
     * @return リソース文字列
     */
    private String getResourceString(int id) {
        return mResources.getString(id);
    }

    /**
     * ビューの表示可否を設定する。
     */
    public void enableViews() {
        mLogger.d("IN");

        // アプリケーションパスワードを取得する。
        boolean mailEnabled      = false;
        boolean mailClearEnabled = false;
        boolean etcEnabled       = true;
        String appPassword = mPrefs.getString(PrefsKey.APP_PASSWORD, "");

        // アプリケーションパスワードが取得できた場合
        if (StringUtil.isNotNullOrEmpty(appPassword)) {
            mAppPassword.setSummary(getResourceString(R.string.summary_password));
            // メール設定を表示可とする。
            mailEnabled = true;

            // メール設定を取得する。
            String mailUserName = mPrefs.getString(PrefsKey.MAIL_USER_NAME, "");
            String mailPassword = mPrefs.getString(PrefsKey.MAIL_PASSWORD,  "");
            String mailFrom     = mPrefs.getString(PrefsKey.MAIL_FROM,      "");
            String mailTo       = mPrefs.getString(PrefsKey.MAIL_TO,        "");

            // メールユーザ名が取得できた場合
            if (StringUtil.isNotNullOrEmpty(mailUserName)) {
                mMailUserName.setSummary(getResourceString(R.string.summary_prefix) + mailUserName);
                mailClearEnabled = true;

            // メールユーザ名が取得できない場合
            } else {
                mMailUserName.setSummary(getResourceString(R.string.mail_user_name_summary));
                etcEnabled = false;
            }

            // メールパスワードが取得できた場合
            if (StringUtil.isNotNullOrEmpty(mailPassword)) {
                mMailPassword.setSummary(getResourceString(R.string.summary_password));
                mailClearEnabled = true;

            // メールパスワードが取得できない場合
            } else {
                mMailPassword.setSummary(getResourceString(R.string.mail_password_summary));
                etcEnabled = false;
            }

            // 送信元メールアドレスが取得できた場合
            if (StringUtil.isNotNullOrEmpty(mailFrom)) {
                mMailFrom.setSummary(getResourceString(R.string.summary_prefix) + mailFrom);
                mailClearEnabled = true;

            // 送信元メールアドレスが取得できない場合
            } else {
                mMailFrom.setSummary(getResourceString(R.string.mail_from_summary));
                etcEnabled = false;
            }

            // 送信先メールアドレスが取得できた場合
            if (StringUtil.isNotNullOrEmpty(mailTo)) {
                mMailTo.setSummary(getResourceString(R.string.summary_prefix) + mailTo);
                mailClearEnabled = true;

            // 送信先メールアドレスが取得できない場合
            } else {
                mMailTo.setSummary(getResourceString(R.string.mail_to_summary));
                etcEnabled = false;
            }

            // 滞在通知時間を取得する。
            String noticeStatyTime = mPrefs.getString(PrefsKey.NOTICE_STAY_TIME, "");
            // 滞在通知時間が取得できた場合
            if (StringUtil.isNotNullOrEmpty(noticeStatyTime)) {
                mNoticeStayTime.setSummary(getResourceString(R.string.summary_prefix) + noticeStatyTime + "分");

            // 滞在通知時間が取得できない場合
            } else {
                mNoticeStayTime.setSummary(getResourceString(R.string.notice_stay_time_summary));
            }

        // アプリケーションパスワードが取得できない場合
        } else {
            mAppPassword.setSummary(getResourceString(R.string.app_password_summary));
            etcEnabled = false;
        }

        // メール設定の表示可否を設定する。
        mMailUserName.setEnabled(mailEnabled);
        mMailPassword.setEnabled(mailEnabled);
        mMailFrom.setEnabled(    mailEnabled);
        mMailTo.setEnabled(      mailEnabled);
        if (mailEnabled) {
            mMailClear.setEnabled(mailClearEnabled);
        } else {
            mMailClear.setEnabled(false);
        }


        // その他の設定の表示可否を設定する。
        mNoticePowerOn.setEnabled( etcEnabled);
        mNoticePowerOff.setEnabled(etcEnabled);
        mNoticeStay.setEnabled(    etcEnabled);
        mNoticeStayTime.setEnabled(etcEnabled);
        mExclusionPlace.setEnabled(etcEnabled);

        mLogger.d("OUT(OK)");
    }

    /**
     * 送信元メールアドレスを取得する。
     *
     * @param contactUri コンタクトURI
     * @return コンタクト送信元メールアドレス
     */
    private String getEmailAddressFrom(Uri contactUri) {
        mLogger.d("IN");

        mLogger.d("OUT(OK)");
        return getEmailAddress(contactUri, true);
    }

    /**
     * 送信先メールアドレスを取得する。
     *
     * @param contactUri コンタクトURI
     * @return 送信先メールアドレス
     */
    private String getEmailAddressTo(Uri contactUri) {
        mLogger.d("IN");

        mLogger.d("OUT(OK)");
        return getEmailAddress(contactUri, false);
    }

    /**
     * メールアドレスを取得する。
     *
     * @param contactUri コンタクトURI
     * @param check GMailアドレスチェック
     * @return メールアドレス
     */
    private String getEmailAddress(Uri contactUri, boolean check) {
        mLogger.d("IN");

        Cursor cursor = null;
        String emailAddress = null;
        try {
            cursor = getContentResolver().query(contactUri, null, null, null, null);
            // カーソルが取得できた場合
            if (null != cursor) {
                // データがある場合
                if (cursor.moveToFirst()) {
                    // データを取得する。
                    emailAddress = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));

                    // GMailアドレスチェック有りの場合
                    if (check) {
                        // GMailアドレスではない場合
                        if (!emailAddress.contains(GMAIL_ADDR_SUFFIX)) {
                            // クリアする。
                            emailAddress = "";
                        }
                    }
                }
            }
        } finally {
            // カーソルが有効な場合
            if (null != cursor) {
                // カーソルをクローズする。
                cursor.close();
            }
        }

        mLogger.d("OUT(OK)");
        return emailAddress;
    }

    /**************************************************************************/
    /**
     * 送信元メールアドレスプリフェレンスクリックリスナークラス
     *
     */
    private class MailFromOnPreferenceClickListener implements OnPreferenceClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(MailFromOnPreferenceClickListener.class);

        /**
         * 設定をクリックされた時に呼び出される。
         *
         * @param preference 設定
         */
        @Override
        public boolean onPreferenceClick(Preference preference) {
            mLogger.d("IN");

            // メールアドレス選択画面を表示する。
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI);
            startActivityForResult(intent, RequestCode.PICK_CONTACT_FROM);

            mLogger.d("OUT(OK)");
            return true;
        }
    }

    /**************************************************************************/
    /**
     * 送信先メールアドレスプリフェレンスクリックリスナークラス
     *
     */
    private class MailToOnPreferenceClickListener implements OnPreferenceClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(MailToOnPreferenceClickListener.class);

        /**
         * 設定をクリックされた時に呼び出される。
         *
         * @param preference 設定
         */
        @Override
        public boolean onPreferenceClick(Preference preference) {
            mLogger.d("IN");

            // メールアドレス選択画面を表示する。
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Email.CONTENT_URI);
            startActivityForResult(intent, RequestCode.PICK_CONTACT_TO);

            mLogger.d("OUT(OK)");
            return true;
        }
    }

    /**************************************************************************/
    /**
     * メール設定クリアプリフェレンスクリックリスナークラス
     *
     */
    private class MailClearOnPreferenceClickListener implements OnPreferenceClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(MailClearOnPreferenceClickListener.class);

        /** アクティビティ */
        private Activity mActivity;

        /**
         * コンストラクタ
         *
         * @param activity アクティビティ
         */
        public MailClearOnPreferenceClickListener(Activity activity) {
            mActivity = activity;
        }

        /**
         * 設定をクリックされた時に呼び出される。
         *
         * @param preference 設定
         */
        @Override
        public boolean onPreferenceClick(Preference preference) {
            mLogger.d("IN");

            // メール設定クリア画面を表示する。
            Intent intent = new Intent(SettingsPreferenceActivity.this, MailClearActivity.class);
            mActivity.startActivityForResult(intent, RequestCode.MAIL_CLEAR);

            mLogger.d("OUT(OK)");
            return true;
        }
    }

    /**************************************************************************/
    /**
     * 除外場所プリフェレンスクリックリスナークラス
     *
     */
    private class ExclusionPlaceOnPreferenceClickListener implements OnPreferenceClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(ExclusionPlaceOnPreferenceClickListener.class);

        /**
         * 設定をクリックされた時に呼び出される。
         *
         * @param preference 設定
         */
        @Override
        public boolean onPreferenceClick(Preference preference) {
            mLogger.d("IN");

            // 除外選択画面を表示する。
            Intent intent = new Intent(SettingsPreferenceActivity.this, SelectExclusionPlaceActivity.class);
            startActivityForResult(intent, RequestCode.EXCLUSION_PLACE);

            mLogger.d("OUT(OK)");
            return true;
        }
    }
}
