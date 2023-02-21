import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServerThread extends Thread{
	private ServerSocket serverSocket;
	private SocketInfo socket;
	private Peer peer = null;

	
	public ServerThread(String host, int port, String name, boolean isLeader) throws IOException {

		System.out.println("Host connected: " + host);
		System.out.println("Port connected: " + port);
		socket = new SocketInfo(host, port, name, isLeader);

		serverSocket = new ServerSocket(port);

		System.out.println("Listening on: " + host + ":" + port);
	}
	
	public void setPeer(Peer peer){
		this.peer = peer;
	}

	public String getHost(){
		return socket.getHost();
	}

	public int getPort(){
		return socket.getPort();
	}

	public String getPName(){
		return socket.getName();
	}

	public boolean isLeader(){
		return socket.getLeader();
	}


	public void run() {
		try {
			while (true) {
				Socket sock = serverSocket.accept();
				new ServerTask(sock, peer).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}