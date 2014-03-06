
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;

import javax.comm.*;

public class Xbee{

	private CommPort commPort;
	private SerialPort serialPort;
	private ArrayList<String> dbList;
	private String hash = "";
	private String ast = "";
	private static SerialWriter writer;
	public static String incoming = "";
	public static String message = "";
	public static int errors = 0;

	public Xbee(){
		super();
	}

	public boolean connect ( String portName ){
		CommPortIdentifier portIdentifier;
		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		} catch (NoSuchPortException e) {
			Xbee.message = "The application could not detect a communicator.\nCheck your connection and restart the application.";
			Xbee.errors++;
			return false;
		}
		try {
			this.commPort = portIdentifier.open(this.getClass().getName(),2000);this.serialPort = (SerialPort) this.commPort;
			this.serialPort.setSerialPortParams(9600,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE); 
			InputStream in = serialPort.getInputStream();
			OutputStream out = serialPort.getOutputStream(); 
			(new Thread(new SerialReader(in))).start();
			Xbee.writer = new SerialWriter(out);
			Xbee.message = "PC Connection Established.";
			return true;
		} 
		catch (UnsupportedCommOperationException | IOException e) {
			Xbee.message="An error occurred while connecting to the communicator. Check the device, then restart the application.";
			Xbee.errors++;
			return false;
		}
		catch(PortInUseException o) {
			Xbee.message="The port is currently in use. Close the program using it, then restart the application.";
			Xbee.errors++;
			return false;
		}
	}

	public void fetch() throws Exception{ 
		boolean errorlog = true;
		Date today = new Date();
		boolean errorfree = true;
		int errors = 0;
		this.hash =  Xbee.incoming.substring(0,1);
		this.ast = Xbee.incoming.substring(Xbee.incoming.length()-1);
		Xbee.incoming = Xbee.incoming.substring(1, Xbee.incoming.length()-2);
		this.dbList = new ArrayList<String> (Arrays.asList( Xbee.incoming.split("\\;")));
		DBConnector db = DBConnector.getConnector();
		if(acknowledge()&& dbList!=null){
			if(db.connect()){
				ArrayList<Integer> indexes= new ArrayList<Integer>();
				for(int i=0; i<dbList.size(); i++){
					String id = dbList.get(i).substring(0, 12);
					String type = dbList.get(i).substring(12, 13);
					String data = dbList.get(i).substring(13);
					Double dataNoBin = Double.parseDouble(data);
					if(dataNoBin>0){
						dataNoBin-=10;
					}
					data = dataNoBin+"";
					try {
						db.setPoints(id, db.getPoints(id), Integer.parseInt(type), data);
						db.recycleTransaction(id, data, type);
						
					} catch (Exception e) {
						errors++;
						errorfree = false;
						File file = new File("DB_Backup.txt");
						FileWriter fw;
						if (!file.exists()) {
							file.createNewFile();
							fw = new FileWriter(file.getAbsoluteFile());
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write("BACKUP\n\n"+today.toString()+"\n");
							bw.close();
							errorlog=false;
						}
						else if(file.exists()&& errorlog){
							fw = new FileWriter("DB_Backup.txt", true);
							BufferedWriter bw = new BufferedWriter(fw);
							bw.write("\n\n"+today.toString()+"\n");
							errorlog = false;
							bw.close();
						}
						fw = new FileWriter("DB_Backup.txt", true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(dbList.get(i)+"\n");
						bw.close();
						indexes.add(i);
					}
				}
				for(int h = indexes.size()-1; h>=0;h--){
					this.dbList.remove((int)indexes.get(h));
				}
				File file = new File("DB_Log.txt");
				FileWriter fw;
				if(dbList.size()!=0){
					if (!file.exists()) {
						file.createNewFile();
						fw = new FileWriter(file.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write("LOG\n\n"+today.toString()+"\n");
						bw.close();
					}
					else if(file.exists()){
						fw = new FileWriter("DB_Log.txt", true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write("\n\n"+today.toString()+"\n");
						bw.close();
					}
					fw = new FileWriter("DB_Log.txt", true);
					BufferedWriter bw = new BufferedWriter(fw);
					for(String s: dbList){
						bw.write(s+"\n");
					}
					bw.close();
				}
				if(errorfree){
					Xbee.message = dbList.size()+" records were succesfully uploaded to the database. A log can be found in the application directory.";
				}
				else{
					Xbee.message = (dbList.size())+" records were succesfully uploaded to the database: view log.\n"
							+errors+" errors were detected: check backup file.";
					Xbee.errors++;
				}
			}
			else{
				File file = new File("DB_Backup.txt");
				FileWriter fw;
				if (!file.exists()) {
					file.createNewFile();
					fw = new FileWriter(file.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write("BACKUP\n\n"+today.toString()+"\n");
					bw.close();
				}
				else if(file.exists()){
					fw = new FileWriter("DB_Backup.txt", true);
					BufferedWriter bw = new BufferedWriter(fw);
					bw.write("\n\n"+today.toString()+"\n");
					bw.close();
				}
				fw = new FileWriter("DB_Backup.txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				for(String s: dbList){
					bw.write(s+"\n");
				}
				bw.close();
				Xbee.message = "Database connection can not be established. A backup file is being created with all records. " +
						"Check application directory.";
				Xbee.errors++;
			}
		}
	}

	public void query(String s) throws Exception{
		writer.run(s);
	}

	public boolean acknowledge() throws Exception{
		for(int i=0; i<this.dbList.size()-1;i++){
			if(this.dbList.get(i).length()!=18){
				Xbee.message = "Data corrupted. Waiting for retransmission...";
				return false;
			}
		}
		if(this.hash.equalsIgnoreCase("#")&&this.ast.equalsIgnoreCase("*")){
			query("ack");
			Xbee.message = "Data recieved. Uploading...";
			Xbee.errors++;
			return true;
		}
		else{
			Xbee.message = "Data corrupted. Waiting for retransmission...";
			Xbee.errors++;
			return false;
		}
	}

	public ArrayList<String> getSerialPorts() throws IOException{
		ArrayList<String> stringList = new ArrayList<String>();
		ArrayList<CommPortIdentifier> portList = new ArrayList<CommPortIdentifier>();
		Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
		while(portEnum.hasMoreElements()){
			portList.add(portEnum.nextElement());
		}
		for(CommPortIdentifier comm : portList){
			if(comm.getPortType()==2){
				portList.remove(comm);
				continue;
			}
			stringList.add(comm.getName());
		}
		return stringList;
	}

	public void printDBList(){
		for(String e : this.dbList){
			System.out.println(e);
		}
	}	
}

