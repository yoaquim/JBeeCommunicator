import java.sql.SQLException;


public class DBConnTester {

	public static void main(String[] args) throws SQLException {
		DBConnector db = DBConnector.getConnector();
		db.connect();
		//Preparing DB for test, Transactions are rfid based
		String id = "123456789ABC";
		String data = "024.5";
		String type = "1";
		db.recycleTransaction(id, data, type);
		//Transaction is already in DB
		String points = db.getPoints(id);
		System.out.println(points);
		db.setPoints(id, points, Integer.parseInt(type), data);
		points = db.getPoints(id);
		System.out.println(points);
	}

}
