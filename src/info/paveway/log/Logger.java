package info.paveway.log;

import android.util.Log;

/**
 * ログ出力クラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 *
 */
public class Logger {

    /** ログを出力しないログレベル */
    @SuppressWarnings("all")
    private static final int LOG_LEVEL_NONE = Log.ASSERT + 1;

    /**
     * ログ出力レベル
     * 設定したログ出力レベルより低いログを出力しないようにします。
     * Log.VERBOSE/DEBUG/INFO/WARN/ERROR/ASSERTを指定して下さい。
     * ログを出力させない場合はLOG_LEVEL_NONEを設定して下さい。
     */
    private static final int LOG_LEVEL = Log.VERBOSE;

    /**
     * デフォルトのタグ名
     * コンストラクタでタグ名の指定を省略した場合に使用されます。
     */
    private static final String TAG = "PAVEWAY";

    /** 例外発生時のメッセージ */
    private static final String EXCEPTION_MESSAGE = "Exception";

    /** クラス名 */
    private String mClassName = null;

    /** タグ名 */
    private String mTag = null;

    /**
     * コンストラクタ
     * デフォルトのタグ名を使用します。
     *
     * @param cls クラス
     */
    public Logger(Class<?> cls) {
        this(TAG, cls);
    }

    /**
     * コンストラクタ
     *
     * @param tag タグ名
     * @param cls クラス
     */
    public Logger(String tag, Class<?> cls) {
        mTag = tag;
        mClassName = cls.getName();
    }

    /**
     * 詳細レベルのログを出力します。
     *
     * @param msg メッセージ
     */
    @SuppressWarnings("all")
    public void v(String msg) {
        if (LOG_LEVEL <= Log.VERBOSE) {
            Log.v(mTag, getLogMessage(msg));
        }
    }

    /**
     * 詳細レベルのログを出力します。
     *
     * @param msg メッセージ
     * @param t 例外
     */
    @SuppressWarnings("all")
    public void v(String msg, Throwable t) {
        if (LOG_LEVEL <= Log.VERBOSE) {
            Log.v(mTag, getLogMessage(msg), t);
        }
    }

    /**
     * 詳細レベルのログを出力します。
     * @param t 例外
     */
    @SuppressWarnings("all")
    public void v(Throwable t) {
        if (LOG_LEVEL <= Log.VERBOSE) {
            Log.v(mTag, getLogMessage(EXCEPTION_MESSAGE), t);
        }
    }

    /**
     * デバッグレベルのログを出力します。
     *
     * @param msg メッセージ
     */
    @SuppressWarnings("all")
    public void d(String msg) {
        if (LOG_LEVEL <= Log.DEBUG) {
            Log.d(mTag, getLogMessage(msg));
        }
    }

    /**
     * デバッグレベルのログを出力します。
     *
     * @param msg メッセージ
     * @param t 例外
     */
    @SuppressWarnings("all")
    public void d(String msg, Throwable t) {
        if (LOG_LEVEL <= Log.DEBUG) {
            Log.d(mTag, getLogMessage(msg), t);
        }
    }

    /**
     * デバッグレベルのログを出力します。
     *
     * @param t 例外
     */
    @SuppressWarnings("all")
    public void d(Throwable t) {
        if (LOG_LEVEL <= Log.DEBUG) {
            Log.d(mTag, getLogMessage(EXCEPTION_MESSAGE), t);
        }
    }

    /**
     * インフォレベルのログを出力します。
     *
     * @param msg メッセージ
     */
    @SuppressWarnings("all")
    public void i(String msg) {
        if (LOG_LEVEL <= Log.INFO) {
            Log.i(mTag, getLogMessage(msg));
        }
    }

    /**
     * インフォレベルのログを出力します。
     *
     * @param msg メッセージ
     * @param t 例外
     */
    @SuppressWarnings("all")
    public void i(String msg, Throwable t) {
        if (LOG_LEVEL <= Log.INFO) {
            Log.i(mTag, getLogMessage(msg), t);
        }
    }

    /**
     * インフォレベルのログを出力します。
     *
     * @param t 例外
     */
    @SuppressWarnings("all")
    public void i(Throwable t) {
        if (LOG_LEVEL <= Log.INFO) {
            Log.i(mTag, getLogMessage(EXCEPTION_MESSAGE), t);
        }
    }

