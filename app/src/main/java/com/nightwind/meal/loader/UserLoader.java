package com.nightwind.meal.loader;

import android.content.Context;

import com.nightwind.meal.exception.HttpForbiddenException;
import com.nightwind.meal.exception.NoLoginException;
import com.nightwind.meal.manager.UserManager;
import com.nightwind.meal.model.User;
import com.nightwind.meal.utils.ToastUtils;

import java.io.IOException;

/**
 * Created by nightwind on 15/4/13.
 */
public class UserLoader extends DataLoader<User> {

    private UserManager um;

    public UserLoader(Context context, UserManager um) {
        super(context);
        this.um = um;
    }

    @Override
    public User loadInBackground() {
        User user = null;
        try {
            user = um.pullUser();
        } catch (Exception e) {
            ToastUtils.showNetworkResponse(getContext(), e);
        }
        return user;
    }
}
