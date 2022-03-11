package com.browser;

import android.content.Context;
import android.content.Intent;

import com.browser.activity.BrowserActivity;
import com.browser.util.BrowserConstant;

public class BrowserClassUtil {

    public static void open(Context context, String webUrl) {
        open(context, null, webUrl, false);
    }

    public static void open(Context context, String title, String webUrl) {
        open(context, title, webUrl, false);
    }

    public static void open(Context context, String title, String webUrl, boolean isEmbedPdf) {
        open(context, title, webUrl, isEmbedPdf, false);
    }

    public static void open(Context context, String title, String webUrl, boolean isEmbedPdf, boolean isRemoveHeaderFooter) {
        try {
            Intent intent = new Intent(context, BrowserActivity.class);
            intent.putExtra(BrowserConstant.WEB_VIEW_URL, webUrl);
            intent.putExtra(BrowserConstant.TITLE, title);
            intent.putExtra(BrowserConstant.IS_EMBED_PDF, isEmbedPdf);
            intent.putExtra(BrowserConstant.IS_REMOVE_HEADER_FOOTER, isRemoveHeaderFooter);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            BrowserSdk.showToast(context, "No option available for take action.");
        }
    }

    public static void openPDFViewer(Context context, String title, String webUrl) {
        try {
            Intent intent = new Intent(context, BrowserActivity.class);
            intent.putExtra(BrowserConstant.WEB_VIEW_URL, webUrl);
            intent.putExtra(BrowserConstant.TITLE, title);
            intent.putExtra(BrowserConstant.IS_OPEN_PDF_IN_WEBVIEW, true);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            BrowserSdk.showToast(context, "No option available for take action.");
        }
    }

    public static void openProfileLink(Context context, String title, String webUrl) {
        openLinkWithoutHistory(context, title, webUrl);
    }

    /**
     * @apiNote : No open page history maintain exit on single back pressed.
     */
    public static void openLinkWithoutHistory(Context context, String title, String webUrl) {
        try {
            Intent intent = new Intent(context, BrowserActivity.class);
            intent.putExtra(BrowserConstant.WEB_VIEW_URL, webUrl);
            intent.putExtra(BrowserConstant.TITLE, title);
            intent.putExtra(BrowserConstant.IS_FIX_CROP_RATIO, true);
            intent.putExtra(BrowserConstant.IS_DISABLE_BACK_BUTTON_HISTORY, true);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            BrowserSdk.showToast(context, "No option available for take action.");
        }
    }
}
