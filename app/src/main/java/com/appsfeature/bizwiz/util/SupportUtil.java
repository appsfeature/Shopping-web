package com.appsfeature.bizwiz.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.helper.util.BaseUtil;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class SupportUtil extends BaseUtil {

    public static void hideKeybord(Activity activity) {
        if(activity!=null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            View f = activity.getCurrentFocus();
            if (null != f && null != f.getWindowToken() && EditText.class.isAssignableFrom(f.getClass()))
                imm.hideSoftInputFromWindow(f.getWindowToken(), 0);
            else
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }

    public static SpannableString generateBoldTitle(String title, String boldText) {
        SpannableString s = new SpannableString(title + " " + boldText);
        s.setSpan(new StyleSpan(Typeface.BOLD), title.length(), title.length() + boldText.length() + 1  , 0);
        return s;
    }


    public static void showNoDataProgress(View view) {
        if (view != null) {
            view.setVisibility(VISIBLE);
            if (view.findViewById(com.helper.R.id.player_progressbar) != null) {
                view.findViewById(com.helper.R.id.player_progressbar).setVisibility(VISIBLE);
            }
            TextView tvNoData = view.findViewById(com.helper.R.id.tv_no_data);
            if (tvNoData != null) {
                tvNoData.setVisibility(GONE);
            }
        }
    }
}
