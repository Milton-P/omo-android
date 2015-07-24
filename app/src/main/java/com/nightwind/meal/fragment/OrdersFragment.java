package com.nightwind.meal.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nightwind.meal.R;
import com.nightwind.meal.activity.MainActivity;
import com.nightwind.meal.activity.OrderDetailActivity;
import com.nightwind.meal.exception.HttpBadRequestException;
import com.nightwind.meal.exception.HttpForbiddenException;
import com.nightwind.meal.exception.NoLoginException;
import com.nightwind.meal.manager.DishManager;
import com.nightwind.meal.manager.UserManager;
import com.nightwind.meal.model.Dish;
import com.nightwind.meal.model.Order;
import com.nightwind.meal.utils.NetworkUtils;
import com.nightwind.meal.utils.ToastUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends MainActivity.PlaceholderFragment {

    private RecyclerView.Adapter mAdapter;
    private UserManager.OrderManager orderManager;
    private TextView tvLoadingOrEmpty;

    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dishes, container, false);

        tvLoadingOrEmpty = (TextView) view.findViewById(R.id.loadingOrEmpty);
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        mRecyclerView.setHasFixedSize(true);

        boolean isLogin = UserManager.getInstance(getActivity()).checkLogin();

        //use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        orderManager = UserManager.getInstance(getActivity()).getOrderManager();
        List<Order> orders = orderManager.getOrders();
        mAdapter = new OrderAdapter(orders);
        mRecyclerView.setAdapter(mAdapter);

        if (!isLogin) {
            orders.clear();
            ToastUtils.showNoLogin(getActivity());
        }

        if (mAdapter.getItemCount() == 0) {
            if (isLogin) {
                tvLoadingOrEmpty.setText(R.string.loading);
            } else {
                tvLoadingOrEmpty.setText(R.string.list_empty);
            }
            tvLoadingOrEmpty.setVisibility(View.VISIBLE);
        } else {
            tvLoadingOrEmpty.setVisibility(View.GONE);
        }

        if (isLogin) {
            // pull data
            new PullOrdersTask().execute();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    private class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

        private List<Order> orders;

        public OrderAdapter(List<Order> orders) {
            this.orders = orders;
        }

        @Override
        public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_order_recyclerview, parent, false);
            return new OrderViewHolder(view);
        }

        @Override
        public void onBindViewHolder(OrderViewHolder holder, final int position) {
            final Order order = orders.get(position);
            holder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOrderDetail(order, position);
                }
            });
            holder.bindView(order, order.getCost());
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }
    }

    private void onOrderDetail(Order order, int position) {
        Intent intent = new Intent(getActivity(), OrderDetailActivity.class);
        intent.putExtra("orderPosition", position);
        startActivity(intent);
    }

    public static class OrderViewHolder extends DishesFragment.DishViewHolder{

        public TextView status;
        public TextView time;
        public TextView count;

        public OrderViewHolder(View itemView) {
            super(itemView);
            status = (TextView) itemView.findViewById(R.id.status);
            time = (TextView) itemView.findViewById(R.id.time);
            count = (TextView) itemView.findViewById(R.id.count);
        }

        public void bindView(Order order, double displayCost) {
            bindView(order.getDish(), displayCost);
            status.setText(order.getStatus());
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            String strTime = sdf.format(order.getTime());
//            time.setText(strTime);
            time.setText(order.getTime());
            count.setText("共" + order.getDishCount() + "份");
        }
    }

    private class PullOrdersTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            int resultCode = 200;
            try {
                orderManager.pullOrders();
            } catch (Exception e) {
//                e.printStackTrace();
                resultCode = NetworkUtils.Exception2Code(e);
            }
            return resultCode;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer == 200) {
                mAdapter.notifyDataSetChanged();

            } else {
                ToastUtils.showNetworkResponse(getActivity(), integer);
            }
            if (mAdapter.getItemCount() == 0) {
                tvLoadingOrEmpty.setText(R.string.list_empty);
                tvLoadingOrEmpty.setVisibility(View.VISIBLE);
            } else {
                tvLoadingOrEmpty.setVisibility(View.GONE);
            }
        }
    }

}
