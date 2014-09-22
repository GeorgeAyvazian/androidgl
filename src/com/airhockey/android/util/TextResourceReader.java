package com.airhockey.android.util;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class TextResourceReader {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String readTextFileFromResource(Resources resources, int resourceId) throws IOException {
        final StringBuilder body = new StringBuilder();
        try (InputStream inputStream = resources.openRawResource(resourceId); InputStreamReader inputStreamReader = new InputStreamReader(inputStream); BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String nextLine;
            while ((nextLine = bufferedReader.readLine()) != null) {
                body.append(nextLine).append('\n');
            }
        }
        return body.toString();
    }
}
