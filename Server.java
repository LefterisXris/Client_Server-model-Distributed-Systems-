import java.net.*;
import java.io.*;

/*
 * ����� Server. 
 * ����� � server ��� ��������� � ������ ������ ������ ���� ����� 
 * ��� ������� �� ������ ���� ��� �������� ���. 
 * ����� ������������� ��� ���������� ������ ����� ���� ������� ���������� 
 * ��� ��������� thread ��� ��� ������. �� thread ����� ����������� ��� 
 * ������ MultiServerThread.
 */
public class Server {

	// private static final int PORT = 1234;
	public static void main(String[] args) throws IOException {

		// �� ��������� ����� ��������� ��� ��� ������.
		if (args.length != 1) {
			System.err.println("Usage: java Server <port number>");
			System.exit(1);
		}

		// � ����� ���� ����� �� ������ � server ����� �� ����� ������.
		int portNumber = Integer.parseInt(args[0]);
		boolean listening = true;

		// ������� ��� server ��� ������� ��� �������.
		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			System.out.println("Server started on port: " + portNumber);

			while (listening) {
				/*
				 * � server ��� ��������� ��� �������. ���� ����� �������,
				 * ���������� ��� ���� ���� �� ��� ������������ ��� �� ���������
				 * ��� ������� ������.
				 */
				new MultiServerThread(serverSocket.accept()).start();
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port " + portNumber);
			System.exit(-1);
		}

	} // main

} // Server class