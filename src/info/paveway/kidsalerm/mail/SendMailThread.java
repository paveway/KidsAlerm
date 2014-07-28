package info.paveway.kidsalerm.mail;

import info.paveway.kidsalerm.CommonConstants.Encoding;
import info.paveway.kidsalerm.CommonConstants.MailPropKey;
import info.paveway.log.Logger;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.event.TransportListener;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.os.StrictMode;

/**
 * キッズアラーム
 * メールデータクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class SendMailThread implements Runnable {

    /** ロガー */
    private Logger mLogger = new Logger(SendMailThread.class);

    /** プロトコル */
    private static final String PROTOCOL = "smtp";

    private class MailPropValue {
        private static final String SMTP_HOST = "smtp.gmail.com";
        private static final String SMTP_AUTH = "true";
        private static final String SMTP_PORT = "587";
        private static final String SMTP_STARTTLS_ENABLE = "true";
    }

    /** メールデータ */
    private MailData mMailData;

    /** 送信リスナー */
    private TransportListener mListener;

    /**
     * コンストラクタ
     *
     * @param mailData メールデータ
     */
    public SendMailThread(MailData mailData) {
        this(mailData, null);
    }

    /**
     * コンストラクタ
     *
     * @param mailData メールデータ
     * @param listener リスナー
     */
    public SendMailThread(MailData mailData, TransportListener listener) {
        mMailData = mailData;
        mListener = listener;
    }

    /**
     * メールを送信する。
     */
    @Override
    public void run() {
        mLogger.d("IN");

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());

        // セッションプロパティを設定する。
        Properties props = new Properties();
        props.put(MailPropKey.SMTP_HOST,            MailPropValue.SMTP_HOST);
        props.put(MailPropKey.SMTP_AUTH,            MailPropValue.SMTP_AUTH);
        props.put(MailPropKey.SMTP_PORT,            MailPropValue.SMTP_PORT);
        props.put(MailPropKey.SMTP_STARTTLS_ENABLE, MailPropValue.SMTP_STARTTLS_ENABLE);

        // セッション
        Session session = Session.getDefaultInstance(props);
        session.setDebug(true);

        Transport t = null;
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(     new InternetAddress(mMailData.getFrom()));
            msg.setSender(   new InternetAddress(mMailData.getTo()));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(mMailData.getTo()));
            msg.setSubject(  mMailData.getSubject());
            msg.setText(     mMailData.getText(),	Encoding.UTF_8);

            // トランスポートを取得する。
            t = session.getTransport(PROTOCOL);

            // リスナーが有効な場合
            if (null != mListener) {
                // リスナーを設定する。
                t.addTransportListener(mListener);
            }

            // 接続する。
            t.connect(mMailData.getUserName(), mMailData.getPassword());

            // メールを送信する。
            t.sendMessage(msg, msg.getAllRecipients());
        } catch (Exception e) {
            mLogger.e(e);

        } finally {
            if (null != t) {
                try {
                    t.close();
                } catch (Exception e) {
                    mLogger.e(e);
                }
            }
        }

        mLogger.d("OUT(OK)");
    }
}
