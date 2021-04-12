package com.gbsoft.smartpatient.ui.main.medicinelist;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.gbsoft.smartpatient.data.ReminderWithMedicine;

public class CustomListAdapter extends ListAdapter<ReminderWithMedicine, CustomViewHolder> {

    private SelectionTracker<Long> tracker;
    private static final DiffUtil.ItemCallback<ReminderWithMedicine> callback = new DiffUtil.ItemCallback<ReminderWithMedicine>() {
        @Override
        public boolean areItemsTheSame(@NonNull ReminderWithMedicine oldItem, @NonNull ReminderWithMedicine newItem) {
            return oldItem.reminder.getReminderId() == newItem.reminder.getReminderId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ReminderWithMedicine oldItem, @NonNull ReminderWithMedicine newItem) {
            return oldItem.reminder.equals(newItem.reminder) && oldItem.medicine.equals(newItem.medicine);
        }
    };

    CustomListAdapter() {
        super(callback);
        setHasStableIds(true);
    }

    void setTracker(SelectionTracker<Long> tracker) {
        this.tracker = tracker;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return CustomViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder holder, int position) {
        holder.bindModel(getItem(position), tracker.isSelected(((long) position)));
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull CustomViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.unbind();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
