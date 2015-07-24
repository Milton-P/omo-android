package com.nightwind.meal.utils;

import android.content.Context;
import android.widget.Toast;

import com.nightwind.meal.R;
import com.nightwind.meal.exception.HttpForbiddenException;
import com.nightwind.meal.exception.NoLoginException;

import java.io.IOException;

/**
 * Created by nightwind on 15/4/12.
 */
public class ToastUtils {

    public static void showNetworkResponse(Context context, int statusCode) {
        if (context == null) {
            return;
        }
        if (statusCode == 401) {
            Toast.makeText(context, R.string.no_login, Toast.LENGTH_SHORT).show();
        } else if (statusCode == 403) {
            Toast.makeText(context, R.string.auth_timeout, Toast.LENGTH_SHORT).show();
        } else if (statusCode == 500) {
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showNetworkResponse(Context context, Exception e) {
        if (context == null) {
            return;
        }
        if (e instanceof NoLoginException) {
            Toast.makeText(context, R.string.no_login, Toast.LENGTH_SHORT).show();
        } else if (e instanceof HttpForbiddenException) {
            Toast.makeText(context, R.string.auth_timeout, Toast.LENGTH_SHORT).show();
        } else if (e instanceof IOException) {
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show();
        }
    }


    public static void showNoLogin(Context context) {
        if (context != null) {
            Toast.makeText(context, R.string.no_login, Toast.LENGTH_SHORT).show();
        }
    }
}
