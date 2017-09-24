package sprout.clipcon.server.controller;

import java.io.IOException;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import lombok.Getter;
import lombok.Setter;
import message.GCMessageHandler;
import sprout.clipcon.server.model.message.Message;
import sprout.clipcon.server.model.message.MessageDecoder;
import sprout.clipcon.server.model.message.MessageEncoder;
import sprout.clipcon.server.model.user.User;
import sprout.clipcon.server.model.user.WindowsUser;

@ServerEndpoint(value = "/ServerEndpoint", encoders = { MessageEncoder.class }, decoders = { MessageDecoder.class })
public class WebsocketEndpoint {
	private static GCMessageHandler messageHandler;
	@Getter
	@Setter
	private User user; // user information
	@Getter
	private Session session;

	public WebsocketEndpoint() {
		System.out.println("delflog WebsocketEndpoint 생성자");
		messageHandler = GCMessageHandler.getInstance();
	}
	
	// change source
	@OnOpen
	public void handleOpen(Session userSession) {
		this.session = userSession;
		System.out.print("session open: " + userSession.getId());
		System.out.print("(" + UploadServlet.uploadTime() + ")");
	}

	@OnMessage
	public void handleMessage(Message message, Session session) throws IOException, EncodeException {
		if(user == null) {
			user = new WindowsUser(this);
		}
		messageHandler.handleMessage(message, user);
	}

	@OnClose
	public void handleClose(Session userSession) {
		closeSession();
		messageHandler.exitUserAtGroup(user);
		System.out.println("[handleClose] " + UploadServlet.uploadTime());
	}

	@OnError
	public void handleError(Throwable t) {
		System.err.println("[WebsocketEndpoint] Error was occured.");
	}

	public void sendMessage(Message message) {
		try {
			session.getBasicRemote().sendObject(message);
		} catch (IOException e) {
			System.err.println(getClass().getName() + ". error at sending message. " + e.getMessage());
		} catch (EncodeException e) {
			System.err.println(getClass().getName() + ". error at sending message. " + e.getMessage());
		}
	}

	public void closeSession() {
		try {
			session.close();
			session = null;
		} catch (IOException e) {
		}
	}
}