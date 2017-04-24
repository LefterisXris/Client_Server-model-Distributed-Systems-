import java.net.*;
import java.io.*;

/*
 * ����� MultiServerThread.
 * ����� � ����� ��� ���������� ���� �������. � ���� ������� ���� �� ���� ���
 * ��������� ���� ��� ������������ ��������� �� ��� server. 
 */
public class MultiServerThread extends Thread {
	private Socket socket = null;
	private int balance; // �� �������� ��� ���� ������.
	private int weekly_amount; // �� ����������� �������� ��� ���� ������.
	
	// ��������� ������� ���������� �������� ��� ��� "��������������� ��� ��������� ��� �����������."
	private static final String SHOW_OPTIONS_IDENTIFIER = "Show Options";
	private static final String ANALIPSI_IDENTIFIER = "Analipsi";
	private static final String KATATHESI_IDENTIFIER = "Katathesi";
	private static final String YPOLOIPO_IDENTIFIER = "Ypoloipo";
	private static final String EKSODOS_IDENTIFIER = "Eksodos";

	/*
	 * �������������.
	 */
	public MultiServerThread(Socket socket) {
		super("MultiServerThread");
		this.socket = socket;

		balance = 550; // �������������� �� �������� ��� ���� ������ �� 550 ����.
		weekly_amount = 0; // ������ �� ����������� �������� ��� ���� ������ ����� 0 (����������).
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
			// �� ����������� �������� �� ���� �� ����������.
			String inputLine, outputLine, protocolResponse;

			// ���������� ������������.
			ATMProtocol atmP = new ATMProtocol();

			// ������ ������ (null).
			protocolResponse = atmP.processInput(null);

			// � �������� ��� �����������.
			outputLine = protocolResponse;

			// �������� ��� ��������� ���� ������.
			out.println(protocolResponse);

			int amount; // �� ���� ��� ������� � ��������.

			/* ��� ��� � ������� �� ������ ����, �� ������ ��� �� �������������
			 * ��� �� ���������� ��� �� �������� ���� ��� ���������� ��������. */
			while ((inputLine = in.readLine()) != null) {
				System.out.println("Received message from client " + currentThread().getName() + " :" + inputLine);

				protocolResponse = atmP.processInput(inputLine);
				outputLine = protocolResponse;

				if (protocolResponse.contains(SHOW_OPTIONS_IDENTIFIER)) {
					outputLine += "Show Options";
				}
				else if (protocolResponse.contains(ANALIPSI_IDENTIFIER)) {
					amount = Integer.parseInt(inputLine.substring(2, inputLine.length()));
					// ������� �� ������ �� ����� � ���������.
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
				
				// ���������� � �������� ���� ������.
				out.println(outputLine);
				
			} // while
			
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	} // run

} // MultiServerThread
