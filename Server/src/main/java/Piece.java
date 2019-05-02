import java.awt.Point;

import org.json.JSONObject;
import org.json.JSONStringer;

public class Piece {
	String positioninteam;
	String name;
	String broom;
	String sex;
	int oldxPos;
	int oldyPos;
	int xPos;
	int yPos;
	boolean banned;
	boolean holdsQuaffle;
	boolean turnUsed;
	
	public Piece(String positioninteam, JSONObject piece) {
		this.positioninteam = positioninteam;
		this.name = piece.getString("name");
		this.broom = piece.getString("broom");
		this.sex = piece.getString("sex");
	}

	public void updatePosition(int x, int y){
		this.oldxPos = this.xPos;
		this.oldyPos = this.yPos;
		this.xPos = x;
		this.yPos = y;
	}

	public Point getOldPositionPoint(){
		return new Point(this.oldxPos, this.oldyPos);
	}

	public JSONObject forteamSnapshot() {
		JSONStringer message = new JSONStringer();
		message.object();
		message.key("xPos");
		message.value(this.xPos);
		message.key("yPos");
		message.value(this.yPos);
		message.key("banned");
		message.value(this.banned);
		message.key("turnUsed");
		message.value(this.turnUsed);
		message.endObject();
		
		return new JSONObject(message.toString());
	}

	public boolean isBanned() {
		return banned;
	}

	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	public boolean isHoldsQuaffle() {
		return holdsQuaffle;
	}

	public void setHoldsQuaffle(boolean holdsQuaffle) {
		this.holdsQuaffle = holdsQuaffle;
	}

	public boolean isTurnUsed() {
		return turnUsed;
	}

	public void setTurnUsed(boolean turnUsed) {
		this.turnUsed = turnUsed;
	}

	public Point getPositionPoint() {
		return new Point(xPos, yPos);
	}
	
	public String getPositioninteam() {
		return positioninteam;
	}

	public void setPositioninteam(String positioninteam) {
		this.positioninteam = positioninteam;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBroom() {
		return broom;
	}

	public void setBroom(String broom) {
		this.broom = broom;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
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
