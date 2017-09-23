package message;

import sprout.clipcon.server.model.message.Message;
import sprout.clipcon.server.model.user.User;

public class NotificationTask {
	private Topic topic;
	private int tryCount;
	private Message message;
	private User user;

	// send message to all user topic subscriber
	public NotificationTask(Message message, Topic topic, User user) {
		System.out.println("   [delflog] NotificationTask 1 - " + this.getClass());
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
		System.out.println("   [delflog] NotificationTask 2 - " + this.getClass());
	}

	// send message to user
	public NotificationTask(Message message, User sender) {
		this(message, null, sender);
		System.out.println("   [delflog] NotificationTask 3 - " + this.getClass());
	}

	public void send() {
		if (topic == null) {
			user.send(message);
		} else {
			if (message == null) {
				System.out.println("   [delflog] message is null 0009 - " + this.getClass());
			}
			if (topic == null) {
				System.out.println("   [delflog] topic is null 0009 - " + this.getClass());
			}
			if (user == null) {
				System.out.println("   [delflog] user is null 0009 - " + this.getClass());
			}
			if (user == null) {
				topic.publishMessage(message); // send all users
			} else {
				topic.publishMessage(message, user.getUserName()); // send except one user 
			}
		}
	}
}
