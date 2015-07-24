package com.nightwind.meal.utils;

import android.support.v4.app.FragmentManager;
import android.view.View;

import com.nightwind.meal.R;
import com.nightwind.meal.fragment.PhotoFragment;

/**
 * Created by nightwind on 15/4/26.
 */
public class PhotoOnClickListener implements View.OnClickListener {

    private final FragmentManager fm;
    private final String imageUri;

    public PhotoOnClickListener(FragmentManager fm, String imageUri) {
        this.fm = fm;
        this.imageUri = imageUri;
    }

    @Override
    public void onClick(View v) {
        String tag = PhotoFragment.class.getSimpleName();
        fm.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                .add(R.id.container, PhotoFragment.newInstance(imageUri), tag)
                .addToBackStack(tag)
                .commit();

    }
}
