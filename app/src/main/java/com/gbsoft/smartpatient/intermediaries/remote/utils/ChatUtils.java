package com.gbsoft.smartpatient.intermediaries.remote.utils;

import com.gbsoft.smartpatient.data.ChatIdentifier;
import com.gbsoft.smartpatient.data.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import timber.log.Timber;

public class ChatUtils {
    private static final String TAG = "ChatUtils";

    private static final String CHATS_COL_REF = "chats";
    private static final String MESSAGES_COL_REF = "messages";

    private static final String CHAT_IDS_COL_REF = "chat_ids";
    private static final String CHAT_ID_COL_REF = "chat_id";

    private static final String CHAT_ID_FIELD = "chatId";
    private static final String MESSAGE_ID_FIELD = "msgId";

    private final FirebaseFirestore firestore;
    private final FirebaseAuth firebaseAuth;

    @Inject
    public ChatUtils(FirebaseFirestore firestore, FirebaseAuth firebaseAuth) {
        this.firestore = firestore;
        this.firebaseAuth = firebaseAuth;
    }

    public void insertChatId(ChatIdentifier chatIdentifier) {
        firestore.collection(CHAT_IDS_COL_REF)
                .document(chatIdentifier.getChatId())
                .set(chatIdentifier)
                .addOnSuccessListener(task -> Timber.d("Successfully created the chat id"))
                .addOnFailureListener(e -> Timber.d("Couldn't create the chat id : %s", e.getLocalizedMessage()));

    }

    public Query getChatIdQuery(boolean isHp) {
        return firestore.collection(CHAT_IDS_COL_REF)
                .orderBy(CHAT_ID_FIELD)
                .whereEqualTo(isHp ? "hpUid" : "pUid", getUid());
    }

    public void sendMessage(Message msg, String chatId) {
        Task<DocumentReference> insertMsg = firestore.collection(CHATS_COL_REF)
                .document(String.valueOf(chatId))
                .collection(MESSAGES_COL_REF)
                .add(msg);

        Task<Void> editLastMsg = firestore.collection(CHAT_IDS_COL_REF)
                .document(chatId).update("lastMsg", msg.getContent());

        insertMsg.continueWithTask(task -> editLastMsg)
                .addOnSuccessListener(docRef -> Timber.tag(TAG).d("Successfully sent the msg"))
                .addOnFailureListener(e -> Timber.tag(TAG).d("Sending msg failed : %s", e.getLocalizedMessage()));
    }

    public Query getMessagesQuery(ChatIdentifier chatIdentifier) {
        firestore.collection(CHAT_IDS_COL_REF)
                .document(chatIdentifier.getChatId())
                .get()
                .addOnSuccessListener(task -> {
                    if (task.getData() == null)
                        insertChatId(chatIdentifier);
                })
                .addOnFailureListener(e -> Timber.d("Error occurred : %s", e.getLocalizedMessage()));
        return firestore.collection(CHATS_COL_REF)
                .document(chatIdentifier.getChatId())
                .collection(MESSAGES_COL_REF)
                .orderBy(MESSAGE_ID_FIELD);
    }

    private @NotNull String getUid() {
        if (firebaseAuth == null) return "";
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) return "";
        return user.getUid();
    }
}
