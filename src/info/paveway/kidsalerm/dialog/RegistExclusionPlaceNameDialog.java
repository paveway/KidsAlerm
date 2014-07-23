package info.paveway.kidsalerm.dialog;

import info.paveway.kidsalerm.R;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * キッズアラーム
 * 除外場所名登録ダイアログクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class RegistExclusionPlaceNameDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(RegistExclusionPlaceNameDialog.class);

    /** 除外場所名入力 */
    private EditText mExclusionPlaceNameValue;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static RegistExclusionPlaceNameDialog newInstance() {
        RegistExclusionPlaceNameDialog instance = new RegistExclusionPlaceNameDialog();
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
        View rootView = inflater.inflate(R.layout.dialog_regist_exclusion_place_name, null);

        // 入力項目を取得する。
        mExclusionPlaceNameValue  = (EditText)rootView.findViewById(R.id.exclusionPlaceNameValue);

        // 除外場所名登録ダイアログを生成する。
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("除外場所名登録");
        builder.setPositiveButton("登録", null);
        builder.setNegativeButton("キャンセル", null);
        builder.setView(rootView);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // ボタン押下でダイアログが閉じないようにリスナーを設定する。
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // 登録ボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 登録ボタン処理を行う。
                        doRegistButton();
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
     * 登録ボタンの処理を行う。
     */
    private void doRegistButton() {
        mLogger.d("IN");

        // 入力値を取得する。
        String exclusionPlaceName = mExclusionPlaceNameValue.getText().toString();

        // 未入力の場合
        if (StringUtil.isNullOrEmpty(exclusionPlaceName)) {
            // エラーメッセージを表示する。
            toast(R.string.login_dialog_error_input_password);
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
