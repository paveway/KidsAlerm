package info.paveway.kidsalerm.dialog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import info.paveway.kidsalerm.R;
import info.paveway.kidsalerm.SelectExclusionPlaceActivity;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;
import android.annotation.SuppressLint;
import android.app.Activity;
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

    /** 登録リスナー */
    private OnRegistListener mListener;

    /** 登録済み除外場所名 */
    private List<String> mExclusionPlaceNameList;

    /** 緯度 */
    private double mLatitude;

    /** 経度 */
    private double mLongitude;

    /** 除外場所名入力 */
    private EditText mExclusionPlaceNameValue;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static RegistExclusionPlaceNameDialog newInstance(ArrayList<String> exclusionPlaceNameList, double latitude, double longitude) {
        RegistExclusionPlaceNameDialog instance = new RegistExclusionPlaceNameDialog();
        Bundle args = new Bundle();
        args.putStringArrayList("exclusionPlaceNameList", exclusionPlaceNameList);
        args.putDouble("latitude", latitude);
        args.putDouble("longitude", longitude);
        instance.setArguments(args);
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

        mExclusionPlaceNameList = (ArrayList<String>)getArguments().getStringArrayList("exclusionPlaceNameList");
        mLatitude  = (double)getArguments().getDouble("latitude");
        mLongitude = (double)getArguments().getDouble("longitude");

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
     * アタッチする時に呼び出される。
     *
     * @param activity アクティビティ
     */
    @Override
    public void onAttach(Activity activity) {
        mLogger.d("IN");

        try {
            // リスナーを設定する。
            mListener = (SelectExclusionPlaceActivity)activity;
        } catch (Exception e) {
            mLogger.e(e);
        }

        mLogger.d("OUT(OK)");
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
            toast("未入力です");
            mLogger.w("OUT(NG)");
            return;
        }

        // 登録済みの名前の場合
        if (mExclusionPlaceNameList.contains(exclusionPlaceName)) {
            // エラーメッセージを表示する。
            toast("登録済みです");
            mLogger.w("OUT(NG)");
            return;
        }

        // 登録を通知する。
        mListener.onRegist(exclusionPlaceName, mLatitude, mLongitude);

        mLogger.d("OUT(OK)");
    }

    /**
     * 終了ボタンの処理を行う。
     */
    private void doEndButton() {
        mLogger.d("IN");

        // ダイアログを終了する。
        dismiss();

        // 登録を通知する。
        mListener.onRegist("", mLatitude, mLongitude);

        mLogger.d("OUT(OK)");
    }

    /**************************************************************************/
    /**
     * 登録リスナークラス
     *
     */
    public interface OnRegistListener {

        /**
         * 登録した時に呼び出される。
         *
         * @param exclusionPlaceName 除外場所名
         * @param latitude 緯度
         * @param longitude 経度
         */
        void onRegist(String exclusionPlaceName, double latitude, double longitude);
    }
}
