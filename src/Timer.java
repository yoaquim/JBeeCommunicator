
public class Timer implements Runnable{

	public static long stamp = 0;
	public static boolean reset = false;
	public static boolean stop = true;
	public static final Timer CLOCK = new Timer();
	private long marker = 0;

	public void run() {
		while(true){
			if(!stop){
				marker = new java.util.Date().getTime();
				if(marker-stamp>5000){
					stop = true;
					reset=true;
					Xbee.message = "The connection to the other communicator timed out. Please restart the transmission.";
					Xbee.errors++;
					marker = new java.util.Date().getTime();
					stamp = new java.util.Date().getTime();
					SerialReader.buffer = new byte[10000000];
					SerialReader.len=0;
				}
			}
		}
	}

}
