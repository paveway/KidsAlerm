package info.paveway.kidsalerm.dialog;

import info.paveway.kidsalerm.R;
import info.paveway.kidsalerm.service.LocationService;
import info.paveway.log.Logger;
import info.paveway.util.ServiceUtil;
import info.paveway.util.StringUtil;
import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.util.AttributeSet;

/**
 * キッズアラーム
 * 滞在時間入力ダイアログプリフェレンスクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class StayTimeDialogPreference extends EditDialogPreference {

    /** ロガー */
    private Logger mLogger = new Logger(StayTimeDialogPreference.class);

    /** コンテキスト */
    private Context mContext;

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param attrs アトリビュート
     */
    public StayTimeDialogPreference(Context context, AttributeSet attrs) {
        // スーパークラスのコンストラクタを呼び出す。
        super(context, attrs);

        mContext = context;
    }

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param attrs アトリビュート
     * @param defStyle デフォルトのスタイル
     */
    public StayTimeDialogPreference(Context context, AttributeSet attrs, int defStyle) {
        // スーパークラスのコンストラクタを呼び出す。
        super(context, attrs, defStyle);

        mContext = context;
    }

    /**
     * ダイアログをクローズした時に呼び出される。
     *
     * @param positiveResult 処理結果
     */
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        mLogger.d("IN positiveResult=[" + positiveResult + "]");

        // スーパークラスのメソッドを呼び出す。
        super.onDialogClosed(positiveResult);

        // サービスが動作している場合
        if (ServiceUtil.isServiceRunning(mContext, LocationService.class)) {
            // サービスを停止する。
            ServiceUtil.stopService(mContext);
        }

        // サービスを開始する。
        ServiceUtil.startService(mContext);

        mLogger.d("OUT(OK)");
    }

    /**
     * 入力値を生成する。
     */
    @Override
    protected void createInputValue() {
        mLogger.d("IN");

        // スーパークラスのメソッドを呼び出す。
        super.createInputValue();

        // 桁数を制限する。
        InputFilter[] inputFilter = new InputFilter[1];
        inputFilter[0] = new InputFilter.LengthFilter(4);
        mInputValue.setFilters(inputFilter);

        // ヒントを設定する。
        mInputValue.setHint(getContext().getResources().getString(R.string.stay_time_dialog_hint));

        mLogger.d("OUT(OK)");
    }

    /**
     * 入力タイプを返却する。
     *
     * @return 入力タイプ
     */
    @Override
    protected int getInputType() {
        mLogger.d("IN");

        int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL;
        mLogger.d("inputType=[" + inputType + "]");

        mLogger.d("OUT(OK)");
        return inputType;
    }

    /**
     * 入力値を保存する。
     */
    @Override
    protected void persist() {
        mLogger.d("IN");

        String inputValue = mInputValue.getText().toString();
        mLogger.d("inputValue=[" + inputValue + "]");
        if (StringUtil.isNullOrEmpty(inputValue)) {
            mInputValue.setText(getContext().getResources().getString(R.string.stay_time_dialog_default));
        }

        // スーパークラスのメソッドを呼び出す。
        super.persist();

        mLogger.d("OUT(OK)");
    }
}
