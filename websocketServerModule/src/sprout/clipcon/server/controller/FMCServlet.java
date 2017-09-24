package sprout.clipcon.server.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import message.GCMessageHandler;
import sprout.clipcon.server.model.message.Message;
/**
 * Servlet implementation class DownloadServlet
 */
@MultipartConfig(maxFileSize = 1024 * 1024 * 10, fileSizeThreshold = 1024 * 1024, maxRequestSize = 1024 * 1024 * 10)
@WebServlet("/sendAndroidMessage")
public class FMCServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private final String outputFileName = "bugReport.txt";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
		System.out.println("here!!");

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/* Gets the bug message entered by the client */
		Part part = request.getPart("fcmmessage");
		String reqMessage = buildString(part);
		System.out.println(reqMessage);
		Message message = new Message().setJson(reqMessage);
		System.out.println(message.toString());
		// GCMessageHandler.getInstance().handleMessage(message);
	
	}
	
	public String buildString(Part part) throws IOException {
		InputStream is = part.getInputStream();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
		StringBuilder stringBuilder = new StringBuilder();
		String bugMessage = null;

		try {
			while ((bugMessage = bufferedReader.readLine()) != null) {
				stringBuilder.append(bugMessage + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return stringBuilder.toString();
	
	}
	
}
