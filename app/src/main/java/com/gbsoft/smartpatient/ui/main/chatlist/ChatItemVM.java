package com.gbsoft.smartpatient.ui.main.chatlist;

import android.os.Bundle;
import android.view.View;

import androidx.navigation.Navigation;

import com.gbsoft.smartpatient.R;
import com.gbsoft.smartpatient.ui.main.chatdetails.ChatDetailsFrag;

public class ChatItemVM {
    private final String receiverInfo;
    private final String lastMsg;

    public ChatItemVM(String receiverInfo, String lastMsg) {
        this.receiverInfo = receiverInfo;
        this.lastMsg = lastMsg;
    }

    public String getPersonName() {
        return "Person name : " + receiverInfo.split("_")[1];
    }

    public String getLastMsg() {
        return "Last msg : " + lastMsg;
    }

    public void onCardClick(View v) {
        Bundle args = new Bundle();
        args.putString(ChatDetailsFrag.KEY_RECEIVER_INFO, receiverInfo);
        Navigation.findNavController(v).navigate(R.id.nav_chat_details, args, null, null);
    }
}
