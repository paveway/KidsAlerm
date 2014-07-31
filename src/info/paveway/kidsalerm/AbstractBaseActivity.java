package info.paveway.kidsalerm;

import info.paveway.log.Logger;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

/**
 * ここにいるクライアント
 * 抽象ベース画面クラス
 *
 * @version 1.0 新規作成
 */
public abstract class AbstractBaseActivity extends ActionBarActivity {

    /** ロガー */
    private Logger mLogger = new Logger(AbstractBaseActivity.class);

    /** コンテントリゾルバ */
    protected ContentResolver mResolver;

    /** リソース */
    protected Resources mResources;

    /** プリフェレンス */
    protected SharedPreferences mPrefs;

    /**
     * 生成された時に呼び出される。
     *
     * @param savedInstanceState 保存した時のインスタンスの状態
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLogger.d("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // コンテントリゾルバを取得する。
        mResolver = getContentResolver();

        // リソースを取得する。
        mResources = getResources();

        // プリフェレンスを取得する。
        mPrefs = PreferenceManager.getDefaultSharedPreferences(AbstractBaseActivity.this);

        mLogger.d("OUT(OK)");
    }

    /**
     * リソース文字列を返却する。
     *
     * @param id 文字列のリソースID
     * @return リソース文字列
     */
    protected String getResourceString(int id) {
        return mResources.getString(id);
    }

    /**
     * トースト表示する。
     *
     * @param id 文字列リソースID
     */
    protected void toast(int id) {
        toast(getResources().getString(id));
    }

    /**
     * トースト表示する。
     *
     * @param text 文字列
     */
    protected void toast(String text) {
        Toast.makeText(AbstractBaseActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}
