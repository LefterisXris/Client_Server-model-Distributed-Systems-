import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ConnectToDB {

	private Connection connection = null;
	String driverName = "com.mysql.jdbc.Driver";
	String serverName = "localhost";
	String portNumber = "3306";
	String databaseName = "atm";
	String url = "jdbc:mysql://" + serverName + ":" + portNumber + "/" + databaseName;
	String username = "lefteris";
	String password = "lefteris";
	
	//public ConnectToDB() {}
	
	
	/*
	 * Μέθοδος που πραγματοποιεί την σύνδεση και επιστρέφει 
	 * true ή false ανάλογα με το αν έγινε η σύνδεση.
	 */
	public boolean doConnection() {
		try {
			// Load the JDBC driver.
			Class.forName(driverName);
			
			// Create a connection to the database.
			connection = DriverManager.getConnection(url, username, password);
		} catch (ClassNotFoundException e){
			// Could not find the database driver.
            System.out.println("ClassNotFoundException : "+e.getMessage());
            return false;
		} catch (SQLException e) {
            // Could not connect to the database
            System.out.println(e.getMessage());
            return false;
        }
		
		return true;
	}
	
	/*
	 * Μέθοδος που ψάχνει στην βάση για τον χρήστη με όνομα name και id το id 
	 * και αν υπάρχει επιστρέφει true αλλιώς false;
	 */
	public boolean identifyCustomer(String cName, String cId) throws SQLException {
		
		Statement stmt = null;
		String query = "select count(*) from atm.customer where customerName = '" + cName + "' and customerId = '" + cId + "'";
		stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		if (rs.next()) {
			int count = rs.getInt(1);
			
			if(count == 1) 
				return true;
			
		}
		return false;
	}
	
	/*
	 * Μέθοδος που ενημερώνει το υπόλοιπο ενός πελάτη (ΑΝΑΛΗΨΗ)
	 */
	public boolean withdraw(int cId, double newBalance) throws SQLException{
		
		Statement stmt = null;
		String query = "update atm.balance set customerBalance = " + newBalance + " where id = " + cId;
		stmt = connection.createStatement();
		int rows = stmt.executeUpdate(query);
				
		if (rows == 0)
			return false;
		return true;
	}
	
	/*
	 * Μέθοδος που επιστρέφει το υπόλοιπο ενός πελάτη από την ΒΔ με βάση το όνομά του.
	 */
	public double getCustomerBalance(int cId) throws SQLException{
		
		Statement stmt = null;
		String query = "select customerBalance from atm.customer join atm.balance on customerId = id where customerId = " + cId;
		stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		// εκτύπωση αποτελεσμάτων.
		rs.next();
		return rs.getDouble(1);
			
	}
	
	/*
	 * Μέθοδος που αποθηκευει τις συναλλαγές που γίνονται.
	 */
	public boolean logTransaction( int cId, char transactionType, double amount) throws SQLException {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date(); 
		
		Statement stmt = null;
		String query = "INSERT INTO atm.transactions (date, customerId, transactionType, amount) VALUES ('" + dateFormat.format(date) + "', '" + cId + "', '" + transactionType + "', " + amount + ")";
		stmt = connection.createStatement();
		int rows = stmt.executeUpdate(query);
		
		if (rows == 0)
			return false;
		return true;
	}
	
	/*
	 * Μέθοδος που ενημερώνει το υπόλοιπό ενός πελάτη (ΚΑΤΑΘΕΣΗ).
	 */
	public boolean deposit(int cId, double newBalance) throws SQLException{
		
		Statement stmt = null;
		String query = "update atm.balance set customerBalance = " + newBalance + " where id=" + cId ;
		stmt = connection.createStatement();
		int rows = stmt.executeUpdate(query);
		
		if (rows == 0)
			return false;
		return true;
	}
	
	/*
	 * Μέθοδος που τυπώνει όλους τους πελάτες που υπάρχουν
	 * στον πίνακα customer της ΒΔ με το διαθέσιμο υπόλοιπό τους.
	 */
	public void printAllUsers() throws SQLException {
		
		Statement stmt = null;
		String query = "select customerId, customerName, customerBalance from atm.customer JOIN atm.balance ON customerID = id";
		stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		// εκτύπωση αποτελεσμάτων.
		while (rs.next()){
			int id = rs.getInt("customerId");
			String name = rs.getString("customerName");
			String balance = rs.getString("customerBalance");
						
			System.out.println(" Name: " + name);
			System.out.println(" Balance: " + balance);
			System.out.println(" ID: " + id);
			
			System.out.println("\n +++++++++++++++++++++++++++ \n");
		}	
		
		stmt.close();
		
	}
	
	/*
	 * Μέθοδος που εμφανίζει όλες τις συναλλαγές.
	 */
	public void printAllTransactions() throws SQLException {
		
		Statement stmt = null;
		String query = "select customerName, transactionType, amount, date, transactionId from atm.customer c JOIN atm.transactions t ON c.customerId = t.customerId";
		stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		
		// εκτύπωση αποτελεσμάτων.
		while (rs.next()){
			String name = rs.getString("customerName");
			String transactionType = rs.getString("transactionType");
			double amount = rs.getDouble("amount");
			Date date = rs.getDate("date");
			int transactionId = rs.getInt("transactionId");
			
			
			System.out.println(" Name: " + name);
			System.out.println(" Transaction Type: " + transactionType);
			System.out.println(" Amount: " + amount);
			System.out.println(" Date: " + date);
			System.out.println(" Transaction ID: " + transactionId);
			
			System.out.println("\n +++++++++++++++++++++++++++ \n");
		}	
		
		stmt.close();
		
	}
	
	/*
	 * Μέθοδος η οποία υπολογίζει το συνολικό ποσό των αναλήψεων της τρέχουσας εβδομάδας. 
	 */
	public double calculateWeeklyWithdraw(int cId) throws SQLException {
		
		Calendar calendar = Calendar.getInstance();
		  
		Statement stmt = null;
		String query = "select weekofyear(date) as n, amount from atm.transactions where transactionType = 'A' and customerId = " + cId;
		stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		double weekTotal = 0;
		
		// εκτύπωση αποτελεσμάτων.
		while (rs.next()){
			if (rs.getInt("n") == calendar.get(Calendar.WEEK_OF_YEAR)) {
				weekTotal += rs.getDouble("amount");
			}
		}
		
		stmt.close();
		
		return weekTotal;
	}
	
	public Connection getConnection (){
		return connection;
	}
	
}
