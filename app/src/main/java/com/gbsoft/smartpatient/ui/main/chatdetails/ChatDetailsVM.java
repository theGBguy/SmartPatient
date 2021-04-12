package com.gbsoft.smartpatient.ui.main.chatdetails;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.gbsoft.smartpatient.data.ChatIdentifier;
import com.gbsoft.smartpatient.data.Message;
import com.gbsoft.smartpatient.data.Patient;
import com.gbsoft.smartpatient.data.User;
import com.gbsoft.smartpatient.intermediaries.remote.RemoteRepo;
import com.google.firebase.firestore.Query;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatDetailsVM extends ViewModel {
    private final MutableLiveData<ChatIdentifier> chatIdentifier = new MutableLiveData<>();
    public final MutableLiveData<String> msg = new MutableLiveData<>();

    private String senderInfo;
    private String receiverInfo;
    private final RemoteRepo remoteRepo;

    @Inject
    public ChatDetailsVM(RemoteRepo remoteRepo) {
        this.remoteRepo = remoteRepo;
    }

    public void init(Bundle args, User currentUser) {
        if (args == null) return;
        senderInfo = currentUser.getId() + "_" + currentUser.getName();
        receiverInfo = args.getString(ChatDetailsFrag.KEY_RECEIVER_INFO);
        String[] receiver = receiverInfo.split("_");
        if (currentUser instanceof Patient) {
            String chatId = currentUser.getId() + "_" + receiver[0];
            this.chatIdentifier.setValue(new ChatIdentifier(chatId, currentUser.getId(), currentUser.getName(), receiver[0], receiver[1], "No any messages"));
        } else {
            String chatId = receiver[0] + "_" + currentUser.getId();
            this.chatIdentifier.setValue(new ChatIdentifier(chatId, receiver[0], receiver[1], currentUser.getId(), currentUser.getName(), "No any messages"));
        }
    }

    public LiveData<String> toolbarTitle = Transformations.map(chatIdentifier, input -> {
        if (input == null) return "";
        return receiverInfo.split("_")[1];
    });

    public Query getMessagesQuery() {
        return remoteRepo.getMessagesQuery(chatIdentifier.getValue());
    }

    public void insertChatId(ChatIdentifier chatIdentifier) {
        remoteRepo.insertChatId(chatIdentifier);
    }

    public void onFabSendClick() {
        Message message = new Message(System.currentTimeMillis(), msg.getValue(), senderInfo, receiverInfo);
        remoteRepo.sendMessage(message, chatIdentifier.getValue().getChatId());
        msg.setValue("");
    }

}
