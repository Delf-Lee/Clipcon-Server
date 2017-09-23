package message;

import java.io.IOException;

import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import sprout.clipcon.server.controller.GCServer;
import sprout.clipcon.server.controller.WebsocketEndpoint;
import sprout.clipcon.server.model.Group;
import sprout.clipcon.server.model.message.Message;
import sprout.clipcon.server.model.message.MessageParser;
import sprout.clipcon.server.model.user.User;

public class GCMessageHandler implements MessageHandler {
	private static GCServer server = GCServer.getInstance();
	private static GCMessageHandler uniqeInstance;
	private MessageBroker messageBroker = MessageBroker.getInstance();

	private GCMessageHandler() {}

	public static GCMessageHandler getInstance() {
		if (uniqeInstance == null) {
			uniqeInstance = new GCMessageHandler();
		}
		return uniqeInstance;
	}

	public void handleMessage(Message msessage, String tmp) {

	}

	public void handleMessage(Message message, WebsocketEndpoint endpoint) {
		String type = message.getType(); // extract type of received message
		System.out.println("[WebsocketEndpoint] message received success. type: " + type); // check message type
		Message responseMsg = null; // Initialize message to send to client
		Message noti = null;

		Group group = null;
		User user = null;
		String userName = null;
		String sessionId = null;

		switch (type) {
		/* Request Type: Confirm Version */
		case Message.HELLO:

		case Message.REQUEST_CONFIRM_VERSION:
			
			sessionId = message.get(Message.SESSION);
			user = server.createUser(endpoint);
			server.putUserInWaitingLine(sessionId, user);
			
			System.out.println("   [delflog] 2042 userName: " + user.getUserName());
			
			String versionFromClient = message.get(Message.CLIPCON_VERSION);
			if(user == null) {
				System.err.println("   [delflog] user is null 1444 -" + this.getClass());
			}
			responseMsg = new Message().setType(Message.RESPONSE_CONFIRM_VERSION); // create response message: Confirm Version

			if (versionFromClient.equals(GCServer.LATEST_WINDOWS_CLIENT_VERSION)) {
				responseMsg.add(Message.RESULT, Message.CONFIRM); // add response result
			} else {
				responseMsg.add(Message.RESULT, Message.REJECT); // add response result
			}
			break;

		/* Request Type: Create Group */
		case Message.REQUEST_CREATE_GROUP:
			group = server.createGroup(); // get Group in Server and add The instance of group that this object belongs to
			user = server.getUserBySessionIdInWatingLine(message.get(Message.SESSION));
			user.setGroup(group);
			userName = group.addUser(user); // add yourself to the group, get the user's name / XXX need to fix
			
			System.out.println("   [delflog] add userName 2037: " + user.getUserName());
			
			responseMsg = new Message().setType(Message.RESPONSE_CREATE_GROUP); // create response message: Create Group
			responseMsg.add(Message.RESULT, Message.CONFIRM); // add response result
			responseMsg.add(Message.NAME, userName); // add user name
			MessageParser.addMessageToGroup(responseMsg, group); // add group info
			break;

		/* Request Type: Join Group */
		case Message.REQUEST_JOIN_GROUP:
			group = server.getGroupByPrimaryKey(message.get(Message.GROUP_PK)); // get the "object corresponding to the requested group key" on the server
			if(group == null) {
				System.out.println("   [delflog] group is null 2011 - " + this.getClass());
			} else {
				System.out.println("   [delflog] group key 2030 - " + group.getPrimaryKey());
			}
			user = server.getUserBySessionIdInWatingLine(message.get(Message.SESSION));
			user.setGroup(group);
			userName = group.addUser(user); // add yourself to the group, get the user's name / XXX need to fix
			
			responseMsg = new Message().setType(Message.RESPONSE_JOIN_GROUP); // create response message: Join Group

			// If there is a group mapped to this group key
			if (group != null) {
				sessionId = message.get(Message.SESSION);
				user = server.getUserBySessionIdInWatingLine(sessionId);
				userName = group.addUser(user);

				responseMsg.add(Message.RESULT, Message.CONFIRM);
				responseMsg.add(Message.NAME, userName);
				MessageParser.addMessageToGroup(responseMsg, group);

				noti = new Message().setType(Message.NOTI_ADD_PARTICIPANT); // create notification message: participant's info
				noti.add(Message.PARTICIPANT_NAME, userName); // add participant's info
				messageBroker.addMessage(noti, group.getTopic(), user);
			} else {
				responseMsg.add(Message.RESULT, Message.REJECT); // add response result
			}
			break;

		/* Request Type: Exit Group */
		case Message.REQUEST_EXIT_GROUP:
			responseMsg = new Message().setType(Message.RESPONSE_EXIT_GROUP);

			group = endpoint.getUser().getGroup();
			noti = new Message().setType(Message.NOTI_EXIT_PARTICIPANT); // create notification message: outgoing user's info
			noti.add(Message.PARTICIPANT_NAME, user.getUserName()); // add outgoing user's info
			messageBroker.addMessage(noti, group.getTopic(), user);

			server.exitUserAtGroup(user);
			break;

		/* Request Type: Change Nickname */
		case Message.REQUEST_CHANGE_NAME:
			responseMsg = new Message().setType(Message.RESPONSE_CHANGE_NAME); // create response message: Change Nickname
			user = endpoint.getUser();
			String originName = user.getUserName(); // The user's origin name
			String changeUserName = message.get(Message.CHANGE_NAME); // The user's new name

			System.out.println("   [delflog] userName: " + userName);
			System.out.println("   [delflog] changeUserName: " + changeUserName);
			
			user.getGroup().changeUserName(originName, changeUserName); // Change User Nickname

			responseMsg.add(Message.RESULT, Message.CONFIRM);
			responseMsg.add(Message.CHANGE_NAME, changeUserName); // add new nickname

			noti = new Message().setType(Message.NOTI_CHANGE_NAME); // create notification message: user's info who request changing name
			noti.add(Message.NAME, originName); // add user's origin name
			noti.add(Message.CHANGE_NAME, changeUserName); // add user's new name

			System.out.println("originName: " + originName + ", changeUserName: " + changeUserName);
			group = user.getGroup();
			messageBroker.addMessage(message, group.getTopic(), user);
			break;

		case Message.REQUEST_EXIT_PROGRAM:
			System.out.println("[INFO] user exit the program");
			responseMsg = new Message().setType(Message.RESPONSE_EXIT_GROUP);
			exitUserAtGroup(endpoint.getUser());
			break;

		case Message.PING:
			System.out.println("ping");
			user = endpoint.getUser();
			responseMsg = new Message().setType(Message.PONG);
			break;

		default:
			responseMsg = new Message().setType(Message.TEST_DEBUG_MODE);
			System.out.println("Exception");
			break;
		}

		messageBroker.addMessage(responseMsg, user);
	}

	public void exitUserAtGroup(User user) {
		if (user == null) {
			System.out.println("user is null//");
		}
		Message noti = new Message().setType(Message.NOTI_EXIT_PARTICIPANT); // create notification message: outgoing user's info
		noti.add(Message.PARTICIPANT_NAME, user.getUserName()); // add outgoing user's info
		if (server.exitUserAtGroup(user) != null) {
			messageBroker.addMessage(noti, user.getGroup().getTopic(), user);
		}
	}

	// case Message.HELLO:
	// case Message.REQUEST_CONFIRM_VERSION:
	// case Message.REQUEST_CREATE_GROUP:
	// case Message.REQUEST_JOIN_GROUP:
	// case Message.REQUEST_EXIT_GROUP:
	// case Message.REQUEST_CHANGE_NAME:
	// case Message.REQUEST_EXIT_PROGRAM:
	// case Message.PING:

}
