package com.gbsoft.smartpatient.ui.main.profile;

import androidx.databinding.BindingAdapter;

import com.bumptech.glide.Glide;
import com.gbsoft.smartpatient.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;

public class ProfileBindingAdapter {

//    @BindingMethods({
//            @BindingMethod(type = "ExtendedFloatingActionButton.class", attribute = "icon", method = "setIconResource")
//    })

    @BindingAdapter("fabCustomIcon")
    public static void setFabImage(ExtendedFloatingActionButton fab, Integer res) {
        if (res == 0) return;
        fab.setIconResource(res);
    }

    @BindingAdapter("profilePic")
    public static void setProfileImage(ShapeableImageView imageView, String imgPath) {
        Glide.with(imageView).load(imgPath)
                .error(R.drawable.ic_add_user)
                .circleCrop()
                .into(imageView);
    }

}
