import java.net.*;
import java.sql.SQLException;
import java.io.*;

/*
 * ����� MultiServerThread.
 * ����� � ����� ��� ���������� ���� �������. ���������������� ������� �� ��� ��
 * ��� ��� ���� ���������� �� ������� �������� ��� ���� ������.
 */
public class MultiServerThread extends Thread {
	private Socket socket = null;

	// ������������ ������� ���������.
	private String[] States = { "Show Options. ", "Identification. ", "Analipsi. ", "Katathesi. ", "Ypoloipo. ", "Eksodos. " };
	private String StateString = "Current State is: ";
	private String[] answers = { "User Authenticated.", "Cannot authenticate user." };
	private String authenticationWarning = "You must authenticate yourself first. (I)";
	private boolean authenticated = false;
	
	// ���������� ���� ������ ��������� ������ �������� ��� ���������� ��������.
	private String cName;
	private int cId;
	private double cBalance; 
	
	/*
	 * �������������.
	 */
	public MultiServerThread(Socket socket) {
		//super("MultiServerThread");
		this.socket = socket;		
	}

	/*
	 * � ������ ���������� ��� thread.
	 */
	public void run() {

		try (
				// ������ ���������� �� ������� ��� ��� ����������� �� ��� ������.
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));) {
			// ���� ��� try.
			
			String inputLine, outputLine;

			// ������� �� �� ��
			ConnectToDB conn = new ConnectToDB();
			
			if (!conn.doConnection())
				System.exit(1);
			

			// ������ �������� �����������.
			outputLine = StateString + States[0];
			
			// �������� ��� ��������� ���� ������.
			out.println(outputLine);

			int amount; // �� ���� ��� ������� � ��������.

			// ��� ��� � ������� �� ������ ����, �� ������ ��� �� �������������
			// ��� �� �������� ���� ��� ���������� ��������. 
			while ((inputLine = in.readLine()) != null) {
				System.out.println("Received message from client " + currentThread().getName() + " :" + inputLine);

				// ������������ � ������� ��� ������ (�,�,�,�,�). 
				char input = inputLine.charAt(0);
				
				switch (input){
				
				// �������� ��� ��� �����������.
				case 'I':
					outputLine = StateString + States[1];
					String[] customerCredentials = splitString(inputLine); // ������������ �� ����� ��� �� id ��� ������.
					if (customerCredentials.length != 3){ // �� ��� ������ ���� ��� ����� 3 �� ������������� (�������, �����, id).
						outputLine += answers[1]; // cannot authenticate message;
						break;
					}
					try { // �� ���� �� ����� ��� �� id ��� ����� � �������, ������� ������� ��� ����.
						if (conn.identifyCustomer(customerCredentials[1], customerCredentials[2]) ){
							authenticated = true;
							setCName(customerCredentials[1]); // ������������� ��������� ������ �������� ���.
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
					
				// �������� ��� ��� �����.
				case 'E':
					outputLine = StateString + States[5];
					break;
				
				// �������� ��� �� ����� �� ����������� ���� ��������� (�������, ��������, ��������).
				default:
					if (authenticated) { // ��� ���� ������������.
						if (input == 'A') { // �������� ��� ��� �������.
							outputLine = StateString + States[2];
							
							// ��������� ��� ����� ���� �������.
							amount = Integer.parseInt(inputLine.substring(2, inputLine.length()));
							
							// ������� �� ������ �� ����� � ���������.
							if (amount > conn.getCustomerBalance(cId)) {
								outputLine += "Not enough balance.";
							} 
							else if (conn.calculateWeeklyWithdraw(cId) + amount > 420) {
								outputLine += "You can't take more than 420 per week. You have already taken " + conn.calculateWeeklyWithdraw(cId);
							} 
							else { // �� ������� ���� �������� ���� ���������� � ��������� ��� ������������ ��� ����.
								if (conn.withdraw(cId, cBalance-amount) && conn.logTransaction(cId, input, amount)){
									cBalance -= amount;
									outputLine += " Transaction completed. New balance is: " + cBalance;
								}
								else
									outputLine += " Error... Could not complete the transaction.";
							}								
						} // if (input == 'A')
						
						// �������� ��� ��� ��������.
						else if (input == 'K') { 
							outputLine = StateString + States[3];
							
							// ��������� ��� ����� ���� �������.
							amount = Integer.parseInt(inputLine.substring(2, inputLine.length()));
							
							// ������� �� ������ �� ����� � ���������.
							if (conn.deposit(cId, cBalance+amount) && conn.logTransaction(cId, input, amount)) {
								cBalance += amount;
								outputLine += " Transaction completed. New balance is: " + cBalance;				
							}
							else
								outputLine += " Error.. Cannot complete the transaction.";																							
						} // if (input == 'K')
						
						// �������� ��� ��� ������� ���������.
						else if (input == 'Y') { 
							outputLine = StateString + States[4];
							cBalance = conn.getCustomerBalance(cId);
							outputLine += " Current balance is: " + cBalance;
							
						}
					} 
					else { // �� ��� ���� ������������ �����..
						outputLine = authenticationWarning;
					}
					break;
										
				} // switch
				
				
				if (input == 'E'){
					out.println("CLOSE");
					break;
				}
		
				
				// ���������� � �������� ���� ������.
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
	 * ������� ��� ������� ��� string �� ���� ������ ��� ������ ���� ���� ���������.	
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
