package Model;

public class Chat {
    private String message;
    private String sender;
    private String receiver;
    private String msgDate;
    private String msgTime;
    private boolean isSeen;
    private String msgMillis;

    public Chat() {
    }

    public Chat(String message, String sender, String receiver, boolean isSeen, String msgDate, String msgTime, String msgMillis) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.msgDate = msgDate;
        this.msgTime = msgTime;
        this.isSeen = isSeen;
        this.msgMillis = msgMillis;
    }

    public String getMsgMillis() {
        return msgMillis;
    }

    public void setMsgMillis(String msgMillis) {
        this.msgMillis = msgMillis;
    }

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public boolean getisSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

}
