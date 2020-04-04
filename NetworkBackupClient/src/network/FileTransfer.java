package network;

import java.io.IOException;

import processor.DataProcessor;

public class FileTransfer {

	public FileTransfer() {
		
	}
	
	public void sendFile(String filePath, String contentType) throws IOException, ClassNotFoundException {
		String server1Ip = "127.0.0.1";
		int server1Port = 9393;
		String server2Ip = "127.0.0.2";
		int server2Port = 9394;
		
		try {
			Connector conn1 = new Connector(server1Ip, server1Port);
			conn1.Connect();
           
			// do stuff with the in/out stream gotten from conn.
			DataProcessor processor1 = new DataProcessor(conn1, filePath, contentType);

			//DataProcessor processor2 = new DataProcessor(conn2, filePath, contentType);
			processor1.start();

			//processor2.start();
			conn1.Close();
			
		} catch (IOException e) {
			
		} finally {
			
		}
		try {
			Connector conn2 = new Connector(server2Ip, server2Port);
			
	                conn2.Connect();	 			

			// do stuff with the in/out stream gotten from conn.
			DataProcessor processor2 = new DataProcessor(conn2, filePath, contentType);

			processor2.start();
			conn2.Close();
			
		} catch (IOException e) {
			
		} finally {
			
		}
	}
	
	public void deleteFile(String filePath) throws ClassNotFoundException, IOException{
		String server1Ip = "127.0.0.1";
		int server1Port = 9393;
		String server2Ip = "127.0.0.2";
		int server2Port = 9394;
		
		try {
			Connector conn1 = new Connector(server1Ip, server1Port);
			conn1.Connect();

			// do stuff with the in/out stream gotten from conn.
			DataProcessor processor1 = new DataProcessor(conn1, filePath, "delete");

			//DataProcessor processor2 = new DataProcessor(conn2, filePath, "delete");
			processor1.start();

			//processor2.start();
			conn1.Close();
			
		} catch (IOException e) {
			
		} finally {
			
		}
		try {
			Connector conn2 = new Connector(server2Ip, server2Port);
		        conn2.Connect();	 			

			// do stuff with the in/out stream gotten from conn.
			DataProcessor processor2 = new DataProcessor(conn2, filePath, "delete");

			//processor1.start();
			processor2.start();
			conn2.Close();
			
		} catch (IOException e) {
			
		} finally {
			
		}
	}
}
