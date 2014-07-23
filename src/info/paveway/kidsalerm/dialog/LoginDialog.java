package info.paveway.kidsalerm.dialog;

import info.paveway.kidsalerm.CommonConstants.PrefsKey;
import info.paveway.kidsalerm.R;
import info.paveway.kidsalerm.StartupActivity;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * キッズアラーム
 * ログインダイアログクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class LoginDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(LoginDialog.class);

    /** パスワード入力 */
    private EditText mPasswordValue;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static LoginDialog newInstance() {
        LoginDialog instance = new LoginDialog();
        return instance;
    }

    /**
     * 生成した時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     * @return ダイアログ
     */
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLogger.d("IN");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View rootView = inflater.inflate(R.layout.dialog_login, null);

        // 入力項目を取得する。
        mPasswordValue  = (EditText)rootView.findViewById(R.id.passwordValue);

        // ログインダイアログを生成する。
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.login_dialog_title);
        builder.setPositiveButton(R.string.login_dialog_positive_button, null);
        builder.setNegativeButton(R.string.login_dialog_negative_button, null);
        builder.setView(rootView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // ボタン押下でダイアログが閉じないようにリスナーを設定する。
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // ログインボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ログインボタン処理を行う。
                        doLoginButton();
                    }
                });

                // 終了ボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 終了ボタン処理を行う。
                        doEndButton();
                    }
                });
            }
        });

        mLogger.d("OUT(OK)");
        return dialog;
    }

    /**
     * ログインボタンの処理を行う。
     */
    private void doLoginButton() {
        mLogger.d("IN");

        // 入力値を取得する。
        String userPassword = mPasswordValue.getText().toString();

        // 未入力の場合
        if (StringUtil.isNullOrEmpty(userPassword)) {
            // エラーメッセージを表示する。
            toast(R.string.login_dialog_error_input_password);
            mLogger.w("OUT(NG)");
            return;
        }

        // 設定値のパスワードを取得する。
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String appPassword = prefs.getString(PrefsKey.APP_PASSWORD, "");

        // パスワードが等しい場合
        if (appPassword.equals(userPassword)) {
            // 開始画面を表示する。
            Intent intent = new Intent(getActivity(), StartupActivity.class);
            startActivity(intent);

        // パスワードが異なる場合
        } else {
            // エラーメッセージを表示する。
            toast(R.string.login_dialog_error_password);
            mLogger.w("OUT(NG)");
            return;
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * 終了ボタンの処理を行う。
     */
    private void doEndButton() {
        mLogger.d("IN");

        // ダイアログを終了する。
        dismiss();

        // 呼び出し元画面を終了する。
        getActivity().finish();

        mLogger.d("OUT(OK)");
    }
}
