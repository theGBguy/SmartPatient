package com.gbsoft.smartpillreminder.ui.reminderslist;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.smartpillreminder.R;
import com.gbsoft.smartpillreminder.databinding.FragmentRemindersListBinding;
import com.gbsoft.smartpillreminder.room.ReminderViewModel;

/**
 * A fragment which shows the list of reminders and its data in the recycler view.
 */
public class RemindersListFragment extends Fragment {
    private static final String POSITION_KEY = "position_key";
    private CustomRecyclerViewAdapter adapter;
    private FragmentRemindersListBinding binding;

    public static RemindersListFragment newInstance(int position) {
        RemindersListFragment fragment = new RemindersListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION_KEY, position);
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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        String[] reminderTypeArray = getResources().getStringArray(R.array.reminder_type_array_res);
        int position = requireArguments().getInt(POSITION_KEY, -1);
        if (position != -1) {
            String reminderType = reminderTypeArray[position];

            ReminderViewModel reminderViewModel = new ViewModelProvider(this).get(ReminderViewModel.class);
            adapter = new CustomRecyclerViewAdapter(this);
            reminderViewModel.getAllRemindersByType(reminderType).observe(getViewLifecycleOwner(), reminders -> adapter.submitList(reminders));
            if (savedInstanceState != null)
                adapter.onRestoreInstanceState(savedInstanceState);
        }

        RecyclerView recyclerView = binding.rvList;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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