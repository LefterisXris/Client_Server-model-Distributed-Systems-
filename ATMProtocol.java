/*
 * ����� ATMProtocol.
 * �� ���������� ���� ������ ��� ����������� ��� ���������.
 */
public class ATMProtocol {

	// ������������ ������� ���������.
	private String[] States = { "Show Options. ", "Identification. ", "Analipsi. ", "Katathesi. ", "Ypoloipo. ", "Eksodos. " };
	private String StateString = "Current State is: ";
	private String[] answers = { "User Authenticated.", "Cannot authenticate user." };

	private String authenticationWarning = "You must authenticate yourself first. (I)";

	private boolean authenticated = false;

	// � ������� � ����� ������������� �� �������� ��� ���������� ��� �������� �� ������ String.
	public String processInput(String theInput) {
		String theOutput = null;
		
		// �������� ��� ��� ����� ����.
		if (theInput == null) { 
			theOutput = StateString + States[0];
			return theOutput;
		}
		
		// ������������ � ������� ��� ������. 
		char input = theInput.charAt(0);
		
		switch (input){
		
		// �������� ��� ��� �����������.
		case 'I':
			theOutput = StateString + States[1];
			// ����� ������� ������� ��� �� �� �� ����� ������ ����� ����������� 8 ���������� (���� �� �� � ).
			if (theInput.length() > 8) { 
				authenticated = true;
				theOutput += answers[0]; // authentication message;
			}
			else {
				theOutput += answers[1]; // cannot authenticate message;
			}
			break;
			
		// �������� ��� ��� �����.
		case 'E':
			theOutput = StateString + States[5];
			break;
		
		// �������� ��� �� ����� �� ����������� ���� ��������� (�������, ��������, ��������).
		default:
			if (authenticated) { // ��� ���� ������������.
				if (input == 'A') { // �������� ��� ��� �������.
					theOutput = StateString + States[2];
				} 
				else if (input == 'K') { // �������� ��� ��� ��������.
					theOutput = StateString + States[3];
				} 
				else if (input == 'Y') { // �������� ��� ��� ������� ���������.
					theOutput = StateString + States[4];
				}
			} 
			else { // �� ��� ���� ������������ �����..
				theOutput = authenticationWarning;
			}
			break;
								
		} // switch
		
		return theOutput;

	} // processInput

}
