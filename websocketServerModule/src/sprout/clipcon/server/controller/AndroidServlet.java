package sprout.clipcon.server.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import message.GCMessageHandler;
import sprout.clipcon.server.model.Group;
import sprout.clipcon.server.model.message.Message;
import sprout.clipcon.server.model.user.User;

/**
 * Servlet implementation class DownloadServlet
 */
@MultipartConfig(maxFileSize = 1024 * 1024 * 10, fileSizeThreshold = 1024 * 1024, maxRequestSize = 1024 * 1024 * 10)
@WebServlet("/androridMessage")
public class AndroidServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final String outputFileName = "bugReport.txt";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) {}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Message reqMsg = getMessage(request);
		String key = reqMsg.get(Message.GROUP_PK);
		
		GCServer server = GCServer.getInstance();
		Group group = server.getGroupByPrimaryKey(key);
		User user = group.getUserByName(reqMsg.get(Message.NAME));
		// String resMsg = GCMessageHandler.getInstance().handleMessage(reqMsg, user);
		// response.getWriter().append(reqMsg);
	}

	public Message getMessage(HttpServletRequest request) {
		InputStream is = null;
		BufferedReader bufferedReader = null;
		StringBuilder stringBuilder = new StringBuilder();
		try {
			/* Gets the bug message entered by the client */
			Part part = request.getPart("message");
			try {
				is = part.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			String bugMessage = null;
			while ((bugMessage = bufferedReader.readLine()) != null) {
				stringBuilder.append(bugMessage + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
		return new Message().setJson(stringBuilder.toString());
	}

}
