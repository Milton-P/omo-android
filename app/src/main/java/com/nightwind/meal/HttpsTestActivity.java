package com.nightwind.meal;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nightwind.meal.model.User;
import com.nightwind.meal.utils.NetworkUtils;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;
import java.util.Map;


public class HttpsTestActivity extends ActionBarActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_https_test);
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                httpsTest();
                httpsTest1();
            }
        });
        tvResult = (TextView) findViewById(R.id.result);
    }

    HttpClient hc = new DefaultHttpClient();
    //                initKey();
    String url = "https://omo.nw4869.cc:9443/onlineMealOrdering/rest/dishes";


    private void httpsTest1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                try {
//                    User user = LoginTest();
//                    System.out.println("username = " + user.getUsername() + " name = " + user.getName() + " balance = " + user.getBalance());
//                    UserManager um = UserManager.getInstance(HttpsTestActivity.this);
//                    System.out.println("name = " + um.getUser().getName());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (HttpForbiddenException e) {
//                    e.printStackTrace();
//                } /*catch (NoLoginException e) {
//                    e.printStackTrace();
//                }*/
            }
        }).start();
    }

//    private User LoginTest() throws IOException, HttpForbiddenException {
////        return new UserManager(this, "nw").login("nw");
//        return UserManager.login(this, "nw", "nw").getUser();
//    }

    private void httpsTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
//                    initKey();
//                    final String result = getData(url);
//                    final String result = new NetworkUtils(HttpsTestActivity.this).executeTest(url);
                    String url1 = "https://omo.nw4869.cc:9443/onlineMealOrdering/rest/users/nw/";
                    Map<String, String> params = new HashMap<>();
                    params.put("username", "nw");
                    params.put("password", "nw");
                    params.put("name", "nw");

                    String param = "username=nw&password=nw&name=nw";
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "nw");
                    final String result = new NetworkUtils(HttpsTestActivity.this).executePut(url1, param, headers);
//                    final String result = new NetworkUtils(HttpsTestActivity.this).executeTest(url1);
//                    final String result = new NetworkUtils(HttpsTestActivity.this).executeGet(url1, params, headers);
                    System.out.println(result);

                    // json convert to model
                    Gson gson = new Gson();
                    User user = gson.fromJson(result, User.class);
                    System.out.println(user.getName());

                    tvResult.post(new Runnable() {
                        @Override
                        public void run() {
                            tvResult.setText(result);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_https_test, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
