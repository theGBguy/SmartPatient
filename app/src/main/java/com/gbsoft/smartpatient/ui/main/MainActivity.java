package com.gbsoft.smartpatient.ui.main;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.utils.DialogUtils;
import com.gbsoft.smartpatient.utils.EventObserver;
import com.gbsoft.smartpatient.utils.SnackUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private com.gbsoft.smartpatient.databinding.ActivityMainBinding binding;
    private NavController navController;
    private AppBarConfiguration appBarConfig;
    private MainViewModel viewModel;
    private final NavController.OnDestinationChangedListener listener = new NavController.OnDestinationChangedListener() {
        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            if (destination.getId() == R.id.nav_settings
                    || destination.getId() == R.id.nav_add_update_medicine
                    || destination.getId() == R.id.nav_login
                    || destination.getId() == R.id.nav_register
                    || destination.getId() == R.id.nav_offline_home) {
                binding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                if (destination.getId() == R.id.nav_login)
                    viewModel.logout();
            } else {
                binding.mainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        if (savedInstanceState == null) {
            viewModel.init(true);

            viewModel.getPendingMedicinesSize().observe(this, size -> {
                ComponentName bootCompletedReceiver = new ComponentName(MainActivity.this, BootCompletedReceiver.class);
                if (size > 0) {
                    getPackageManager().setComponentEnabledSetting(bootCompletedReceiver,
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                            PackageManager.DONT_KILL_APP);
                } else {
                    getPackageManager().setComponentEnabledSetting(bootCompletedReceiver,
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                            PackageManager.DONT_KILL_APP);
                }
            });
        }
        setupContentView();

        viewModel.getSnackMsg().observe(this, new EventObserver<>(msg -> {
            if (msg == null || msg == 0)
                return;
            SnackUtils.showMessage(binding.getRoot(), msg);
        }));
    }

    private void setupContentView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (host == null) return;
        navController = host.getNavController();
        NavigationUI.setupWithNavController(binding.navigationView, navController);
        navController.addOnDestinationChangedListener(listener);
    }

    public AppBarConfiguration getAppBarConfig() {
        if (appBarConfig == null) {
            appBarConfig = new AppBarConfiguration.Builder(R.id.nav_online_home,
                    R.id.nav_appointment_list,
                    R.id.nav_chat_list,
                    R.id.nav_profile)
                    .setOpenableLayout(binding.mainDrawerLayout)
                    .build();
        }
        return appBarConfig;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        navController.removeOnDestinationChangedListener(listener);
    }

    @Override
    public void onBackPressed() {
        if (binding.mainDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.mainDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (navController.getCurrentDestination().getId() == R.id.nav_login)
                finish();
            else if (navController.getCurrentDestination().getId() == R.id.nav_online_home)
                DialogUtils.showLogoutDialog(this, navController);
            else
                super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigateUp() {
        return NavigationUI.navigateUp(navController, getAppBarConfig()) || super.onSupportNavigateUp();
    }

}