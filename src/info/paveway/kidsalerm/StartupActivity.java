package info.paveway.kidsalerm;

import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;

import info.paveway.kidsalerm.CommonConstants.PrefsKey;
import info.paveway.kidsalerm.CommonConstants.RequestCode;
import info.paveway.kidsalerm.dialog.ProgressStatusDialog;
import info.paveway.kidsalerm.mail.MailData;
import info.paveway.kidsalerm.mail.SendMailThread;
import info.paveway.kidsalerm.service.KidsAlermService;
import info.paveway.log.Logger;
import info.paveway.util.ServiceUtil;
import info.paveway.util.StringUtil;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * キッズアラーム
 * 開始画面
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class StartupActivity extends ActionBarActivity {

    /** ロガー */
    private Logger mLogger = new Logger(StartupActivity.class);

    /** 通知サービス状態 */
    private TextView mServiceStatusValue;

    /** ネットワーク状態 */
    private TextView mNetworkStatusValue;

    /** メール設定 */
    private TextView mMailSettingValue;

    /** メール送信状態 */
    private TextView mMailSendStatusValue;

    private String mMailSendStatus = "未確認";

    /** サービス開始ボタン */
    private Button mStartServiceButton;

    /** サービス停止ボタン */
    private Button mStopServiceButton;

    /** メール送信確認ボタン */
    private Button mConfirmSendMailButton;

    /** 処理中ダイアログ */
    private ProgressStatusDialog mProgressDialog;

    /** メール送信カウントダウンタイマー */
    private SendMailCountDownTimer mSendMailCountDownTimer;

    /** ハンドラ */
    private Handler mHandler = new Handler();

    /**
     * 生成した時に呼び出される。
     *
     * @param savendInstanceState 保存した時のインスタンスの状態
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLogger.d("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // レイアウトを設定する。
        setContentView(R.layout.activity_startup);

        mServiceStatusValue    = (TextView)findViewById(R.id.serviceStatusValue);
        mNetworkStatusValue    = (TextView)findViewById(R.id.networkStatusValue);
        mMailSettingValue      = (TextView)findViewById(R.id.mailSettingValue);
        mMailSendStatusValue   = (TextView)findViewById(R.id.mailSendStatusValue);
        mStartServiceButton    = (Button)findViewById(R.id.startServiceButton);
        mStopServiceButton     = (Button)findViewById(R.id.stopServiceButton);
        mConfirmSendMailButton = (Button)findViewById(R.id.confirmSendMailButton);
        mStartServiceButton.setOnClickListener(new ButtonOnClickListener(StartupActivity.this));
        mStopServiceButton.setOnClickListener(new ButtonOnClickListener(StartupActivity.this));
        mConfirmSendMailButton.setOnClickListener(new ButtonOnClickListener(StartupActivity.this));
        ((Button)findViewById(R.id.settingsPreferenceButton)).setOnClickListener(new ButtonOnClickListener(StartupActivity.this));

        // 各状態を設定する。
        setStatusValue();

        // ボタンの有効設定を行う。
        enableButton();

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

        // 設定画面の場合
        if (RequestCode.SETTINGS_PREFERENCE == requestCode) {
            // 各状態を設定する。
            setStatusValue();

            // ボタンの有効設定を行う。
            enableButton();
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * 戻るボタンが押された時に呼び出される。
     */
    @Override
    public void onBackPressed() {
        mLogger.d("IN");

        // 終了する。
        finish();

        mLogger.d("OUT(OK)");
    }

    /**
     * 各状態を設定する。
     */
    private void setStatusValue() {
        // サービスが開始されている場合
        String serviceStatus = "停止";
        if (ServiceUtil.isServiceRunning(StartupActivity.this, KidsAlermService.class)) {
            serviceStatus = "開始";
        }
        mServiceStatusValue.setText(serviceStatus);

        String networkStatus = "切断";
        if (isNetworkEnabled()) {
            networkStatus = "接続";
        }
        mNetworkStatusValue.setText(networkStatus);

        String mailSetting = "未設定";
        // プリフェレンスを取得する。
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(StartupActivity.this);
        // メール設定の設定値を取得する。
        String userName = prefs.getString(PrefsKey.MAIL_USER_NAME, "");
        String password = prefs.getString(PrefsKey.MAIL_PASSWORD, "");
        String from     = prefs.getString(PrefsKey.MAIL_FROM, "");
        String to       = prefs.getString(PrefsKey.MAIL_TO, "");
        // 全て設定済みの場合
        if (StringUtil.isNotNullOrEmpty(userName) &&
            StringUtil.isNotNullOrEmpty(password) &&
            StringUtil.isNotNullOrEmpty(from)     &&
            StringUtil.isNotNullOrEmpty(to)) {
            mailSetting = "設定済み";
        }
        mMailSettingValue.setText(mailSetting);

        mMailSendStatusValue.setText(mMailSendStatus);
    }

    /**
     * ボタンの有効無効を設定する。
     */
    private void enableButton() {
        // プリフェレンスを取得する。
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(StartupActivity.this);

        boolean mailEnable = false;
        boolean serviceEnable = false;

        // アプリケーションパスワードを取得する。
        String appPassword = prefs.getString(PrefsKey.APP_PASSWORD, "");
        // アプリケーションパスワードが取得できた場合
        if (StringUtil.isNotNullOrEmpty(appPassword)) {
            // ネットワークが有効な場合
            if (isNetworkEnabled()) {
                // メール設定の設定値を取得する。
                String userName = prefs.getString(PrefsKey.MAIL_USER_NAME, "");
                String password = prefs.getString(PrefsKey.MAIL_PASSWORD, "");
                String from     = prefs.getString(PrefsKey.MAIL_FROM, "");
                String to       = prefs.getString(PrefsKey.MAIL_TO, "");
                // 全て設定済みの場合
                if (StringUtil.isNotNullOrEmpty(userName) &&
                    StringUtil.isNotNullOrEmpty(password) &&
                    StringUtil.isNotNullOrEmpty(from)     &&
                    StringUtil.isNotNullOrEmpty(to)) {
                    mailEnable = true;
                }
            }

            // サービスが開始されている場合
            if (ServiceUtil.isServiceRunning(StartupActivity.this, KidsAlermService.class)) {
                serviceEnable = true;
            }
        }

        mStartServiceButton.setEnabled(!serviceEnable);
        mStopServiceButton.setEnabled(serviceEnable);
        mConfirmSendMailButton.setEnabled(mailEnable);
    }

    /**
     * ネットワークが有効かチェックする。
     *
     * @return チェック結果
     */
    private boolean isNetworkEnabled() {
        boolean result = false;
        ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (null != networkInfo) {
            if (networkInfo.isConnected()) {
                result = true;
            }
        }

        return result;
    }

    /**************************************************************************/
    /**
     * ボタンクリックリスナークラス
     *
     */
    private class ButtonOnClickListener implements OnClickListener {

        /** ロガー */
        private Logger mLogger = new Logger(ButtonOnClickListener.class);

        /** コンテキスト */
        private Context mContext;

        /**
         * コンストラクタ
         *
         * @param context コンテキスト
         */
        public ButtonOnClickListener(Context context) {
            mLogger.d("IN");

            mContext = context;

            mLogger.d("OUT(OK)");
        }

        /**
         * ボタンがクリックされた時に呼び出される。
         *
         * @param v クリックされたボタン
         */
        @Override
        public void onClick(View v) {
            mLogger.d("IN");

            int id = v.getId();
            switch (id) {
            // サービス開始ボタンの場合
            case R.id.startServiceButton:
                // サービスを開始する。
                ServiceUtil.startService(mContext);

                // 各状態を設定する。
                setStatusValue();

                // ボタンの有効無効を設定する。
                enableButton();
                break;

            // サービス停止ボタンの場合
            case R.id.stopServiceButton:
                // サービスを停止する。
                ServiceUtil.stopService(mContext);

                // 各状態を設定する。
                setStatusValue();

                // ボタンの有効無効を設定する。
                enableButton();
                break;

            // 設定画面ボタンの場合
            case R.id.settingsPreferenceButton:
                // 設定画面を表示する。
                Intent intent = new Intent(StartupActivity.this, SettingsPreferenceActivity.class);
                startActivityForResult(intent, RequestCode.SETTINGS_PREFERENCE);
                break;

            // メール送信確認
            case R.id.confirmSendMailButton:
                // リソースを取得する。
                Resources resources = getResources();

                // メール設定の設定値を取得する。
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(StartupActivity.this);
                String userName = prefs.getString(PrefsKey.MAIL_USER_NAME, "");
                String password = prefs.getString(PrefsKey.MAIL_PASSWORD, "");
                String from     = prefs.getString(PrefsKey.MAIL_FROM, "");
                String to       = prefs.getString(PrefsKey.MAIL_TO, "");

                // 未設定項目がある場合
                if (StringUtil.isNullOrEmpty(userName) ||
                    StringUtil.isNullOrEmpty(password) ||
                    StringUtil.isNullOrEmpty(from)     ||
                    StringUtil.isNullOrEmpty(to)) {
                    // エラーメッセージを表示する。
                    Toast.makeText(
                            StartupActivity.this,
                            resources.getString(R.string.send_mail_error_data), Toast.LENGTH_SHORT).show();
                    mLogger.w("OUT(NG)");
                    return;
                }

                // メールデータを生成する。
                MailData mailData = new MailData();
                mailData.setUserName(userName);
                mailData.setPassword(password);
                mailData.setFrom(    from);
                mailData.setTo(      to);
                mailData.setSubject( resources.getString(R.string.send_mail_subject));

                FragmentManager manager = getSupportFragmentManager();
                mProgressDialog = ProgressStatusDialog.newInstance("テストメール送信中", "しばらくお待ちください");
                mProgressDialog.setCancelable(false);
                mProgressDialog.show(manager, ProgressStatusDialog.class.getSimpleName());

                // カウントダウンタイマーを開始する。
                mSendMailCountDownTimer = new SendMailCountDownTimer(60 * 1000, 10 * 1000);
                mSendMailCountDownTimer.start();

                mailData.setText("メール送信テスト");
                new Thread(new SendMailThread(mailData, new MailTransportListener())).start();
                break;
            }

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * メール送信リスナークラス
     *
     */
    private class MailTransportListener implements TransportListener {

        /** ロガー */
        private Logger mLogger = new Logger(MailTransportListener.class);

        @Override
        public void messageDelivered(TransportEvent event) {
            mLogger.d("IN");

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            StartupActivity.this,
                            "メール送信成功", Toast.LENGTH_SHORT).show();

                    mProgressDialog.dismiss();
                    mProgressDialog = null;

                    mMailSendStatus = "メール送信成功";
                    setStatusValue();
                }
            });

            mLogger.d("OUT(OK)");
        }

        @Override
        public void messageNotDelivered(TransportEvent event) {
            mLogger.d("IN");

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            StartupActivity.this,
                            "メール送信失敗", Toast.LENGTH_SHORT).show();

                    mProgressDialog.dismiss();
                    mProgressDialog = null;

                    mMailSendStatus = "メール送信失敗";
                    setStatusValue();
                }
            });

            mLogger.d("OUT(OK)");
        }

        @Override
        public void messagePartiallyDelivered(TransportEvent event) {
            mLogger.d("IN");

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(
                            StartupActivity.this,
                            "メール一部送信", Toast.LENGTH_SHORT).show();

                    mProgressDialog.dismiss();
                    mProgressDialog = null;

                    mMailSendStatus = "メール一部送信";
                    setStatusValue();
                }
            });

            mLogger.d("OUT(OK)");
        }
    }

    /**************************************************************************/
    /**
     * メール送信カウントダウンタイマークラス
     *
     */
    private class SendMailCountDownTimer extends CountDownTimer {

        /** ロガー */
        private Logger mLogger = new Logger(SendMailCountDownTimer.class);

        /**
         * コンストラクタ
         *
         * @param millisInFuture カウントダウン時間
         * @param countDownInterval カウントダウンインターバル
         */
        public SendMailCountDownTimer(long millisInFuture, long countDownInterval) {
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

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // プログレスダイアログが有効な場合
                    if (null != mProgressDialog) {
                        // プログレスダイアログを終了する。
                        mProgressDialog.dismiss();
                        mProgressDialog = null;

                        // エラーメッセージを表示する。
                        Toast.makeText(
                            StartupActivity.this,
                            "メール送信タイムアウト", Toast.LENGTH_SHORT).show();

                        mMailSendStatus = "メール送信タイムアウト";
                        setStatusValue();
                    }
                }
            });

            mLogger.d("OUT(OK)");
        }
    }
}
