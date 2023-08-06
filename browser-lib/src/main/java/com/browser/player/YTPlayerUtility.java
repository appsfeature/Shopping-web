package com.browser.player;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.browser.BrowserSdk;

/**
 * @author Created by Abhijit on 2/6/2018.
 */
public class YTPlayerUtility {

    public static void openYoutubeApp(Activity activity, String videoId) {
        try {
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + videoId));
            try {
                activity.startActivity(appIntent);
            } catch (ActivityNotFoundException ex) {
                activity.startActivity(webIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playVideo(Context context, VideoProperty extraProperty) {
        BrowserSdk.openVideoPlayer(context, extraProperty);
    }

    public static String getVideoIdFromUrl(String lectureVideo) {
        try {
            if (!TextUtils.isEmpty(lectureVideo) && isValidUrl(lectureVideo)) {
                return lectureVideo.substring(lectureVideo.lastIndexOf("/") + 1);
            }else {
                return lectureVideo;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return lectureVideo;
        }
    }

    public static String getYoutubePlaceholderImage(String videoId) {
        return "https://i.ytimg.com/vi/"+ videoId +"/mqdefault.jpg";
    }

    public static boolean isValidUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            return (url.startsWith("file://") || url.startsWith("http://") || url.startsWith("https://"));
        }else {
            return false;
        }
    }
}
