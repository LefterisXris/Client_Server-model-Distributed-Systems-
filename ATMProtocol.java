/*
 * Κλάση ATMProtocol.
 * Το πρωτοκολλο αυτό ορίζει την συμπεριφορά της εφαρμογής.
 */
public class ATMProtocol {

	// Αρχικοποίηση μερικών μηνυμάτων.
	private String[] States = { "Show Options. ", "Identification. ", "Analipsi. ", "Katathesi. ", "Ypoloipo. ", "Eksodos. " };
	private String StateString = "Current State is: ";
	private String[] answers = { "User Authenticated.", "Cannot authenticate user." };

	private String authenticationWarning = "You must authenticate yourself first. (I)";

	private boolean authenticated = false;

	// Η μέθοδος η οποία διαχειρίζεται τα αιτήματα και επιστρέφει την απάντηση ως μήνυμα String.
	public String processInput(String theInput) {
		String theOutput = null;
		
		// Ενέργεια για την πρώτη φορά.
		if (theInput == null) { 
			theOutput = StateString + States[0];
			return theOutput;
		}
		
		// Απομονώνεται η επιλογή του χρήστη. 
		char input = theInput.charAt(0);
		
		switch (input){
		
		// Ενέργεια για την Ταυτοποίηση.
		case 'I':
			theOutput = StateString + States[1];
			// απλός τυπικός έλεγχος για το αν το όνομα χρήστη είναι τουλάχιστον 8 χαρακτήρες (μαζί με το Ι ).
			if (theInput.length() > 8) { 
				authenticated = true;
				theOutput += answers[0]; // authentication message;
			}
			else {
				theOutput += answers[1]; // cannot authenticate message;
			}
			break;
			
		// Ενέργεια για την Έξοδο.
		case 'E':
			theOutput = StateString + States[5];
			break;
		
		// Ενέργεια που θα γίνει σε οποιαδήποτε άλλη περίπτωση (Ανάληψη, Κατάθεση, Υπόλοιπο).
		default:
			if (authenticated) { // Εάν έχει ταυτοποιηθεί.
				if (input == 'A') { // Ενέργεια για την Ανάληψη.
					theOutput = StateString + States[2];
				} 
				else if (input == 'K') { // Ενέργεια για την Κατάθεση.
					theOutput = StateString + States[3];
				} 
				else if (input == 'Y') { // Ενέργεια για την Ερώτηση Υπολοίπου.
					theOutput = StateString + States[4];
				}
			} 
			else { // Αν δεν εχει ταυτοποιηθεί ακόμα..
				theOutput = authenticationWarning;
			}
			break;
								
		} // switch
		
		return theOutput;

	} // processInput

}
