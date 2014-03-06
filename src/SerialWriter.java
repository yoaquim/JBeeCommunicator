import java.io.*;

public class SerialWriter {
        OutputStream out;
        
        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }
        
        public void run (String s)
        {
            try
            {   byte[] b = s.getBytes();
            	this.out.write(b);               
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }            
        }
    }