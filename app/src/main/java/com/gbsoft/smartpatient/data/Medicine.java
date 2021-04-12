package com.gbsoft.smartpatient.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.gbsoft.smartpatient.utils.Converter;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

// a pojo class to represent medicine
@Entity(tableName = "meds_table")
public class Medicine implements Parcelable {
    // primary key which is also the time instance when medicine data was entered
    @PrimaryKey
    @ColumnInfo(name = "med_id")
    private long id;

    // name of the medicine
    @ColumnInfo(name = "med_name")
    private String name;

    // type of the medicine; can be a pill, injection or suspension(liquid)
    @ColumnInfo(name = "med_type")
    private String type;

    // the daily intake of medicine; can be 1, 2, 3, etc.
    @ColumnInfo(name = "daily_intake")
    private int dailyIntake;

    // path where the image of medicine is stored
    @ColumnInfo(name = "img_path")
    private String imagePath;

    // expiry date of the medicine
    @TypeConverters({Converter.class})
    @ColumnInfo(name = "expiry_date")
    private LocalDate expiryDate;

    // name of the health personnel who prescribed this medicine
    @ColumnInfo(name = "prescribed_by")
    private String prescribedBy;

    // name of the patient
    @ColumnInfo(name = "prescribed_to")
    private String prescribedTo;

    // extra information that can be stored about this medicine
    @ColumnInfo(name = "med_notes")
    private String medicineNotes;

    @Ignore
    public Medicine() {
    }

    public Medicine(long id, String name, String type, int dailyIntake, String imagePath, LocalDate expiryDate, String prescribedBy, String prescribedTo, String medicineNotes) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.dailyIntake = dailyIntake;
        this.imagePath = imagePath;
        this.expiryDate = expiryDate;
        this.prescribedBy = prescribedBy;
        if (prescribedTo == null)
            this.prescribedTo = "Self";
        else
            this.prescribedTo = prescribedTo;
        this.medicineNotes = medicineNotes;
    }

    protected Medicine(Parcel in) {
        // id name type dailyIntake imagePath expiryDate prescribedBy reminder medicineNotes
        id = in.readLong();
        name = in.readString();
        type = in.readString();
        dailyIntake = in.readInt();
        imagePath = in.readString();
        expiryDate = (LocalDate) in.readSerializable();
        prescribedBy = in.readString();
        medicineNotes = in.readString();
    }

    public static final Creator<Medicine> CREATOR = new Creator<Medicine>() {
        @Override
        public Medicine createFromParcel(Parcel in) {
            return new Medicine(in);
        }

        @Override
        public Medicine[] newArray(int size) {
            return new Medicine[size];
        }
    };

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getMedicineNotes() {
        return medicineNotes;
    }

    public void setMedicineNotes(String medicineNotes) {
        this.medicineNotes = medicineNotes;
    }

    public String getPrescribedBy() {
        return prescribedBy;
    }

    public void setPrescribedBy(String prescribedBy) {
        this.prescribedBy = prescribedBy;
    }

    public String getPrescribedTo() {
        return prescribedTo;
    }

    public void setPrescribedTo(String prescribedTo) {
        this.prescribedTo = prescribedTo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // id name type dailyIntake imagePath expiryDate pescribedBy reminder medicineNotes
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeInt(dailyIntake);
        dest.writeString(imagePath);
        dest.writeSerializable(expiryDate);
        dest.writeString(prescribedBy);
        dest.writeString(medicineNotes);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Medicine)
            return (this.toString().equals(obj.toString()));
        else
            return false;
    }

    @Override
    public @NotNull String toString() {
        return "Medicine{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", dailyIntake=" + dailyIntake +
                ", imagePath='" + imagePath + '\'' +
                ", expiryDate=" + expiryDate +
                ", prescribedBy='" + prescribedBy + '\'' +
                ", medicineNotes='" + medicineNotes + '\'' +
                '}';
    }
}
