package info.paveway.kidsalerm;

import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import info.paveway.kidsalerm.CommonConstants.PrefsKey;
import info.paveway.kidsalerm.CommonConstants.RequestCode;
import info.paveway.kidsalerm.CommonConstants.TestDeviceId;
import info.paveway.kidsalerm.dialog.ProgressStatusDialog;
import info.paveway.kidsalerm.mail.MailData;
import info.paveway.kidsalerm.mail.SendMailThread;
import info.paveway.kidsalerm.service.LocationService;
import info.paveway.log.Logger;
import info.paveway.util.ServiceUtil;
import info.paveway.util.StringUtil;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * キッズアラーム
 * 開始画面
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class StartupActivity extends AbstractBaseActivity {

    /** ロガー */
    private Logger mLogger = new Logger(StartupActivity.class);

    /** ADビュー */
    private AdView mAdView;

    /** ハンドラ */
    private Handler mHandler = new Handler();

    /** 通知サービス状態 */
    private TextView mServiceStatusValue;

    /** ネットワーク状態 */
    private TextView mNetworkStatusValue;

    /** メール設定 */
    private TextView mMailSettingValue;

    /** メール送信状態 */
    private TextView mMailSendStatusValue;

    /** メール送信状態 */
    private String mMailSendStatus;

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

         // AdView をリソースとしてルックアップしてリクエストを読み込む
        mAdView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest =
                new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(TestDeviceId.NEXUS7)
                    .build();
        mAdView.loadAd(adRequest);

        mMailSendStatus = getResourceString(R.string.startup_mail_send_status_default);

        mServiceStatusValue    = (TextView)findViewById(R.id.serviceStatusValue);
        mNetworkStatusValue    = (TextView)findViewById(R.id.networkStatusValue);
        mMailSettingValue      = (TextView)findViewById(R.id.mailSettingValue);
        mMailSendStatusValue   = (TextView)findViewById(R.id.mailSendStatusValue);
        mStartServiceButton    = (Button)findViewById(R.id.startServiceButton);
        mStopServiceButton     = (Button)findViewById(R.id.stopServiceButton);
        mConfirmSendMailButton = (Button)findViewById(R.id.confirmSendMailButton);
        mStartServiceButton.setOnClickListener(   new ButtonOnClickListener(StartupActivity.this));
        mStopServiceButton.setOnClickListener(    new ButtonOnClickListener(StartupActivity.this));
        mConfirmSendMailButton.setOnClickListener(new ButtonOnClickListener(StartupActivity.this));
        ((Button)findViewById(R.id.settingsPreferenceButton)).setOnClickListener(new ButtonOnClickListener(StartupActivity.this));

        // 各ビューを設定する。
        setViews();

        mLogger.d("OUT(OK)");
    }

    @Override
    public void onPause() {
        mAdView.pause();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdView.resume();
    }

    @Override
    public void onDestroy() {
        mAdView.destroy();
        super.onDestroy();
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
            // 各ビューを設定する。
            setViews();
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
     * 各ビューを設定する。
     */
    private void setViews() {
        mLogger.d("IN");

        // 各状態を設定する。
        setStatusValue();

        // ボタンの有効設定を行う。
        enableButton();
    }

    /**
     * 各状態を設定する。
     */
    private void setStatusValue() {
        mLogger.d("IN");

        // サービスが開始されている場合
        String serviceStatus = getResourceString(R.string.startup_service_status_stop);
        if (ServiceUtil.isServiceRunning(StartupActivity.this, LocationService.class)) {
            serviceStatus = getResourceString(R.string.startup_service_status_start);
        }
        mServiceStatusValue.setText(serviceStatus);

        String networkStatus = getResourceString(R.string.startup_network_status_disconnect);
        if (isNetworkEnabled()) {
            networkStatus = getResourceString(R.string.startup_network_status_connect);
        }
        mNetworkStatusValue.setText(networkStatus);

        String mailSetting = getResourceString(R.string.startup_mail_setting_init);

        // メール設定の設定値を取得する。
        String userName = mPrefs.getString(PrefsKey.MAIL_USER_NAME, "");
        String password = mPrefs.getString(PrefsKey.MAIL_PASSWORD, "");
        String from     = mPrefs.getString(PrefsKey.MAIL_FROM, "");
        String to       = mPrefs.getString(PrefsKey.MAIL_TO, "");
        // 全て設定済みの場合
        if (StringUtil.isNotNullOrEmpty(userName) &&
            StringUtil.isNotNullOrEmpty(password) &&
            StringUtil.isNotNullOrEmpty(from)     &&
            StringUtil.isNotNullOrEmpty(to)) {
            mailSetting = getResourceString(R.string.startup_mail_setting_done);
        }
        mMailSettingValue.setText(mailSetting);

        mMailSendStatusValue.setText(mMailSendStatus);

        mLogger.d("OUT(OK)");
    }

    /**
     * ボタンの有効無効を設定する。
     */
    private void enableButton() {
        mLogger.d("IN");

        boolean mailEnable = false;
        boolean serviceEnable = false;

        // アプリケーションパスワードを取得する。
        String appPassword = mPrefs.getString(PrefsKey.APP_PASSWORD, "");
        // アプリケーションパスワードが取得できた場合
        if (StringUtil.isNotNullOrEmpty(appPassword)) {
            // ネットワークが有効な場合
            if (isNetworkEnabled()) {
                // メール設定の設定値を取得する。
                String userName = mPrefs.getString(PrefsKey.MAIL_USER_NAME, "");
                String password = mPrefs.getString(PrefsKey.MAIL_PASSWORD, "");
                String from     = mPrefs.getString(PrefsKey.MAIL_FROM, "");
                String to       = mPrefs.getString(PrefsKey.MAIL_TO, "");
                // 全て設定済みの場合
                if (StringUtil.isNotNullOrEmpty(userName) &&
                    StringUtil.isNotNullOrEmpty(password) &&
                    StringUtil.isNotNullOrEmpty(from)     &&
                    StringUtil.isNotNullOrEmpty(to)) {
                    mailEnable = true;
                }
            }

            // サービスが開始されている場合
            if (ServiceUtil.isServiceRunning(StartupActivity.this, LocationService.class)) {
                serviceEnable = true;
            }
        }

        mStartServiceButton.setEnabled(!serviceEnable);
        mStopServiceButton.setEnabled(serviceEnable);
        mConfirmSendMailButton.setEnabled(mailEnable);

        mLogger.d("OUT(OK)");
    }

    /**
     * ネットワークが有効かチェックする。
     *
     * @return チェック結果
     */
    private boolean isNetworkEnabled() {
        mLogger.d("IN");

        boolean result = false;
        ConnectivityManager manager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (null != networkInfo) {
            if (networkInfo.isConnected()) {
                result = true;
            }
        }

        mLogger.d("OUT(OK)");
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
                onStartServiceButton();
                break;

            // サービス停止ボタンの場合
            case R.id.stopServiceButton:
                onStopServiceButton();
                break;

            // 設定画面ボタンの場合
            case R.id.settingsPreferenceButton:
                onSettingsPreferenceButton();
                break;

            // メール送信確認
            case R.id.confirmSendMailButton:
                onConfirmSendMailButton();
                break;
            }

            mLogger.d("OUT(OK)");
        }

        /**
         * サービス開始ボタンの処理を行う。
         */
        private void onStartServiceButton() {
            mLogger.d("IN");

            // サービスを開始する。
            ServiceUtil.startService(mContext);

            // 各ビューを設定する。
            setViews();

            mLogger.d("OUT(OK)");
        }

        /**
         * サービス停止ボタンの処理を行う。
         */
        private void onStopServiceButton() {
            mLogger.d("IN");

            // サービスを停止する。
            ServiceUtil.stopService(mContext);

            // 各ビューを設定する。
            setViews();

            mLogger.d("OUT(OK)");
        }

        /**
         * 設定画面ボタンの処理を行う。
         */
        private void onSettingsPreferenceButton() {
            mLogger.d("IN");

            // 設定画面を表示する。
            Intent intent = new Intent(StartupActivity.this, SettingsPreferenceActivity.class);
            startActivityForResult(intent, RequestCode.SETTINGS_PREFERENCE);

            mLogger.d("OUT(OK)");
        }

        /**
         * メール送信確認ボタンの処理を行う。
         */
        private void onConfirmSendMailButton() {
            mLogger.d("IN");

            // メール設定の設定値を取得する。
            String userName = mPrefs.getString(PrefsKey.MAIL_USER_NAME, "");
            String password = mPrefs.getString(PrefsKey.MAIL_PASSWORD, "");
            String from     = mPrefs.getString(PrefsKey.MAIL_FROM, "");
            String to       = mPrefs.getString(PrefsKey.MAIL_TO, "");

            // 未設定項目がある場合
            if (StringUtil.isNullOrEmpty(userName) ||
                StringUtil.isNullOrEmpty(password) ||
                StringUtil.isNullOrEmpty(from)     ||
                StringUtil.isNullOrEmpty(to)) {
                // エラーメッセージを表示する。
                toast(R.string.send_mail_error_data);
                mLogger.w("OUT(NG)");
                return;
            }

            // プログレスダイアログを表示する。
            showProgressDialog();

            // カウントダウンタイマーを開始する。
            startSendMailCountDownTimer();

            // メールを送信する。
            sendMail(userName, password, from, to);

            mLogger.d("OUT(OK)");
        }

        /**
         * プログレスダイアログを表示する。
         */
        private void showProgressDialog() {
            mLogger.d("IN");

            FragmentManager manager = getSupportFragmentManager();
            mProgressDialog =
                    ProgressStatusDialog.newInstance(
                            getResourceString(R.string.progress_dialog_title_send_mail),
                            getResourceString(R.string.progress_dialog_message));
            mProgressDialog.setCancelable(false);
            mProgressDialog.show(manager, ProgressStatusDialog.class.getSimpleName());

            mLogger.d("OUT(OK)");
        }

        /**
         * メール送信カウントダウンタイマーを開始する。
         */
        private void startSendMailCountDownTimer() {
            mLogger.d("IN");

            mSendMailCountDownTimer = new SendMailCountDownTimer(60 * 1000, 10 * 1000);
            mSendMailCountDownTimer.start();

            mLogger.d("OUT(OK)");
        }

        /**
         * メールを送信する。
         *
         * @param userName ユーザ名
         * @param password パスワード
         * @param from 送信元メールアドレス
         * @param to 送信先メールアドレス
         */
        private void sendMail(String userName, String password, String from, String to) {
            mLogger.d("IN");

            MailData mailData = new MailData();
            mailData.setUserName(userName);
            mailData.setPassword(password);
            mailData.setFrom(    from);
            mailData.setTo(      to);
            mailData.setSubject( getResourceString(R.string.send_mail_subject));
            mailData.setText(getResourceString(R.string.send_mail_text_test));
            new Thread(new SendMailThread(mailData, new MailTransportListener())).start();

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

        /**
         * メールが送信された時に呼び出される。
         *
         * @param event イベント
         */
        @Override
        public void messageDelivered(TransportEvent event) {
            mLogger.d("IN");

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onFinished(getResourceString(R.string.startup_mail_send_status_success));
                }
            });

            mLogger.d("OUT(OK)");
        }

        /**
         * メールが送信されなかった時に呼び出される。
         *
         * @param event イベント
         */
        @Override
        public void messageNotDelivered(TransportEvent event) {
            mLogger.d("IN");

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onFinished(getResourceString(R.string.startup_mail_send_status_failed));
                }
            });

            mLogger.d("OUT(OK)");
        }

        /**
         * メールが一部送信された時に呼び出される。
         *
         * @param event イベント
         */
        @Override
        public void messagePartiallyDelivered(TransportEvent event) {
            mLogger.d("IN");

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onFinished(getResourceString(R.string.startup_mail_send_status_partially));
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
                        onFinished(getResourceString(R.string.startup_mail_send_status_timeout));
                    }
                }
            });

            mLogger.d("OUT(OK)");
        }
    }

    /**
     * 処理が完了した時の処理を行う。
     *
     * @param text テキスト
     */
    private void onFinished(String text) {
        mLogger.d("IN");

        toast(text);

        // 送信中ダイアログを終了する。
        mProgressDialog.dismiss();
        mProgressDialog = null;

        // ステータス表示を変更する。
        mMailSendStatus = text;
        setStatusValue();

        mLogger.d("OUT(OK)");
    }
}
