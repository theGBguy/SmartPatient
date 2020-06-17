package com.gbsoft.smartpillreminder.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders_table")
public class Reminder implements Parcelable {
    @PrimaryKey
    private long id;

    @ColumnInfo(name = "medicine_name")
    private String medicineName;

    @ColumnInfo(name = "medicine_type")
    private String medicineType;

    @ColumnInfo(name = "reminder_time")
    private String reminderTime;

    @ColumnInfo(name = "daily_intake")
    private int dailyIntake;

    @ColumnInfo(name = "image_path")
    private String imagePath;

    // can be pending, completed or missed
    @ColumnInfo(name = "reminder_type")
    private String reminderType;

    @ColumnInfo(name = "medicine_notes")
    private String medicineNotes;

    public Reminder(Parcel in) {
        id = in.readLong();
        medicineName = in.readString();
        medicineType = in.readString();
        reminderTime = in.readString();
        dailyIntake = in.readInt();
        imagePath = in.readString();
        reminderType = in.readString();
        medicineNotes = in.readString();
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getMedicineType() {
        return medicineType;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public int getDailyIntake() {
        return dailyIntake;
    }

    public void setDailyIntake(int dailyIntake) {
        this.dailyIntake = dailyIntake;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public String getMedicineNotes() {
        return medicineNotes;
    }

    public void setMedicineNotes(String medicineNotes) {
        this.medicineNotes = medicineNotes;
    }

    public Reminder(long id, String medicineName, String medicineType, String reminderTime, int dailyIntake, String imagePath, String reminderType, String medicineNotes) {
        this.id = id;
        this.medicineName = medicineName;
        this.medicineType = medicineType;
        this.reminderTime = reminderTime;
        this.dailyIntake = dailyIntake;
        this.imagePath = imagePath;
        this.reminderType = reminderType;
        this.medicineNotes = medicineNotes;
    }

    @Ignore
    public Reminder(String medicineName, String medicineType, String reminderTime, int dailyIntake, String imagePath, String reminderType, String medicineNotes) {
        this.medicineName = medicineName;
        this.medicineType = medicineType;
        this.reminderTime = reminderTime;
        this.dailyIntake = dailyIntake;
        this.imagePath = imagePath;
        this.reminderType = reminderType;
        this.medicineNotes = medicineNotes;
    }

    @Ignore
    public Reminder(Reminder reminder) {
        this.medicineName = reminder.medicineName;
        this.medicineType = reminder.medicineType;
        this.reminderTime = reminder.reminderTime;
        this.dailyIntake = reminder.dailyIntake;
        this.imagePath = reminder.imagePath;
        this.reminderType = reminder.reminderType;
        this.medicineNotes = reminder.medicineNotes;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Reminder)
            return (this.toString().equals(obj.toString()));
        else
            return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "Reminder{" +
                "id=" + id +
                ", medicineName='" + medicineName + '\'' +
                ", medicineType='" + medicineType + '\'' +
                ", reminderTime='" + reminderTime + '\'' +
                ", dailyIntake=" + dailyIntake +
                ", imagePath='" + imagePath + '\'' +
                ", reminderType='" + reminderType + '\'' +
                ", medicineNotes='" + medicineNotes + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(medicineName);
        parcel.writeString(medicineType);
        parcel.writeString(reminderTime);
        parcel.writeInt(dailyIntake);
        parcel.writeString(imagePath);
        parcel.writeString(reminderType);
        parcel.writeString(medicineNotes);
    }
}
