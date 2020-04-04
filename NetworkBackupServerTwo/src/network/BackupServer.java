package network;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import crypt.Crypt;

import processor.Processor;

public class BackupServer {

	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		
		System.out.println("The backup server is running.");
        int clientNumber = 0;
        
        ServerSocket listener = new ServerSocket(9394);
        
		try {

//////////////////////////////////////////////////////SEND FILES to CLIENT

File f = null;
File[] paths;
try{      
///////
	String mykey ="0123456789abcdef"; 
    SecretKey key = new SecretKeySpec(mykey.getBytes(), "AES"); 
    Crypt encrypter = new Crypt(key);	
	
f = new File("c:/test/server2");
paths = f.listFiles();
Socket tempsocket = listener.accept();
OutputStream os = tempsocket.getOutputStream();  
for(File path:paths){

//prints file and directory paths
System.out.println(path);
byte[] mybytearray = new byte[(int) path.length()];  
FileInputStream fis = new FileInputStream(path);  
BufferedInputStream bis = new BufferedInputStream(fis);
DataInputStream dis = new DataInputStream(bis);     
dis.readFully(mybytearray, 0, mybytearray.length);  

//Sending file name and file size
DataOutputStream dos = new DataOutputStream(os);     
dos.writeUTF(encrypter.encrypt(path.getName())); 
dos.writeUTF(encrypter.encrypt(String.valueOf(mybytearray.length))); 
dos.writeUTF(encrypter.encrypt(String.valueOf(path.lastModified())));
dos.write(mybytearray, 0, mybytearray.length);     
dos.flush();
dis.close();
}
os.close();
}catch(Exception e){e.printStackTrace();}          
System.out.println("FFFFFFFFF");

//////////////////////////////////////////////////////
            while (true) {
                new Processor(listener.accept(), clientNumber++).start();
            }
        } finally {
            listener.close();
        }
	}

}
