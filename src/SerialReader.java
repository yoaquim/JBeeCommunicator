import java.io.IOException;
import java.io.InputStream;

public class SerialReader implements Runnable 
{
	private InputStream in;
	private Xbee xbee = new Xbee();
	public static byte[] buffer;
	public static int len;
	public SerialReader ( InputStream in )
	{
		this.in = in;
	}


	public void run (){
		SerialReader.buffer = new byte[10000000];
		SerialReader.len = 0;
		try {
			while (true){
				while(in.available()==0);
				len += this.in.read(buffer,len,1024000);
				Xbee.incoming =(new String(buffer,0,len));
				Timer.stamp = new java.util.Date().getTime();
				Timer.stop=false;
				if(Xbee.incoming.charAt(Xbee.incoming.length()-1)=='*'){
					Timer.stop = true;
					try {
						Xbee.errors=0;
						xbee.fetch();
						buffer = new byte[10000000];
						len=0;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}