package directory;

import java.io.*;
import java.nio.file.*;
import static java.nio.file.StandardCopyOption.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FilenameUtils;
import org.mantlik.xdeltaencoder.XDeltaEncoder;

import network.FileTransfer;

public class DirectoryWatcher {

	public DirectoryWatcher() {
		// TODO Auto-generated constructor stub
	}
	
	public void Start() throws IOException, ClassNotFoundException
	{
		// backup file before beginning the monitoring

		File folder = new File("C:/test/client/");
		File[] listOfFiles = folder.listFiles(); 
		
		if (listOfFiles != null)
		{
			for (int i = 0; i < listOfFiles.length; i++) {		 
			   if (listOfFiles[i].isFile()){

				   // copy file to client_backup folder

				   String source = listOfFiles[i].getAbsolutePath();
				   String dest = "C:/test/client-backup/" + listOfFiles[i].getName(); 
				   Path sp = Paths.get(source);
				   Path dp = Paths.get(dest);
				   Files.copy(sp, dp, REPLACE_EXISTING);

				   //send to server

				   new FileTransfer().sendFile(dest, "full");
			   }
			}
		}
		
		// start directory watcher service

		Path path = Paths.get("C:/test/client/");
		WatchService watchService = FileSystems.getDefault().newWatchService();
		path.register(watchService,
					  StandardWatchEventKinds.ENTRY_CREATE,
					  StandardWatchEventKinds.ENTRY_DELETE,
					  StandardWatchEventKinds.ENTRY_MODIFY);
		
		boolean done = false;
		
		while(!done){
			try{
				WatchKey watchKey = watchService.poll(60, TimeUnit.SECONDS);
				if (watchKey != null)
				{
					List<WatchEvent<?>> events = watchKey.pollEvents();
					for(WatchEvent event : events){
						WatchEvent<Path> e = (WatchEvent<Path>) event; 
						WatchEvent.Kind<Path> kind = e.kind();

						// get the Path object out of the event object
						Path p = e.context();

						// filename
						String fileName = p.getFileName().toString();

						// file path
						String filePath = "C:/test/client/" + fileName;

						// check file extension, add more extension into ./Config/config.properties
						String acceptedExt = "txt";
						String ext = FilenameUtils.getExtension(fileName);

						// ignore what is not in the "watch_extension".
						if (acceptedExt.indexOf(ext) < 0)
							continue;

						// modify, send diff
						if ("ENTRY_MODIFY".equals(kind.name())){
							String[] args = new String[3];					
							args[0] = "C:/test/client-backup/" + fileName;
							args[1] = filePath;
							args[2] = "C:/test/delta/" + fileName;

						///////////////////////find delta	
							XDeltaEncoder.main(args);

						//send delta	
							new FileTransfer().sendFile(args[2], "partial");

							// remove delta
							Files.delete(Paths.get(args[2]));
						}

						// new file send full content to server
						if ("ENTRY_CREATE".equals(kind.name())){

							// copy to backup
							String sourcePath = "C:/test/client/" + p.getFileName().toString();
							String backupPath = "C:/test/client-backup/" + p.getFileName().toString();
							Files.copy(Paths.get(sourcePath), Paths.get(backupPath), StandardCopyOption.REPLACE_EXISTING);

							// send to server in full
							new FileTransfer().sendFile(backupPath, "full");						
						}
						
						if ("ENTRY_DELETE".equals(kind.name())){

							// delete in backup
							Files.delete(Paths.get("C:/test/client-backup/" + p.getFileName().toString()));

							// send delete command to server
							new FileTransfer().deleteFile("C:/test/client/"+ p.getFileName().toString());						
						}
					}
					if(!watchKey.reset()){
						// log message out
					}
				}
			}catch(InterruptedException e){
				Thread.currentThread().interrupt();
			}
		}
	}
}
