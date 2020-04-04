package main;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.zip.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import crypt.Crypt;
import directory.DirectoryWatcher;

public class NetworkBackupClient {

	public NetworkBackupClient() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException {
		
		try {
			
//////////////////////////////////////////////////////receive from server

//server1 
try{
	File f1= new File("c:/test/client_fromserver/s1/");			
	for(File file: f1.listFiles()) file.delete();	
 	
	String mykey ="0123456789abcdef"; 
 	SecretKey key = new SecretKeySpec(mykey.getBytes(), "AES"); 
  	Crypt encrypter = new Crypt(key);	

	String server1Ip = "127.0.0.1";
	int server1Port = 9393;
	Socket csocket = new Socket(server1Ip, server1Port); 
	int bytesRead;   

	InputStream inn = csocket.getInputStream(); 
	DataInputStream clientData = new DataInputStream(inn);   
	while (inn != null){
		String fn=encrypter.decrypt(clientData.readUTF()); 
		File fileName = new File("c:/test/client_fromserver/s1/"+fn);     
		OutputStream output = new FileOutputStream(fileName);     
		long size = Long.valueOf(encrypter.decrypt(clientData.readUTF()));
		long server1time = Long.valueOf(encrypter.decrypt(clientData.readUTF()));
		File fcheck = new File("c:/test/client/"+fn);
		byte[] buffer = new byte[1024];     
		while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)     
		{     
			output.write(buffer, 0, bytesRead); 
			size -= bytesRead;   
		}  

		// Closing the FileOutputStream handle  
		output.close();
		if(!fcheck.exists()||(fcheck.lastModified()<server1time)){
			fileName.renameTo(new File("c:/test/client/" + fileName.getName()));
			fileName.setLastModified(server1time);
		}
	} 
	csocket.close();
	File ff1= new File("c:/test/client_fromserver/s1/");			
	for(File file: ff1.listFiles()) file.delete();	
}
catch(Exception e){;}

//server2 
try{
	File f2= new File("c:/test/client_fromserver/s2/");			
	for(File file: f2.listFiles()) file.delete();	
	
	String mykey ="0123456789abcdef"; 
	SecretKey key = new SecretKeySpec(mykey.getBytes(), "AES"); 
  	Crypt encrypter = new Crypt(key);	

	String server2Ip = "127.0.0.2";
	int server2Port = 9394;
	Socket csocket = new Socket(server2Ip, server2Port); 
	int bytesRead;   

	InputStream inn = csocket.getInputStream();   
	DataInputStream clientData = new DataInputStream(inn);   
	while (inn != null){ 
		String fn=encrypter.decrypt(clientData.readUTF()); 
		File fileName = new File("c:/test/client_fromserver/s2/"+fn);     
		OutputStream output = new FileOutputStream(fileName);     
		long size = Long.valueOf(encrypter.decrypt(clientData.readUTF()));
		long server2time = Long.valueOf(encrypter.decrypt(clientData.readUTF()));
		File fcheck = new File("c:/test/client/"+fn);    
		byte[] buffer = new byte[1024];    
		while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)     
		{     
			output.write(buffer, 0, bytesRead); 
			size -= bytesRead;   
		}  
		output.close();
		if(!fcheck.exists()||(fcheck.lastModified()<server2time)){
			fileName.renameTo(new File("c:/test/client/" + fileName.getName()));
			fileName.setLastModified(server2time);
		}
	} 
	csocket.close();
	File ff2= new File("c:/test/client_fromserver/s2/");			
	for(File file: ff2.listFiles()) file.delete();
}
catch(Exception e){;}

new DirectoryWatcher().Start();			
		} 
	catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
