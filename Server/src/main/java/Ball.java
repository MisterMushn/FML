import java.awt.Point;

import org.json.JSONObject;
import org.json.JSONStringer;

public class Ball {
	String ballType;
	int oldxPos;
	int oldyPos;
	int xPos;
	int yPos;
	
	public Ball(String ballType) {
		this.ballType = ballType;
	}
	
	public JSONObject forsnapshot() {
		JSONStringer message = new JSONStringer();
		
		message.object();
		message.key("xPos").value(this.xPos);
		message.key("yPos").value(this.yPos);
		message.endObject();
		
		return new JSONObject(message.toString());
	}
	public void updatePosition(int x, int y){
		this.oldxPos = xPos;
		this.oldyPos = yPos;
		this.xPos = x;
		this.yPos = y;
	}

	public Point getOldPositionPoint(){
		return new Point(this.oldxPos, this.oldyPos);
	}

	public int getOldxPos() {
		return oldxPos;
	}

	public void setOldxPos(int oldxPos) {
		this.oldxPos = oldxPos;
	}

	public int getOldyPos() {
		return oldyPos;
	}

	public void setOldyPos(int oldyPos) {
		this.oldyPos = oldyPos;
	}

	public Point getPositionPoint() {
		return new Point(xPos, yPos);
	}
	
	public String getBallType() {
		return ballType;
	}

	public void setBallType(String ballType) {
		this.ballType = ballType;
	}

	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}
	
}
