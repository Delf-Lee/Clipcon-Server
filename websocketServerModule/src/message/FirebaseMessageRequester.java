package message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.json.JSONObject;

import sprout.clipcon.server.model.message.Message;

public class FirebaseMessageRequester {

	private static FirebaseMessageRequester uniqueInstance;
	private static URL url = null;

	public static FirebaseMessageRequester getInstance() {
		if (uniqueInstance == null) {
			uniqueInstance = new FirebaseMessageRequester();
		}
		return uniqueInstance;
	}

	private FirebaseMessageRequester() {
		try {
			url = new URL(FirebaseMessageConfig.FCM_URL);
		} catch (MalformedURLException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void requestFcm(Message payload, String topic) {
		System.out.println("[DEBUG] call requestFcm");
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) url.openConnection();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
		try {
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			/*try {
				
			}catch (Exception e) {
				// TODO: handle exception
			}
			if (payload.get("imageString") != null) {
				payload.replace("imageString", "-");
			}*/
			// header setting
			conn.setRequestProperty(FirebaseMessageConfig.CONTENT_TYPE, FirebaseMessageConfig.JSON);
			conn.setRequestProperty(FirebaseMessageConfig.AUTHORIZATION, "key=" + FirebaseMessageConfig.SERVER_KEY);
		} catch (ProtocolException e) {
			System.err.println(e.getMessage());
		}

		// body setting
		JSONObject message = new JSONObject().put(FirebaseMessageConfig.MESSAGE, payload.toString());
		JSONObject request = new JSONObject();
		request.put(FirebaseMessageConfig.TO, "/topics/" + topic); // set topic
		request.put(FirebaseMessageConfig.DATA, message); // set payload(message)

		// request to server
		OutputStream os;
		try {
			os = conn.getOutputStream();
			os.write(request.toString().getBytes());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		// get response
		InputStream in;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buf = new byte[1024 * 8];
		int length = 0;
		try {
			in = conn.getInputStream();
			while ((length = in.read(buf)) != -1) {
				bos.write(buf, 0, length);
				System.out.println(new String(bos.toByteArray(), "UTF-8"));
			}
		} catch (UnsupportedEncodingException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

		conn.disconnect();
	}
}
