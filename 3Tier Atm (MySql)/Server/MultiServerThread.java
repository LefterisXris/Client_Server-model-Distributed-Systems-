import java.net.*;
import java.sql.SQLException;
import java.io.*;

/*
 * Κλάση MultiServerThread.
 * Είναι η κλάση που εξυπηρετεί τους πελάτες. Πραγματοποιείται σύνδεση με την ΒΔ
 * και από εκεί αντλούνται τα διάφορα στοιχεία του κάθε πελάτη.
 */
public class MultiServerThread extends Thread {
	private Socket socket = null;

	// Αρχικοποίηση μερικών μηνυμάτων.
	private String[] States = { "Show Options. ", "Identification. ", "Analipsi. ", "Katathesi. ", "Ypoloipo. ", "Eksodos. " };
	private String StateString = "Current State is: ";
	private String[] answers = { "User Authenticated.", "Cannot authenticate user." };
	private String authenticationWarning = "You must authenticate yourself first. (I)";
	private boolean authenticated = false;
	
	// Μεταβλητές στις οποίες κρατώνται μερικά στοιχεία για μεγαλύτερη ταχύτητα.
	private String cName;
	private int cId;
	private double cBalance; 
	
	/*
	 * Κατασκευαστής.
	 */
	public MultiServerThread(Socket socket) {
		//super("MultiServerThread");
		this.socket = socket;		
	}

	/*
	 * Η κυρίως λειτουργία του thread.
	 */
	public void run() {

		try (
				// Αρχικά ανοίγονται οι δίαυλοι για την επικοινωνία με τον πελάτη.
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
			// Μέσα στο try.
			
			String inputLine, outputLine;

			// Σύνδεση με τη ΒΔ
			ConnectToDB conn = new ConnectToDB();
			
			if (!conn.doConnection())
				System.exit(1);
			

			// Αρχικά εμφάνιση λειτουργιών.
			outputLine = StateString + States[0];
			
			// Προώθηση της απάντησης στον πελάτη.
			out.println(outputLine);

			int amount; // Το ποσό για ανάληψη ή κατάθεση.

			// Για όσο ο πελάτης θα ρωτάει κάτι, το αίτημά του θα διαχειρίζεται
			// και θα λαμβάνει πίσω την αντίστοιχη απάντηση. 
			while ((inputLine = in.readLine()) != null) {
				System.out.println("Received message from client " + currentThread().getName() + " :" + inputLine);

				// Απομονώνεται η επιλογή του χρήστη (Ι,Κ,Α,Υ,Ε). 
				char input = inputLine.charAt(0);
				
				switch (input){
				
				// Ενέργεια για την Ταυτοποίηση.
				case 'I':
					outputLine = StateString + States[1];
					String[] customerCredentials = splitString(inputLine); // Απομονώνεται το όνομα και το id του χρήστη.
					if (customerCredentials.length != 3){ // Αν για κάποιο λόγο δεν είναι 3 τα αλφαριθμητικά (επιλογή, όνομα, id).
						outputLine += answers[1]; // cannot authenticate message;
						break;
					}
					try { // Με βάση το όνομα και το id που έδωσε ο χρήστης, γίνεται αναζήτη στη βάση.
						if (conn.identifyCustomer(customerCredentials[1], customerCredentials[2]) ){
							authenticated = true;
							setCName(customerCredentials[1]); // Αποθηκεύονται προσωρινά μερικά στοιχεία του.
							setCId(Integer.parseInt(customerCredentials[2]));
							cBalance = conn.getCustomerBalance(cId);
							outputLine += answers[0]; // authentication message;
						}
						else {
							outputLine += answers[1]; // cannot authenticate message;
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
					break;	
					
				// Ενέργεια για την Έξοδο.
				case 'E':
					outputLine = StateString + States[5];
					break;
				
				// Ενέργεια που θα γίνει σε οποιαδήποτε άλλη περίπτωση (Ανάληψη, Κατάθεση, Υπόλοιπο).
				default:
					if (authenticated) { // Εάν έχει ταυτοποιηθεί.
						if (input == 'A') { // Ενέργεια για την Ανάληψη.
							outputLine = StateString + States[2];
							
							// Απομόνωση του ποσού προς ανάληψη.
							amount = Integer.parseInt(inputLine.substring(2, inputLine.length()));
							
							// έλεχγος αν μπορεί να γίνει η συναλλαγή.
							if (amount > conn.getCustomerBalance(cId)) {
								outputLine += "Not enough balance.";
							} 
							else if (conn.calculateWeeklyWithdraw(cId) + amount > 420) {
								outputLine += "You can't take more than 420 per week. You have already taken " + conn.calculateWeeklyWithdraw(cId);
							} 
							else { // Αν περάσει τους ελέγχους τότε εκτελείται η συναλλαγή και καταγράφεται στη βάση.
								if (conn.withdraw(cId, cBalance-amount) && conn.logTransaction(cId, input, amount)){
									cBalance -= amount;
									outputLine += " Transaction completed. New balance is: " + cBalance;
								}
								else
									outputLine += " Error... Could not complete the transaction.";
							}								
						} // if (input == 'A')
						
						// Ενέργεια για την Κατάθεση.
						else if (input == 'K') { 
							outputLine = StateString + States[3];
							
							// Απομόνωση του ποσού προς ανάληψη.
							amount = Integer.parseInt(inputLine.substring(2, inputLine.length()));
							
							// έλεχγος αν μπορεί να γίνει η συναλλαγή.
							if (conn.deposit(cId, cBalance+amount) && conn.logTransaction(cId, input, amount)) {
								cBalance += amount;
								outputLine += " Transaction completed. New balance is: " + cBalance;				
							}
							else
								outputLine += " Error.. Cannot complete the transaction.";																							
						} // if (input == 'K')
						
						// Ενέργεια για την Ερώτηση Υπολοίπου.
						else if (input == 'Y') { 
							outputLine = StateString + States[4];
							cBalance = conn.getCustomerBalance(cId);
							outputLine += " Current balance is: " + cBalance;
							
						}
					} 
					else { // Αν δεν εχει ταυτοποιηθεί ακόμα..
						outputLine = authenticationWarning;
					}
					break;
										
				} // switch
				
				
				if (input == 'E'){
					out.println("CLOSE");
					break;
				}
		
				
				// Προωθείται η απάντηση στον πελάτη.
				out.println(outputLine);
				
			} // while
			
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	} // run
	
	/*
	 * Μέθοδος που χωρίζει ένα string σε κάθε σημείο που βλέπει έναν κενό χαρακτήρα.	
	 */
	private String[] splitString(String str){
		String[] splitStr = str.split("\\s+");
		return splitStr;
	}
	
	// cName Getter & Setter
	public void setCName(String s) {
		this.cName = s;
	}
	public String getCName(){
		return this.cName;
	}
	
	// cId Getter & Setter
	public void setCId(int s) {
		this.cId = s;
	}
	public int getCId(){
		return this.cId;
	}
	
	// cBalance Getter & Setter
	public void setCBalance(double s) {
		this.cBalance = s;
	}
	public double getCBalance(){
		return this.cBalance;
	}
	
} // MultiServerThread
