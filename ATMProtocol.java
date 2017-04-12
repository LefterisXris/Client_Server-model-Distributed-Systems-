import java.net.*;
import java.io.*;

public class ATMProtocol {
	

    private String[] States = { "Show Options. ", "Identification. ", "Analipsi. ", "Katathesi. ", "Ypoloipo. ", "Eksodos. " };
    private String StateString = "Current State is: ";
    private String[] answers = { "User Authenticated.",
                                 "Cannot authenticate user.",
                                 "Bless you!",
                                 "Is there an owl in here?",
                                 "Is there an echo in here?" };
    
    private String authenticationWarning = "You must authenticate yourself first. (I)";
    
    private boolean authenticated = false;
    
    
    

    public String processInput(String theInput) {
        String theOutput = null;
        
            
        if (theInput == null) { // σημαίνει ότι είναι το πρώτο στάδιο.
        	theOutput = StateString + States[0] ;
        	
        } else if (theInput.charAt(0) == 'I') {
        	theOutput = StateString + States[1] ;
            if (theInput.length() > 8) {            	
            	authenticated = true;
        		theOutput += answers[0]; // authentication message;
            } else {
            	theOutput += answers[1]; // cannot authenticate message;
            }
        } else if (theInput.charAt(0) == 'E' ) {
        	theOutput = StateString + States[5] ;             
        } else if (authenticated) {
        	if (theInput.charAt(0) == 'A' ) {            
            	theOutput = StateString + States[2] ;            
            } else if (theInput.charAt(0) == 'K' ) {
            	theOutput = StateString + States[3] ;             
            } else if (theInput.charAt(0) == 'Y' ) {
            	theOutput = StateString + States[4] ;             
            }
        } else { // αν δεν εχει ταυτοποιηθεί ακόμα.
        	theOutput = authenticationWarning;    
        }
        	
        
        return theOutput;
    }
    
    
}
