import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.PrintWriter;

import org.json.*;

public class ServerTask extends Thread {
	private BufferedReader bufferedReader;
	private Peer peer = null;
	private PrintWriter out = null;
	private Socket socket = null;

	public ServerTask(Socket socket, Peer peer) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
		this.peer = peer;
		this.socket = socket;
	}

	public void run() {
		while (true) {
			try {
				JSONObject json = new JSONObject(bufferedReader.readLine());

				switch (json.getString("type")) {
					case "join":
						System.out.println(json.getString("username") + " wants to join the network");
						peer.updateListenToPeers(json.getString("ip") + "-" + json.getInt("port") + "-" + json.getString("username") + "-" + json.getBoolean("leader"));
						out.println(("{'type': 'join', 'list': '" + peer.getPeers() + "'}"));

						if (peer.isLeader()) {
							peer.pushMessage(json.toString());
						}

						break;			
					default:
						System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
						break;
				}

			}
			catch (Exception e) {
				interrupt();
				break;
			}
		}
	}

}
