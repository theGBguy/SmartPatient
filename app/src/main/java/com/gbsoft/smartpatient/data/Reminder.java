package com.gbsoft.smartpatient.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gbsoft.smartpatient.utils.Converter;

import java.time.LocalDateTime;

@Entity(tableName = "rems_table")
// a pojo class to represent reminder
public class Reminder implements Parcelable {

    // primary key to uniquely identify the reminder object
    @PrimaryKey
    @ColumnInfo(name = "reminder_id")
    private long reminderId;

    // reference to the primary key of the owner entity
    @ColumnInfo(name = "medicine_id")
    private long medicineId;

    // the time when reminder alerts the patient in future
    @TypeConverters({Converter.class})
    @ColumnInfo(name = "reminder_time")
    private LocalDateTime reminderTime;

    // can be pending, completed or missed string
    @ColumnInfo(name = "reminder_type")
    private String reminderType;

    // can be user or health personnel
    @ColumnInfo(name = "assigned_by")
    private String assignedBy;

    protected Reminder(Parcel in) {
        reminderId = in.readLong();
        medicineId = in.readLong();
        reminderTime = (LocalDateTime) in.readSerializable();
        reminderType = in.readString();
        assignedBy = in.readString();
    }

    public static final Creator<Reminder> CREATOR = new Creator<Reminder>() {
        @Override
        public Reminder createFromParcel(Parcel in) {
            return new Reminder(in);
        }

        @Override
        public Reminder[] newArray(int size) {
            return new Reminder[size];
        }
    };

    public long getReminderId() {
        return reminderId;
    }

    public void setReminderId(long reminderId) {
        this.reminderId = reminderId;
    }

    public long getMedicineId() {
        return medicineId;
    }

    public void setMedicineId(long medicineId) {
        this.medicineId = medicineId;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public Reminder(long reminderId, long medicineId, LocalDateTime reminderTime, String reminderType, String assignedBy) {
        this.reminderId = reminderId;
        this.medicineId = medicineId;
        this.reminderTime = reminderTime;
        this.reminderType = reminderType;
        this.assignedBy = assignedBy;
    }

    @Ignore
    public Reminder() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(reminderId);
        dest.writeLong(medicineId);
        dest.writeSerializable(reminderTime);
        dest.writeString(reminderType);
        dest.writeString(assignedBy);
    }
}
