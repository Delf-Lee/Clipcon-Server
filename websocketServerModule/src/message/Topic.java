package message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import sprout.clipcon.server.model.message.Message;
import sprout.clipcon.server.model.user.User;

@Getter
@Setter
public class Topic {
	private String topicName;
	private Topic upperTopic;
	private Map<String, Topic> downerTopic;
	private Map<String, User> subscribers;

	public Topic(String topicName, Topic upperTopic) {
		this.topicName = topicName;
		this.upperTopic = upperTopic;
		subscribers = Collections.synchronizedMap(new HashMap<String, User>());
	}

	public Topic(String topic) {
		this(topic, null);
	}

	public void addDownerTopic(Topic topic) {
		downerTopic.put(topic.getTopicName(), topic);
	}

	public void addSubscriber(String userName, User user) {
		subscribers.put(userName, user);
	}

	public void publishMessage(Message message, String exempter) {
		if (downerTopic == null && subscribers != null) { // if lowest level topic
			Set<String> set = subscribers.keySet();
			Iterator<String> it = set.iterator();
			String userName;
			while (it.hasNext()) {
				userName = it.next();
				try {
					if (!userName.equals(exempter)) {
						subscribers.get(userName).send(message);
					}
				} catch (NullPointerException e) {}
			}
		}
	}

	public void publishMessage(Message message) {
		if (downerTopic == null && subscribers != null) { // if lowest level topic
			Set<String> set = subscribers.keySet();
			Iterator<String> it = set.iterator();
			String userName;
			while (it.hasNext()) {
				userName = it.next();
				subscribers.get(userName).send(message);
			}
		}
	}
}
