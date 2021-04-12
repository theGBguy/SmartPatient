package com.gbsoft.smartpatient.ui.main.dialogs;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.archit.calendardaterangepicker.customviews.CalendarListener;
import com.gbsoft.smartpatient.databinding.FragmentReminderDateTimePickerBinding;
import com.gbsoft.smartpatient.utils.TimeHelper;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReminderDateTimePickerFragment extends BottomSheetDialogFragment {
    private FragmentReminderDateTimePickerBinding binding;

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReminderDateTimePickerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.set(Calendar.YEAR, start.get(Calendar.YEAR) + 1);
        binding.dateRangePicker.setSelectedDateRange(start, end);

        List<LocalDate> selectedDates = new ArrayList<>();
        binding.dateRangePicker.setCalendarListener(new CalendarListener() {
            @Override
            public void onFirstDateSelected(@NotNull Calendar calendar) {
                selectedDates.clear();
                selectedDates.add(LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).toLocalDate());
            }

            @Override
            public void onDateRangeSelected(@NotNull Calendar calendar, @NotNull Calendar calendar1) {
                selectedDates.clear();
                while (!calendar.after(calendar1)) {
                    selectedDates.add(LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()).toLocalDate());
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }
            }
        });

        List<LocalTime> selectedTimes = new ArrayList<>();
        binding.chipAdd.setOnClickListener(v -> {
            Chip newChip = new Chip(requireContext());
            LocalTime current = LocalTime.now();
            selectedTimes.add(current);
            newChip.setText(TimeHelper.formatLocalTime(current));
            newChip.setTag(current);
            newChip.setCloseIconVisible(true);
            newChip.setOnCloseIconClickListener(v1 -> {
                binding.chipGroupTime.removeView(newChip);
                selectedTimes.remove(newChip.getTag());
            });

            newChip.setOnClickListener(v12 -> {
                selectedTimes.remove(newChip.getTag());
                final TimePickerDialog dialog1 = new TimePickerDialog(requireContext(), (picker, hourOfDay, minute) -> {
                    LocalTime selected = LocalTime.of(hourOfDay, minute);
                    newChip.setText(TimeHelper.formatLocalTime(selected));
                    newChip.setTag(selected);
                    selectedTimes.add(selected);
                }, LocalTime.now().getHour(), LocalTime.now().getMinute(), false);
                dialog1.show();
            });

            binding.chipGroupTime.addView(newChip);
        });

        binding.btnAlarmPickerOk.setOnClickListener(v -> {
                    dismiss();
//                    Bundle args = new Bundle();
//                    args.putSerializable(AddUpdateMedicineFrag.KEY_SELECTED_DAYS, (Serializable) selectedDates);
//                    args.putSerializable(AddUpdateMedicineFrag.KEY_SELECTED_TIMES, (Serializable) selectedTimes);
//                    NavHostFragment.findNavController(this).navigate(R.id.nav_add_update_medicine, args);
                }
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}