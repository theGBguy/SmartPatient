package com.gbsoft.smartpatient.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BindingAdapter;
import androidx.databinding.adapters.ListenerUtil;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.ui.main.addupdatemed.AddUpdateViewModel;
import com.gbsoft.smartpatient.ui.main.home.offline.OfflineHomeViewModel;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class BindingAdapters {
    @BindingAdapter(value = {"addTabSelectionWatcher"})
    public static void setOnTabSelectionChangeListener(TabLayout v, OfflineHomeViewModel vm) {
        TabLayout.OnTabSelectedListener newListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (vm.getCurrentTabPos().getValue() == tab.getPosition()) return;
                vm.setCurrentTabPos(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        };

        TabLayout.OnTabSelectedListener oldListener = ListenerUtil.trackListener(v, newListener, R.id.onTabSelectedListener);
        if (oldListener != null)
            v.removeOnTabSelectedListener(oldListener);
        v.addOnTabSelectedListener(newListener);
    }

    @BindingAdapter(value = "android:showHide")
    public static void showHideFab(ExtendedFloatingActionButton v, int tabPos) {
        if (tabPos == 0) v.show();
        else v.hide();
    }


    @BindingAdapter(value = "android:setSpannableText")
    public static void setSpannableText(TextView v, boolean isLoginScreen) {
        Context context = v.getContext();
        SpannableString spannableStr = new SpannableString(context.getString(
                isLoginScreen ? R.string.register_text : R.string.login_acc_already));
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                if (isLoginScreen)
                    Navigation.findNavController(widget).navigate(R.id.nav_register);
                else
                    Navigation.findNavController(widget).navigateUp();
            }
        };
        spannableStr.setSpan(clickableSpan, isLoginScreen ? 23 : 25, spannableStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableStr.setSpan(new ForegroundColorSpan(ResourceUtils.getColor(context, R.color.colorPrimary)),
                isLoginScreen ? 23 : 25, spannableStr.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        v.setText(spannableStr, TextView.BufferType.SPANNABLE);
        v.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @BindingAdapter(value = "android:srcCompat")
    public static void setMedicineImg(ImageView v, String imagePath) {
        CircularProgressDrawable drawable = new CircularProgressDrawable(v.getContext());
        drawable.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark, R.color.colorPrimaryLight);
        drawable.setCenterRadius(30f);
        drawable.setStrokeWidth(5f);
        drawable.start();
        Glide.with(v).load(imagePath).placeholder(drawable)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        drawable.stop();
                        SnackUtils.showMessage(v, "Error loading image : " + e.getLocalizedMessage());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        drawable.stop();
                        return false;
                    }
                })
                .into(v);
    }

    @BindingAdapter(value = "android:selectedStr")
    public static void setSpinnerSelection(Spinner spinner, int position) {
        if (spinner.getSelectedItemPosition() == position) return;
        spinner.setSelection(position);
    }

    @BindingAdapter(value = "android:spinnerSelectionWatcher")
    public static void setSpinnerSelectionWatcher(Spinner spinner, AddUpdateViewModel vm) {
        AdapterView.OnItemSelectedListener newListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                vm.setMedType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vm.setMedType(1);
            }
        };
        AdapterView.OnItemSelectedListener oldListener = ListenerUtil.trackListener(spinner, newListener, R.id.onItemSelectedListener);
        if (oldListener != null)
            spinner.setOnItemSelectedListener(null);
        spinner.setOnItemSelectedListener(newListener);
    }

    @BindingAdapter("textRes")
    public static void setText(View view, Integer res) {
        if (res == 0) return;
        if (view instanceof TextView)
            ((TextView) view).setText(view.getContext().getString(res));
        if (view instanceof ExtendedFloatingActionButton)
            ((ExtendedFloatingActionButton) view).setText(view.getContext().getString(res));
    }
}
