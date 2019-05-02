import org.java_websocket.WebSocket;

public class Client {
	WebSocket socket = null;
	String name = null;
	String password = null;
	
	public Client(WebSocket socket, String name, String password){
		this.socket = socket;
		this.name = name;
		this.password = password;
	}

	public WebSocket getSocket() {
		return socket;
	}

	public void setSocket(WebSocket socket) {
		this.socket = socket;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
}
