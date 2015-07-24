package com.nightwind.meal.fragment;


import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nightwind.meal.R;
import com.nightwind.meal.utils.Options;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends Fragment {


    private static final String ARG_IMAGE_URI = "arg_image_uri";

    public PhotoFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance(String imageUri) {
        Fragment fragment = new PhotoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_IMAGE_URI, imageUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        //图片加载
        String imageUri = getArguments().getString(ARG_IMAGE_URI);
        ImageView imageView = (ImageView) view.findViewById(R.id.image);
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(imageUri, imageView, Options.getRoundedImageOptions());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });
        return view;
    }


}
