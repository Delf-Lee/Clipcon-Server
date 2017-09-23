package sprout.clipcon.server.controller;

public class StartGCServer {
	public static GCServer server ;
	public StartGCServer() {
		server = GCServer.getInstance();
	}
}
