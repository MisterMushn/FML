import java.util.ArrayList;

import org.json.JSONObject;

public class TeamConfig {
	int points;
	
	
	String name;
	String motto;
	String image;
	
	JSONObject players;
	JSONObject fans;
	JSONObject colors;
	
	Piece seeker;
	Piece keeper;
	Piece chaser1;
	Piece chaser2;
	Piece chaser3;
	Piece beater1;
	Piece beater2;
	
	ArrayList<Fan> fanList = new ArrayList<Fan>();
	ArrayList<Piece> pieceList = new ArrayList<Piece>();
	
	
	
	
	public TeamConfig(JSONObject payload) {
		this.colors = payload.getJSONObject("colors");
		this.fans = payload.getJSONObject("fans");
		this.players = payload.getJSONObject("players");
		this.image = payload.getString("image");
		
		this.seeker = new Piece("seeker", this.players.getJSONObject("seeker"));
		this.keeper = new Piece("keeper", this.players.getJSONObject("keeper"));
		this.chaser1 = new Piece("chaser1", this.players.getJSONObject("chaser1"));
		this.chaser2 = new Piece("chaser2", this.players.getJSONObject("chaser2"));
		this.chaser3 = new Piece("chaser3", this.players.getJSONObject("chaser3"));
		this.beater1 = new Piece("beater1", this.players.getJSONObject("beater1"));
		this.beater2 = new Piece("beater2", this.players.getJSONObject("beater2"));
		
		this.pieceList.add(this.seeker);
		this.pieceList.add(this.keeper);
		this.pieceList.add(this.chaser1);
		this.pieceList.add(this.chaser2);
		this.pieceList.add(this.chaser3);
		this.pieceList.add(this.beater1);
		this.pieceList.add(this.beater2);
	
		
	}
	
	public void setPositions(JSONObject payload) {
		JSONObject players = payload.getJSONObject("players");
		this.seeker.setxPos(players.getJSONObject("seeker").getInt("xPos"));
		this.seeker.setyPos(players.getJSONObject("seeker").getInt("yPos"));
		this.keeper.setxPos(players.getJSONObject("keeper").getInt("xPos"));
		this.keeper.setyPos(players.getJSONObject("keeper").getInt("yPos"));
		this.chaser1.setxPos(players.getJSONObject("chaser1").getInt("xPos"));
		this.chaser1.setyPos(players.getJSONObject("chaser1").getInt("yPos"));
		this.chaser2.setxPos(players.getJSONObject("chaser2").getInt("xPos"));
		this.chaser2.setyPos(players.getJSONObject("chaser2").getInt("yPos"));
		this.chaser3.setxPos(players.getJSONObject("chaser3").getInt("xPos"));
		this.chaser3.setyPos(players.getJSONObject("chaser3").getInt("yPos"));
		this.beater1.setxPos(players.getJSONObject("beater1").getInt("xPos"));
		this.beater1.setyPos(players.getJSONObject("beater1").getInt("yPos"));
		this.beater2.setxPos(players.getJSONObject("beater2").getInt("xPos"));
		this.beater2.setyPos(players.getJSONObject("beater2").getInt("yPos"));
	}
	
	
	
	public ArrayList<Piece> getPieceList() {
		return pieceList;
	}

	public void setPieceList(ArrayList<Piece> pieceList) {
		this.pieceList = pieceList;
	}

	public ArrayList<Fan> getFanList() {
		return fanList;
	}

	public void setFanList(ArrayList<Fan> fanList) {
		this.fanList = fanList;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getMotto() {
		return motto;
	}


	public void setMotto(String motto) {
		this.motto = motto;
	}


	public String getImage() {
		return image;
	}


	public void setImage(String image) {
		this.image = image;
	}


	public JSONObject getPlayers() {
		return players;
	}


	public void setPlayers(JSONObject players) {
		this.players = players;
	}


	public JSONObject getFans() {
		return fans;
	}


	public void setFans(JSONObject fans) {
		this.fans = fans;
	}


	public JSONObject getColors() {
		return colors;
	}


	public void setColors(JSONObject colors) {
		this.colors = colors;
	}


	public Piece getSeeker() {
		return seeker;
	}


	public void setSeeker(Piece seeker) {
		this.seeker = seeker;
	}


	public Piece getKeeper() {
		return keeper;
	}


	public void setKeeper(Piece keeper) {
		this.keeper = keeper;
	}


	public Piece getChaser1() {
		return chaser1;
	}


	public void setChaser1(Piece chaser1) {
		this.chaser1 = chaser1;
	}


	public Piece getChaser2() {
		return chaser2;
	}


	public void setChaser2(Piece chaser2) {
		this.chaser2 = chaser2;
	}


	public Piece getChaser3() {
		return chaser3;
	}


	public void setChaser3(Piece chaser3) {
		this.chaser3 = chaser3;
	}


	public Piece getBeater1() {
		return beater1;
	}


	public void setBeater1(Piece beater1) {
		this.beater1 = beater1;
	}


	public Piece getBeater2() {
		return beater2;
	}


	public void setBeater2(Piece beater2) {
		this.beater2 = beater2;
	}

	
}
