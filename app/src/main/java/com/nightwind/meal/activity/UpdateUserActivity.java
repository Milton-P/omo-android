package com.nightwind.meal.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nightwind.meal.R;
import com.nightwind.meal.exception.HttpBadRequestException;
import com.nightwind.meal.exception.HttpForbiddenException;
import com.nightwind.meal.exception.NoLoginException;
import com.nightwind.meal.loader.ShowDialogAsyncTask;
import com.nightwind.meal.manager.UserManager;
import com.nightwind.meal.model.User;
import com.nightwind.meal.utils.NetworkUtils;
import com.nightwind.meal.utils.ToastUtils;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class UpdateUserActivity extends ActionBarActivity {


    private static final String APPKEY = "5d1eac0fa81a";
    private static final String APPSECRET = "9bf4795785b0b3017dcd40a2fc449118";
    private View vgDelivery;
    private View vgName;


    public static final String INTENT_UPDATE_TYPE = "UPDATE_TYPE";
    public static final int INTENT_UPDATE_TYPE_NAME = 0;
    public static final int INTENT_UPDATE_TYPE_DELIVERY = 1;

    private int type = INTENT_UPDATE_TYPE_NAME;
    private EditText etSmscode;
    private View vgSmscode;
//    private RequestSmsCodeTask requestSmsCodeTask;
    private Button btnRequestSmscode;
    private EditText etTel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        View rootView = inflater.inflate(R.layout.fragment_update_user, container, false);


        vgDelivery = findViewById(R.id.lr_delivery);
        vgName = findViewById(R.id.lr_name);

        findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitUpdate();
            }
        });


        try {
            final User user = UserManager.getInstance(this).getUser();

            Intent intent = getIntent();
            type = intent.getIntExtra(INTENT_UPDATE_TYPE, INTENT_UPDATE_TYPE_NAME);
            if (type == INTENT_UPDATE_TYPE_DELIVERY) {
                // show delivery, hide name
                vgDelivery.setVisibility(View.VISIBLE);
                vgName.setVisibility(View.GONE);
            } else {
                vgDelivery.setVisibility(View.GONE);
                vgName.setVisibility(View.VISIBLE);
            }

            TextView tvName = (TextView) vgName.findViewById(R.id.name);
            tvName.setText(user.getName());
            vgSmscode = vgDelivery.findViewById(R.id.lr_smscode);
            etSmscode = (EditText) vgSmscode.findViewById(R.id.smscode);
            btnRequestSmscode = (Button) vgSmscode.findViewById(R.id.requestSmscode);
            btnRequestSmscode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (requestSmsCodeTask == null || requestSmsCodeTask.isCancelled()) {
//                        requestSmsCodeTask = new RequestSmsCodeTask(UpdateUserActivity.this, "获取验证码中");
//                        requestSmsCodeTask.execute();
//                    }
                    onRequestSmsCode();
                }
            });

            etTel = (EditText) vgDelivery.findViewById(R.id.tel);
            etTel.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (user.getTel() != null &&user.getTel().equals(String.valueOf(s))) {
                        vgSmscode.setVisibility(View.GONE);
                    } else {
                        vgSmscode.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
            TextView tvAddress = (TextView) vgDelivery.findViewById(R.id.address);
            etTel.setText(user.getTel());
            tvAddress.setText(user.getAddress());

        } catch (NoLoginException e) {
            Toast.makeText(this, R.string.no_login, Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initSmsCodeSDK();

    }

    private void onRequestSmsCode() {
        String tel = String.valueOf(etTel.getText());

        if (tel.length() == 11) {
            SMSSDK.getVerificationCode("86", tel);
            btnRequestSmscode.setEnabled(false);
            btnRequestSmscode.setText("正在获取验证码");
        } else {
            Toast.makeText(this, "手机号码的长度不为11", Toast.LENGTH_SHORT).show();
        }
    }

    private void initSmsCodeSDK () {
        SMSSDK.initSDK(this, APPKEY, APPSECRET);
        EventHandler eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        btnRequestSmscode.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(UpdateUserActivity.this, R.string.request_smscode_success, Toast.LENGTH_SHORT).show();
                                btnRequestSmscode.setText("获取验证码成功");
                            }
                        });
                    }
                }else{
                    ((Throwable)data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }


    private void commitUpdate() {
        User user = new User();
        int smsCode = 0;
        if (type == INTENT_UPDATE_TYPE_NAME) {
            TextView tvName = (TextView) vgName.findViewById(R.id.name);
            String name = String.valueOf(tvName.getText());
            if (name.trim().length() == 0) {
                tvName.setError("姓名不能为空");
                return;
            }
            user.setName(name);
        } else {
            TextView tvTel = (TextView) vgDelivery.findViewById(R.id.tel);
            TextView tvAddress = (TextView) vgDelivery.findViewById(R.id.address);

            String tel = String.valueOf(tvTel.getText());
            String address = String.valueOf(tvAddress.getText());
            String strSmsCode = String.valueOf(etSmscode.getText());
            if (!TextUtils.isEmpty(strSmsCode)) {
                smsCode = Integer.valueOf(strSmsCode);
            }
            if (tel.trim().length() == 0) {
                tvTel.setError("手机号不能为空");
                return;
            }
            if (address.trim().length() == 0) {
                tvAddress.setError("地址不能为空");
                return;
            }
            user.setTel(tel);
            user.setAddress(address);
        }
        new UpdateUserTask(user).execute(smsCode);

    }

//    class RequestSmsCodeTask extends ShowDialogAsyncTask<Void, Void, Integer> {
//
//        public RequestSmsCodeTask(Context context, String dialogMsg) {
//            super(context, dialogMsg);
//        }
//
//        @Override
//        protected Integer doInBackground(Void... params) {
//            int result = 0;
//            try {
//                UserManager.getInstance(getContext()).requestSmsCode();
//            } catch (Exception e) {
//                result = NetworkUtils.Exception2Code(e);
//            }
//            return result;
//        }
//
//        @Override
//        protected void onPostExecute(Integer integer) {
//            super.onPostExecute(integer);
//            if (integer == 0) {
//                Toast.makeText(getContext(), R.string.request_smscode_success, Toast.LENGTH_SHORT).show();
//            } else {
//                ToastUtils.showNetworkResponse(getContext(), integer);
//            }
//        }
//    }


    class UpdateUserTask extends AsyncTask<Integer, Void, Integer> {

        private ProgressDialog dialog;

        private String msg;

        private User user;

        public UpdateUserTask(User user) {
            this.user = user;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(UpdateUserActivity.this);
            dialog.setMessage("更新中，请稍后");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int smsCode = params[0];
            int resultCode = 201;
            try {
                UserManager um = UserManager.getInstance(UpdateUserActivity.this);
                um.updateUser(user.getName(), user.getTel(), user.getAddress(), smsCode);
            } catch (IOException e) {
                resultCode = 500;
                msg = e.getMessage();
            } catch (HttpForbiddenException e) {
                resultCode = 403;
                msg = e.getMessage();
            } catch (HttpBadRequestException e) {
                resultCode = 400;
                msg = e.getMessage();
            } catch (NoLoginException e) {
                resultCode = 401;
                msg = e.getMessage();
            }
            return resultCode;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            try {
                dialog.cancel();
            } catch (Exception ignored) {
            }
            if (integer == 201) {
                Toast.makeText(UpdateUserActivity.this, "更新成功", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            } else if (integer == 400 || integer == 403) {
                Toast.makeText(UpdateUserActivity.this, msg, Toast.LENGTH_SHORT).show();
            } else {
                ToastUtils.showNetworkResponse(UpdateUserActivity.this, integer);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_update_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {



        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_update_user, container, false);



            return rootView;
        }
    }
}
