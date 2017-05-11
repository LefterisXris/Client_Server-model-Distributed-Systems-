import java.net.*;
import java.io.*;

/*
 * Κλάση Server. 
 * Είναι ο server της εφαρμογής ο οποίος αρχικά ακούει στην πόρτα 
 * που δίνεται ως όρισμα κατά την εκτέλεσή του. 
 * Είναι πολυνηματικός και εξυπηρετεί έμμεσα όλους τους πελάτες ανοίγοντας 
 * ένα ξεχωριστό thread για τον καθένα. Το thread είναι αντικείμενο της 
 * κλάσης MultiServerThread.
 */
public class Server {

	// private static final int PORT = 1234;
	public static void main(String[] args) throws IOException {

		// Σε περίπτωση λάθος εκτέλεσης από τον χρήστη.
		if (args.length != 1) {
			System.err.println("Usage: java Server <port number>");
			System.exit(1);
		}

		// Η πόρτα στην οποία θα ακούει ο server είναι το πρώτο όρισμα.
		int portNumber = Integer.parseInt(args[0]);
		boolean listening = true;

		// άνοιγμα του server και αναμονή για πελάτες.
		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			System.out.println("Server started on port: " + portNumber);

			while (listening) {
				/*
				 * ο server εδώ περιμένει για πελάτες. Όταν έρθει κάποιος,
				 * δημιουργεί ένα νήμα ώστε να τον εξυπηρετήσει και να περιμένει
				 * τον επόμενο πελάτη.
				 */
				new MultiServerThread(serverSocket.accept()).start();
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}

	} // main

} // Server class