package info.paveway.kidsalerm.dialog;

import info.paveway.kidsalerm.CommonConstants.ExtraKey;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * キッズアラーム
 * プログレスダイアログクラス
 *
 * @version 1.0 新規作成
 *
 */
public class ProgressStatusDialog extends DialogFragment {

    /** プログレスダイアログ */
    private static ProgressDialog mProgressDialog = null;

    /**
     * インスタンスを生成する。
     *
     * @param title タイトル
     * @param message メッセージ
     * @return インスタンス
     */
    public static ProgressStatusDialog newInstance(String title, String message) {
        ProgressStatusDialog instance = new ProgressStatusDialog();

        // ダイアログにパラメータを渡す
        Bundle arguments = new Bundle();
        arguments.putString(ExtraKey.PROGRESS_TITLE, title);
        arguments.putString(ExtraKey.PROGRESS_MESSAGE, message);
        instance.setArguments(arguments);

        return instance;
    }

    /**
     * 生成した時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        // ダイアログが生成されている場合
        if (mProgressDialog != null) {
            // ダイアログを返却する。
            return mProgressDialog;
        }

        // パラメータを取得
        String title = getArguments().getString(ExtraKey.PROGRESS_TITLE);
        String message = getArguments().getString(ExtraKey.PROGRESS_MESSAGE);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCancelable(false);

        return mProgressDialog;
    }

    /**
     * ダイアログを返却する。
     *
     * @return ダイアログ
     */
    @Override
    public Dialog getDialog(){
        return mProgressDialog;
    }

    /**
     * 終了した時に呼び出される。
     */
    @Override
    public void onDestroy(){
        // スーパークラスのメソッドを呼び出す。
        super.onDestroy();

        // ダイアログをクリアする。
        mProgressDialog = null;
    }
}
