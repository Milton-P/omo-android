//package com.nightwind.meal.loader;
//
//import android.content.Context;
//import android.os.AsyncTask;
//
//import com.nightwind.meal.exception.HttpForbiddenException;
//import com.nightwind.meal.exception.NoLoginException;
//import com.nightwind.meal.manager.UserManager;
//
//import java.io.IOException;
//
///**
// * Created by nightwind on 15/4/13.
// */
//public class PullUserTask extends AsyncTask<Void, Void, Integer> {
//
//    private UserManager um;
//
//    private PullUserTaskCallbacks callbacks;
//
//    PullUserTask(UserManager um) {
//        this.um = um;
//    }
//
//    @Override
//    protected Integer doInBackground(Void... params) {
//        int resultCode = 200;
//        try {
//            um.pullUser();
//        } catch (IOException e) {
//            resultCode = 500;
//        } catch (HttpForbiddenException e) {
//            resultCode = 403;
//        } catch (NoLoginException e) {
//            resultCode = 401;
//        }
//        return resultCode;
//    }
//
//    @Override
//    protected void onPostExecute(Integer resultCode) {
//        super.onPostExecute(resultCode);
//        callbacks.onPostExecute(resultCode);
//    }
//
//    public static interface PullUserTaskCallbacks {
//        void onPostExecute(Integer resultCode);
//    }
//
//    public void setCallbacks(PullUserTaskCallbacks callbacks) {
//        this.callbacks = callbacks;
//    }
//}
