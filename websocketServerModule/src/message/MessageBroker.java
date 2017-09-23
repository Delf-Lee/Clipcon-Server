package message;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import sprout.clipcon.server.model.message.Message;
import sprout.clipcon.server.model.user.User;

public class MessageBroker {

	// private List<NotificationTask> tasks = Collections.synchronizedList(new ArrayList<NotificationTask>());
	private Queue<NotificationTask> tasks2 = new ArrayBlockingQueue<NotificationTask>(2048);
	private Thread sender;
	private static MessageBroker uniqueInstance;

	private MessageBroker() {
		System.out.println("generate MessageBroker");
		sender = new MessageSendThread(tasks2);
		System.out.println("generate Message send thread");
		sender.start();
		System.out.println("start Message send thread");
	}

	public static MessageBroker getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new MessageBroker();
		}
		return uniqueInstance;
	}

	private void startMessageSendThread() {
		sender.start();
	}

	public synchronized void addMessage(Message message, Topic topic) {
		addMessage(message, topic, null);
	}

	public synchronized void addMessage(Message message, Topic topic, User user) {
		if (message == null) {
			return;
		}
		if (user == null) {
			System.out.println("   [delflog] user is null 0012 - " + this.getClass());
		}
		if (sender.isAlive()) {
			System.out.println("addMessage1");
			tasks2.add(new NotificationTask(message, topic, user));
		} else {
			if (sender == null) {
				sender = new MessageSendThread(tasks2);
			}
			// sender.start();
		}
	}

	public synchronized void addMessage(Message message, User user) {
		if (sender.isAlive()) {
			System.out.println("addMessage2");
			tasks2.add(new NotificationTask(message, user));
		} else {
			if (sender == null) {
				System.out.println("asign sender thread.");
				sender = new MessageSendThread(tasks2);
			}
			// sender.start();
		}
	}
}