package message;

import java.util.Queue;

import lombok.Getter;

public class MessageSendThread extends Thread {

	private Queue<NotificationTask> tasks;
	@Getter
	private int loopCount = 0;

	public synchronized int editLoopCount(int cnt) {
		loopCount += cnt;
		return loopCount;
	}

	public MessageSendThread(Queue tasks) {
		System.out.println("[DEBUG] create message send thread");
		this.tasks = tasks;
	}

	@Override
	public void run() {
		System.out.println("[INFO] start running send thread.");
		System.out.println("[DEBUG] loop count: " + loopCount);
		System.out.println();
		while (true) {
			while (loopCount > 0) {
				editLoopCount(-1);
				System.out.print("loop/");
				if (!tasks.isEmpty()) {
					synchronized (tasks) {
						tasks.remove().send();
					}
				}
			}
			try {
				synchronized (this) {
					System.out.println("[DEBUG] 0058 call wait in send thread");
					wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println();
		}
	}
}
