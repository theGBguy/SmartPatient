package com.gbsoft.smartpatient.data;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Message {
    private long msgId;
    private String content;
    // senderUid + "_" + sendName
    private String senderInfo;
    private String receiverInfo;

    public Message() {
    }

    public Message(long msgId, String content, String senderInfo, String receiverInfo) {
        this.msgId = msgId;
        this.content = content;
        this.senderInfo = senderInfo;
        this.receiverInfo = receiverInfo;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderInfo() {
        return senderInfo;
    }

    public void setSenderInfo(String senderInfo) {
        this.senderInfo = senderInfo;
    }

    public String getReceiverInfo() {
        return receiverInfo;
    }

    public void setReceiverInfo(String receiverInfo) {
        this.receiverInfo = receiverInfo;
    }

    @Override
    public @NotNull String toString() {
        return "Message{" +
                "msgID=" + msgId +
                ", content='" + content + '\'' +
                ", senderInfo='" + senderInfo + '\'' +
                ", receiverInfo='" + receiverInfo + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return msgId == message.msgId &&
                content.equals(message.content) &&
                senderInfo.equals(message.senderInfo) &&
                receiverInfo.equals(message.receiverInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(msgId, content, senderInfo, receiverInfo);
    }
}
