package info.paveway.kidsalerm.dialog;

import info.paveway.kidsalerm.CommonConstants.ExtraKey;
import info.paveway.kidsalerm.SelectExclusionPlaceActivity;
import info.paveway.log.Logger;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * ここにいるクライアント
 * ユーザ削除ダイアログクラス
 *
 * @version 1.0 新規作成
 *
 */
public class DeleteExclusionPlaceDialog extends AbstractBaseDialogFragment {

    /** ロガー */
    private Logger mLogger = new Logger(DeleteExclusionPlaceDialog.class);

    private String mTitle;

    /** 削除リスナー */
    private OnDeleteListener mListener;

    /**
     * インスタンスを返却する。
     *
     * @return インスタンス
     */
    public static DeleteExclusionPlaceDialog newInstance(String title) {
        DeleteExclusionPlaceDialog instance = new DeleteExclusionPlaceDialog();
        Bundle args = new Bundle();
        args.putString(ExtraKey.TITLE, title);
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

        // 引数のデータを取得する。
        mTitle = (String)getArguments().getString(ExtraKey.TITLE);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("除外場所削除確認");
        builder.setPositiveButton("削除", null);
        builder.setNegativeButton("キャンセル",  null);
        String message = mTitle + "を削除しますか";
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
            mListener = (SelectExclusionPlaceActivity)activity;
        } catch (Exception e) {
            mLogger.e(e);
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * 削除ボタンの処理を行う。
     */
    private void doDeleteButton() {
        mLogger.d("IN");

        // 削除処理を行う。
        mListener.onDelete();

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

    /**************************************************************************/
    /**
     * 削除リスナークラス
     *
     */
    public interface OnDeleteListener {

        /**
         * 削除した時に呼び出される。
         *
         */
        void onDelete();
    }
}
