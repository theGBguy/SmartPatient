package com.gbsoft.smartpatient.intermediaries.remote;

import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.gbsoft.smartpatient.data.Appointment;
import com.gbsoft.smartpatient.data.ChatIdentifier;
import com.gbsoft.smartpatient.data.LoginDetail;
import com.gbsoft.smartpatient.data.Medicine;
import com.gbsoft.smartpatient.data.Message;
import com.gbsoft.smartpatient.data.Reminder;
import com.gbsoft.smartpatient.data.User;
import com.gbsoft.smartpatient.intermediaries.remote.utils.AppointmentUtils;
import com.gbsoft.smartpatient.intermediaries.remote.utils.ChatUtils;
import com.gbsoft.smartpatient.intermediaries.remote.utils.MedicineUtils;
import com.gbsoft.smartpatient.intermediaries.remote.utils.UserUtils;
import com.google.firebase.firestore.Query;

import java.util.List;

import javax.inject.Inject;
//        $ keytool -exportcert -alias "key0" -keystore "/home/chiranjeevi/StudioProjects/SmartPatient/smartpatient.jks" -list -v

/**
 * Class that handles authentication w/ login credentials and retrieves user/medicines/reminders/appointments
 * related information.
 */
public class RemoteDataSource {
    private final UserUtils userUtils;
    private final ChatUtils chatUtils;
    private final AppointmentUtils appointmentUtils;
    private final MedicineUtils medicineUtils;

    @Inject
    RemoteDataSource(UserUtils userUtils, ChatUtils chatUtils, AppointmentUtils appointmentUtils, MedicineUtils medicineUtils) {
        this.userUtils = userUtils;
        this.chatUtils = chatUtils;
        this.appointmentUtils = appointmentUtils;
        this.medicineUtils = medicineUtils;
    }

    public LiveData<Result> login(LoginDetail loginDetail) {
        return userUtils.login(loginDetail);
    }

    public LiveData<Result> register(User user) {
        return userUtils.register(user);
    }

    public String getUid() {
        return userUtils.getUid();
    }

    public Query getHealthPersonnelQuery() {
        return userUtils.getHealthPersonnelQuery();
    }

    public LiveData<Query> getPatientQuery() {
        return userUtils.getPatientQuery(appointmentUtils.getPatientIdList());
    }

    public Query getAllPatientsQuery() {
        return userUtils.getAllPatientsQuery();
    }

    public LiveData<Result> setPhotoUrl(Uri localUri, boolean isPatient) {
        return userUtils.setPhotoUrl(localUri, isPatient);
    }

    public LiveData<Result> setAvailability(String availability) {
        return userUtils.setAvailability(availability);
    }

    public LiveData<String> getUserType() {
        return userUtils.getUserType();
    }

    public LiveData<User> getAccountDetails(String id) {
        return userUtils.getAccountDetails(id);
    }

    public boolean isLoggedIn() {
        return userUtils.isLoggedIn();
    }

    public boolean logout() {
        return userUtils.logout();
    }

    public void insertChatId(ChatIdentifier chatIdentifier) {
        chatUtils.insertChatId(chatIdentifier);
    }

    public Query getChatIdQuery(boolean isHp) {
        return chatUtils.getChatIdQuery(isHp);
    }

    public Query getMessagesQuery(ChatIdentifier chatIdentifier) {
        return chatUtils.getMessagesQuery(chatIdentifier);
    }

    public void sendMessage(Message msg, String chatId) {
        chatUtils.sendMessage(msg, chatId);
    }

    public Query getPatientSpecificAppQuery() {
        return appointmentUtils.getPatientSpecificAppQuery();
    }

    public Query getHpSpecificAppQuery() {
        return appointmentUtils.getHpSpecificAppQuery();
    }

    public void insertAppointment(Appointment appointment) {
        appointmentUtils.insertAppointment(appointment);
    }

    public LiveData<Result> setApprovalStatus(long appId, boolean isApproved) {
        return appointmentUtils.setApprovalStatus(appId, isApproved);
    }

    public void insertMedicineAndItsReminders(Medicine medicine, List<Reminder> reminders) {
        medicineUtils.insertMedicineAndItsReminders(medicine, reminders);
    }

    public LiveData<Boolean> deleteMedicineAndItsReminders(Medicine medicine, boolean isHp) {
        return medicineUtils.deleteMedicineAndItsReminders(medicine, isHp);
    }

    public void cancelAllPendingReminders(Medicine medicine) {
        medicineUtils.cancelAllPendingReminders(medicine);
    }

    public Query getHPMedicinesQuery(String queryString) {
        return medicineUtils.getHPMedicinesQuery(queryString);
    }

    public Query getPatientMedicinesQuery(String queryString) {
        return medicineUtils.getPatientMedicinesQuery(queryString);
    }

    public LiveData<List<Reminder>> getRemindersOfAMedicine(long medicineId) {
        return medicineUtils.getRemindersOfAMedicine(medicineId);
    }

    public void schedulePendingReminders(Medicine medicine) {
        medicineUtils.schedulePendingReminders(medicine);
    }

    public void updateAReminder(Reminder reminder) {
        medicineUtils.updateAReminder(reminder);
    }
}