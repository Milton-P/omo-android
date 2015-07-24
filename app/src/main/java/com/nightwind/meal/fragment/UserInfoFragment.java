package com.nightwind.meal.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nightwind.meal.activity.LoginRegisterActivity;
import com.nightwind.meal.activity.MainActivity;
import com.nightwind.meal.R;
import com.nightwind.meal.activity.UpdateUserActivity;
import com.nightwind.meal.exception.HttpForbiddenException;
import com.nightwind.meal.exception.NoLoginException;
import com.nightwind.meal.manager.UserManager;
import com.nightwind.meal.model.User;
import com.nightwind.meal.utils.ToastUtils;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserInfoFragment extends MainActivity.PlaceholderFragment {

    private static final int REQUEST_REGISTER = 0;
    private static final int REQUEST_UPDATE_USER = 1;

    private TextView tvName;
    private TextView tvBalance;
    private TextView tvTel;
    private TextView tvAddress;
    private Button btnLogin;
    private Button btnLogout;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);

        tvName = (TextView) view.findViewById(R.id.name);
        tvBalance = (TextView) view.findViewById(R.id.balance);
        tvTel = (TextView) view.findViewById(R.id.tel);
        tvAddress = (TextView) view.findViewById(R.id.address);

        btnLogin = (Button) view.findViewById(R.id.login);
        btnLogout = (Button) view.findViewById(R.id.logout);

        // 修改delivery
        view.findViewById(R.id.lr_delivery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UserManager.getInstance(getActivity()).assertLogin();
                    updateUser(UpdateUserActivity.INTENT_UPDATE_TYPE_DELIVERY);
                } catch (NoLoginException e) {
                    Toast.makeText(getActivity(), R.string.no_login, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 修改name
        view.findViewById(R.id.lr_name).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    UserManager.getInstance(getActivity()).assertLogin();
                    updateUser(UpdateUserActivity.INTENT_UPDATE_TYPE_NAME);
                } catch (NoLoginException e) {
                    Toast.makeText(getActivity(), R.string.no_login, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogin();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogout();
            }
        });

        updateData(null);

        Context context = getActivity();
        try {
            UserManager um = UserManager.getInstance(context);
            User user = um.getUser();
            updateData(user);
            // pull user from server
            new PullUserTask(context, um).execute();
        } catch (NoLoginException e) {
            updateData(null);
//            Toast.makeText(context, R.string.no_login, Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void updateUser(int type) {
        Intent intent = new Intent(getActivity(), UpdateUserActivity.class);
        intent.putExtra(UpdateUserActivity.INTENT_UPDATE_TYPE, type);
        startActivityForResult(intent, REQUEST_UPDATE_USER);
    }

    private void onLogin() {
        Intent intent = new Intent(getActivity(), LoginRegisterActivity.class);
        startActivityForResult(intent, REQUEST_REGISTER);
    }

    private void onLogout() {
        UserManager.getInstance(getActivity()).logout();
        // 更新界面
        updateData(null);
        Toast.makeText(getActivity(), R.string.logout_success, Toast.LENGTH_SHORT).show();
    }

    private void setBtnVisibility(boolean logged) {
        if (logged) {
            btnLogin.setVisibility(View.GONE);
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            btnLogin.setVisibility(View.VISIBLE);
            btnLogout.setVisibility(View.GONE);
        }
    }

    private void updateData(User user) {
        String name = null, balance = null, tel = null, address = null;
        if (user != null) {
            name = user.getName();
            balance = user.getBalance() != null ? "¥ " + user.getBalance() : "";
            tel = user.getTel();
            address = user.getAddress();
            setBtnVisibility(true);
        } else {
            setBtnVisibility(false);
        }
        tvName.setText(name);
        tvBalance.setText(balance);
        tvTel.setText(tel);
        tvAddress.setText(address);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            try {
                updateData(UserManager.getInstance(getActivity()).getUser());
            } catch (NoLoginException e) {
                e.printStackTrace();
            }
        }
//        if (requestCode == REQUEST_REGISTER && resultCode == Activity.RESULT_OK) {
//            updateData(UserManager.getInstance(getActivity()).getUser());
//        } else if (requestCode == REQUEST_UPDATE_USER && resultCode == Activity.RESULT_OK) {
//            updateData(UserManager.getInstance(getActivity()).getUser());
//        }
    }

    class PullUserTask extends AsyncTask<Void, Void, Integer> {

        private Context context;
        private UserManager um;

        PullUserTask(Context context, UserManager um) {
            this.context = context;
            this.um = um;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int resultCode = 200;
            try {
                um.pullUser();
            } catch (IOException e) {
                resultCode = 500;
            } catch (HttpForbiddenException e) {
                resultCode = 403;
            } catch (NoLoginException e) {
                resultCode = 401;
            }
            return resultCode;
        }

        @Override
        protected void onPostExecute(Integer resultCode) {
            super.onPostExecute(resultCode);
            if (resultCode == 200) {
                try {
                    updateData(um.getUser());
                } catch (NoLoginException e) {
                    ToastUtils.showNoLogin(context);
                }
            } else {
                ToastUtils.showNetworkResponse(context, resultCode);
            }
        }
    }
}
