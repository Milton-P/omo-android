package com.nightwind.meal.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nightwind.meal.R;
import com.nightwind.meal.exception.HttpBadRequestException;
import com.nightwind.meal.exception.NoLoginException;
import com.nightwind.meal.fragment.DishesFragment;
import com.nightwind.meal.loader.ShowDialogAsyncTask;
import com.nightwind.meal.manager.UserManager;
import com.nightwind.meal.model.Dish;
import com.nightwind.meal.model.Order;
import com.nightwind.meal.model.User;
import com.nightwind.meal.utils.NetworkUtils;
import com.nightwind.meal.utils.ToastUtils;

public class OrderConfirmActivity extends ActionBarActivity {

    private static final int REQUEST_UPDATE_USER = 0;
//    private static final int LOAD_USER = 0;
    private TextView tvCount;
    private TextView tvTotalPrice;
    private TextView tvAddress;
    private TextView tvTel;

    private int count = 1;
    private Dish dish;
    private AsyncTask<Void, Void, Integer> pullUserTask;
    private UserManager um;
    private AsyncTask<Integer, Void, Integer> commitOrderTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        um = UserManager.getInstance(OrderConfirmActivity.this);

        DishesFragment.DishViewHolder dishViewHolder = new DishesFragment.DishViewHolder(getWindow().getDecorView());
        dish = getIntent().getParcelableExtra("dish");
        dishViewHolder.bindView(dish, dish.getCost());

        tvCount = (TextView) findViewById(R.id.count);
        tvTotalPrice = (TextView) findViewById(R.id.total_price);
        tvAddress = (TextView) findViewById(R.id.address);
        tvTel = (TextView) findViewById(R.id.tel);
        findViewById(R.id.lr_delivery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    um.assertLogin();
                    Intent intent = new Intent(OrderConfirmActivity.this, UpdateUserActivity.class);
                    intent.putExtra(UpdateUserActivity.INTENT_UPDATE_TYPE, UpdateUserActivity.INTENT_UPDATE_TYPE_DELIVERY);
                    startActivityForResult(intent, REQUEST_UPDATE_USER);
                } catch (NoLoginException e) {
                    um.getNoLoginAlertDialog().show();
                }
            }
        });
        findViewById(R.id.commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    um.assertLogin();
                    if (commitOrderTask == null || commitOrderTask.isCancelled()) {
                        commitOrderTask = new CommitOrderTask(OrderConfirmActivity.this, "订单提交中").execute(dish.getId(), count);
                    }
                } catch (NoLoginException e) {
                    um.getNoLoginAlertDialog().show();
                }
            }
        });
//        getSupportLoaderManager().initLoader(LOAD_USER, null, new UserLoaderCallbacks());

        try {
            User user = um.getUser();
            refreshData(user);
            if (TextUtils.isEmpty(user.getTel()) || TextUtils.isEmpty(user.getAddress())) {
                pullUserTask = new PullUserTask().execute();
            }
        } catch (NoLoginException ignored) {
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (pullUserTask != null) {
            pullUserTask.cancel(true);
        }
    }

    class CommitOrderTask extends ShowDialogAsyncTask<Integer, Void, Integer> {
        String msg;

        Order order;

        public CommitOrderTask(Context context, String dialogMsg) {
            super(context, dialogMsg);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int result = 0;
            try {
                int dishId = params[0];
                int dishCount = params[1];
                order = um.getOrderManager().pushOrder(dishId, dishCount);
            } catch (HttpBadRequestException e) {
                result = 400;
                msg = e.getMessage();
            } catch (Exception e) {
                result = NetworkUtils.Exception2Code(e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (getContext() != null) {
                if (integer == 0) {
                    Toast.makeText(getContext(), R.string.commit_order_success, Toast.LENGTH_SHORT).show();
                    //结束自己，跳转到订单详情
                    Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                    getContext().startActivity(intent);
                    intent.putExtra(OrderDetailActivity.INTENT_ARG_ORDER_POSITION, 0);
                    finish();
                } else if (integer == 400) {
                    if (msg != null) {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    ToastUtils.showNetworkResponse(getContext(), integer);
                }
            }
        }
    }

    class PullUserTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            try {
                um.pullUser();
            } catch (Exception e) {
                return NetworkUtils.Exception2Code(e);
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            if (result == 0) {
                try {
                    refreshData(um.getUser());
                } catch (NoLoginException e) {
                    ToastUtils.showNoLogin(OrderConfirmActivity.this);
                }
            }
        }
    }

//    private class UserLoaderCallbacks implements LoaderManager.LoaderCallbacks<User> {
//        @Override
//        public Loader<User> onCreateLoader(int id, Bundle args) {
//            return new DataLoader<User>(OrderConfirmActivity.this) {
//                @Override
//                public User loadInBackground() {
//                    return null;
//                }
//            };
//        }
//
//        @Override
//        public void onLoadFinished(Loader<User> loader, User data) {
//
//            data.getTel();
//        }
//
//        @Override
//        public void onLoaderReset(Loader<User> loader) {
//
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
//            Log.d("OrderConfirmActivity", "OK");
            try {
                refreshData(um.getUser());
            } catch (NoLoginException e) {
                ToastUtils.showNoLogin(this);
            }
        }
    }

    private void refreshData(User user) {
        updateTotalPrice();
        tvTel.setText(user.getTel());
        tvAddress.setText(user.getAddress());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_confirm, menu);
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

    private void updateTotalPrice() {
        tvTotalPrice.setText("¥" + String.valueOf(count * dish.getCost()));
    }

    public void onAddition(View view) {
        if (count < 9) {
            count++;
        }
        tvCount.setText(String.valueOf(count));
        updateTotalPrice();
    }

    public void onSubtraction(View view) {
        if (count > 1) {
            count--;
        }
        tvCount.setText(String.valueOf(count));
        updateTotalPrice();
    }
}
