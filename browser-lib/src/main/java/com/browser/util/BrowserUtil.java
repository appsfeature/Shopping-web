package com.browser.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.browser.R;

public class BrowserUtil {
    public static void showErrorView(View view, boolean isVisible, String errorDetail) {
        if (view != null) {
            if(BrowserPreferences.isEnableDeveloperMode(view.getContext())){
                view.setVisibility(View.GONE);
                return;
            }

            if(BrowserPreferences.isEnableInternetErrorViewOnly(view.getContext())){
                if(!isConnected(view.getContext())){
                    view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                }else {
                    view.setVisibility(View.GONE);
                }
            }else {
                view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                if(isConnected(view.getContext())){
                    // show server error view
                    ImageView ivError = view.findViewById(R.id.iv_error);
                    TextView tvErrorTitle = view.findViewById(R.id.tv_error_title);
                    TextView tvErrorMessage = view.findViewById(R.id.tv_error_message);
                    Button btnRefresh = view.findViewById(R.id.btn_refresh);
                    tvErrorTitle.setText(view.getContext().getString(R.string.server_error_title));
                    tvErrorMessage.setText(view.getContext().getString(R.string.server_error_message));
                    ivError.setImageResource(R.drawable.ic_server_error);
                    btnRefresh.setText("Show Detail");
                    btnRefresh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tvErrorMessage.setText(!TextUtils.isEmpty(errorDetail) ? errorDetail : "null");
                        }
                    });
                }
            }

        }
    }

    public static boolean isConnected(Context context) {
        boolean isConnected = false;
        try {
            if ( context != null && context.getSystemService(Context.CONNECTIVITY_SERVICE) != null
                    && context.getSystemService(Context.CONNECTIVITY_SERVICE) instanceof ConnectivityManager) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Network nw = connectivityManager.getActiveNetwork();
                    if (nw == null) return false;
                    NetworkCapabilities actNw = connectivityManager.getNetworkCapabilities(nw);
                    return actNw != null && (actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                            || actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                            || actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                            || actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH));
                } else {
                    NetworkInfo nwInfo = connectivityManager.getActiveNetworkInfo();
                    return nwInfo != null && nwInfo.isConnected();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isConnected;
    }

    public static void hideKeyboard(Activity activity) {
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

    public static void showToastCentre(Context context, String msg) {
        Toast toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        if (html == null) {
            return new SpannableString("");
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }
}
