package message;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.swing.plaf.synth.SynthSeparatorUI;

import sprout.clipcon.server.model.message.Message;
import sprout.clipcon.server.model.user.User;

public class MessageBroker {

	// private List<NotificationTask> tasks = Collections.synchronizedList(new ArrayList<NotificationTask>());
	private Queue<NotificationTask> tasks2 = new ArrayBlockingQueue<NotificationTask>(2048);
	private MessageSendThread sender;
	private static MessageBroker uniqueInstance;

	private MessageBroker() {
		sender = new MessageSendThread(tasks2);
		sender.start();
	}

	public static MessageBroker getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new MessageBroker();
		}
		return uniqueInstance;
	}

	private void startMessageSendThread() {
		if (sender == null) {
			sender = new MessageSendThread(tasks2);
		}

		sender.editLoopCount(1);
		try {
			synchronized (sender) {
				sender.notify();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// message be sent to all topic subscribers(group members)
	public synchronized void addMessage(Message message, Topic topic) {
		addMessage(message, topic, null);
	}

	// message be sent to only sender
	public synchronized void addMessage(Message message, User user) {
		addMessage(message, null, user);
		// tasks2.add(new NotificationTask(message, user));
	}

	// message be sent to topic subscribers(group members) except sender
	public synchronized void addMessage(Message message, Topic topic, User user) {
		if (message == null) {
			return;
		}
		tasks2.add(new NotificationTask(message, topic, user));
		startMessageSendThread();
	}
}