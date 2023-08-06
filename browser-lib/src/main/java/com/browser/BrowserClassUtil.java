package com.browser;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.browser.activity.BrowserActivity;
import com.browser.player.VideoProperty;
import com.browser.player.YTPlayerActivity;
import com.browser.util.BrowserConstant;

import java.net.URISyntaxException;

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

    public static void openVideoPlayer(Context context, String videoId, String videoTitle) {
        VideoProperty property = new VideoProperty();
        property.setVideoId(videoId);
        property.setVideoTitle(videoTitle);
        openVideoPlayer(context, property);
    }
    public static void openVideoPlayer(Context context, VideoProperty extraProperty) {
        try {
            context.startActivity(new Intent(context, YTPlayerActivity.class)
                    .putExtra(BrowserConstant.EXTRA_PROPERTY, extraProperty)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } catch (Exception e) {
            e.printStackTrace();
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

    public static void openIntentUrl(Context context, String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
            context.startActivity(intent);
        } catch (URISyntaxException | ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }


    static void openUrlExternal(Context context, String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
