package info.paveway.kidsalerm.mail;

/**
 * キッズアラーム
 * メールデータクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class MailData {

    /** ユーザ名 */
    private String mUserName;

    /** パスワード */
    private String mPassword;

    /** 送信元メールアドレス */
    private String mFrom;

    /** 送信先メールアドレス */
    private String mTo;

    /** 件名 */
    private String mSubject;

    /** メール本文 */
    private String mText;

    /**
     * ユーザ名を設定する。
     *
     * @param userName ユーザ名
     */
    public void setUserName(String userName) {
        mUserName = userName;
    }

    /**
     * ユーザ名を取得する。
     *
     * @return ユーザ名
     */
    public String getUserName() {
        return mUserName;
    }

    /**
     * パスワードを設定する。
     *
     * @param password パスワード
     */
    public void setPassword(String password) {
        mPassword = password;
    }

    /**
     * パスワードを取得する。
     *
     * @return パスワード
     */
    public String getPassword() {
        return mPassword;
    }

    /**
     * 送信元メールアドレスを設定する。
     *
     * @param from 送信元メールアドレス
     */
    public void setFrom(String from) {
        mFrom = from;
    }

    /**
     * 送信元メールアドレスを取得する。
     *
     * @return 送信元メールアドレス
     */
    public String getFrom() {
        return mFrom;
    }

    /**
     * 送信先メールアドレスを設定する。
     *
     * @param to 送信先メールアドレス
     */
    public void setTo(String to) {
        mTo = to;
    }

    /**
     * 送信先メールアドレスを取得する。
     *
     * @return 送信先メールアドレス
     */
    public String getTo() {
        return mTo;
    }

    /**
     * 件名を設定する。
     *
     * @param subject 件名
     */
    public void setSubject(String subject) {
        mSubject = subject;
    }

    /**
     * 件名を取得する。
     *
     * @return 件名
     */
    public String getSubject() {
        return mSubject;
    }

    /**
     * メール本文を設定する。
     *
     * @param text メール本文
     */
    public void setText(String text) {
        mText = text;
    }

    /**
     * メール本文を取得する。
     *
     * @return メール本文
     */
    public String getText() {
        return mText;
    }
}
