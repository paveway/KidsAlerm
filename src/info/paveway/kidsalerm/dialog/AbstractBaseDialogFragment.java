package info.paveway.kidsalerm.dialog;

import android.app.Activity;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

/**
 * キッズアラーム
 * 抽象基底ダイアログクラス
 *
 * @version 1.0 新規作成
 * @author paveway.info@gmail.com
 * Copyright (C) 2014 paveway.info. All rights reserved.
 *
 */
public abstract class AbstractBaseDialogFragment extends DialogFragment {

    protected void toast(String text) {
        Activity activity = getActivity();
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    /**
     * トースト表示する。
     *
     * @param id 文字列リソースID
     */
    protected void toast(int id) {
        Activity activity = getActivity();
        Toast.makeText(activity, activity.getResources().getString(id), Toast.LENGTH_SHORT).show();
    }

    /**
     * リソース文字列を取得する。
     *
     * @param id リソース文字列ID
     * @return
     */
    protected String getResourceString(int id) {
        return getActivity().getResources().getString(id);
    }
}
