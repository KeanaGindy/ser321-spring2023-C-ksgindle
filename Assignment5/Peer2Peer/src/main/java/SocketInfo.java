public class SocketInfo {
	private String host;
	private int port;
	private String name;
	private boolean leader;

	public SocketInfo (String host, int port, String name, boolean leader){
		this.host = host;
		this.port = port;
		this.name = name;
		this.leader = leader;
	}

	public int getPort(){
		return port;
	}

	public String getHost(){
		return host;
	}

	public String getName() {
		return name;
	}

	public boolean getLeader() {
		return leader;
	}
}