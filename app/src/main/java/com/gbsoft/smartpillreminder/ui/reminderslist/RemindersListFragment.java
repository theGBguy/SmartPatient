package com.gbsoft.smartpillreminder.ui.reminderslist;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.smartpillreminder.databinding.FragmentRemindersListBinding;
import com.gbsoft.smartpillreminder.model.Reminder;
import com.gbsoft.smartpillreminder.room.ReminderViewModel;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

/**
 * A fragment which shows the list of reminders and its data in the recycler view.
 */
public class RemindersListFragment extends Fragment {
    private static final String REMINDER_TYPE_KEY = "reminder_type_key";
    private CustomRecyclerViewAdapter adapter;
    private FragmentRemindersListBinding binding;

    public static RemindersListFragment newInstance(String reminderType) {
        RemindersListFragment fragment = new RemindersListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(REMINDER_TYPE_KEY, reminderType);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (adapter != null)
            adapter.onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentRemindersListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String reminderType = getArguments() != null ? getArguments().getString(REMINDER_TYPE_KEY) : "";
        if (reminderType != null && reminderType.length() > 1) {
            ReminderViewModel reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
            adapter = new CustomRecyclerViewAdapter(this);
            reminderViewModel.getAllRemindersByType(reminderType).observe(getViewLifecycleOwner(), new Observer<List<Reminder>>() {
                @Override
                public void onChanged(final List<Reminder> reminders) {
                    Log.d("list_size", String.valueOf(reminders.size()));
                    adapter.submitList(reminders);
                }
            });
            if (savedInstanceState != null)
                adapter.onRestoreInstanceState(savedInstanceState);
        }

        RecyclerView recyclerView = binding.rvList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.finishActionMode();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adapter.finishActionMode();
        adapter = null;
        binding = null;
    }
}