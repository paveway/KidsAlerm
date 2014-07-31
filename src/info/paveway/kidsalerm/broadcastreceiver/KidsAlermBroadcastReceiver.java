package info.paveway.kidsalerm.broadcastreceiver;

import info.paveway.kidsalerm.CommonConstants.Action;
import info.paveway.kidsalerm.CommonConstants.ExtraKey;
import info.paveway.kidsalerm.CommonConstants.PrefsKey;
import info.paveway.kidsalerm.R;
import info.paveway.kidsalerm.mail.MailData;
import info.paveway.kidsalerm.mail.SendMailThread;
import info.paveway.log.Logger;
import info.paveway.util.ServiceUtil;
import info.paveway.util.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * キッズアラーム
 * ブロードキャストレシーバークラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class KidsAlermBroadcastReceiver extends BroadcastReceiver {

    /** ロガー */
    private Logger mLogger = new Logger(KidsAlermBroadcastReceiver.class);

    /** 緯度 */
    private String mLatitude;

    /** 経度 */
    private String mLongitude;

    /**
     * ブロードキャストを受信した場合
     *
     * @param context コンテキスト
     * @param intent インテント
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        mLogger.d("IN");

        // アクションを取得する。
        String action = intent.getAction();
        mLogger.d("action=[" + action + "]");

        // リソースを取得する。
        Resources resources = context.getResources();

        // プリフェレンスを取得する。
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // メールデータが生成する。
        MailData mailData = createMailData(resources, prefs);

        // メールデータが生成できない場合
        if (null == mailData) {
            // エラーメッセージを表示する。
            Toast.makeText(context, resources.getString(R.string.send_mail_error_data), Toast.LENGTH_SHORT).show();
            mLogger.w("OUT(NG)");
            return;
        }

        // 起動した時
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            actionBootComplete(context, resources, prefs, mailData);

        // 終了した時
        } else if (Intent.ACTION_SHUTDOWN.equals(action)) {
            actionShutdown(resources, prefs, mailData);

        // 電源ONメールの場合
        } else if (Action.POWER_ON_MAIL.equals(action)) {
            actionPowerOnMail(resources, mailData);

        // 位置情報の場合
        } else if (Action.LOCATION.equals(action)) {
            actionLocation(intent);

        // 滞在通知の場合
        } else if (Action.STAY.equals(action)) {
            actionStay(resources, prefs, mailData, intent);
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * メールデータを生成する。
     *
     * @param resources リソース
     * @param prefs プリフェレンス
     * @return メールデータ
     */
    private MailData createMailData(Resources resources, SharedPreferences prefs) {
        mLogger.d("IN");

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
            mLogger.w("OUT(NG)");
            return null;
        }

        // メールデータを生成する。
        MailData mailData = new MailData();
        mailData.setUserName(userName);
        mailData.setPassword(password);
        mailData.setFrom(    from);
        mailData.setTo(      to);
        mailData.setSubject(resources.getString(R.string.send_mail_subject));

        mLogger.d("OUT(OK)");
        return mailData;
    }

    /**
     * 起動完了の処理を行う。
     *
     * @param context コンテキスト
     * @param resources リソース
     * @param prefs プリフェレンス
     * @param mailData メールデータ
     */
    private void actionBootComplete(Context context, Resources resources, SharedPreferences prefs, MailData mailData) {
        mLogger.d("IN");

        // 通知対象の場合
        if (prefs.getBoolean(PrefsKey.NOTICE_POWER_ON, false)) {
            // 起動完了を保存する。
            Editor editor = prefs.edit();
            editor.putBoolean(PrefsKey.POWER_ON, true);
            editor.commit();
        }

        // サービスを起動する。
        if (!ServiceUtil.startService(context)) {
            // サービスが起動できない場合
            // メールを送信する。
            mailData.setText(resources.getString(R.string.send_mail_text_error_service));
            sendMail(mailData);
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * 終了時の処理を行う。
     *
     * @param resources リソース
     * @param prefs プリフェレンス
     * @param mailData メールデータ
     */
    private void actionShutdown(Resources resources, SharedPreferences prefs, MailData mailData) {
        mLogger.d("IN");

        // 通知対象の場合
        if (prefs.getBoolean(PrefsKey.NOTICE_POWER_OFF, false)) {
            // メールを送信する。
            StringBuilder text = new StringBuilder();
            text.append(resources.getString(R.string.send_mail_text_power_off1));
            text.append(getTimeString(resources));
            text.append(resources.getString(R.string.send_mail_text_power_off2));
            text.append(mLatitude);
            text.append(resources.getString(R.string.send_mail_text_power_off3));
            text.append(mLongitude);
            mailData.setText(text.toString());
            sendMail(mailData);
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * 電源ONメール送信の処理を行う。
     *
     * @param resources リソース
     * @param mailData メールデータ
     */
    private void actionPowerOnMail(Resources resources, MailData mailData) {
        mLogger.d("IN");

        // メールを送信する。
        StringBuilder text = new StringBuilder();
        text.append(resources.getString(R.string.send_mail_text_power_on1));
        text.append(getTimeString(resources));
        text.append(resources.getString(R.string.send_mail_text_power_on2));
        text.append(mLatitude);
        text.append(resources.getString(R.string.send_mail_text_power_on3));
        text.append(mLongitude);
        mailData.setText(text.toString());
        sendMail(mailData);

        mLogger.d("OUT(OK)");
    }

    /**
     * ロケーションの処理を行う。
     *
     * @param intent データ
     */
    private void actionLocation(Intent intent) {
        mLogger.d("IN");

        // 緯度、経度を取得する。
        mLatitude  = String.valueOf(intent.getDoubleExtra(ExtraKey.LATITUDE,  0));
        mLongitude = String.valueOf(intent.getDoubleExtra(ExtraKey.LONGITUDE, 0));

        mLogger.d("OUT(OK)");
    }

    /**
     * 滞在の処理を行う。
     *
     * @param resources リソース
     * @param prefs プリフェレンス
     * @param mailData メールデータ
     * @param intent データ
     */
    private void actionStay(Resources resources, SharedPreferences prefs, MailData mailData, Intent intent) {
        mLogger.d("IN");

        // 緯度、経度を取得する。
        mLatitude  = String.valueOf(intent.getDoubleExtra(ExtraKey.LATITUDE,  0));
        mLongitude = String.valueOf(intent.getDoubleExtra(ExtraKey.LONGITUDE, 0));

        // メールを送信する。
        StringBuilder text = new StringBuilder();
        text.append(resources.getString(R.string.send_mail_text_stay1));
        text.append(prefs.getString(PrefsKey.NOTICE_STAY_TIME, ""));
        text.append(resources.getString(R.string.send_mail_text_stay2));
        text.append(getTimeString(resources));
        text.append(resources.getString(R.string.send_mail_text_stay3));
        text.append(mLatitude);
        text.append(resources.getString(R.string.send_mail_text_stay4));
        text.append(mLongitude);
        mailData.setText(text.toString());
        sendMail(mailData);

        mLogger.d("OUT(OK)");
    }

    /**
     * メールを送信する。
     *
     * @param mailData メールデータ
     */
    private void sendMail(MailData mailData) {
        mLogger.d("IN");

        // メールを送信する。
        new Thread(new SendMailThread(mailData)).start();

        mLogger.d("OUT(OK)");
    }

    /**
     * 日時文字列を取得する。
     *
     * @param resources リソース
     * @return 日時文字列
     */
    private String getTimeString(Resources resources) {
        SimpleDateFormat sdf =
                new SimpleDateFormat(resources.getString(R.string.send_mail_date_format), Locale.getDefault());
        return sdf.format(new Date());
    }
}
