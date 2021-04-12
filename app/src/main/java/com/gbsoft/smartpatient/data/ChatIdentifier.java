package com.gbsoft.smartpatient.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ChatIdentifier implements Parcelable {
    private String chatId;
    private String pUid;
    private String pName;
    private String hpUid;
    private String hpName;
    private String lastMsg;

    public ChatIdentifier() {
    }

    public ChatIdentifier(String chatId, String pUid, String pName, String hpUid, String hpName, String lastMsg) {
        this.chatId = chatId;
        this.pUid = pUid;
        this.pName = pName;
        this.hpName = hpName;
        this.hpUid = hpUid;
        this.lastMsg = lastMsg;
    }

    protected ChatIdentifier(Parcel in) {
        chatId = in.readString();
        pUid = in.readString();
        pName = in.readString();
        hpUid = in.readString();
        hpName = in.readString();
        lastMsg = in.readString();
    }

    public static final Creator<ChatIdentifier> CREATOR = new Creator<ChatIdentifier>() {
        @Override
        public ChatIdentifier createFromParcel(Parcel in) {
            return new ChatIdentifier(in);
        }

        @Override
        public ChatIdentifier[] newArray(int size) {
            return new ChatIdentifier[size];
        }
    };

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getpUid() {
        return pUid;
    }

    public void setpUid(String pUid) {
        this.pUid = pUid;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getHpUid() {
        return hpUid;
    }

    public void setHpUid(String hpUid) {
        this.hpUid = hpUid;
    }

    public String getHpName() {
        return hpName;
    }

    public void setHpName(String hpName) {
        this.hpName = hpName;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chatId);
        dest.writeString(pUid);
        dest.writeString(pName);
        dest.writeString(hpUid);
        dest.writeString(hpName);
        dest.writeString(lastMsg);
    }

    @Override
    public @NotNull String toString() {
        return "ChatIdentifier{" +
                "chatId=" + chatId +
                ", pUid='" + pUid + '\'' +
                ", pName='" + pName + '\'' +
                ", hpUid='" + hpUid + '\'' +
                ", hpName='" + hpName + '\'' +
                ", lastMsg='" + lastMsg + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatIdentifier that = (ChatIdentifier) o;
        return chatId.equals(that.chatId) &&
                pUid.equals(that.pUid) &&
                pName.equals(that.pName) &&
                hpUid.equals(that.hpUid) &&
                hpName.equals(that.hpName) &&
                lastMsg.equals(that.lastMsg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, pUid, pName, hpUid, hpName, lastMsg);
    }
}
