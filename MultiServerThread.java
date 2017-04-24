import java.net.*;
import java.io.*;

/*
 * Κλάση MultiServerThread.
 * Είναι η κλάση που εξυπηρετεί τους πελάτες. Ο κάθε πελάτης έχει το δικό του
 * ξεχωριστό νήμα και επικοινωνούν αμφίδρομα με τον server. 
 */
public class MultiServerThread extends Thread {
	private Socket socket = null;
	private int balance; // Το υπόλοιπο του κάθε πελάτη.
	private int weekly_amount; // Το εβδομαδιαίο υπόλοιπο του κάθε πελάτη.
	
	// Ορίζονται μερικές βοηθητικές σταθερές για την "αποκωδικοποίηση της απάντησης του πρωτοκόλλου."
	private static final String SHOW_OPTIONS_IDENTIFIER = "Show Options";
	private static final String ANALIPSI_IDENTIFIER = "Analipsi";
	private static final String KATATHESI_IDENTIFIER = "Katathesi";
	private static final String YPOLOIPO_IDENTIFIER = "Ypoloipo";
	private static final String EKSODOS_IDENTIFIER = "Eksodos";

	/*
	 * Κατασκευαστής.
	 */
	public MultiServerThread(Socket socket) {
		super("MultiServerThread");
		this.socket = socket;

		balance = 550; // Αρχικοποιείται το υπόλοιπο του κάθε πελάτη με 550 ευρώ.
		weekly_amount = 0; // Αρχικά το εβδομαδιαίο υπόλοιπο του κάθε πελάτη είναι 0 (υποθέτουμε).
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
			// Οι λειτουργίες γίνονται με βάση το πρωτόκολλο.
			String inputLine, outputLine, protocolResponse;

			// Δημιουργία στιγμιοτύπου.
			ATMProtocol atmP = new ATMProtocol();

			// Αρχικό αίτημα (null).
			protocolResponse = atmP.processInput(null);

			// Η απάντηση του πρωτοκόλλου.
			outputLine = protocolResponse;

			// Προώθηση της απάντησης στον πελάτη.
			out.println(protocolResponse);

			int amount; // Το ποσό για ανάληψη ή κατάθεση.

			/* Για όσο ο πελάτης θα ρωτάει κάτι, το αίτημά του θα διαχειρίζεται
			 * από το πρωτόκολλο και θα λαμβάνει πίσω την αντίστοιχη απάντηση. */
			while ((inputLine = in.readLine()) != null) {
				System.out.println("Received message from client " + currentThread().getName() + " :" + inputLine);

				protocolResponse = atmP.processInput(inputLine);
				outputLine = protocolResponse;

				if (protocolResponse.contains(SHOW_OPTIONS_IDENTIFIER)) {
					outputLine += "Show Options";
				}
				else if (protocolResponse.contains(ANALIPSI_IDENTIFIER)) {
					amount = Integer.parseInt(inputLine.substring(2, inputLine.length()));
					// έλεχγος αν μπορεί να γίνει η συναλλαγή.
					if (amount > balance) {
						outputLine += "Not enough balance.";
					} 
					else if (weekly_amount + amount > 420) {
						outputLine += "You can't take more than 420 per week. You have already taken " + weekly_amount;
					} 
					else {
						balance -= amount;
						weekly_amount += amount;
						outputLine += " Transaction completed. New balance is: " + balance;
					}				
				}
				else if (protocolResponse.contains(KATATHESI_IDENTIFIER)) {
					amount = Integer.parseInt(inputLine.substring(2, inputLine.length()));
					balance += amount;
					outputLine += " Transaction completed. New balance is: " + balance;
				}
				else if (protocolResponse.contains(YPOLOIPO_IDENTIFIER)) {
					outputLine += " Current balance is: " + balance;
				}
				else if (protocolResponse.contains(EKSODOS_IDENTIFIER)) {
					out.println("CLOSE");
					break;
				}
				
				// Προωθείται η απάντηση στον πελάτη.
				out.println(outputLine);
				
			} // while
			
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	} // run

} // MultiServerThread
