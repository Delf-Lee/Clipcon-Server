package sprout.clipcon.server.model.user;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import sprout.clipcon.server.controller.WebsocketEndpoint;
import sprout.clipcon.server.model.message.Message;

@Getter
@Setter
@AllArgsConstructor
public class WindowsUser extends User {

	private WebsocketEndpoint endpoint;
	
	@Override
	public void send(Message message) {
		if(subscribe) {
			endpoint.sendMessage(message);
		}
	}
			
	@Override
	public void close() {
		endpoint.closeSession();
	}
}
