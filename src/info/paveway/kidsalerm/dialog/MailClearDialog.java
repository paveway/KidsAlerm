package info.paveway.kidsalerm.dialog;

import info.paveway.kidsalerm.MailClearActivity;
import info.paveway.kidsalerm.R;
import info.paveway.log.Logger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * キッズアラーム
 * メール設定クリアダイアログクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class MailClearDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(MailClearDialog.class);

    /** クリアリスナー */
    private OnClearListener mListener;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static MailClearDialog newInstance() {
        MailClearDialog instance = new MailClearDialog();
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

        // メール設定クリアダイアログを生成する。
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.mail_clear_dialog_title);
        builder.setPositiveButton(R.string.mail_clear_dialog_positive_button, null);
        builder.setNegativeButton(R.string.mail_clear_dialog_negative_button, null);
        builder.setMessage(R.string.mail_clear_dialog_message);
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
                        doClearButton();
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
     * アタッチする時に呼び出される。
     *
     * @param activity アクティビティ
     */
    @Override
    public void onAttach(Activity activity) {
        mLogger.d("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onAttach(activity);

        try {
            // リスナーを設定する。
            mListener = (MailClearActivity)activity;
        } catch (Exception e) {
            mLogger.e(e);
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * クリアボタンの処理を行う。
     */
    private void doClearButton() {
        mLogger.d("IN");

        // クリアを通知する。
        mListener.onClear(true);

        // ダイアログを終了する。
        dismiss();

        mLogger.d("OUT(OK)");
    }

    /**
     * 終了ボタンの処理を行う。
     */
    private void doEndButton() {
        mLogger.d("IN");

        // クリアを通知する。
        mListener.onClear(false);

        // ダイアログを終了する。
        dismiss();

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /**
     * クリアリスナークラス
     *
     */
    public interface OnClearListener {

        /**
         * クリアした時に呼び出される。
         *
         * @param clear クリア実行
         */
        void onClear(boolean clear);
    }
}
