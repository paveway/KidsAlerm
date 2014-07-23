package info.paveway.kidsalerm;

import info.paveway.kidsalerm.CommonConstants.PrefsKey;
import info.paveway.kidsalerm.CommonConstants.RequestCode;
import info.paveway.kidsalerm.dialog.EditDialogPreference;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
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

    /** プリフェレンス */
    SharedPreferences mPrefs;

    /** アプリケーションパスワード */
    private EditDialogPreference mAppPassword;

    /** メールユーザ名 */
    private EditTextPreference mMailUserName;

    /** メールパスワード */
    private EditTextPreference mMailPassword;

    /** 送信元メールアドレス */
    private PreferenceScreen mMailFrom;

    /** 送信先メールアドレス */
    private PreferenceScreen mMailTo;

    /** 電源ON監視 */
    private CheckBoxPreference mMonitorPowerOn;

    /** 電源OFF監視 */
    private CheckBoxPreference mMonitorPowerOff;

    /** 滞在監視 */
    private CheckBoxPreference mMonitorStay;

    /** 滞在監視時間 */
    private EditTextPreference mMonitorStayTime;

    /** 滞在監視除外場所選択 */
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

        // 各入力項目を取得する。
        mAppPassword     = (EditDialogPreference)findPreference(PrefsKey.APP_PASSWORD);
        mMailUserName    = (EditTextPreference)findPreference(PrefsKey.MAIL_USER_NAME);
        mMailUserName    = (EditTextPreference)findPreference(PrefsKey.MAIL_USER_NAME);
        mMailPassword    = (EditTextPreference)findPreference(PrefsKey.MAIL_PASSWORD);
        mMailFrom        = (PreferenceScreen)findPreference(PrefsKey.MAIL_FROM);
        mMailTo          = (PreferenceScreen)findPreference(PrefsKey.MAIL_TO);
        mMonitorPowerOn  = (CheckBoxPreference)findPreference(PrefsKey.MONITOR_POWER_ON);
        mMonitorPowerOff = (CheckBoxPreference)findPreference(PrefsKey.MONITOR_POWER_OFF);
        mMonitorStay     = (CheckBoxPreference)findPreference(PrefsKey.MONITOR_STAY);
        mMonitorStayTime = (EditTextPreference)findPreference(PrefsKey.MONITOR_STAY_TIME);
        mExclusionPlace  = (PreferenceScreen)findPreference(PrefsKey.EXCULSION_PLACE);

        // アプリケーションパスワードが入力済みの場合
        String appPassword = mPrefs.getString(PrefsKey.APP_PASSWORD, "");
        if (StringUtil.isNotNullOrEmpty(appPassword)) {
            // サマリーを設定する。
            mAppPassword.setSummary("設定済み：●●●●●");
        }

        // メールユーザ名が入力済みの場合
        String mailUserName = mPrefs.getString(PrefsKey.MAIL_USER_NAME, "");
        if (StringUtil.isNotNullOrEmpty(mailUserName)) {
            // サマリーを設定する。
            mMailUserName.setSummary("設定済み：" + mailUserName);
        }

        String mailPassword = mPrefs.getString(PrefsKey.MAIL_PASSWORD, "");
        if (StringUtil.isNotNullOrEmpty(mailPassword)) {
            // サマリーを設定する。
            mMailPassword.setSummary("設定済み：●●●●●");
        }

        String mailFrom = mPrefs.getString(PrefsKey.MAIL_FROM, "");
        if (StringUtil.isNotNullOrEmpty(mailFrom)) {
            // サマリーを設定する。
            mMailFrom.setSummary("設定済み：" + mailFrom);
        }

        String mailTo = mPrefs.getString(PrefsKey.MAIL_TO, "");
        if (StringUtil.isNotNullOrEmpty(mailTo)) {
            // サマリーを設定する。
            mMailTo.setSummary("設定済み：" + mailTo);
        }

        String monitorStatyTime = mPrefs.getString(PrefsKey.MONITOR_STAY_TIME, "");
        if (StringUtil.isNotNullOrEmpty(monitorStatyTime)) {
            // サマリーを設定する。
            mMonitorStayTime.setSummary("設定済み：" + monitorStatyTime + "分");
        }

        // クリックリスナーを設定する。
        mMailFrom.setOnPreferenceClickListener(new MailFromOnPreferenceClickListener());
        mMailTo.setOnPreferenceClickListener(new MailToOnPreferenceClickListener());
        mExclusionPlace.setOnPreferenceClickListener(new ExclusionPlaceOnPreferenceClickListener());

        // ビューの可否を設定する。
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

        // 送信元メールアドレス選択の場合
        if (RequestCode.PICK_CONTACT_FROM == requestCode) {
            // 正常終了の場合
            if (RESULT_OK == resultCode) {
                // データがある場合
                if (null != data) {
                    // メールアドレスを取得する。
                    String emailAddress = getEmailAddressFrom(data.getData());
                    // メールアドレスが取得できた場合
                    if (StringUtil.isNotNullOrEmpty(emailAddress)) {
                        Editor editor = mPrefs.edit();
                        editor.putString(PrefsKey.MAIL_FROM, emailAddress);
                        editor.commit();
                        mMailFrom.setSummary(emailAddress);
                        mLogger.d("OUT(OK)");
                        return;
                    } else {
                        Toast.makeText(this, "GMailアドレスではありません", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        // 送信先メールアドレス選択の場合
        } else if (RequestCode.PICK_CONTACT_TO == requestCode) {
            if (RESULT_OK == resultCode) {
                if (null != data) {
                    String emailAddress = getEmailAddressTo(data.getData());
                    if (StringUtil.isNotNullOrEmpty(emailAddress)) {
                        Editor editor = mPrefs.edit();
                        editor.putString(PrefsKey.MAIL_TO, emailAddress);
                        editor.commit();
                        mMailTo.setSummary(emailAddress);
                        mLogger.d("OUT(OK)");
                        return;
                    }
                }
            }

        // 滞在監視除外選択画面の場合
        } else if (RequestCode.EXCLUSION_PLACE == requestCode) {

        }

        // スーパークラスのメソッドを呼び出す。
        mLogger.d("OUT(OK)");
    }

    /**
     * ビューを有効にする。
     */
    public void enableViews() {
        mLogger.d("IN");

        // アプリケーションパスワードを取得する。
        boolean enabled = false;
        String appPassword = mPrefs.getString(PrefsKey.APP_PASSWORD, "");

        // アプリケーションパスワードが取得できた場合
        if (StringUtil.isNotNullOrEmpty(appPassword)) {
            mAppPassword.setSummary("設定済み：●●●●●");
            enabled = true;

        // アプリケーションパスワードが取得できない場合
        } else {
            mAppPassword.setSummary("アプリケーションのパスワードを入力してください");
        }

        // 各項目の可否を設定する。
        mMailUserName.setEnabled(enabled);
        mMailPassword.setEnabled(enabled);
        mMailFrom.setEnabled(enabled);
        mMailTo.setEnabled(enabled);
        mMonitorPowerOn.setEnabled(enabled);
        mMonitorPowerOff.setEnabled(enabled);
        mMonitorStay.setEnabled(enabled);
        mMonitorStayTime.setEnabled(enabled);
        mExclusionPlace.setEnabled(enabled);

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
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    emailAddress = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA1));

                    // GMailアドレスチェック有りの場合
                    if (check) {
                        // GMailアドレスではない場合
                        if (!emailAddress.contains("@gmail.com")) {
                            // クリアする。
                            emailAddress = "";
                        }
                    }
                }
            }
        } finally {
            if (null != cursor) {
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
     * 除外場所プリフェレンスクリックリスナークラス
     *
     */
    private class ExclusionPlaceOnPreferenceClickListener implements OnPreferenceClickListener {

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
