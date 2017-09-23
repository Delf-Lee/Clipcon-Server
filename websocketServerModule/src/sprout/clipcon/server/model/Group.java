package sprout.clipcon.server.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.websocket.EncodeException;

import lombok.Getter;
import lombok.Setter;
import message.Topic;
import sprout.clipcon.server.controller.WebsocketEndpoint;
import sprout.clipcon.server.model.message.Message;
import sprout.clipcon.server.model.user.User;

public class Group {
	@Getter
	private String primaryKey;
	// private Map<String, WebsocketEndpoint> users2 = Collections.synchronizedMap(new HashMap<String, WebsocketEndpoint>());
	private Map<String, User> users2 = Collections.synchronizedMap(new HashMap<String, User>());
	private History history;
	@Getter
	@Setter
	private Topic topic;

	public Group(String primaryKey) {
		this.primaryKey = primaryKey;
		this.history = new History(primaryKey);
		topic = new Topic(primaryKey);
	}

//	public void sendWithout(String userName, Message message) throws IOException, EncodeException {
//		System.out.println("[Group] send message to all users of group except \"" + userName + "\" : " + message.toString());
//		for (String key : users2.keySet()) {
//			if (key.equals(userName)) // except
//				continue;
//			users2.get(key).getSession().getBasicRemote().sendObject(message);
//		}
//	}
//
//	public boolean sendAll(Message message) throws IOException, EncodeException {
//		if (users2.size() == 0) {
//			return true;
//		}
//
//		for (String key : users2.keySet()) {
//			users2.get(key).getSession().getBasicRemote().sendObject(message);
//		}
//		return false;
//	}

	public String addUser(User user) {
		String tmpName = getTempUsername();
		users2.put(tmpName, user);
		user.setUserName(tmpName);
		topic.addSubscriber(tmpName, user);
		System.out.println("[Group] new user take part in group: " + primaryKey + ":" + tmpName);
		return tmpName;
	}

	public List<String> getUserList() {
		List<String> list = new ArrayList<String>();
		for (String key : users2.keySet()) {
			list.add(key);
		}
		return list;
	}

	public void addContents(Contents contents) {
		history.addContents(contents);
	}

	public Contents getContents(String key) {
		return history.getContentsByPK(key);
	}

	public int getSize() {
		return users2.size();
	}

	public String getTempUsername() {
		StringBuffer temp = new StringBuffer();
		Random rnd = new Random();
		for (int i = 0; i < 6; i++) {
			int rIndex = rnd.nextInt(1);
			switch (rIndex) {
			case 0: // a-z
				temp.append((char) ((int) (rnd.nextInt(26)) + 97));	
				break;
			case 1: // 0-9
				temp.append((rnd.nextInt(10)));
				break; 
			}
		}
		return temp.toString();
	}

	public User removeUser(String userName) {
		return users2.remove(userName);
	}

	/**
	 * change user name
	 * 
	 * @param userName
	 *            - user's origin name
	 * @param changeUserName
	 *            - the name that user want to change
	 */
	public void changeUserName(String userName, String changeUserName) {
		User user = users2.get(userName); // assign new newUserController
		user.setUserName(changeUserName); // set changeUserName to newUserController

		removeUser(userName); // delete origin user who request change nickname
		users2.put(changeUserName, user); // add new user that key name is changeUserName
	}
}
