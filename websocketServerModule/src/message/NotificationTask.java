package message;

import org.json.JSONException;

import sprout.clipcon.server.model.message.Message;
import sprout.clipcon.server.model.user.User;

public class NotificationTask {
	private Topic topic;
	private int tryCount;
	private Message message;
	private User user;

	// send message to all user topic subscriber
	public NotificationTask(Message message, Topic topic, User user) {
		System.out.println("[DEBUG] message task is created");
		this.message = message;
		this.topic = topic;
		this.user = user;
		if (user == null) {
			System.out.println("   [delflog] user is null 0012 - " + this.getClass());
		}
	}

	// send message to all user topic subscriber except sender
	public NotificationTask(Message message, Topic topic) {
		this(message, topic, null);
	}

	// send message to user
	public NotificationTask(Message message, User sender) {
		this(message, null, sender);
	}

	public void send() {
		System.out.println("[INFO] send message");
		if (topic == null) {
			user.send(message); // send to only sender
		} else {
			
			if (user == null) {
				System.out.println("   @ send all user");
				topic.publishMessage(message); // send all users
			} else {
				System.out.println("   @ send except one user");
				topic.publishMessage(message, user.getUserName()); // send except one user
			}
			try {
				message.replace("imageString", "-");
			} catch (JSONException e) {
				System.out.println("예외 발생");
			}
			System.out.println("try 밖");
			FirebaseMessageRequester.getInstance();
			FirebaseMessageRequester.requestFcm(message, topic.getTopicName());
		}
	}
}
