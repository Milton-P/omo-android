package com.nightwind.meal.manager;

import android.content.Context;

import com.google.gson.Gson;
import com.nightwind.meal.exception.HttpBadRequestException;
import com.nightwind.meal.exception.HttpForbiddenException;
import com.nightwind.meal.model.Dish;
import com.nightwind.meal.utils.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nightwind on 15/4/12.
 */
public class DishManager {

    private Context context;

    private static DishManager dm;

    private static List<Dish> dishList;

//    private static Map<Integer, Dish> dishMap;

    private DishManager() {

    }

    private DishManager(Context context) {
        this.context = context;
    }

    public static DishManager getInstance(Context context) {
        if (dm == null) {
            dm = new DishManager(context);
            dishList = new ArrayList<>();
//            dishMap = new HashMap<>();
//            init();
        }
        dm.context = context;
        return dm;
    }

    public List<Dish> getDishList() {
        return dishList;
    }

//    public Map<Integer, Dish> getDishMap() {
//        return dishMap;
//    }

    public void pullDishes() throws IOException {
        String result;
        try {
            result = new NetworkUtils(context).executeGet(NetworkUtils.SERVER_HOST + "/dishes", null, null);
            Dish[] dishArray = new Gson().fromJson(result, Dish[].class);
            dishList.clear();
            Collections.addAll(dishList, dishArray);
        } catch (HttpForbiddenException | HttpBadRequestException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        }
    }
}
