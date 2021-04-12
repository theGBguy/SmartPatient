package com.gbsoft.smartpatient.ui.main.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.databinding.FragmentRegisterBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.ref.WeakReference;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterFrag extends Fragment {
    private FragmentRegisterBinding binding;
    private TabLayoutMediator mediator;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.vpRegister.setAdapter(new RegisterAdapter(getChildFragmentManager(), getViewLifecycleOwner().getLifecycle()));
        String[] users = getResources().getStringArray(R.array.users);
        WeakReference<TabLayout> weakTab = new WeakReference<>(binding.registerTabs);
        WeakReference<ViewPager2> weakViewPager = new WeakReference<>(binding.vpRegister);
        mediator = new TabLayoutMediator(weakTab.get(), weakViewPager.get(), (tab, position) -> tab.setText(users[position]));
        mediator.attach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mediator.detach();
        mediator = null;
        binding = null;
    }
}