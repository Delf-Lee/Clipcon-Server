package sprout.clipcon.server;

public class StartGCServer {
	public static GCServer server ;
	public StartGCServer() {
		server = GCServer.getInstance();
	}
}
