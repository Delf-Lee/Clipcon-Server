package sprout.clipcon.server.model.user;

import lombok.Getter;
import lombok.Setter;
import sprout.clipcon.server.model.Group;
import sprout.clipcon.server.model.message.Message;

@Getter
@Setter
public abstract class User {
	
	protected String userName;
	protected Group group;
	protected boolean subscribe = true;

	public void send(Message message) {
	}
	
	public void close() {
	}
}
