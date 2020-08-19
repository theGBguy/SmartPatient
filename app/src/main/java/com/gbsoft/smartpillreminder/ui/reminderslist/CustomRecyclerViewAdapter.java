package com.gbsoft.smartpillreminder.ui.reminderslist;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.gbsoft.smartpillreminder.R;
import com.gbsoft.smartpillreminder.databinding.ReminderRowBinding;
import com.gbsoft.smartpillreminder.model.Reminder;
import com.gbsoft.smartpillreminder.room.ReminderViewModel;
import com.gbsoft.smartpillreminder.ui.addorupdate.AddOrUpdateReminderFragment;
import com.gbsoft.smartpillreminder.utils.Helper;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class CustomRecyclerViewAdapter extends ChoiceCapableAdapter<CustomRecyclerViewAdapter.CustomViewHolder> {

    private WeakReference<RemindersListFragment> context;
    private ActionMode actionMode;
    private AsyncListDiffer<Reminder> differ = new AsyncListDiffer<>(this, callback);
    private static DiffUtil.ItemCallback<Reminder> callback = new DiffUtil.ItemCallback<Reminder>() {
        @Override
        public boolean areItemsTheSame(@NonNull Reminder oldItem, @NonNull Reminder newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Reminder oldItem, @NonNull Reminder newItem) {
            return oldItem.equals(newItem);
        }
    };

    CustomRecyclerViewAdapter(RemindersListFragment context) {
        super(new MultiChoiceMode());
        this.context = new WeakReference<>(context);
    }

    void submitList(List<Reminder> reminders) {
        differ.submitList(reminders);
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomViewHolder(LayoutInflater.from(context.get().requireContext()).inflate(R.layout.reminder_row, parent, false), this);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
        holder.bindModel(differ.getCurrentList().get(position));
    }

    @Override
    public void setSelected(int position, boolean isSelected) {
        super.setSelected(position, isSelected);
        if (getSelectedCount() == 0 && actionMode != null)
            actionMode.finish();
        else {
            if (isSelected) {
                if (actionMode == null)
                    actionMode = context.get().requireActivity().startActionMode(new MyActionModeCallback(this));
            }
            Objects.requireNonNull(actionMode).invalidate();
        }
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    private static class MyActionModeCallback implements ActionMode.Callback {
        private WeakReference<CustomRecyclerViewAdapter> customRecyclerViewAdapterWeakReference;

        MyActionModeCallback(CustomRecyclerViewAdapter adapter) {
            this.customRecyclerViewAdapterWeakReference = new WeakReference<>(adapter);
        }


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action, menu);
            customRecyclerViewAdapterWeakReference.get().actionMode = mode;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            CustomRecyclerViewAdapter adapter = customRecyclerViewAdapterWeakReference.get();
            adapter.actionMode.setTitle(
                    (adapter.getSelectedCount() == 1) ? adapter.context.get().getString(R.string.menu_action_title, 1, "")
                            : adapter.context.get().getString(R.string.menu_action_title, adapter.getSelectedCount(), "s"));
            MenuItem edit = menu.findItem(R.id.menu_action_edit);
            if (adapter.getSelectedCount() == 1)
                edit.setVisible(true);
            else
                edit.setVisible(false);
            return true;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            CustomRecyclerViewAdapter adapter = customRecyclerViewAdapterWeakReference.get();
            List<Reminder> currentList = adapter.differ.getCurrentList();
            int id = item.getItemId();
            switch (id) {
                case R.id.menu_action_edit:
                    final int[] recViewPos = new int[1];
                    adapter.visitChecks(position -> recViewPos[0] = position);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(AddOrUpdateReminderFragment.KEY_REMINDER_TO_BE_UPDATED, currentList.get(recViewPos[0]));
                    Navigation.findNavController(adapter.context.get().requireView())
                            .navigate(R.id.action_navigation_home_to_addOrUpdateReminderFragment, bundle, null, null);
                    adapter.clearSelections();
                    adapter.actionMode.finish();
                    break;
                case R.id.menu_action_delete:
                    final List<Integer> positionList = new ArrayList<>();
                    adapter.visitChecks(positionList::add);
                    Collections.sort(positionList, Collections.reverseOrder());
                    ReminderViewModel reminderViewModel = new ViewModelProvider(adapter.context.get()).get(ReminderViewModel.class);
                    Helper.ReminderHelper reminderHelper = new Helper.ReminderHelper(adapter.context.get().requireContext());
                    for (Integer position : positionList) {
                        Reminder reminder = currentList.get(position);
                        reminderViewModel.deleteAReminder(reminder);
                        reminderHelper.cancelReminder(reminder);
                    }
                    adapter.clearSelections();
                    adapter.actionMode.finish();
                    String msg = positionList.size() == 1 ? "A reminder has been cancelled!" : "Selected reminders have been cancelled!";
                    Snackbar.make(adapter.context.get().requireView(), msg, Snackbar.LENGTH_LONG).show();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            final CustomRecyclerViewAdapter adapter = customRecyclerViewAdapterWeakReference.get();
            if (adapter.actionMode != null) {
                adapter.visitChecks(position -> {
                    adapter.setSelected(position, false);
                    adapter.notifyItemChanged(position);
                });
                adapter.actionMode = null;
            }
        }
    }

    void finishActionMode() {
        if (actionMode != null)
            actionMode.finish();
    }

    static class CustomViewHolder extends RecyclerView.ViewHolder {
        private MaterialCardView reminderRowCard;
        private TextView tvMedName, tvTimeToRemind, tvRemaining, tvBar;
        private ProgressBar pgbarImgLoading;
        private ShapeableImageView ivMedicine;
        private CustomRecyclerViewAdapter adapter;

        CustomViewHolder(@NonNull final View itemView, final CustomRecyclerViewAdapter adapter) {
            super(itemView);
            ReminderRowBinding binding = ReminderRowBinding.bind(itemView);
            this.adapter = adapter;
            tvMedName = binding.rrTvMedName;
            tvTimeToRemind = binding.rrTvTimeToRemind;
            tvRemaining = binding.rrTvRemaining;
            tvBar = binding.rrTvBar;
            ivMedicine = binding.rrIvMed;
            pgbarImgLoading = binding.rrPgbarImgLoading;

            reminderRowCard = binding.reminderRowCard;

            reminderRowCard.setOnLongClickListener(v -> {
                boolean isSelected = adapter.isSelected(getAdapterPosition());
                if (!isSelected) {
                    adapter.setSelected(getAdapterPosition(), true);
                    ((MaterialCardView) v).setChecked(true);
                    ivMedicine.setAlpha(0.5f);
                }
                return true;
            });

            reminderRowCard.setOnClickListener(v -> {
                Timber.d(String.valueOf(adapter.getSelectedCount()));
                if (adapter.getSelectedCount() > 0) {
                    boolean isSelected = adapter.isSelected(getAdapterPosition());
                    adapter.setSelected(getAdapterPosition(), !isSelected);
                    ((MaterialCardView) v).setChecked(!isSelected);
                    ivMedicine.setAlpha(!isSelected ? 0.5f : 1f);
                }
            });
        }

        void bindModel(final Reminder reminder) {
            tvMedName.setText(adapter.context.get().getString(R.string.tvMedName_placeholder_text, reminder.getMedicineName(), reminder.getDailyIntake()));
            if (!reminder.getImagePath().isEmpty()) {
                new Handler().postDelayed(new PhotoLoader(reminder.getImagePath(),
                        ivMedicine, pgbarImgLoading), 2000);
            }
            reminderRowCard.setSelected(adapter.isSelected(getAdapterPosition()));
            tvTimeToRemind.setText(new Helper.TimeHelper().formatTime(reminder.getReminderTime()));
            if ((reminder.getReminderType().equals("Pending"))) {
                tvRemaining.setText(new Helper.TimeHelper().calculateTimeDiff(reminder.getReminderTime()));
                int secNow = Calendar.getInstance().get(Calendar.SECOND);
                new Handler().postDelayed(new RemainingTextUpdater(reminder.getReminderTime(),
                        tvRemaining), (60 - (long) secNow) * 1000);
            } else {
                tvBar.setVisibility(View.GONE);
                tvRemaining.setVisibility(View.GONE);
            }
        }

        static class PhotoLoader implements Runnable {
            private String imgPath;
            private WeakReference<ImageView> imageViewWeakReference;
            private WeakReference<ProgressBar> progressBarWeakReference;

            PhotoLoader(String imgPath, ImageView ivMedicine, ProgressBar pgbarImgLoading) {
                this.imgPath = imgPath;
                this.imageViewWeakReference = new WeakReference<>(ivMedicine);
                this.progressBarWeakReference = new WeakReference<>(pgbarImgLoading);
            }

            @Override
            public void run() {
                ImageView ivMedicine = imageViewWeakReference.get();
                if (ivMedicine != null) {
                    BitmapFactory.Options options = new Helper.ImageHelper().getSuitableOptions(imgPath
                            , ivMedicine.getWidth(), ivMedicine.getHeight());
                    ivMedicine.setImageBitmap(BitmapFactory.decodeFile(imgPath, options));
                    progressBarWeakReference.get().setVisibility(View.GONE);
                }
            }
        }

        static class RemainingTextUpdater implements Runnable {
            private String reminderTime;
            private WeakReference<TextView> textViewWeakReference;

            RemainingTextUpdater(String reminderTime, TextView tvRemaining) {
                this.reminderTime = reminderTime;
                this.textViewWeakReference = new WeakReference<>(tvRemaining);
            }

            @Override
            public void run() {
                TextView tvRemainingText = textViewWeakReference.get();
                if (tvRemainingText != null)
                    tvRemainingText.setText(new Helper.TimeHelper().calculateTimeDiff(reminderTime));
                new Handler().postDelayed(this, 60 * 1000);
            }
        }
    }
}
