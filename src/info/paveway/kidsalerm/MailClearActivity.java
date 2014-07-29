package info.paveway.kidsalerm;

import info.paveway.kidsalerm.dialog.MailClearDialog;
import info.paveway.kidsalerm.dialog.MailClearDialog.OnClearListener;
import info.paveway.log.Logger;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

public class MailClearActivity extends ActionBarActivity implements OnClearListener {

    /** ロガー */
    private Logger mLogger = new Logger(StartupActivity.class);

    /** メール設定クリアダイアログ */
    private MailClearDialog mMailClearDialog;

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

        // メール設定クリアダイアログを表示する。
        FragmentManager manager = getSupportFragmentManager();
        mMailClearDialog = MailClearDialog.newInstance();
        mMailClearDialog.setCancelable(false);
        mMailClearDialog.show(manager, mMailClearDialog.getClass().getSimpleName());

        mLogger.d("OUT(OK)");
    }

    @Override
    public void onClear(boolean clear) {
        if (clear) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }
}
