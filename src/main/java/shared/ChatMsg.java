package shared;

import java.io.Serializable;

public class ChatMsg implements Serializable {

    public final int from;
    public final String content;

    public ChatMsg(int from, String content) {
        this.from = from;
        this.content = content;
    }
}
