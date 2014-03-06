import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;


public class ConnectionWorker extends SwingWorker<Boolean, String> {

	private ConnectionListener callBack;
	private Xbee xbee = new Xbee();
	private QboComm comm;

	public ConnectionWorker(ConnectionListener listener, QboComm comm) {
		this.callBack = listener;
		this.comm = comm;
	}


	protected void process(List<String> chunks) {
		String msg = chunks.get(chunks.size() - 1);
		if (msg.equals("WAITING")) {
			comm.publishError();
		}
	}

	protected Boolean doInBackground() throws Exception {
		boolean isConnected = false;
		this.xbee = QboComm.xbee;
		ArrayList<String> list = new ArrayList<String>();
		if(!isConnected){
			publish("WAITING");
			while(!isConnected){
				list = xbee.getSerialPorts();
				for(String s : list){
					isConnected = xbee.connect(s);
					if(isConnected){
						publish("DONE");
						break;
					}
				}
			}
		}
		return isConnected;
	}
	

	protected void done() {
		boolean check;
		try {
			check = get();
			if (check) {
				comm.removeError();
				callBack.connectionEstablished();
			} else {
				callBack.connectionFailed();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}  
}
