import org.json.JSONObject;
import org.json.JSONStringer;

public class Fan {
	String fanType;
	boolean banned = false;
	
	public Fan(String fanType) {
		this.fanType = fanType;
	}
	
	public JSONObject forteamSnapshot() {
		JSONStringer message = new JSONStringer();
		
		message.object()
		.key("fanType").value(this.fanType)
		.key("banned").value(this.banned);
		message.endObject();
		
		return new JSONObject(message.toString());
		
	}
}
