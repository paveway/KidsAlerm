package info.paveway.kidsalerm.dialog;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;

/**
 * キッズアラーム
 * パスワードダイアログプリフェレンスクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class PasswordDialogPreference extends EditDialogPreference {

    /**
     * コンストラクタ
     *
     * @param context コンテキスト
     * @param attrs アトリビュート
     */
    public PasswordDialogPreference(Context context, AttributeSet attrs) {
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
    public PasswordDialogPreference(Context context, AttributeSet attrs, int defStyle) {
        // スーパークラスのコンストラクタを呼び出す。
        super(context, attrs, defStyle);
    }

    /**
     * 入力タイプを返却する。
     *
     * @return 入力タイプ
     */
    protected int getInputType() {
        return InputType.TYPE_CLASS_TEXT  | InputType.TYPE_TEXT_VARIATION_PASSWORD;
    }
}
