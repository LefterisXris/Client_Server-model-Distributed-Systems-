import java.net.*;
import java.io.*;

public class MultiServerThread extends Thread {
    private Socket socket = null;
    private int balance;
	private int weekly_amount;
	
    

    public MultiServerThread(Socket socket) {
        super("MultiServerThread");
        this.socket = socket;
        
        balance = 550;
		weekly_amount = 0;
    }
    
    public void run() {

        try (
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(
                    socket.getInputStream()));
        ) {
            String inputLine, outputLine, protocolResponse;
            ATMProtocol atmP = new ATMProtocol();
            protocolResponse = atmP.processInput(null);
            outputLine = protocolResponse;
            out.println(protocolResponse); // Current state is: Show Options
            
            int amount; // το ποσό για ανάληψη ή κατάθεση.

            while ((inputLine = in.readLine()) != null) {
            	System.out.println("Received message from client " + currentThread().getName() + " :" + inputLine);
            	
            	protocolResponse = atmP.processInput(inputLine);
            	outputLine = protocolResponse;
                // prepei na epeksergasteis analoga to output line.
            	char state = protocolResponse.charAt(18);
            	
            	
            	switch (state){
            		case 'S':
            			outputLine += "Show Options";
            			break;
            		case 'A':
            			amount = Integer.parseInt(inputLine.substring(2, inputLine.length()));
						if (amount > balance){
							outputLine += "Not enough balance.";
						}
						else if (weekly_amount+amount > 420) {
							out.println("You can't take more than 420 per week. You have already take " + weekly_amount);							
						}
						else {
							balance -= amount;
							weekly_amount += amount;
							outputLine += " Transaction completed. New balance is: " + balance;
						}
            			break;
            		case 'K':
            			amount = Integer.parseInt(inputLine.substring(2, inputLine.length()));
						balance += amount;
						outputLine += " Transaction completed. New balance is: " + balance;
            			break;
            		case 'Y':
            			outputLine += " Current balance is: " + balance; 
            			break;
            		default:
            			break;
            	
            	}
            	
                                           
                if (protocolResponse.charAt(18) == 'E'){
                	out.println("CLOSE");
                    break;
                }
                    
                out.println(outputLine);
            }
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
