import java.net.*;
import java.io.*;

public class Server {
	
	//private static final int PORT = 1234;
	public static void main(String[] args) throws IOException {
		
		if (args.length != 1) {
	        System.err.println("Usage: java Server <port number>");
	        System.exit(1);
	    }

	        int portNumber = Integer.parseInt(args[0]);
	        boolean listening = true;
	        
	        try (ServerSocket serverSocket = new ServerSocket(portNumber)) { 
	        	System.out.println("Server started on port: " + portNumber);
	            while (listening) {
		            new MultiServerThread(serverSocket.accept()).start();
		        }
		    } catch (IOException e) {
	            System.err.println("Could not listen on port " + portNumber);
	            System.exit(-1);
	        }
		
 		
 		/*int connectionCount = 0;
 		// Bhma 1o: Dhmioyrgia ypodoxhs reymatos ServerSocket sthn thyra PORT
 		ServerSocket connectionSocket = new ServerSocket(PORT);
 		System.out.println("Server started");
 		int count = 0;
 		
 		while (true) { // Anamoni gia pelates syndeshs
 			// Bhma 2o: Anamoni kai apodoxh aithshs syndeshs apo pelath 
 			count ++;
			System.out.println("times = " + count);
		Socket dataSocket = connectionSocket.accept();
			connectionCount++;
			System.out.println("Received " + connectionCount + " request from " + dataSocket.getInetAddress());
			
			MultiServerThread cthread = new MultiServerThread(dataSocket);
			cthread.start();
 		}*/

	}
	
		
}