package info.paveway.kidsalerm.dialog;

import info.paveway.kidsalerm.CommonConstants.PrefsKey;
import info.paveway.kidsalerm.CommonConstants.RequestCode;
import info.paveway.kidsalerm.R;
import info.paveway.kidsalerm.SettingsPreferenceActivity;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * キッズアラーム
 * 開始ダイアログクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class StartupDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(StartupDialog.class);

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static StartupDialog newInstance() {
        StartupDialog instance = new StartupDialog();
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

        // 開始ダイアログを生成する。
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.startup_dialog_title);
        builder.setPositiveButton(R.string.startup_dialog_positive_button, null);
        builder.setNegativeButton(R.string.startup_dialog_negative_button, null);
        builder.setMessage(R.string.startup_dialog_message);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // ボタン押下でダイアログが閉じないようにリスナーを設定する。
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // 設定画面ボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doSettingsPrerenceButton();
                    }
                });

                // 終了ボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doEndButton();
                    }
                });
            }
        });

        mLogger.d("OUT(OK)");
        return dialog;
    }

    /**
     * 呼び出した画面から戻った時に呼び出される。
     *
     * @param requestCode リクエストコード
     * @param resultCode 結果コード
     * @param data データ
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLogger.d("IN");

        // 設定画面の場合
        if (RequestCode.SETTINGS_PREFERENCE == (requestCode & 0xFFFF)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (StringUtil.isNullOrEmpty(prefs.getString(PrefsKey.APP_PASSWORD, ""))) {
                Toast.makeText(getActivity(), "設定画面でアプリケーションパスワードを設定してください", Toast.LENGTH_SHORT).show();

            } else {
                // 開始画面を表示する。
                Intent intent = new Intent(getActivity(), StartupActivity.class);
                startActivity(intent);
            }
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * 設定画面ボタンの処理を行う。
     */
    private void doSettingsPrerenceButton() {
        mLogger.d("IN");

        // 設定画面を表示する。
        Intent intent = new Intent(getActivity(), SettingsPreferenceActivity.class);
        startActivityForResult(intent, RequestCode.SETTINGS_PREFERENCE);

        mLogger.d("OUT(OK)");
    }

    /**
     * 終了ボタンの処理を行う。
     */
    private void doEndButton() {
        mLogger.d("IN");

        // 呼び出し元画面を終了する。
        getActivity().finish();

        mLogger.d("OUT(OK)");
    }
}
