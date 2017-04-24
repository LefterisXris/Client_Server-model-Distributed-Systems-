import java.net.*;
import java.io.*;

/*
 * ����� Client.
 * � ������� ��� ���������. ������ ������ �� ������������ ��� ����
 * �������� ������ �� ����� ����������� ���� ��������, �������,
 * ������� ��������� ��� ���� ���������� ���.
 */
public class Client {
	
	private static String[] options = {
    		"(I) Tautopoiisi pelati me onoma kai ID",
    		"(A) Analipsi mexri 420 euro tin evdomada",
    		"(K) Katathesi)",
    		"(Y) Enimerosi Ypoloipou",
    		"(E) Eksodos" };
	
	private static String fromServer; // � �������� ��� ��� server.
	private static String fromUser;  // �� ������ ��� ��� ������.
	
	private static final String SHOW_OPTIONS_IDENTIFIER = "Show Options"; // ��������� �������.
	//private static final int PORT = 2223;
	
	public static void main(String[] args) throws IOException{
		
		// �� �� ���������� ��� ����� ���� ������.
		if (args.length != 2){
			System.err.println(
					"Usage: java Client <host name> <port number>");
			System.exit(1);
		}
		
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		
		// ������� �� ��� server.
		try( 
			Socket dataSocket = new Socket(hostName, portNumber);
	        PrintWriter out = new PrintWriter(dataSocket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(
	            new InputStreamReader(dataSocket.getInputStream()));
		){
			BufferedReader stdIn =
	                new BufferedReader(new InputStreamReader(System.in));
            
            // ��� ����������� � server.
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                
                if (fromServer.contains(SHOW_OPTIONS_IDENTIFIER)){
                	PrintOptions();
                }
                else if (fromServer.equals("CLOSE"))
                    break;
                
                System.out.print("Epilekste leitourgia: ");
                // ���������� � ������� ��� ������.
                fromUser = stdIn.readLine();
                
                // ��������� ����� �� ����� ��� ����� �������� ������� ���� ��� ������� CheckLine().
                while(!CheckLine(fromUser)) {
                	System.out.print("Epilekste leitourgia: ");
                	fromUser = stdIn.readLine();
                }
                
                // ��������� �� ������ ��� ������ ���� server.
                out.println(fromUser);
                
                // ��� ���� �������� �����, ������� �����������.
                if(fromUser.charAt(0) == 'E'){
                	dataSocket.close();
        			System.out.println("Data Socket closed");
        			System.exit(1);
                }
             
            } // while
            
		} catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }			
	
	} // main
	
	/*
	 * ������� ��� ������� �� ����� ����� �������� ��� ������ ��� ������.
	 */
	public static boolean CheckLine(String line){
		int amount;
		
		if(line != null && !line.equals("H") && line.length() < 2 && !line.equals("E") && !line.equals("Y")){
			System.out.println("Sorry.. Your selection is not valid. Insert 'H' for Help");
			return false;
		}
		
		// ������� �� �� ������ ����������� (� ������ ����������) ������� ��� � ���������� �������.
		switch (line.charAt(0)){
			case 'I':
				return true;
			// ������� ��� ��������
			case 'K': 
				if (line.charAt(1) != ' ') // �� � ������� ������ ������� �� ����, ���� ����������� ��� ���� �� ��� ������.
					addSpaceFromUser();
				amount = Integer.parseInt(line.substring(2,line.length()));
				
				// ������� ��� �������� ����.
				if (amount <= 0 ) {
					System.out.println("Sorry.. Insert a valid amount.");
				}
				else {
					return true;
				}
				break;
				
			// ������� ��� �������.
			case 'A':
				if (line.charAt(1) != ' ')
					addSpaceFromUser();
				amount = Integer.parseInt(line.substring(2,line.length()));
				
				// ������� ��� Capital Controls.
				if (amount > 420){
					System.out.println("Sorry.. Capital controls.. Try a different amount.");
				}
				else if (amount <= 0){ // ������� ��������� �����.
					System.out.println("Sorry.. Insert a valid amount.");
				}
				else {
					return true;
				}
				break;
			// ���� ����������� ���������, ������ ��� �������� (H) ��� ���������� ������� �������.
			case 'Y':
				return true;
			case 'E':
				return true;
			case 'H':
				PrintOptions();
				return false;
			default:
				System.out.println("Sorry.. Your selection is not valid. Insert 'H' for Help");
				PrintOptions();
				break;
				
		} // switch
			
		return false;
	} // CheckLine
	
	/*
	 * ������� ��� ��������� ���� ����� ��� ���������� �������� ��� ������.
	 */
	public static void PrintOptions(){
		for(String s: options)
			System.out.println(s);        
	}
	
	/*
	 * ������� ��� ��������� ��� ���� ��������� ������ ���� �� ����.
	 */
	public static void addSpaceFromUser(){
		fromUser = new StringBuilder(new String(fromUser)).insert(1, ' ').toString();
	}
}			

