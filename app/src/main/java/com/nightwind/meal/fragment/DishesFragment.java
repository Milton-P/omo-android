package com.nightwind.meal.fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nightwind.meal.R;
import com.nightwind.meal.activity.MainActivity;
import com.nightwind.meal.activity.OrderConfirmActivity;
import com.nightwind.meal.manager.DishManager;
import com.nightwind.meal.model.Dish;
import com.nightwind.meal.utils.Options;
import com.nightwind.meal.utils.PhotoOnClickListener;
import com.nightwind.meal.utils.ToastUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.IOException;
import java.util.List;

import javax.net.ssl.SSLHandshakeException;

/**
 * A simple {@link Fragment} subclass.
 */
public class DishesFragment extends MainActivity.PlaceholderFragment {

    private static final int REQUEST_BUY = 0;
    private RecyclerView.Adapter mAdapter;
    private List<Dish> mDishes;
    private TextView tvLoadingOrEmpty;
    private AsyncTask<Void, Void, Integer> pullDishesTask;

    public DishesFragment() {
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

        //use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter
        mDishes = DishManager.getInstance(getActivity()).getDishList();
        mAdapter = new DishAdapter(mDishes);
        mRecyclerView.setAdapter(mAdapter);

        if (mAdapter.getItemCount() == 0) {
            tvLoadingOrEmpty.setText(R.string.loading);
            tvLoadingOrEmpty.setVisibility(View.VISIBLE);
        } else {
            tvLoadingOrEmpty.setVisibility(View.GONE);
        }

//        PopupWindow mPopupWindow = new PopupWindow(image, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true););
//        mPopupWindow.setBackgroundDrawable(getActivity().getResources().getDrawable(R.color.translucent));
//        mPopupWindow.showAtLocation(aParentView, Gravity.CENTER, 0, 0);
//        mPopupWindow.update();

        // pull data
        pullDishesTask = new PullDishesTask().execute();

        return view;
    }

    class PullDishesTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            int code = 200;
            try {
                DishManager.getInstance(getActivity()).pullDishes();
            } catch (SSLHandshakeException e) {
                e.printStackTrace();
                code = 500;
            } catch (IOException e) {
                code = 500;
            }
            return code;
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

    private class DishAdapter extends RecyclerView.Adapter<DishViewHolder> {

        private List<Dish> dishList;

        public DishAdapter(List<Dish> culturalDishes) {
            dishList = culturalDishes;
        }

        @Override
        public DishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_dish_recyclerview, parent, false);
            return new DishViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final DishViewHolder holder, int position) {
            final Dish dish = dishList.get(position);
            holder.bindView(dish, dish.getCost());
            if (dish.getPicUrl() != null) {
                holder.image.setOnClickListener(new PhotoOnClickListener(getFragmentManager(), dish.getPicUrl()));
            }
            if (dish.getStatus().equals("正常")) {
                holder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBuy(dish, v);
                    }
                });
            } else {
                holder.cost.setText(dish.getStatus());
                holder.setOnClickListener(null);
            }
        }

        @Override
        public int getItemCount() {
            return dishList.size();
        }
    }

    private void onBuy(Dish dish, View shareView) {
        Intent intent = new Intent(getActivity(), OrderConfirmActivity.class);
        intent.putExtra("dish", dish);
        startActivityForResult(intent, REQUEST_BUY);
    }

    public static class DishViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView info;
        public TextView cost;
        public ImageView image;

        private View.OnClickListener onClickListener;

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            if( itemView != null ) {
                itemView.setOnClickListener(onClickListener);
            }
        }

        public DishViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            info = (TextView) itemView.findViewById(R.id.info);
            cost = (TextView) itemView.findViewById(R.id.cost);
            image = (ImageView) itemView.findViewById(R.id.icon);
        }

        public void bindView(Dish dish, double displayCost) {
            name.setText(dish.getName());
            info.setText(dish.getInfo());
//            cost.setText("¥" + dish.getCost());
            cost.setText("¥" + displayCost);

            //图片下载选项
            ImageLoader imageLoader = ImageLoader.getInstance();
            imageLoader.displayImage(dish.getPicUrl(), image, Options.getImageOptions());
            itemView.setOnClickListener(onClickListener);
        }
    }
}
