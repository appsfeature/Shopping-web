package com.browser.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.core.content.FileProvider;

import com.browser.R;

import java.io.File;

public class BrowserFileUtil {

    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return "";
        }
        if (!filePath.contains("/")) {
            return filePath;
        }
        return filePath.substring(filePath.lastIndexOf("/") + 1);
    }


    public static File getFileStoreDirectory(Context context) {
        if (isSupportLegacyExternalStorage()) {
            final String filePath = Environment.getExternalStorageDirectory() + "/" + "Browser";
            return new File(filePath);
        } else {
            return context.getExternalFilesDir("");
        }
    }

    public static boolean isSupportLegacyExternalStorage() {
        return Build.VERSION.SDK_INT < 29;
    }

    public static File getFile(Context context, String fileName) {
        if (isSupportLegacyExternalStorage()) {
            return new File(getFileStoreDirectory(context), fileName);
        } else {
            return new File(getFileStoreDirectory(context), fileName);
        }
    }

    public static boolean shouldAskPermissions(Context context) {
        if (context != null && isSupportLegacyExternalStorage()) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
                return context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
            }
        } else {
            return false;
        }
        return false;
    }

    public static Uri getUriFromFile(Context context, File file) {
        Uri fileUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fileUri = FileProvider.getUriForFile(context, context.getPackageName() + context.getString(R.string.file_provider), file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }
}
