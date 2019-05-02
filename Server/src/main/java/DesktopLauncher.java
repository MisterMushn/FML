
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jdk.incubator.http.WebSocket;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;
import org.json.JSONStringer;

public class DesktopLauncher extends WebSocketServer {


	String currentPhase = "Lobby";
	int currentRound = 0;
	ArrayList<String> playernames = new ArrayList<String>();
	ArrayList<String> passwordlist = new ArrayList<String>();
	ArrayList<Client> allConnectedClients = new ArrayList<Client>();
	ArrayList<String> allConnectedSpectatorNames = new ArrayList<String>();
	
	Ball snitch;
	Ball quaffle;
	Ball bludger1;
	Ball bludger2;

	WebSocket player1;
	WebSocket player2;

	WebSocket madeLastMove;
	Piece madeLastMovePiece;

	TeamConfig player1TeamConfig;
	JSONObject player1TeamConfigJSON;
	TeamConfig player2TeamConfig;
	JSONObject player2TeamConfigJSON;

	ArrayList<Piece> stillhavetomakeaMoveTeam1;
	ArrayList<Piece> stillhavetomakeaMoveTeam2;



	JSONObject matchConfig = new JSONObject(
			"{\"maxRounds\": \"(int)\",\"timeouts\": {\"playerTurnTimeout\": \"(int/millisec)\",\"fanTurnTimeout\": \"(int/millisec)\",\"playerPhaseTime\": \"(int/millisec)\",\"fanPhaseTime\": \"(int/millisec)\",\"ballPhaseTime\": \"(int/millisec)\"},\"probabilities\": {\"throwSuccess\": \"(float/prob)\",\"knockOut\": \"(float/prob)\",\"foolAway\": \"(float/prob)\",\"catchSnitch\": \"(float/prob)\",\"catchQuaffle\": \"(float/prob)\",\"wrestQuaffle\": \"(float/prob)\",\"extraMove\": {\"tinderblast\": \"(float/prob)\",\"cleansweep11\": \"(float/prob)\",\"comet260\": \"(float/prob)\",\"nimbus2001\": \"(float/prob)\",\"firebolt\": \"(float/prob)\"},\"foulDetection\": {\"flacking\": \"(float/prob)\",\"haversacking\": \"(float/prob)\",\"stooging\": \"(float/prob)\",\"blatching\": \"(float/prob)\",\"snitchnip\": \"(float/prob)\"},\"fanFoulDetection\": {\"elfTeleportation\": \"(float/prob)\",\"goblinShock\": \"(float/prob)\",\"trollRoar\": \"(float/prob)\",\"snitchSnatch\": \"(float/prob)\"}}}");

	public DesktopLauncher(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
	}

	public DesktopLauncher(InetSocketAddress address) {
		super(address);
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		int port = 8887;
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception ex) {
		}
		DesktopLauncher s = new DesktopLauncher(port);
		s.start();
		System.out.println(s.generateContainer("hallo", new String[] { "message", "welcome, please enjoy" }));
		System.out.println("ChatServer started on port: " + s.getPort());
		System.out.println(s.calculateDistance(4, 4, 1, 1));
		

		BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String in = sysin.readLine();
			s.broadcast(in);
			if (in.equals("exit")) {
				s.stop(1000);
				break;
			}
		}

	}

	@Override
	public void onOpen(WebSocket conn, ClientHandshake handshake) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClose(WebSocket conn, int code, String reason, boolean remote) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMessage(WebSocket conn, String message) {
		System.out.println("----------------------------------------------------------------------");
		this.player1 = conn;
		player1.send("Message received");
		System.out.println("Received following message: " + message);

		JSONObject msgasJSON = new JSONObject(message);
		String payloadType = (String) msgasJSON.get("payloadType");
		JSONObject payload = msgasJSON.getJSONObject("payload");

		switch (payloadType) {
		case "joinRequest":
			System.out.println("payloadType was joinRequest with payload: " + payload.toString());
			String userloggingin = (String) payload.get("userName");
			String userpassword = (String) payload.get("password");

			switch (checkPlayerlist(userloggingin, userpassword)) {
			case (1):
				System.out.println("User reconnected with correct password");
				// user reconnected with correct password

				break;
			case (2):
				System.out.println("User reconnected with wrong password");
				// user reconnected with wrong password

				break;
			case (-1):
				System.out.println("Users first time logging in. Add his Client to allConnectedClients");
				// Users first time logging in. add his Client to allConnectedClients
				allConnectedClients.add(new Client(conn, userloggingin, userpassword));

				// broadcast loginGreeting
				broadcast(generateContainer("loginGreeting", new String[] { "userName", userloggingin }));

				// unicast joinResponse
				conn.send(generateContainer("loginResponse", new String[] { "message", "welcome, please enjoy" }));

				break;
			}
			break;

		case "teamConfig":
			if (this.player1 != null && this.player2 != null) {
				conn.send(generateContainer("privateDebug",
						new String[] { "information", "There are already to players" }));
			} else if (this.player1 == null) {
				this.player1 = conn;
				this.player1TeamConfig = new TeamConfig(payload);
				this.player1TeamConfigJSON = payload;
			} else {
				this.player2 = conn;
				this.player2TeamConfig = new TeamConfig(payload);
				this.player2TeamConfigJSON = payload;
				startMatch();


				// broadcast matchStart Message
				broadcast(generatematchStartMessage());

			}

			break;
		case "teamFormation":
			if (conn != this.player1 && conn != this.player2) {

			} else if (conn == this.player1) {
				this.player1TeamConfig.setPositions(payload);
			} else {
				this.player2TeamConfig.setPositions(payload);
			}
            break;
		case "globalDebug":
		    break;

        case "deltaRequest":
            String deltaType = payload.get("deltaType");

            switch(deltaType){
                case "move":
                    if(payload.getString("activeEntity").equals(this.madeLastMovePiece.getName())){
                        if(madeLastMovePiece.getHoldsQuaffle()){
                            this.quaffle.updatePosition(payload.getInt("xPosNew"), payload.getInt("yPosNew"));

                        }
                        madeLastMovePiece.updatePosition(payload.getInt("xPosNew"), payload.getInt("yPosNew"));
                        generateContainer("snapshot", generatesnapshotMessage(generatedeltaBroadcast("move", madeLastMovePiece.getOldPositionPoint(), madeLastMovePiece.getPositionPoint(), madeLastMovePiece.getName(), null)));
                        if(madeLastMovePiece.getName().contains("chaser") || madeLastMovePiece.getName().contains("keeper")){
                            if(madeLastMovePiece.getPositionPoint().equals(this.quaffle.getPositionPoint())){
                                madeLastMovePiece.setHoldsQuaffle(true);
                            }
                        }else{
                            if(madeLastMovePiece.getPositionPoint().equals(this.quaffle.getPositionPoint())){
                                ;löä
                            }
                        }
                    }




            }

            break;
		}
		case

	}

	@Override
	public void onError(WebSocket conn, Exception ex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub

	}
    public void generatedeltaBroadcast(String deltaType, Point oldPos, Point newPos, String active, String passive){
	    JSONStringer message = new JSONStringer();
        message.key("deltaType").value(deltaType);
        message.key("xPosOld").value(oldPos.getX());
        message.key("yPosOld").value(oldPos.getY());
        message.key("xPosNew").value(newPos.getX());
        message.key("yPosNew").value(newPos.getY());
        message.key("activeEntity").value(active);
        message.key("passiveEntity").value(passive);
    }
	public void startMatch(){
		generatematchStartMessage();
		this.currentPhase = "Ballphase";
		bludgerMove(this.bludger1);
		bludgerMove(this.bludger2);
		startPlayerPhase();
	}

	public void startPlayerPhase(){
		this.stillhavetomakeaMoveTeam1 = player1TeamConfig.getPieceList();
		this.stillhavetomakeaMoveTeam2 = player2TeamConfig.getPieceList();

		if(Math.round(Math.random()) == 1){
		    this.madeLastMovePiece = stillhavetomakeaMoveTeam1.get(()int)(Math.random()*stillhavetomakeaMoveTeam1.size());
            generatenextMessage(this.madeLastMovePiece);
        }else{
            this.madeLastMovePiece = stillhavetomakeaMoveTeam2.get(()int)(Math.random()*stillhavetomakeaMoveTeam2.size())
            generatenextMessage(this.madeLastMovePiece);
        }

	}



	public String generatenextMessage(Piece piece){
		JSONStringer message = new JSONStringer();

		message.object();
		message.key("turn").value(piece.getName());
		message.key("type").value("move");
		message.key("timeout").value(this.movetimeoutTime);
		message.endObject();

        String next = generateContainer("next", new JSONOject(message.toString()));

        return next;
	}

	public String generateContainer(String payloadType, String[] payload) {
		System.out.println("Generating Container Message with " + payloadType + " as payloadType");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String date = dateFormat.format(new Date());
		JSONStringer message = new JSONStringer();

		message.object();
		message.key("timestamp");
		message.value(date);
		message.key("payloadType");
		message.value(payloadType);
		message.key("payload");
		message.object();
		for (int i = 0; i < payload.length; i = i + 2) {
			message.key(payload[i]);
			message.value(payload[i + 1]);
		}
		message.endObject();
		message.endObject();

		return message.toString();
	}
	
	public String generateContainer(String payloadType, JSONObject payload) {
		System.out.println("Generating Container Message with " + payloadType + " as payloadType");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String date = dateFormat.format(new Date());
		JSONStringer message = new JSONStringer();

		message.object();
		message.key("timestamp");
		message.value(date);
		message.key("payloadType");
		message.value(payloadType);
		message.key("payload");
		message.value(payload);
		message.endObject();

		return message.toString();
	}
	
	

	public int checkPlayerlist(String name, String password) {
		System.out.println("Checking allConnectedPlayers for:" + name);
		for (Client client : allConnectedClients) {
			if (client.getName().equals(name)) {
				if (client.getPassword().equals(password)) {
					// login with correct password
					return 1;
				} else {
					// player logged in before but used wrong password now
					System.out.println(
							"Old password was: " + client.getPassword() + " tried to log in with: " + password);
					return 2;
				}
			}
		}
		// player never logged in before
		return -1;

	}

	public String generatematchStartMessage() {
//		System.out.println("Generating Container Message with " + "matchStart" + " as payloadType");
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//		String date = dateFormat.format(new Date());
//		JSONStringer message = new JSONStringer();
//		
//		message.object();
//		message.key("timestamp");
//		message.value(date);
//		message.key("payloadType");
//		message.value("matchStart");
//		message.key("payload");
//		message.object();
//		message.key("matchConfig");
//		message.value(this.matchConfig);
//		message.key("leftTeamConfig");
//		message.value(this.player1TeamConfigJSON);
//		message.key("rightTeamConfig");
//		message.value(this.player2TeamConfigJSON);
//		message.key("leftTeamUserName");
//		message.value(this.player1TeamConfig.getName());
//		message.key("rightTeamUserName");
//		message.value(this.player2TeamConfig.getName());
//		
//		return message.toString();

		return generateContainer("matchStart",
				new String[] { "matchConfig", this.matchConfig.toString(), "leftTeamConfig",
						this.player1TeamConfigJSON.toString(), "rightTeamConfig", this.player2TeamConfigJSON.toString(),
						"leftTeamUserName", this.player1TeamConfig.getName(), "rightTeamUserName",
						this.player2TeamConfig.getName() });

	}

	public String generatematchFinishMessage(int endRound, int leftPoints, int rightPoints, String winnerusername,
			String victoryReason) {
		return generateContainer("matchFinish",
				new String[] { "endRound", String.valueOf(endRound), "leftPoints", String.valueOf(leftPoints),
						"rightPoints", String.valueOf(rightPoints), "winnerUserName", winnerusername, "victoryReason",
						victoryReason });
	}

	public String generateteamSnapshotJSONText(TeamConfig team) {
		ArrayList<Fan> fanList = team.getFanList();

		JSONStringer message = new JSONStringer();
		message.object().key("points").value(team.getPoints()).key("fans");
		for (Fan fan : fanList) {
			message.value(fan.forteamSnapshot());
		}
		message.endArray().key("players").object();
		message.key("seeker").value(team.getSeeker().forteamSnapshot())
				.key("keeper").value(team.getKeeper().forteamSnapshot())
				.key("chaser1").value(team.getChaser1().forteamSnapshot())
				.key("chaser2").value(team.getChaser2().forteamSnapshot())
				.key("chaser3").value(team.getChaser3().forteamSnapshot())
				.key("beater1").value(team.getBeater1().forteamSnapshot())
				.key("beater2").value(team.getBeater2().forteamSnapshot());

		message.endObject().endObject();
		
		return message.toString();
	}
	
	
	public JSONObject generatesnapshotMessage(String deltaBroadcast) {
		JSONStringer message = new JSONStringer();
		message.object();
		message.key("lastDeltaBroadcast").value(deltaBroadcast);
		message.key("phase").value(this.currentPhase);
		message.key("spectatorUserName").array();
			for(String name: this.allConnectedSpectatorNames) {
				message.value(name);
			}
		message.endArray();
		message.key("round").key(String.valueOf(this.currentRound));
		message.key("leftTeam").value(generateteamSnapshotJSONText(player1TeamConfig));
		message.key("rightTeam").value(generateteamSnapshotJSONText(player2TeamConfig));
		message.key("balls").object();
		message.key("snitch").value(this.snitch.forsnapshot());
		message.key("quaffle").value(this.quaffle.forsnapshot());
		message.key("bludger1").value(this.bludger1.forsnapshot());
		message.key("bludger2").value(this.bludger2.forsnapshot());
		message.endObject().endObject();
		
		return new JSONObject(message.toString());
	}
	
	public void snitchMove() {
		//calculate nearest piece
		Piece nearestPiece;
		float distance = 30;
		for(Piece piece: player1TeamConfig.getPieceList()) {
			if(Point2D.distance(piece.getxPos(), piece.getyPos(), this.snitch.getxPos(), this.snitch.getyPos()) < distance) {
				nearestPiece = piece;
			}
		}
		
		for(Piece piece: player2TeamConfig.getPieceList()) {
			if(Point2D.distance(piece.getxPos(), piece.getyPos(), this.snitch.getxPos(), this.snitch.getyPos()) < distance) {
				nearestPiece = piece;
			}
		}
		//now move away from that piece to an available square
		ArrayList<Point> availableSquares = new ArrayList<Point>();
		
		for(int x = -1; x<=1;x++) {
			for(int y = -1; y<=1; y++) {
				availableSquares.add(new Point(this.snitch.getxPos()+x, this.snitch.getxPos()+y));
			}
		}
		availableSquares.remove(this.snitch.getPositionPoint());
		for(Point point: availableSquares) {
			if(point.getY() == 0 || point.getY() == 14) {
				availableSquares.remove(point);
				continue;
			}
			if(point.getX() == 0 || point.getX() == 18){
				availableSquares.remove(point);
			}		
		}
		
		
		
	}
	
	public void bludgerMove(Ball bludger) {
		//calculate nearest piece
		Piece nearestPiece;
		int distance = 20;
		for(Piece piece: player1TeamConfig.getPieceList()) {
			if(calculateDistance(bludger.getxPos(), bludger.getyPos(), piece.getxPos(), piece.getyPos()) < distance){
				nearestPiece = piece;
			}
		}
		
		//move towards it
		
	}
	
	public int calculateDistance(double x1, double y1, double x2, double y2) {
		int distance = 0;
		double vectorx = x2-x1;
		double vectory= y2-y1;
		ArrayList<Point> gridTilesHit = new ArrayList<Point>();
		
		double tempx3 = x1;
		double tempy3 = y1;
		
		while(true) {
			tempx3 = tempx3 + (vectorx * 0.001);
			tempy3 = tempy3 + (vectory * 0.001);
			
			if((tempx3 % 0.5 != 0) || (tempy3 % 0.5 != 0)) {
				Point a = new Point((int)Math.round(tempx3),(int) Math.round(tempy3));
				if(!gridTilesHit.contains(a)) {
					gridTilesHit.add(a);
					System.out.println("hit" + a.toString());
				}
				if(a.equals(new Point(x2, y2))) {
					break;
				}
			}	
		}
		
		
		return gridTilesHit.size()-2;
	}

}
