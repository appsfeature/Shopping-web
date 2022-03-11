package com.browser.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
}
