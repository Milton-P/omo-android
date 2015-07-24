package com.nightwind.meal.manager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nightwind.meal.activity.LoginRegisterActivity;
import com.nightwind.meal.exception.HttpBadRequestException;
import com.nightwind.meal.exception.HttpForbiddenException;
import com.nightwind.meal.exception.NoLoginException;
import com.nightwind.meal.model.Order;
import com.nightwind.meal.model.User;
import com.nightwind.meal.utils.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nightwind on 15/4/11.
 */
public class UserManager {

    private static UserManager um;

    private User user;

    private String token;

    private Context context;

    private static OrderManager om;

    private UserManager() {
        om = new OrderManager();
    }

    private UserManager(String username) {
        this();
        this.user = new User(username);
    }

    private UserManager(User user) {
        this();
        this.user = user;
    }

    public static UserManager getInstance(Context context) /*throws NoLoginException*/ {
        if (um == null) {
            um = new UserManager();
            um.context = context;
            initFromSP();
        }
        um.context = context;
//        assertLogin();
        return um;
    }

    public static UserManager login(Context context, String username, String password) throws IOException, HttpForbiddenException {
//        um = new UserManager(username);
        um = getInstance(context);
        um.user = new User(username);
        try {
            return um.login(password);
        } catch (Exception e) {
            um.user = null;
            throw e;
        }
    }

    public static UserManager register(Context context, String username, String name, String password) throws IOException, HttpBadRequestException {
//        um = new UserManager(username);
        um = getInstance(context);
        um.user = new User(username);
        return um.register(name, password);
    }


    private static void initFromSP() {
//        // DEBUG
//        um.user = new User("nw");
//        um.token = "nw";

        SharedPreferences sp = um.context.getSharedPreferences("user", Context.MODE_PRIVATE);
        String username = sp.getString("username", null);
        String token = sp.getString("token", null);
        um.user = new User(username);
        um.token = token;
    }

    public OrderManager getOrderManager() {
        return om;
    }

    public void assertLogin() throws NoLoginException {
        if (!checkLogin()) {
            throw new NoLoginException();
        }
    }

    public boolean checkLogin()  {
        return um.token != null && um.user != null && um.user.getUsername() != null;
    }

    public User getUser() throws NoLoginException {
        assertLogin();
        return this.user;
    }

