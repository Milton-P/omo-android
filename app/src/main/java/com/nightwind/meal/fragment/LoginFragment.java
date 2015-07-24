package com.nightwind.meal.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
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
import com.nightwind.meal.exception.HttpForbiddenException;
import com.nightwind.meal.manager.UserManager;
import com.nightwind.meal.utils.ToastUtils;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends LoginRegisterActivity.PlaceholderFragment {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private static final int REQUEST_REGISTER = 0;
    private EditText etUsername;
    private EditText etPassword;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public String getTitle() {
//        return getActivity().getResources().getString(R.string.login);
        return "登录";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);


        View view = inflater.inflate(R.layout.fragment_login, container, false);

        etUsername = (EditText) view.findViewById(R.id.username);
        etPassword = (EditText) view.findViewById(R.id.password);

        view.findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });

        return view;
    }

    private void onLogin() {
        String username = String.valueOf(etUsername.getText());
        String password = String.valueOf(etPassword.getText());
        if (username.trim().length() == 0) {
            etUsername.setError("用户名不能为空");
            return;
        }
        if (password.trim().length() == 0) {
            etPassword.setError("密码不能为空");
            return;
        }

        new LoginTask().execute(username, password);
    }



    class LoginTask extends AsyncTask<String, Void, Integer> {

        private ProgressDialog dialog;

        private Context context;

        @Override
        protected void onPreExecute() {
            context = getActivity().getApplicationContext();
            super.onPreExecute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("登录中，请稍后");
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Integer doInBackground(String... params) {
            int resultCode = 201;
            UserManager um;
            try {
                um = UserManager.login(context, params[0], params[1]);
                Log.d("LoginFragment", um.toString());
            } catch (IOException e) {
                resultCode = 500;
            } catch (HttpForbiddenException e) {
                resultCode = 403;
            }
            return resultCode;
        }

        @Override
        protected void onPostExecute(Integer statusCode) {
            super.onPostExecute(statusCode);
            try {
                dialog.cancel();
            } catch (Exception ignored) {
            }
            if (statusCode == 201) {
                Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show();

                if (getActivity() != null) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                }
            } else if (statusCode == 403) {
                Toast.makeText(context, R.string.auth_failed, Toast.LENGTH_SHORT).show();
            }else {
                ToastUtils.showNetworkResponse(context, statusCode);
            }
        }
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_login_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_register) {
            replaceFragment(new RegisterFragment());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
