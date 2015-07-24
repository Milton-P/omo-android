package com.nightwind.meal.loader;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

/**
 * Created by nightwind on 15/4/13.
 */
public abstract class ShowDialogAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    protected Context context;
    protected ProgressDialog dialog;

    public ShowDialogAsyncTask(Context context, String dialogMsg) {
        this.context = context;
        if (dialogMsg != null) {
            dialog = new ProgressDialog(context);
            dialog.setMessage(dialogMsg);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    cancel(true);
                }
            });
        }
    }

    public Context getContext() {
        return context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        try {
            dialog.cancel();
        } catch (Exception ignored) {
        }
    }
}
