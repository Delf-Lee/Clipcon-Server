package message;

import java.util.Queue;

public class MessageSendThread extends Thread {

	private Queue<NotificationTask> tasks;

	public MessageSendThread(Queue tasks) {
		this.tasks = tasks;
	}

	@Override
	public void run() {
		try {
			while (true) {
				if (!tasks.isEmpty()) {
					tasks.poll().send();
				}
			}
		} catch (Exception e) {
			System.out.println("2321");
			e.printStackTrace();
		}
	}
}
