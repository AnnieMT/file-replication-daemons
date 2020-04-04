package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.zip.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Connector {
	protected String serverIp;
	protected int serverPort;
		    
    protected GZIPInputStream in = null;
    protected GZIPOutputStream out = null;
    
    protected Socket socket = null;
	
	public String getServerIp() {
		return serverIp;
	}
	
	public int getServerPort() {
		return serverPort;
	}
	
	public GZIPInputStream getIn() {
	//public ObjectInputStream getIn() {
		return in;
	}

	public GZIPOutputStream getOut() {
	//public ObjectOutputStream getOut() {
		return out;
	}
	
	public Connector(String serverIp, int port) {
		// TODO Auto-generated constructor stub
		this.serverIp = serverIp;
		this.serverPort = port;
	}
	
	public Socket getSocket()
	{
		return socket;
	}
	
	public void Connect() throws UnknownHostException, IOException
	{
		// Make connection and initialize streams
		socket = new Socket(serverIp, serverPort);
		out = new GZIPOutputStream(socket.getOutputStream());

		in = new GZIPInputStream(socket.getInputStream());
	}

	public void Flush() throws UnknownHostException, IOException{
	}
	
	public void Close() throws IOException{
		
		if (out != null)
			out.close();
		    
		if (in != null)
			in.close();
		    
		if (socket != null)
			socket.close();
	}
}
