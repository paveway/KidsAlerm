package info.paveway.kidsalerm.dialog;

import info.paveway.kidsalerm.CommonConstants.ExtraKey;
import info.paveway.log.Logger;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;

import com.google.android.gms.maps.model.Marker;

/**
 * ここにいるクライアント
 * ユーザ削除ダイアログクラス
 *
 * @version 1.0 新規作成
 *
 */
public class DetailExclusionPlaceDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(DetailExclusionPlaceDialog.class);

    /** タイトル */
    private String mTitle;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static DetailExclusionPlaceDialog newInstance(Marker marker) {
        DetailExclusionPlaceDialog instance = new DetailExclusionPlaceDialog();
        Bundle args = new Bundle();
        args.putString(ExtraKey.TITLE,     marker.getTitle());
        args.putString(ExtraKey.LATITUDE,  String.valueOf(marker.getPosition().latitude));
        args.putString(ExtraKey.LONGITUDE, String.valueOf(marker.getPosition().longitude));
        instance.setArguments(args);
        return instance;
    }

    /**
     * 生成した時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     * @return ダイアログ
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mLogger.d("IN");

        mTitle           = (String)getArguments().getString(ExtraKey.TITLE);
        String latitude  = (String)getArguments().getString(ExtraKey.LATITUDE);
        String longitude = (String)getArguments().getString(ExtraKey.LONGITUDE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("除外場所詳細");
        builder.setPositiveButton("削除", null);
        builder.setNegativeButton("キャンセル",  null);
        String message =
                "除外場所名：" + mTitle   + "\n" +
                "緯度："       + latitude + "\n" +
                "経度："       + longitude;
        builder.setMessage(message);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        // ボタン押下でダイアログが閉じないようにリスナーを設定する。
        dialog.setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                // 削除ボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doDeleteButton();
                    }
                });

                // キャンセルボタン
                ((AlertDialog)dialog).getButton(Dialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        doCancelButton();
                    }
                });
            }
        });

        mLogger.d("OUT(OK)");
        return dialog;
    }

    /**
     * 削除ボタンの処理を行う。
     */
    private void doDeleteButton() {
        mLogger.d("IN");

        // 除外場所削除確認ダイアログを表示する。
        FragmentManager manager = getActivity().getSupportFragmentManager();
        DeleteExclusionPlaceDialog dialog =
                DeleteExclusionPlaceDialog.newInstance(mTitle);
        dialog.setCancelable(false);
        dialog.show(manager, dialog.getClass().getSimpleName());

        // ダイアログを終了する。
        dismiss();

        mLogger.d("OUT(OK)");
    }

    /**
     * キャンセルボタンの処理を行う。
     */
    private void doCancelButton() {
        mLogger.d("IN");

        // ダイアログを終了する。
        dismiss();

        mLogger.d("OUT(OK)");
    }
}
