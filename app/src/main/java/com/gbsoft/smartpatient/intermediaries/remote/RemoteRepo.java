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
import com.google.firebase.firestore.Query;

import java.util.List;

import javax.inject.Inject;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

public class RemoteRepo {
    private final RemoteDataSource remoteDataSource;

    @Inject
    public RemoteRepo(RemoteDataSource remoteDataSource) {
        this.remoteDataSource = remoteDataSource;
    }

    public boolean logout() {
        return remoteDataSource.logout();
    }

    public LiveData<Result> login(LoginDetail loginDetail) {
        return remoteDataSource.login(loginDetail);
    }

    public LiveData<Result> register(User user) {
        // handle registration
        return remoteDataSource.register(user);
    }

    public String getUid() {
        return remoteDataSource.getUid();
    }

    public LiveData<String> getUserType() {
        return remoteDataSource.getUserType();
    }

    public LiveData<Result> setPhotoUrl(Uri localUri, boolean isPatient) {
        return remoteDataSource.setPhotoUrl(localUri, isPatient);
    }

    public LiveData<Result> setAvailability(String availability) {
        return remoteDataSource.setAvailability(availability);
    }

    public Query getMessagesQuery(ChatIdentifier chatIdentifier) {
        return remoteDataSource.getMessagesQuery(chatIdentifier);
    }

    public void insertChatId(ChatIdentifier chatIdentifier) {
        remoteDataSource.insertChatId(chatIdentifier);
    }

    public Query getChatIdQuery(boolean isHp) {
        return remoteDataSource.getChatIdQuery(isHp);
    }

    public Query getHealthPersonnelQuery() {
        return remoteDataSource.getHealthPersonnelQuery();
    }

    public LiveData<Query> getPatientQuery() {
        return remoteDataSource.getPatientQuery();
    }

    public Query getAllPatientsQuery() {
        return remoteDataSource.getAllPatientsQuery();
    }

    public void sendMessage(Message msg, String chatId) {
        remoteDataSource.sendMessage(msg, chatId);
    }

    public void insertAppointment(Appointment appointment) {
        remoteDataSource.insertAppointment(appointment);
    }

    public LiveData<Result> setApprovalStatus(long appId, boolean isApproved) {
        return remoteDataSource.setApprovalStatus(appId, isApproved);
    }

    public LiveData<User> getAccountDetails(String uid) {
        return remoteDataSource.getAccountDetails(uid);
    }

    public Query getPatientSpecificAppQuery() {
        return remoteDataSource.getPatientSpecificAppQuery();
    }

    public Query getHpSpecificAppQuery() {
        return remoteDataSource.getHpSpecificAppQuery();
    }

    public void insertMedicineAndItsReminders(Medicine medicine, List<Reminder> reminders) {
        remoteDataSource.insertMedicineAndItsReminders(medicine, reminders);
    }

    public LiveData<Boolean> deleteMedicineAndItsReminders(Medicine medicine, boolean isHp) {
        return remoteDataSource.deleteMedicineAndItsReminders(medicine, isHp);
    }

    public void cancelAllPendingReminders(Medicine medicine) {
        remoteDataSource.cancelAllPendingReminders(medicine);
    }

    public Query getHPMedicinesQuery(String queryString) {
        return remoteDataSource.getHPMedicinesQuery(queryString);
    }

    public Query getPatientMedicinesQuery(String queryString) {
        return remoteDataSource.getPatientMedicinesQuery(queryString);
    }

    public LiveData<List<Reminder>> getRemindersOfAMedicine(long medicineId) {
        return remoteDataSource.getRemindersOfAMedicine(medicineId);
    }

    public void schedulePendingReminders(Medicine medicine) {
        remoteDataSource.schedulePendingReminders(medicine);
    }

    public void updateAReminder(Reminder reminder) {
        remoteDataSource.updateAReminder(reminder);
    }

    public boolean isLoggedIn() {
        return remoteDataSource.isLoggedIn();
    }
}