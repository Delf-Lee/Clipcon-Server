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
		System.out.println("[DEBUG] start message send thread");
		if (sender == null) {
			System.out.println("[DEBUG] sender not null");
			sender = new MessageSendThread(tasks2);
		}

		System.out.println("[DEBUG] 0101 sender thread dead. restart thread");
		sender.editLoopCount(1);
		System.out.println("[DEBUG] 2004 loop count: " + sender.getLoopCount());
		try {
			System.out.println("[DEBUG] sender thread state: " + sender.getState());
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
		System.out.println("[INFO] add message task");
		if (message == null) {
			return;
		}
		tasks2.add(new NotificationTask(message, topic, user));
		startMessageSendThread();
	}
}