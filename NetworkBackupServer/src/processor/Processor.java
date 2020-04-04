package processor;

import java.net.*;  
import java.io.*; 
import java.nio.file.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.mantlik.xdeltaencoder.XDeltaEncoder;
import java.util.zip.*;
import network.TransferObject;
import crypt.Crypt;

public class Processor extends Thread  {

	public Processor() {

	}
	
	private Socket socket;
    	private int clientNumber;
    	public Processor(Socket socket, int clientNumber) {
        this.socket = socket;
        this.clientNumber = clientNumber;
        log("New connection with client# " + clientNumber + " at " + socket);
    }

    public void run() {
        try {
        	GZIPOutputStream out = new   GZIPOutputStream(socket.getOutputStream());
        	            
    ////////////////////encrypt
            String mykey ="0123456789abcdef"; 
            SecretKey key = new SecretKeySpec(mykey.getBytes(), "AES"); 
            Crypt encrypter = new Crypt(key);
            byte[] readbuff=new byte[1024];

            // Send an start message to the client.
            TransferObject to = new TransferObject();
            String stringsetMessage=encrypter.encrypt("##RECV##");  
            to.setMessage(stringsetMessage);
            byte[] writebuff=new byte[1024];
	    writebuff=TransferObject.serialize(to);
            out.write(writebuff);
            out.finish();
            while (true) {
            	GZIPInputStream in = new GZIPInputStream(socket.getInputStream());
            	in.read(readbuff);
            	TransferObject tobj = (TransferObject)TransferObject.deserialize(readbuff);
            	String stringgetMessage=encrypter.decrypt(tobj.getMessage());
                String stringgetContentType=encrypter.decrypt(tobj.getContentType());
                String stringgetFileName=encrypter.decrypt(tobj.getFileName());
                byte[] bytegetFileContent=encrypter.decrypt(tobj.getFileContent());
                
                if (tobj == null || stringgetMessage.equals("##ENDRECV##"))
                	break;
            
		    // save input into file
                if ("full".equals(stringgetContentType)){
	                FileOutputStream fo = new FileOutputStream("C:/test/server1/" + stringgetFileName);
	                fo.write(bytegetFileContent);                
	                fo.close();
                }
                
                if ("partial".equals(stringgetContentType)){
                
			// save the delta, call the XDeltaDecode to combine the existing & deta into new file put in server_tmp.
                	String deltaPath = "C:/test/server1_delta/" + stringgetFileName;
                	FileOutputStream fo = new FileOutputStream(deltaPath);
	                fo.write(bytegetFileContent);                
	                fo.close();
	                
	                String source = "C:/test/server1/" + stringgetFileName;
	                String target = "C:/test/server1_tmp/" + stringgetFileName;
	                String delta = deltaPath;
	                
	                // call XDeltaDecode tool
	                String[] args = new String[4];
	                args[0] = "-d";
					args[1] = source;
					args[2] = delta;
					args[3] = target;
					
					XDeltaEncoder.main(args);
					
					// There are problems with file handle, because the XDeltaEncoder is holding the files. 
					
					// copy target in server_tmp to source(server_backup).
					Path ptarget = Paths.get(target); 
					Files.copy(ptarget, Paths.get(source), StandardCopyOption.REPLACE_EXISTING);

					// delete target in server_tmp
					Files.delete(ptarget);
					
					// remove delta
					Files.delete(Paths.get(args[2]));
                }
                
                if ("##DELETE##".equals(stringgetMessage)) {
                	// delete the file
                	Path p = Paths.get("C:/test/server1/" + stringgetFileName);
                	Files.delete(p);
                }
                in.close();
            }
        } catch (IOException e) {
            log("Error handling client# " + clientNumber + ": " + e);
        } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
        	log("Error receiving the object: " + e);
		} finally {
            try {
                socket.close();
            } catch (IOException e) {
                log("Couldn't close a socket, what's going on?");
            }
            log("Connection with client# " + clientNumber + " closed");
        }
    }

    /**
     * Logs a simple message.  In this case we just write the
     * message to the standard output.
     */
    private void log(String message) {
        System.out.println(message);
    }

}
