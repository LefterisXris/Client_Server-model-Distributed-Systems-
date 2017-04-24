import java.net.*;
import java.io.*;

/*
 * Κλάση Client.
 * Ο πελάτης της εφαρμογής. Αρχικά πρέπει να ταυτοποιηθεί και στην
 * συνέχεια μπορεί να κάνει λειτουργίες όπως κατάθεση, ανάληψη,
 * ερώτηση υπολοίπου κλπ στον λογαριασμό του.
 */
public class Client {
	
	private static String[] options = {
    		"(I) Tautopoiisi pelati me onoma kai ID",
    		"(A) Analipsi mexri 420 euro tin evdomada",
    		"(K) Katathesi)",
    		"(Y) Enimerosi Ypoloipou",
    		"(E) Eksodos" };
	
	private static String fromServer; // η απάντηση από τον server.
	private static String fromUser;  // το αίτημα από τον πελάτη.
	
	private static final String SHOW_OPTIONS_IDENTIFIER = "Show Options"; // βοηθητική σταθερά.
	//private static final int PORT = 2223;
	
	public static void main(String[] args) throws IOException{
		
		// Αν οι παράμετροι δεν είναι όπως πρέπει.
		if (args.length != 2){
			System.err.println(
					"Usage: java Client <host name> <port number>");
			System.exit(1);
		}
		
		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		
		// Σύνδεση με τον server.
		try( 
			Socket dataSocket = new Socket(hostName, portNumber);
	        PrintWriter out = new PrintWriter(dataSocket.getOutputStream(), true);
	        BufferedReader in = new BufferedReader(
	            new InputStreamReader(dataSocket.getInputStream()));
		){
			BufferedReader stdIn =
	                new BufferedReader(new InputStreamReader(System.in));
            
            // Όσο αποκρίνεται ο server.
            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                
                if (fromServer.contains(SHOW_OPTIONS_IDENTIFIER)){
                	PrintOptions();
                }
                else if (fromServer.equals("CLOSE"))
                    break;
                
                System.out.print("Epilekste leitourgia: ");
                // Διαβάζεται η επιλογή του πελάτη.
                fromUser = stdIn.readLine();
                
                // Επανάληψη μέχρι να δοθεί μια σωστά ορισμένη επιλογή μέσω της μεθόδου CheckLine().
                while(!CheckLine(fromUser)) {
                	System.out.print("Epilekste leitourgia: ");
                	fromUser = stdIn.readLine();
                }
                
                // Στέλνεται το αίτημα του πελάτη στον server.
                out.println(fromUser);
                
                // Εάν έχει επιλέξει έξοδο, γίνεται τερματισμός.
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
	 * Μέθοδος που ελέγχει αν είναι σωστά ορισμένο ένα αίτημα του πελάτη.
	 */
	public static boolean CheckLine(String line){
		int amount;
		
		if(line != null && !line.equals("H") && line.length() < 2 && !line.equals("E") && !line.equals("Y")){
			System.out.println("Sorry.. Your selection is not valid. Insert 'H' for Help");
			return false;
		}
		
		// Ανάλογα με το γράμμα λειτουργίας (ο πρώτος χαρακτήρας) γίνεται και ο κατάλληλος έλεγχος.
		switch (line.charAt(0)){
			case 'I':
				return true;
			// Έλεγχος για Κατάθεση
			case 'K': 
				if (line.charAt(1) != ' ') // Αν ο χρήστης γράψει κολλητά το ποσό, τότε προστίθεται ένα κενό με την μέθοδο.
					addSpaceFromUser();
				amount = Integer.parseInt(line.substring(2,line.length()));
				
				// Έλεγχος για αρνητικό ποσό.
				if (amount <= 0 ) {
					System.out.println("Sorry.. Insert a valid amount.");
				}
				else {
					return true;
				}
				break;
				
			// Έλεγχος για Ανάληψη.
			case 'A':
				if (line.charAt(1) != ' ')
					addSpaceFromUser();
				amount = Integer.parseInt(line.substring(2,line.length()));
				
				// Έλεγχος για Capital Controls.
				if (amount > 420){
					System.out.println("Sorry.. Capital controls.. Try a different amount.");
				}
				else if (amount <= 0){ // έλεγχος αρνητικού ποσού.
					System.out.println("Sorry.. Insert a valid amount.");
				}
				else {
					return true;
				}
				break;
			// στις περιπτώσεις Υπολοίπου, Εξόδου και Βοήθειας (H) δεν απαιτείται κάποιος έλεγχος.
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
	 * Μέθοδος που εκτυπώνει στην οθόνη τις διαθέσιμες επιλογές του χρήστη.
	 */
	public static void PrintOptions(){
		for(String s: options)
			System.out.println(s);        
	}
	
	/*
	 * Μέθοδος που προσθέτει τον κενό χαρακτήρα αμέσως πριν το ποσό.
	 */
	public static void addSpaceFromUser(){
		fromUser = new StringBuilder(new String(fromUser)).insert(1, ' ').toString();
	}
}			

