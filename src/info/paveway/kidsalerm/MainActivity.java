package info.paveway.kidsalerm;

import info.paveway.kidsalerm.CommonConstants.PrefsKey;
import info.paveway.kidsalerm.CommonConstants.RequestCode;
import info.paveway.kidsalerm.dialog.LoginDialog;
import info.paveway.kidsalerm.dialog.StartupDialog;
import info.paveway.log.Logger;
import info.paveway.util.StringUtil;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

/**
 * キッズアラーム
 * メイン画面
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public class MainActivity extends AbstractBaseActivity {

    /** ロガー */
    private Logger mLogger = new Logger(MainActivity.class);

    /** 開始ダイアログ */
    private StartupDialog mStartupDialog;

    /**
     * 生成した時に呼び出される。
     *
     * @param savendInstanceState 保存した時のインスタンスの状態
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLogger.d("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onCreate(savedInstanceState);

        // レイアウトを設定する。
        setContentView(R.layout.activity_main);

        // 開始処理を行う。
        start();

        mLogger.d("OUT(OK)");
    }

    /**
     * 終了する時に呼び出される。
     */
    @Override
    public void onDestroy() {
        mLogger.d("IN");

        // スーパークラスのメソッドを呼び出す。
        super.onDestroy();

        mLogger.d("OUT(OK)");
    }

    /**
     * 呼び出した画面から戻った時に呼び出される。
     *
     * @param requestCode リクエストコード
     * @param resultCode 結果コード
     * @param data データ
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mLogger.d("IN");

        // 設定画面の場合
        if (RequestCode.SETTINGS_PREFERENCE == requestCode) {
            // 開始処理を行う。
            start();

        // 上記以外
        } else {
            // 開始ダイアログのメソッドを呼び出す。
            mStartupDialog.onActivityResult(requestCode, resultCode, data);
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * 開始処理を行う。
     */
    private void start() {
        mLogger.d("IN");

        // アプリケーションパスワードを取得する。
        String appPassword = mPrefs.getString(PrefsKey.APP_PASSWORD, "");

        // アプリケーションパスワードが未設定の場合
        if (StringUtil.isNullOrEmpty(appPassword)) {
            // 開始ダイアログを表示する。
            showStartupDialog();

        // パスワードが設定済みの場合
        } else {
            // ログインダイアログを表示する。
            showLoginDialog();
        }

        mLogger.d("OUT(OK)");
    }

    /**
     * 開始ダイアログを表示する。
     */
    private void showStartupDialog() {
        mLogger.d("IN");

        // 開始ダイアログを表示する。
        FragmentManager manager = getSupportFragmentManager();
        mStartupDialog = StartupDialog.newInstance();
        mStartupDialog.setCancelable(false);
        mStartupDialog.show(manager, mStartupDialog.getClass().getSimpleName());

        mLogger.d("OUT(OK)");
    }

    /**
     * ログインダイアログを表示する。
     */
    private void showLoginDialog() {
        mLogger.d("IN");

        // ログインダイアログを表示する。
        FragmentManager manager = getSupportFragmentManager();
        LoginDialog dialog = LoginDialog.newInstance();
        dialog.setCancelable(false);
        dialog.show(manager, dialog.getClass().getSimpleName());

        mLogger.d("OUT(OK)");
    }
}
