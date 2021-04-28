package shared;

import java.io.Serial;
import java.io.Serializable;

public class ChatMsg implements Serializable {

	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = -8110232181994209212L;
	public final int from;
	public final String content;

	public ChatMsg(int from, String content) {
		this.from = from;
		this.content = content;
	}
}
