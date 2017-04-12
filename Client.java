import java.net.*;
import java.io.*;

public class Client {
	
	private static String[] options = {
    		"(I) Tautopoiisi pelati me onoma kai ID",
    		"(A) Analipsi mexri 420 euro tin evdomada",
    		"(K) Katathesi)",
    		"(Y) Enimerosi Ypoloipou",
    		"(E) Eksodos" };
	
	//private static final int PORT = 2223;
	
	public static void main(String[] args) throws IOException
	{
		// Αν οι παράμετροι δεν είναι όπως πρέπει.
		if (args.length != 2){
			System.err.println(
					"Usage: java Client <host name> <port number>");
			System.exit(1);
		}
		
		String hostName = args[0]; //"localhost";
		int portNumber = Integer.parseInt(args[1]);
		
		try( 
			Socket dataSocket = new Socket(hostName, portNumber);
	        PrintWriter out = new PrintWriter(dataSocket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(
	            new InputStreamReader(dataSocket.getInputStream()));
		){
			BufferedReader stdIn =
	                new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;
            
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.charAt(18) == 'S'){
                	PrintOptions();
                	
                }
                else if (fromServer.equals("CLOSE"))
                    break;
                
                System.out.print("Epilekste leitourgia: ");
                fromUser = stdIn.readLine();
                while(!CheckLine(fromUser)) {
                	System.out.print("Epilekste leitourgia: ");
                	fromUser = stdIn.readLine();
                }
                
                
                out.println(fromUser);
                
                if(fromUser.charAt(0) == 'E'){
                	dataSocket.close();
        			System.out.println("Data Socket closed");
        			System.exit(1);
                }
               /* if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }*/
            }
            
		} catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
		
		
		/*// Authentikopoiisi xristi me aplo Echo	
		System.out.print("Insert I, your name and your Id (example: I user1 55): ");
		String msg = user.readLine();	
		int attempts = 1;
		while (!msg.equals("CLOSE") && attempts <= 3){
			// apomononei to proto gramma gia tin epilogi leitourgias
			String option = msg.substring(0,1);
			if (!option.equals("I")) {
				System.out.print("Wrong syntax! Please use this syntax instead (example: I user1 55): ");
			}
			else {
				// Bhma 3o: Eggrafh mhnymatos sto diakomisth 
				out.println(msg);
				// Bhma 3o: Anagnosi mhnymatos apo to diakomisth
				String response = in.readLine();
				String r = response.substring(0,1);
				if (r.equals("Y")){
					// petixenei otan o server apantisei thetika (Y) kai sinexizei stis alles epiloges poy exei.
					System.out.println("Authentication succedeed: " + response + " You can procceed." + "\n\n");
					break;
				}
				else {
					System.out.println("Authentication failed: " + response + "  Try again..." + "\n\n");
					
				}
				
			}
						
			System.out.print("Insert I, your name and your Id (example: I user1 55): ");
			msg = user.readLine();
			attempts++;
		}
		
		// apotixia 
		if (attempts >= 3){
			System.out.println("Kanate " + attempts + " apotiximenes prospatheies..\n Den mporeite na sinexisete.. Ginete termatismos...");
			// Bhma 4o: Kleisimo ypodoxhs
			dataSocket.close();
			System.out.println("Data Socket closed");
			System.exit(1);
		}
		
		// Dimiourgia menu me leitourgies gia pelati.
		
		System.out.println("(A) Analipsi mexri 420 euro tin evdomada");
		System.out.println("(K) Katathesi");
		System.out.println("(Y) Enimerosi Ypoloipou");
		System.out.println("(E) Eksodos");
		
		// Anagnosi mhnymatos apo to pliktrologio
		System.out.print("Eisagete to antistoixo gramma kai tin leitourgia poy thelete: ");
		msg = user.readLine();
		
		// apomononei to proto gramma gia tin epilogi leitourgias
		String option = msg.substring(0,1);
		String serverResponse = "";
				
		while(!option.equals("E")) {
			System.out.println("input is " + option);
			switch(option){
				case "I":
					// Bhma 3o: Eggrafh mhnymatos sto diakomisth 
					out.println(msg);
					// Bhma 3o: Anagnosi mhnymatos apo to diakomisth
					System.out.println("Received message from server: " + in.readLine());
					break;
				case "A":
					int amount = Integer.parseInt(msg.substring(2, msg.length()));
					if (amount > 420){
						System.out.println("Sorry.. Capital controls.. Try a different amount.");
					}
					else if (amount <= 0 ){
						System.out.println("Sorry.. Insert a valid amount.");
					}
					else {
						System.out.println("Ok good amount..");
						out.println(msg);
						serverResponse = in.readLine();
						if (serverResponse.substring(0,1).equals("Y") ){
							System.out.println("Transaction completed. New balance: " + serverResponse.substring(2,serverResponse.length()));
						}
						else{
							System.out.println("Transaction cannot be completed: " + serverResponse.substring(2,serverResponse.length()));
						}
					}
					break;
				case "K":
					out.println(msg);
					serverResponse = in.readLine();
					if (serverResponse.substring(0,1).equals("Y")){
						System.out.println("Transaction completed. New balance: " + serverResponse.substring(2,serverResponse.length()));
					}
					else {
						System.out.println("Something went wrong!");
					}
					break;
				case "Y":
					out.println(msg);
					serverResponse = in.readLine();
					if (serverResponse.substring(0,1).equals("Y")){
						System.out.println("Your current balance is: " + serverResponse.substring(2,serverResponse.length()));
					}
					else {
						System.out.println("Something went wrong!");
					}
					break;
				default:
					System.out.println("Something went wrong!");
					break;
			
			}
			
			// Bhma 3o: Eggrafh mhnymatos sto diakomisth 
			out.println(msg);
			
			// Bhma 3o: Anagnosi mhnymatos apo to diakomisth
			System.out.println("Received message from server: " + in.readLine());
			
			
			// Anagnosi epomenoy mhnymatos apo to pliktrologio
			System.out.print("Eisagete to antistoixo gramma kai tin leitourgia poy thelete: ");
			msg = user.readLine();
			option = msg.substring(0,1);
		}	
		
		out.println(msg);
		
		// Bhma 4o: Kleisimo ypodoxhs
		dataSocket.close();
		System.out.println("Data Socket closed");*/
	}
	
	public static boolean CheckLine(String line){
		int amount;
		switch (line.charAt(0)){
			case 'I':
				return true;
			case 'K':
				amount = Integer.parseInt(line.substring(2,line.length()));
				if (amount <= 0 ) {
					System.out.println("Sorry.. Insert a valid amount.");
				}
				else {
					return true;
				}
				break;
			case 'A':
				amount = Integer.parseInt(line.substring(2,line.length()));
				if (amount > 420){
					System.out.println("Sorry.. Capital controls.. Try a different amount.");
				}
				else if (amount <= 0){
					System.out.println("Sorry.. Insert a valid amount.");
				}
				else {
					return true;
				}
				break;
			case 'Y':
				return true;
			case 'E':
				return true;
			default:
				PrintOptions();
				break;
		}
		
		
		return false;
	}
	
	public static void PrintOptions(){
		for(String s: options)
			System.out.println(s);        
	}
}			

