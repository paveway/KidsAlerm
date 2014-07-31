package info.paveway.kidsalerm.dialog;

import info.paveway.kidsalerm.CommonConstants.ExtraKey;
import info.paveway.kidsalerm.R;
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
        builder.setTitle(R.string.detail_exclusion_place_dialog_title);
        builder.setPositiveButton(R.string.detail_exclusion_place_dialog_positive_button, null);
        builder.setNegativeButton(R.string.detail_exclusion_place_dialog_negative_button,  null);
        String message =
                getResourceString(R.string.detail_exclusion_place_dialog_message1) +
                mTitle +
                getResourceString(R.string.detail_exclusion_place_dialog_message4) +
                getResourceString(R.string.detail_exclusion_place_dialog_message2) +
                latitude +
                getResourceString(R.string.detail_exclusion_place_dialog_message4) +
                getResourceString(R.string.detail_exclusion_place_dialog_message3) +
                longitude;
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
