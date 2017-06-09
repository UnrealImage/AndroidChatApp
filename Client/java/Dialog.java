package aki.chat;

/**
 * Created by Shadow on 2016/12/14.
 */
public class Dialog {
    String messageId;
    String senderId;
    String receiverId;
    String content;
    String time;
    // isRead > 0: read
    // isRead <= 0: not read
    int isRead;
    Dialog(String messageId, String senderId, String receiverId, String content, String time, int isRead) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.time = time;
        this.isRead = isRead;
    }
}
