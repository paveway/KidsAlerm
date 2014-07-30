package info.paveway.kidsalerm.dialog;

import info.paveway.kidsalerm.SettingsPreferenceActivity;
import info.paveway.log.Logger;
import android.content.Context;
import android.preference.DialogPreference;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * キッズアラーム
 * エディットダイアログプリフェレンスクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class EditDialogPreference extends DialogPreference {

    /** ロガー */
    private Logger mLogger = new Logger(EditDialogPreference.class);

    /** 入力値 */
    protected EditText mInputValue;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param attrs アトリビュート
     */
    public EditDialogPreference(Context context, AttributeSet attrs) {
        // スーパークラスのコンストラクタを呼び出す。
        super(context, attrs);
    }

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param attrs アトリビュート
     * @param defStyle デフォルトのスタイル
     */
    public EditDialogPreference(Context context, AttributeSet attrs, int defStyle) {
        // スーパークラスのコンストラクタを呼び出す。
        super(context, attrs, defStyle);
    }

    /**
     * ダイアログのビューを生成した時に呼び出される。
     *
     * @return 生成したビュー
     */
    @Override
    protected View onCreateDialogView() {
        mLogger.d("IN");

        // 入力値を生成する。
        createInputValue();

        mLogger.d("OUT(OK)");
        return mInputValue;
    }

    /**
     * ダイアログをクローズした時に呼び出される。
     *
     * @param positiveResult 処理結果
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        mLogger.d("IN positiveResult=[" + positiveResult + "]");

        // 処理結果が正常の場合
        if (positiveResult) {
            // 入力値を保存する。
            persist();
        }

        // 呼び出し元画面のビューを可否を設定する。
        ((SettingsPreferenceActivity)getContext()).enableViews();

        // スーパークラスのメソッドを呼び出す。
        super.onDialogClosed(positiveResult);

        mLogger.d("OUT(OK)");
    }

    /**
     * 入力値を生成する。
     */
    protected void createInputValue() {
        mLogger.d("IN");

        mInputValue = new EditText(getContext());
        mInputValue.setInputType(getInputType());
        String defaultValue = getPersistedString("default");
        mLogger.d("defaultValue=[" + defaultValue + "]");
        mInputValue.setText(defaultValue);

        mLogger.d("OUT(OK)");
    }

    /**
     * 入力タイプを返却する。
     *
     * @return 入力タイプ
     */
    protected int getInputType() {
        mLogger.d("IN");

        int inputType = InputType.TYPE_CLASS_TEXT  | InputType.TYPE_TEXT_VARIATION_NORMAL;

        mLogger.d("OUT(OK) result=[" + inputType + "]");
        return inputType;
    }

    /**
     * 入力値を保存する。
     */
    protected void persist() {
        mLogger.d("IN");

        // 入力値を取得する。
        String inputValue = mInputValue.getText().toString();
        mLogger.d("inputValue=[" + inputValue + "]");

        // 入力値を保存する。
        persistString(inputValue);

        mLogger.d("OUT(OK)");
    }
}
