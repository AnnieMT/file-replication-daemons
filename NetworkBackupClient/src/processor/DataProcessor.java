package processor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.*;  
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.zip.*;
import crypt.Crypt;

import network.Connector;
import network.TransferObject;

public class DataProcessor {
	
	protected Connector conn;
	protected String filePath;
	protected String contentType;
	/**
	 * @throws IOException 
	 * 
	 */
	public DataProcessor(Connector conn, String filePath, String contentType) {
		// TODO Auto-generated constructor stub
		this.conn = conn;
		this.filePath = filePath;
		this.contentType = contentType;		
	}
	
	public void start() throws ClassNotFoundException {		
		
		GZIPOutputStream out = conn.getOut();
		GZIPInputStream in = conn.getIn();

////////////////////encrypt
String mykey ="0123456789abcdef"; 
SecretKey key = new SecretKeySpec(mykey.getBytes(), "AES"); 
Crypt decrypter = new Crypt(key);
byte[] readbuff=new byte[1024];

		try{
			if (in != null) {
				
				in.read(readbuff);
				TransferObject resp = (TransferObject)TransferObject.deserialize(readbuff); 
				String stringgetMessage=decrypter.decrypt(resp.getMessage());
				while (resp != null && !stringgetMessage.equals("##RECV##"))
				{
					System.out.println(resp.getMessage());
					in.read(readbuff);
					resp = (TransferObject)TransferObject.deserialize(readbuff);
				}
			}
			Path fp = Paths.get(filePath);
			
			if (!"delete".equals(contentType)){

				// send file
				TransferObject to = new TransferObject();
				Crypt encrypter = new Crypt(key);
				to.setMessage(encrypter.encrypt("##DATA##"));			
				to.setFileName(encrypter.encrypt(fp.getFileName().toString()));
				to.setFileContent(encrypter.encrypt(Files.readAllBytes(fp)));
				to.setContentType(encrypter.encrypt(contentType));
				byte[] writebuff=new byte[1024];
				writebuff=TransferObject.serialize(to);
				out.write(writebuff);
				out.finish();
				
				to = new TransferObject();
				encrypter = new Crypt(key);
				to.setMessage(encrypter.encrypt("##ENDRECV##"));
				writebuff=TransferObject.serialize(to);
				out.write(writebuff);
				out.finish();
			}
			else {
				TransferObject to = new TransferObject();
				Crypt encrypter = new Crypt(key);
				to.setMessage(encrypter.encrypt("##DELETE##"));			
				to.setFileName(encrypter.encrypt(fp.getFileName().toString()));
				byte[] writebuff=new byte[1024];
				writebuff=TransferObject.serialize(to);
				out.write(writebuff);
				out.finish();
			}
			
		} catch (IOException e) {
			
		}
	}
}
