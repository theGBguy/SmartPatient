package com.gbsoft.smartpatient.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Embedded;
import androidx.room.Ignore;

public class ReminderWithMedicine implements Parcelable {
    @Embedded
    public Medicine medicine;
    @Embedded
    public Reminder reminder;

    public ReminderWithMedicine() {
    }

    @Ignore
    public ReminderWithMedicine(Medicine medicine, Reminder reminder) {
        this.medicine = medicine;
        this.reminder = reminder;
    }

    protected ReminderWithMedicine(Parcel in) {
        medicine = in.readParcelable(Medicine.class.getClassLoader());
        reminder = in.readParcelable(Reminder.class.getClassLoader());
    }

    public static final Creator<ReminderWithMedicine> CREATOR = new Creator<ReminderWithMedicine>() {
        @Override
        public ReminderWithMedicine createFromParcel(Parcel in) {
            return new ReminderWithMedicine(in);
        }

        @Override
        public ReminderWithMedicine[] newArray(int size) {
            return new ReminderWithMedicine[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(medicine, flags);
        dest.writeParcelable(reminder, flags);
    }
}
