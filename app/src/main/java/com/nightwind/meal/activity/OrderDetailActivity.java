package com.nightwind.meal.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nightwind.meal.R;
import com.nightwind.meal.exception.HttpBadRequestException;
import com.nightwind.meal.fragment.OrdersFragment;
import com.nightwind.meal.loader.ShowDialogAsyncTask;
import com.nightwind.meal.manager.UserManager;
import com.nightwind.meal.model.Order;
import com.nightwind.meal.utils.NetworkUtils;
import com.nightwind.meal.utils.ToastUtils;

public class OrderDetailActivity extends ActionBarActivity {

    public static final String INTENT_ARG_ORDER_POSITION = "orderPosition";

    //delivery
    private TextView tvAddress;
    private TextView tvTel;

    //time
    private TextView tvCommitTime;
    private TextView tvConfirmTime;
    private TextView tvSendTime;

    private Button btnUpdate;

    private Order order;
    private AsyncTask<Integer, Void, Integer> orderCancelTask;
    private UserManager.OrderManager orderManager;
    private OrdersFragment.OrderViewHolder orderViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        orderManager = UserManager.getInstance(OrderDetailActivity.this).getOrderManager();

        int orderPosition = getIntent().getIntExtra(INTENT_ARG_ORDER_POSITION, 0);
        order = orderManager.getOrders().get(orderPosition);

        tvAddress = (TextView) findViewById(R.id.address);
        tvTel = (TextView) findViewById(R.id.tel);

        tvCommitTime = (TextView) findViewById(R.id.commit_time);
        tvConfirmTime = (TextView) findViewById(R.id.confirm_time);
        tvSendTime = (TextView) findViewById(R.id.send_time);

        btnUpdate = (Button) findViewById(R.id.cancel_order);

        orderViewHolder = new OrdersFragment.OrderViewHolder(getWindow().getDecorView());

        findViewById(R.id.rl_dish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OrderDetailActivity.this, OrderConfirmActivity.class);
                intent.putExtra("dish", order.getDish());
                startActivity(intent);
            }
        });

        refreshData(order);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        int orderPosition = getIntent().getIntExtra(INTENT_ARG_ORDER_POSITION, 0);
        order = orderManager.getOrders().get(orderPosition);
        refreshData(order);
    }

    public void refreshData(Order order) {

        int type = 0;
        if (order.getStatus().equals("待确认")) {
            btnUpdate.setText(R.string.cancel_order);
            type = 0;
        } else if (order.getStatus().equals("商家已确认")) {
            btnUpdate.setText(R.string.order_success_confirm);
            type = 1;
        } else {
            btnUpdate.setVisibility(View.GONE);
        }

        final int finalType = type;
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUpdateOrder(finalType);
            }
        });


        orderViewHolder.bindView(order, order.getCost());

        tvAddress.setText(order.getAddress());
        tvTel.setText(order.getTel());

        tvCommitTime.setText(order.getTime());
        tvConfirmTime.setText(order.getConfirmTime());
        tvSendTime.setText(order.getSendTime());
    }

    private void onUpdateOrder(int type) {
        String msg = null;
        if (type == 0) {
            msg = "订单取消中";
        } else if (type == 1) {
            msg = "确认收货中";
        }
        orderCancelTask = new OrderUpdateTask(this, msg).execute(order.getId(), type);
    }

    class OrderUpdateTask extends ShowDialogAsyncTask<Integer, Void, Integer> {

        String msg;

        Order order;

        private Integer type;

        public OrderUpdateTask(Context context, String dialogMsg) {
            super(context, dialogMsg);
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            int orderId = params[0];
            type = params[1];
            int result = 0;
            try {
                order = orderManager.updateOrder(orderId, type);
            } catch (HttpBadRequestException e) {
                result = 1;
                msg = e.getMessage();
            } catch (Exception e) {
                result = NetworkUtils.Exception2Code(e);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer resultCode) {
            super.onPostExecute(resultCode);
            if (getContext() != null) {
                if (resultCode == 0) {
                    if (type == 0) {
                        Toast.makeText(getContext(), R.string.cancel_order_success, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), R.string.order_success_confirm_success, Toast.LENGTH_SHORT).show();
                    }
                    refreshData(order);
                } else if (resultCode == 1) {
                    if (msg == null) {
                        if (type == 0) {
                            msg = "取消失败";
                        } else {
                            msg = "确认失败";
                        }
                    }
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                } else {
                    ToastUtils.showNetworkResponse(getContext(), resultCode);
                }
            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (orderCancelTask != null) {
            orderCancelTask.cancel(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_detail, menu);
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
}
