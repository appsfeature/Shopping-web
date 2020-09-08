package com.appsfeature.bizwiz.util;

import android.content.Context;
import android.content.Intent;

import com.appsfeature.bizwiz.activity.BrowserActivity;
import com.helper.util.BaseConstants;
import com.helper.util.BaseUtil;

public class ClassUtil {


    public static void openAppBrowser(Context context, String title, String webUrl, boolean isRemoveHeaderFooter) {
        try {
            Intent intent = new Intent(context, BrowserActivity.class);
            intent.putExtra(BaseConstants.WEB_VIEW_URL, webUrl);
            intent.putExtra(BaseConstants.TITLE, title);
            intent.putExtra(AppConstant.IS_REMOVE_HEADER_FOOTER, isRemoveHeaderFooter);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            BaseUtil.showToast(context, "No option available for take action.");
        }
    }
}