    /**
     * 警告レベルのログを出力します。
     *
     * @param msg メッセージ
     */
    @SuppressWarnings("all")
    public void w(String msg) {
        if (LOG_LEVEL <= Log.WARN) {
            Log.w(mTag, getLogMessage(msg));
        }
    }

    /**
     * 警告レベルのログを出力します。
     *
     * @param msg メッセージ
     * @param t 例外
     */
    @SuppressWarnings("all")
    public void w(String msg, Throwable t) {
        if (LOG_LEVEL <= Log.WARN) {
            Log.w(mTag, getLogMessage(msg), t);
        }
    }

    /**
     * 警告レベルのログを出力します。
     *
     * @param t 例外
     */
    @SuppressWarnings("all")
    public void w(Throwable t) {
        if (LOG_LEVEL <= Log.WARN) {
            Log.w(mTag, t);
        }
    }

    /**
     * エラーレベルのログを出力します。
     *
     * @param msg メッセージ
     */
    @SuppressWarnings("all")
    public void e(String msg) {
        if (LOG_LEVEL <= Log.ERROR) {
            Log.e(mTag, getLogMessage(msg));
        }
    }

    /**
     * エラーレベルのログを出力します。
     *
     * @param msg メッセージ
     * @param t 例外
     */
    @SuppressWarnings("all")
    public void e(String msg, Throwable t) {
        if (LOG_LEVEL <= Log.ERROR) {
            Log.e(mTag, getLogMessage(msg), t);
        }
    }

    /**
     * エラーレベルのログを出力します。
     *
     * @param t 例外
     */
    @SuppressWarnings("all")
    public void e(Throwable t) {
        if (LOG_LEVEL <= Log.ERROR) {
            Log.e(mTag, getLogMessage(EXCEPTION_MESSAGE), t);
        }
    }

    /**
     * 致命的レベルのログを出力します。
     *
     * @param msg メッセージ
     * @param t 例外
     */
    @SuppressWarnings("all")
    public void wtf(String msg, Throwable t) {
        if (LOG_LEVEL <= Log.ASSERT) {
            Log.wtf(mTag, getLogMessage(msg), t);
        }
    }

    /**
     * 致命的レベルのログを出力します。
     *
     * @param t 例外
     */
    @SuppressWarnings("all")
    public void wtf(Throwable t) {
        if (LOG_LEVEL <= Log.ASSERT) {
            Log.wtf(mTag, t);
        }
    }

    /**
     * ログメッセージを取得します。
     *
     * @param msg メッセージ
     * @return ログメッセージ
     */
    private String getLogMessage(String msg) {
        return "[0x" + Thread.currentThread().getId() + "] " + getMethodName() + " : " + msg;
    }

    /**
     * メソッド名を取得します。
     *
     * @return メソッド名
     */
    private String getMethodName() {
        // スタックトレースエレメントを取得します。
        StackTraceElement[] te = Thread.currentThread().getStackTrace();

        // スタックトレースエレメント数分繰り返します。
        for (StackTraceElement stackTraceElement : te) {
            // スタックトレースエレメントのクラスメイト等しい場合
            if(stackTraceElement.getClassName().equals(mClassName)) {
                // クラス名.メソッド名 (ファイル名:行番号)の文字列を返却します。
                StringBuilder sb = new StringBuilder();
                sb.append(getSimpleClassName(mClassName));
                sb.append(".");
                sb.append(stackTraceElement.getMethodName());
                sb.append(" (");
                sb.append(stackTraceElement.getFileName());
                sb.append(":");
                sb.append(stackTraceElement.getLineNumber());
                sb.append(")");
                return sb.toString();
            }
        }

        // 該当するメソッド名をがない場合、クラス名のみ出力します。
        return getSimpleClassName(mClassName) + " ";
    }

    /**
     * クラス名を取得します。
     *
     * @param packageClassName パッケージ名つきクラス名
     *
     * @return クラス名
     */
    private String getSimpleClassName(String packageClassName) {
        return packageClassName.substring(packageClassName.lastIndexOf(".") + 1);
    }
}
