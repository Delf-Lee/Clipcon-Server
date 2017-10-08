package sprout.clipcon.server;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.websocket.ContainerProvider;
import javax.websocket.MessageHandler;
import javax.websocket.server.ServerContainer;

import message.GCMessageHandler;
import message.Topic;
import sprout.clipcon.server.controller.WebsocketEndpoint;
import sprout.clipcon.server.model.Group;
import sprout.clipcon.server.model.user.AndroidUser;
import sprout.clipcon.server.model.user.User;
import sprout.clipcon.server.model.user.WindowsUser;

public class GCServer {
	private static GCServer uniqueInstance;
	/** All groups on the server */
	private Map<String, Group> groups = Collections.synchronizedMap(new HashMap<String, Group>());
	// private Map<String, User> watingLine = Collections.synchronizedMap(new HashMap<String, User>());

	// private MessageBroker messageBroker = MessageBroker.getInstance();
	private Topic topTopic = new Topic("Server"); // top topic
	private MessageHandler messageHandler = GCMessageHandler.getInstance();
	public static final String SERVER_ROOT_LOCATION = System.getProperty("user.home") + File.separator + "Desktop" + File.separator;

	// directory of download files
	public static final String RECEIVE_LOCATION = SERVER_ROOT_LOCATION + "clipcon_download";

	public static final String LATEST_WINDOWS_CLIENT_VERSION = "1.1";
	public static final String LATEST_ADNROID_CLIENT_VERSION = "1.1";

	// change source
	private GCServer() {
		System.out.println("GCServer start");
		File initReceiveLocation = new File(RECEIVE_LOCATION); // create directory for receiving download file
		if (!initReceiveLocation.exists()) {
			initReceiveLocation.mkdir(); // Create Directory
		}
	}

	public static GCServer getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new GCServer();
		}
		return uniqueInstance;
	}

	public Group getGroupByPrimaryKey(String key) {
		// Set<String> set = groups.keySet();
		// Iterator<String> it = set.iterator();

		Group targetGroup = groups.get(key);
		return targetGroup;
	}

	public synchronized Group createGroup() {
		String groupKey = generatePrimaryKey(5);
		System.out.println("[INFO] create the group (" + groupKey + ")");
		Group newGroup = new Group(groupKey);
		newGroup.setTopic(topTopic);
		groups.put(groupKey, newGroup);
		return newGroup;
	}

	/** If no user in group, remove this group at list and remote directory(file download dir). */
	public void destroyGroup(String groupPrimaryKey) {
		groups.remove(groupPrimaryKey);

		deleteAllFilesInGroupDir(RECEIVE_LOCATION + File.separator + groupPrimaryKey);
	}

	/** Delete all files in group directory */
	public void deleteAllFilesInGroupDir(String parentDirPath) {
		// Get the files in the folder into an array.
		File file = new File(parentDirPath);

		if (file.exists()) {
			File[] tempFile = file.listFiles();

			if (tempFile.length > 0) {
				for (int i = 0; i < tempFile.length; i++) {
					if (tempFile[i].isFile()) {
						tempFile[i].delete();
					} else { // Recursive function
						deleteAllFilesInGroupDir(tempFile[i].getPath());
					}
					tempFile[i].delete();
				}
				file.delete();
			}
		}
	}

	public synchronized void removeGroup(Group group) {
		Group removeGroup = groups.remove(group.getPrimaryKey());
		if (removeGroup != null) {
			deleteAllFilesInGroupDir(removeGroup.getPrimaryKey());
		}
	}

	/**
	 * generate random String
	 * 
	 * @param length
	 *            length of String
	 * @return string generated randomly
	 */
	private String generatePrimaryKey(int length) {
		StringBuffer temp = new StringBuffer();
		Random rnd = new Random();
		for (int i = 0; i < 2; i++) {
			temp.append((char) ((int) (rnd.nextInt(26)) + 97));
		}
		for (int i = 0; i < length - 2; i++) {
			temp.append((rnd.nextInt(10)));
		}
		return temp.toString();
	}

	/** the method for test and debug. */
	public static void subDirList(String source) {
		File dir = new File(source);
		File[] fileList = dir.listFiles();
		try {
			for (int i = 0; i < fileList.length; i++) {
				File file = fileList[i];
				if (file.isFile()) {
					System.out.println("File name = " + file.getPath());
				} else if (file.isDirectory()) {
					System.out.println("Dir name = " + file.getPath());
					subDirList(file.getCanonicalPath().toString());
				}
			}
		} catch (IOException e) {}
	}

	public static String getTime() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("[yy-MM-dd hh:mm:ss]");
		return sdf.format(date).toString();
	}

	// public User getUserBySession(String session) {
	// return watingLine.get(session);
	// }

	public User createUser(WebsocketEndpoint endpoint) {
		System.out.println("   [delflog] create user" + this.getClass());
		System.out.println("   [delflog] session: " + endpoint.getSession().getId());
		WindowsUser user = new WindowsUser(endpoint);
		endpoint.setUser(user);
		return user;
	}

	public User createUser(String tmpArg) {
		return new AndroidUser();
	}

	// public synchronized void putUserInWaitingLine(String sessionId, User user) {
	// watingLine.put(sessionId, user);
	// }

	// public synchronized User getUserBySessionIdInWatingLine(String seesionId) {
	// return watingLine.remove(seesionId);
	// }

	// public User peekUserBySessionInWatingLine(String sessionId) {
	// System.out.println(" [delflog] peek user - " + this.getClass());
	// System.out.println(" [delflog] session: " + sessionId);
	// return watingLine.get(sessionId);
	// }

	public User exitUserAtGroup(User user) {
		Group group = user.getGroup();
		User exitUser = group.removeUser(user.getUserName());

		if (group.getSize() == 0) {
			GCServer.getInstance().removeGroup(group);
		}
		return exitUser;
	}
}
