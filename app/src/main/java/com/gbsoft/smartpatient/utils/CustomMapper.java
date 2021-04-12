package com.gbsoft.smartpatient.utils;

import com.gbsoft.smartpatient.data.Appointment;
import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.data.Reminder;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CustomMapper {
    public static Appointment getAppointmentFromSnapshot(@NotNull DocumentSnapshot snapshot) {
        Appointment appointment = new Appointment();
        appointment.setId(snapshot.getLong("id"));
        appointment.setName(snapshot.getString("name"));
        appointment.setPatientId(snapshot.getString("patientId"));
        appointment.setPatientName(snapshot.getString("patientName"));
        appointment.setHpId(snapshot.getString("hpId"));
        appointment.setHpName(snapshot.getString("hpName"));
        appointment.setScheduledTime(getScheduledTime((HashMap<String, Object>) snapshot.get("scheduledTime")));
        appointment.setApproved(snapshot.getBoolean("approved"));
        return appointment;
    }

    private static LocalDateTime getScheduledTime(@NotNull HashMap<String, Object> timeField) {
        // int year, int month, int dayOfMonth, int hour, int minute
        int year = Integer.parseInt(timeField.get("year") + "");
        int monthValue = Integer.parseInt(timeField.get("monthValue") + "");
        int dayOfMonth = Integer.parseInt(timeField.get("dayOfMonth") + "");
        int hour = Integer.parseInt(timeField.get("hour") + "");
        int minute = Integer.parseInt(timeField.get("minute") + "");

        return LocalDateTime.of(year, monthValue, dayOfMonth, hour, minute);
    }

    public static Reminder getReminderFromSnapshot(@NotNull DocumentSnapshot snapshot) {
        Reminder reminder = new Reminder();
        reminder.setReminderId(snapshot.getLong("reminderId"));
        reminder.setMedicineId(snapshot.getLong("medicineId"));
        reminder.setReminderTime(getScheduledTime((HashMap<String, Object>) snapshot.get("reminderTime")));
        reminder.setReminderType(snapshot.getString("reminderType"));
        reminder.setAssignedBy(snapshot.getString("assignedBy"));
        return reminder;
    }

    public static Medicine getMedicineFromSnapshot(@NotNull DocumentSnapshot snapshot) {
        // id, name, type, dailyIntake, imagePath, expiryDate, prescribedBy, prescribedTo, medicineNotes
        Medicine medicine = new Medicine();
        medicine.setId(snapshot.getLong("id"));
        medicine.setName(snapshot.getString("name"));
        medicine.setType(snapshot.getString("type"));
        medicine.setDailyIntake(Math.toIntExact(snapshot.getLong("dailyIntake")));
        medicine.setImagePath(snapshot.getString("imagePath"));
        medicine.setExpiryDate(TimeHelper.toLocalDate(snapshot.getLong("expiryDate")));
        medicine.setPrescribedBy(snapshot.getString("prescribedBy"));
        medicine.setPrescribedTo(snapshot.getString("prescribedTo"));
        medicine.setMedicineNotes(snapshot.getString("medicineNotes"));
        return medicine;
    }

    public static Map<String, Object> getMapFromMedicine(Medicine medicine) {
        // id, name, type, dailyIntake, imagePath, expiryDate, prescribedBy, prescribedTo, medicineNotes
        Map<String, Object> med = new HashMap<>();
        med.put("id", medicine.getId());
        med.put("name", medicine.getName());
        med.put("type", medicine.getType());
        med.put("dailyIntake", medicine.getDailyIntake());
        med.put("imagePath", medicine.getImagePath());
        med.put("expiryDate", TimeHelper.toTimestamp(medicine.getExpiryDate()));
        med.put("prescribedBy", medicine.getPrescribedBy());
        med.put("prescribedTo", medicine.getPrescribedTo());
        med.put("medicineNotes", medicine.getMedicineNotes());
        return med;
    }
}
