package com.nightwind.meal.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.nightwind.meal.R;
import com.nightwind.meal.activity.LoginRegisterActivity;
import com.nightwind.meal.exception.HttpBadRequestException;
import com.nightwind.meal.exception.HttpForbiddenException;
import com.nightwind.meal.manager.UserManager;
import com.nightwind.meal.utils.ToastUtils;

import java.io.IOException;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class RegisterFragment extends LoginRegisterActivity.PlaceholderFragment {

    public static final String TAG = RegisterFragment.class.getSimpleName();

    private EditText etUsername;
    private EditText etName;
    private EditText etPassword;
    private EditText etPasswordConfirm;

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public String getTitle() {
        return "注册";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        View view = inflater.inflate(R.layout.fragment_register, container, false);

        etUsername = (EditText) view.findViewById(R.id.username);
        etName = (EditText) view.findViewById(R.id.name);
        etPassword = (EditText) view.findViewById(R.id.password);
        etPasswordConfirm = (EditText) view.findViewById(R.id.password_confirm);

        view.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRegister();
            }
        });

        return view;
    }

    private void onRegister() {

        boolean ok = true;

        String username = String.valueOf(etUsername.getText());
        String name = String.valueOf(etName.getText());
        String password = String.valueOf(etPassword.getText());
        String passwordConfirm = String.valueOf(etPasswordConfirm.getText());
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("用户名不可为空");
            ok = false;
        }
        if (TextUtils.isEmpty(name)) {
            etName.setError("姓名不可为空");
            ok = false;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("密码不可为空");
            ok = false;
        }
        if (TextUtils.isEmpty(passwordConfirm)) {
            etPasswordConfirm.setError("确认密码不可为空");
            ok = false;
        }
        if (!password.equals(passwordConfirm)) {
            etPasswordConfirm.setError("密码与确认密码不一致");
            ok = false;
        }
        if (password.length() < 6) {
            etPasswordConfirm.setError("密码长度必须大于6位");
            ok = false;
        }
        if (ok) {
            new RegisterTask().execute(username, name, password);
        }
    }


    class RegisterTask extends AsyncTask<String, Void, Integer> {

        private ProgressDialog dialog;
        private String msg;

        private Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            context = getActivity().getApplicationContext();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("登录中，请稍后");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }

        /**
         *  register user
         * @param params username, name, password
         * @return
         */
        @Override
        protected Integer doInBackground(String... params) {
            int resultCode = 201;
            try {
                UserManager.register(context, params[0], params[1], params[2]);
            } catch (IOException e) {
                msg = e.getMessage();
                resultCode = 500;
            } catch (HttpBadRequestException e) {
                msg = e.getMessage();
                resultCode = 400;
            }
            return resultCode;
        }

        @Override
        protected void onPostExecute(Integer statusCode) {
            try {
                dialog.cancel();
            } catch (Exception ignored) {
            }
            super.onPostExecute(statusCode);
            if (statusCode == 201) {
                Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            } else if (statusCode == 400) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            } else if (statusCode == 403) {
                Toast.makeText(context, R.string.auth_failed, Toast.LENGTH_SHORT).show();
            } else {
                ToastUtils.showNetworkResponse(context, statusCode);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_register_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_login) {
            replaceFragment(new LoginFragment());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
