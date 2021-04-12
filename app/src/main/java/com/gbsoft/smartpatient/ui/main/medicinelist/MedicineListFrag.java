package com.gbsoft.smartpatient.ui.main.medicinelist;

import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.selection.SelectionPredicates;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.selection.StorageStrategy;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.ReminderWithMedicine;
import com.gbsoft.smartpatient.databinding.FragmentMedicineListBinding;
import com.gbsoft.smartpatient.ui.main.addupdatemed.AddUpdateMedicineFrag;
import com.gbsoft.smartpatient.utils.SnackUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A fragment which shows the list of reminders and its data in the recycler view.
 */
@AndroidEntryPoint
public class MedicineListFrag extends Fragment {
    public static final String POSITION_KEY = "position_key";

    private CustomListAdapter adapter;
    private FragmentMedicineListBinding binding;
    private SelectionTracker<Long> tracker;
    private MedicineListViewModel viewModel;
    private ActionMode actionMode;
    private ActionMode.Callback callback;

    public static MedicineListFrag newInstance(int position) {
        MedicineListFrag fragment = new MedicineListFrag();
        Bundle args = new Bundle();
        args.putInt(POSITION_KEY, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (getTracker() != null)
            getTracker().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentMedicineListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(MedicineListViewModel.class);
        viewModel.setReminderType(getArguments());

        adapter = new CustomListAdapter();
        binding.rvList.setHasFixedSize(true);
        binding.rvList.setAdapter(adapter);
        adapter.setTracker(getTracker());

        viewModel.getAllMedicinesByReminderType().observe(getViewLifecycleOwner(), medicines -> adapter.submitList(medicines));

        if (savedInstanceState != null)
            getTracker().onRestoreInstanceState(savedInstanceState);

    }

    private SelectionTracker<Long> getTracker() {
        if (tracker == null && binding != null) {
            tracker = new SelectionTracker.Builder<>(
                    "medicines_list",
                    binding.rvList,
                    new CustomItemKeyProvider(binding.rvList),
                    new CustomLookup(binding.rvList),
                    StorageStrategy.createLongStorage()
            ).withSelectionPredicate(SelectionPredicates.createSelectAnything())
                    .build();
            tracker.addObserver(new SelectionTracker.SelectionObserver<Long>() {
                @Override
                public void onSelectionChanged() {
                    int size = tracker.getSelection().size();
                    if (size > 0) {
                        if (actionMode == null)
                            actionMode = requireActivity().startActionMode(getCallback());
                        else
                            actionMode.invalidate();
                    } else
                        actionMode = null;
                }
            });
        }
        return tracker;
    }

    private ActionMode.Callback getCallback() {
        if (callback == null) {
            callback = new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    mode.getMenuInflater().inflate(R.menu.menu_action, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    int size = getTracker().getSelection().size();
                    mode.setTitle(getResources().getQuantityString(R.plurals.menu_action_title,
                            size, size));
                    menu.findItem(R.id.menu_action_edit).setVisible(size == 1);
                    return true;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    List<ReminderWithMedicine> currentList = adapter.getCurrentList();
                    int itemId = item.getItemId();
                    if (itemId == R.id.menu_action_edit) {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(AddUpdateMedicineFrag.KEY_MEDICINE_ID_TO_BE_UPDATED, currentList.get(Math.toIntExact(
                                getTracker().getSelection().iterator().next())));
                        Navigation.findNavController(requireView())
                                .navigate(R.id.action_home_to_add_update_medicine, bundle, null, null);
                    } else if (itemId == R.id.menu_action_delete) {
                        List<Long> keyList = new ArrayList<>();
                        for (Long key : getTracker().getSelection()) {
                            keyList.add(key);
                        }
                        Collections.sort(keyList, Comparator.reverseOrder());
                        for (Long key : keyList) {
                            viewModel.deleteAReminder(currentList.get(Math.toIntExact(key)));
                        }
                        SnackUtils.showMessage(requireView(), R.plurals.med_entry_deletion_text, getTracker().getSelection().size());

                    }
                    mode.finish();
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    getTracker().clearSelection();
                }
            };
        }
        return callback;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        actionMode = null;
        callback = null;
        adapter = null;
        binding = null;
        tracker = null;
        viewModel = null;
    }
}