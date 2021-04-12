package com.gbsoft.smartpatient.ui.main.home.online;

import android.os.Bundle;
import android.view.View;

import androidx.navigation.Navigation;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.ui.main.medicinedetails.MedicineDetailFrag;

import java.util.Locale;

public class MedicineItemVm {
    private final Medicine medicine;
    private final boolean isHp;

    public MedicineItemVm(Medicine medicine, boolean isHp) {
        this.medicine = medicine;
        this.isHp = isHp;
    }

    public Medicine getMedicine() {
        return medicine;
    }

    public String getPrescribedBy() {
        return String.format(Locale.getDefault(), "Prescribed by : %s", medicine.getPrescribedBy().split("_")[1]);
    }

    public String getPrescribedTo() {
        return String.format(Locale.getDefault(), "Prescribed to : %s", medicine.getPrescribedTo().split("_")[1]);
    }

    public String getMedicineImagePath() {
        return medicine.getImagePath();
    }

    public String getMedNameText() {
        return String.format(Locale.getDefault(),
                "%s (%d times a day)", medicine.getName(), medicine.getDailyIntake());
    }

    public boolean isHp() {
        return isHp;
    }

    public void onCardClick(View card) {
        Bundle args = new Bundle();
        args.putBoolean(MedicineDetailFrag.KEY_IS_HP, isHp);
        args.putParcelable(MedicineDetailFrag.KEY_MEDICINE, medicine);
        Navigation.findNavController(card).navigate(R.id.nav_medicine_details, args, null, null);
    }
}