    private String pullToken(String password) throws IOException, HttpForbiddenException {
        String token = null;
        String params = "password=" + password;
        String targetURL = NetworkUtils.SERVER_HOST + "/auth/" + URLEncoder.encode(user.getUsername(), "UTF-8") ;
        String result = null;
        try {
            result = new NetworkUtils(context).executePost(targetURL, params, null);
        } catch (HttpBadRequestException e) {
            e.printStackTrace();
        }
        try {
            JSONObject jo = new JSONObject(result);
            token = (String) jo.get("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }

    public User pullUser() throws IOException, HttpForbiddenException, NoLoginException {
        assertLogin();
        String result = null;
        try {
            result = new NetworkUtils(context).executeGet(NetworkUtils.SERVER_HOST + "/users/" + URLEncoder.encode(user.getUsername(), "UTF-8"), null, getAuthHeader());
        } catch (HttpBadRequestException e) {
            e.printStackTrace();
        }
        user = new Gson().fromJson(result, User.class);
        return user;
    }

    private Map<String, String> getAuthHeader() {
        Map<String, String> authHeader = new HashMap<>();
        authHeader.put("Authorization", token);
        return authHeader;
    }

    private UserManager login(String password) throws IOException, HttpForbiddenException {

//        if (token == null) {
            token = pullToken(password);
//        }
        try {
            user = pullUser();
        } catch (NoLoginException e) {
            e.printStackTrace();
        }
        saveUsernameToken();
        return this;
    }

    private void saveUsernameToken() {
        SharedPreferences sp = um.context.getSharedPreferences("user", Context.MODE_PRIVATE);
        sp.edit().putString("username", user.getUsername())
                .putString("token", token)
                .apply();

    }

    public void logout() {
        SharedPreferences sp = um.context.getSharedPreferences("user", Context.MODE_PRIVATE);
        sp.edit().remove("username").remove("token").apply();
        um.user = null;
        um.token = null;
    }

    private String registerGetToken(String name, String password) throws HttpForbiddenException, IOException, HttpBadRequestException {
        String result = null;
        String params = "username=" + user.getUsername() + "&name=" + name + "&password=" + password;
        result = new NetworkUtils(context).executePost(NetworkUtils.SERVER_HOST + "/users/", params, null);

        try {
            JSONObject jo = new JSONObject(result);
            token = (String) jo.get("token");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }

    private UserManager register(String name, String password) throws IOException, HttpBadRequestException {
        try {
            registerGetToken(name, password);
            user = pullUser();
        } catch (NoLoginException | HttpForbiddenException e) {
            e.printStackTrace();
        }
        saveUsernameToken();
        return this;
    }

    public User updateUser(String name, String tel, String address, int smsCode) throws IOException, HttpForbiddenException, HttpBadRequestException, NoLoginException {
        assertLogin();
        String result;
        StringBuilder params = new StringBuilder();
        if (name != null) {
            params.append("&name=").append(URLEncoder.encode(name, "UTF-8"));
        }
        if (tel != null) {
            params.append("&tel=").append(URLEncoder.encode(tel, "UTF-8"));
        }
        if (address != null) {
            params.append("&address=").append(URLEncoder.encode(address, "UTF-8"));
        }
        params.append("&smscode=").append(URLEncoder.encode(String.valueOf(smsCode), "UTF-8"));
        if (params.length() > 0) {
            params.deleteCharAt(0);
        }
        result = new NetworkUtils(context).executePut(NetworkUtils.SERVER_HOST + "/users/" + URLEncoder.encode(getUser().getUsername(), "UTF-8"), params.toString(), getAuthHeader());
        user = new Gson().fromJson(result, User.class);
        return user;
    }


    public static final int INTENT_REQUEST_LOGIN = 0xffff;

    public AlertDialog getNoLoginAlertDialog() {
        return new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("提示")
                .setMessage("是否马上登录或注册？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (context instanceof Activity) {
                            Intent intent = new Intent(context, LoginRegisterActivity.class);
                            ((Activity)context).startActivityForResult(intent, INTENT_REQUEST_LOGIN);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
    }

//    public void requestSmsCode() {
//
//    }


    public class OrderManager {

        private List<Order> orders;

        public OrderManager() {
            orders = new ArrayList<>();
        }

        public List<Order> getOrders() {
            return orders;
        }

        public List<Order> pullOrders() throws NoLoginException, IOException, HttpBadRequestException, HttpForbiddenException {
            String username = um.getUser().getUsername();
            String result = new NetworkUtils(context).executeGet(NetworkUtils.SERVER_HOST + "/users/" + URLEncoder.encode(username, "UTF-8") + "/orders", null, getAuthHeader());
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
//            Gson gson = new Gson();
            Order[] ordersArray = gson.fromJson(result, Order[].class);
            orders.clear();
            Collections.addAll(orders, ordersArray);
            return orders;
        }

        /**
         * 提交订单并将新的订单添加到orders(0)的位置
         * @param dishId
         * @param dishCount
         * @return
         * @throws NoLoginException
         * @throws IOException
         * @throws HttpBadRequestException
         * @throws HttpForbiddenException
         */
        public Order pushOrder(int dishId, int dishCount) throws NoLoginException, IOException, HttpBadRequestException, HttpForbiddenException {
            String username = um.getUser().getUsername();
            String params = "dishId=" + dishId + "&dishCount=" + dishCount;
            String result = new NetworkUtils(context).executePost(NetworkUtils.SERVER_HOST + "/users/" + URLEncoder.encode(username, "UTF-8") + "/orders", params, getAuthHeader());
            Order order = new Gson().fromJson(result, Order.class);
            orders.add(0, order);
            return order;
        }

        public Order updateOrder(int orderId, int type) throws NoLoginException, IOException, HttpBadRequestException, HttpForbiddenException {
            String username = um.getUser().getUsername();
            String params;
            if (type == 0 ) {
                params = "status=订单取消";
            } else {
                params = "status=已完成";
            }
            // 取消失败会引发HttpBadRequestException
            String result = new NetworkUtils(context).executePut(NetworkUtils.SERVER_HOST + "/users/" + URLEncoder.encode(username, "UTF-8") + "/orders/" + orderId, params, getAuthHeader());
            Order order = new Gson().fromJson(result, Order.class);
//            getOrderById(orderId).setStatus("订单取消");
            updateOrderById(order);
            return order;
        }

        public Order getOrderById(int orderId) {
            Order rst = null;
            for (Order order: orders) {
                if (order.getId() == orderId) {
                    rst = order;
                }
            }
            return rst;
        }

        synchronized public boolean updateOrderById(Order order) {
            boolean ok = false;
            for (int i = 0; i < orders.size(); i++) {
                if (orders.get(i).getId() == order.getId()) {
                    orders.remove(i);
                    orders.add(i, order);
                    ok = true;
                }
            }
            return ok;
        }
    }
}

