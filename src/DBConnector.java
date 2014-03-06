import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Statement;


public class DBConnector {
	private Connection conn = null;	
	private String host = "jdbc:mysql://69.195.124.64/yoaquimc_Qbo";
	private String user = "yoaquimc";
	private String pass = "iZe3fN0r>$";
	private static DBConnector db;

	public static DBConnector getConnector(){
		if(db==null){
			db = new DBConnector();
			return db;
		}
		else{
			return db;
		}
	}

	public boolean connect(){
		try {
			DriverManager.setLoginTimeout(10);
			this.conn = DriverManager.getConnection(host, user, pass);
			if(this.conn==null){
				return false;
			}
			return true;
		} 
		catch (SQLException e) {
			return false;
		}
	}

	public void disconnect(){
		try {
			this.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet recycleTransaction(String id, String data, String type){
		Date current = new Date(new java.util.Date().getTime());
		ResultSet rs = null;
		try {
			this.conn.setAutoCommit(false);
			String stmt = "INSERT INTO recyc_trans"+"(rfid, weight, date, type)"+"VALUES (?,?,?,?)";
			PreparedStatement prestmt= conn.prepareStatement(stmt);
			prestmt.setString(1, id);
			prestmt.setString(2, data);
			prestmt.setDate(3, current);
			prestmt.setString(4, type);
			prestmt.addBatch();
			prestmt.executeBatch();
			rs = prestmt.getResultSet();
			String binstmt = "UPDATE bins SET active = ?  WHERE rfid = ?";
			PreparedStatement prebin= conn.prepareStatement(binstmt);
			prebin.setInt(1, 1);
			prebin.setString(2, id);
			prebin.executeUpdate();
			this.conn.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return rs;
	}

	public String getPoints(String id) throws SQLException{
		ResultSet rs = null;
		String stmt = "SELECT users.points FROM users INNER JOIN bins ON users.uname = bins.uname WHERE bins.rfid = '"+id+"'";
		Statement st = conn.createStatement();
		rs = st.executeQuery(stmt);
		rs.next();
		String points = rs.getString(1);
		return points;
	}

	public boolean setPoints(String id, String points, int type, String data) throws SQLException{
		Double pointsCount = Double.parseDouble(points);
		Double dataInDouble = Double.parseDouble(data);
		switch(type){
		case 1: //MIXED
			pointsCount+= dataInDouble;
			break;
		case 2: //PLASTIC
			pointsCount+= (dataInDouble * 3);
			break;
		case 3: //ALUMINUM
			pointsCount+= (dataInDouble * 3.5);
			break;
		case 4: //GLASS
			pointsCount+= (dataInDouble * .75);
			break;
		case 5: //PAPER
			pointsCount+= (dataInDouble * 3.25);
			break;
		default:
			pointsCount+= dataInDouble;
			break;
		}
		String updatedPoints = pointsCount+"";
		this.conn.setAutoCommit(false);
		String stmt = "UPDATE (bins JOIN users ON bins.userID = users.userID AND bins.rfid= ?) SET points= ?";
		PreparedStatement prestmt= conn.prepareStatement(stmt);
		prestmt.setString(1, id);
		prestmt.setString(2, updatedPoints);
		int succeeded = prestmt.executeUpdate();
		this.conn.commit();
		if(succeeded>0){
			return true;
		}
		return false;
	}

}
