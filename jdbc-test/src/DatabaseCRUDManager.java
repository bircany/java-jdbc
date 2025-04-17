import java.sql.Connection;
import java.util.Properties;
import java.util.Scanner;
import java.io.FileInputStream;
import java.sql.*;

public class DatabaseCRUDManager {
	private static final String PROPERTIES_FILE = "demo.properties";
	private Connection connection;
	private Scanner scanner;
	public DatabaseCRUDManager() {
		scanner = new Scanner(System.in);
	}
	public static void main(String[] args) {
		DatabaseCRUDManager manager = new DatabaseCRUDManager();
		manager.run();
	}
	public void run() {
		try {
			connectToDatabase();
			showMainMenu();
			
		}
		catch(SQLException e) {
			System.err.println("Database connection error: " + e.getMessage());
		}
		finally {
			closeConnection();
			scanner.close();
		}
	}
	private void closeConnection() {
		try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
		
	}
	private void showMainMenu() {
		while (true) {
            System.out.println("\n=== Database CRUD Management System ===");
            System.out.println("1. CRUD Operations (Employees)");
            System.out.println("2. Database Metadata");
            System.out.println("3. ResultSet Metadata");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1:
                        showCRUDMenu();
                        break;
                    case 2:
                        showDatabaseMetadata();
                        break;
                    case 3:
                        showResultSetMetadata();
                        break;
                    case 4:
                        System.out.println("Exiting program...");
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
		}
		
	}
	private void showResultSetMetadata() throws SQLException {
        System.out.println("\n=== ResultSet Metadata ===");
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employees")) {
            
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            System.out.println("\nEmployee Table Structure:");
            System.out.println("-----------------------");
            System.out.printf("%-15s %-15s %-15s %-10s\n", 
                "Column Name", "Type", "Nullable", "Auto Increment");
            System.out.println("------------------------------------------------");
            
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%-15s %-15s %-15s %-10s\n",
                    metaData.getColumnName(i),
                    metaData.getColumnTypeName(i),
                    metaData.isNullable(i) == ResultSetMetaData.columnNullable ? "YES" : "NO",
                    metaData.isAutoIncrement(i) ? "YES" : "NO");
            }
        }
    }
    private void showDatabaseMetadata() throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        
        System.out.println("\n=== Database Metadata ===");
        System.out.println("\nList of Tables:");
        System.out.println("--------------");
        try (ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"})) {
            while (tables.next()) {
                System.out.println(tables.getString("TABLE_NAME"));
            }
        }        
        System.out.println("\nColumns in Employees Table:");
        System.out.println("-------------------------");
        try (ResultSet columns = metaData.getColumns(null, null, "employees", null)) {
            while (columns.next()) {
                System.out.printf("%-15s %-15s\n", 
                    columns.getString("COLUMN_NAME"),
                    columns.getString("TYPE_NAME"));
            }
        }
    }
	private void showCRUDMenu() throws SQLException {
        while (true) {
            System.out.println("\n=== Employee CRUD Operations ===");
            System.out.println("1. Create (Insert) Employee");
            System.out.println("2. Read (List) Employees");
            System.out.println("3. Update Employee");
            System.out.println("4. Delete Employee");
            System.out.println("5. Search Employee");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    insertEmployee();
                    break;
                case 2:
                    listEmployees();
                    break;
                case 3:
                    updateEmployee();
                    break;
                case 4:
                    deleteEmployee();
                    break;
                case 5:
                    searchEmployee();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
	// CRUD Operations
    private void insertEmployee() throws SQLException {
        System.out.println("\n=== Add New Employee ===");
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Department: ");
        String department = scanner.nextLine();
        System.out.print("Salary: ");
        double salary = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        String sql = "INSERT INTO employees (last_name, first_name, email, department, salary) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, lastName);
            stmt.setString(2, firstName);
            stmt.setString(3, email);
            stmt.setString(4, department);
            stmt.setDouble(5, salary);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("\nEmployee added successfully!");
                displayEmployee(lastName, firstName);
            } else {
                System.out.println("\nFailed to add employee.");
            }
        }
    }

    private void listEmployees() throws SQLException {
        System.out.println("\n=== Employee List ===");
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM employees ORDER BY last_name")) {
            
            System.out.printf("%-15s %-15s %-25s %-15s %10s\n", 
                "Last Name", "First Name", "Email", "Department", "Salary");
            System.out.println("----------------------------------------------------------------");
            
            while (rs.next()) {
                String lastName = rs.getString("last_name");
                String firstName = rs.getString("first_name");
                String email = rs.getString("email");
                String department = rs.getString("department");
                double salary = rs.getDouble("salary");
                
                System.out.printf("%-15s %-15s %-25s %-15s %,10.2f\n", 
                    lastName, firstName, email, department, salary);
            }
        }
    }

    private void updateEmployee() throws SQLException {
        System.out.println("\n=== Update Employee ===");
        
        System.out.print("Enter last name of employee to update: ");
        String lastName = scanner.nextLine();
        
        System.out.print("Enter first name of employee to update: ");
        String firstName = scanner.nextLine();
        
        // First display the current data
        System.out.println("\nCurrent employee data:");
        if (!displayEmployee(lastName, firstName)) {
            return; // Employee not found
        }
        
        // Get new data
        System.out.println("\nEnter new data (leave blank to keep current value):");
        
        System.out.print("New last name: ");
        String newLastName = scanner.nextLine();
        
        System.out.print("New first name: ");
        String newFirstName = scanner.nextLine();
        
        System.out.print("New email: ");
        String newEmail = scanner.nextLine();
        
        System.out.print("New department: ");
        String newDepartment = scanner.nextLine();
        
        System.out.print("New salary (0 to keep current): ");
        double newSalary = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        // Build the update query dynamically based on what fields changed
        StringBuilder sql = new StringBuilder("UPDATE employees SET ");
        boolean needsComma = false;
        
        if (!newLastName.isEmpty()) {
            sql.append("last_name = ?");
            needsComma = true;
        }
        
        if (!newFirstName.isEmpty()) {
            if (needsComma) sql.append(", ");
            sql.append("first_name = ?");
            needsComma = true;
        }
        
        if (!newEmail.isEmpty()) {
            if (needsComma) sql.append(", ");
            sql.append("email = ?");
            needsComma = true;
        }
        
        if (!newDepartment.isEmpty()) {
            if (needsComma) sql.append(", ");
            sql.append("department = ?");
            needsComma = true;
        }
        
        if (newSalary > 0) {
            if (needsComma) sql.append(", ");
            sql.append("salary = ?");
        }
        
        sql.append(" WHERE last_name = ? AND first_name = ?");
        
        try (PreparedStatement stmt = connection.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            
            if (!newLastName.isEmpty()) {
                stmt.setString(paramIndex++, newLastName);
            }
            
            if (!newFirstName.isEmpty()) {
                stmt.setString(paramIndex++, newFirstName);
            }
            
            if (!newEmail.isEmpty()) {
                stmt.setString(paramIndex++, newEmail);
            }
            
            if (!newDepartment.isEmpty()) {
                stmt.setString(paramIndex++, newDepartment);
            }
            
            if (newSalary > 0) {
                stmt.setDouble(paramIndex++, newSalary);
            }
            
            stmt.setString(paramIndex++, lastName);
            stmt.setString(paramIndex, firstName);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("\nEmployee updated successfully!");
                displayEmployee(
                    newLastName.isEmpty() ? lastName : newLastName,
                    newFirstName.isEmpty() ? firstName : newFirstName
                );
            } else {
                System.out.println("\nFailed to update employee.");
            }
        }
    }

    private void deleteEmployee() throws SQLException {
        System.out.println("\n=== Delete Employee ===");
        
        System.out.print("Enter last name of employee to delete: ");
        String lastName = scanner.nextLine();
        
        System.out.print("Enter first name of employee to delete: ");
        String firstName = scanner.nextLine();
        
        // First display the employee to confirm
        System.out.println("\nEmployee to delete:");
        if (!displayEmployee(lastName, firstName)) {
            return; // Employee not found
        }
        
        System.out.print("\nAre you sure you want to delete this employee? (yes/no): ");
        String confirmation = scanner.nextLine();
        
        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Delete operation cancelled.");
            return;
        }
        
        String sql = "DELETE FROM employees WHERE last_name = ? AND first_name = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, lastName);
            stmt.setString(2, firstName);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("\nEmployee deleted successfully!");
            } else {
                System.out.println("\nFailed to delete employee.");
            }
        }
    }

    private void searchEmployee() throws SQLException {
        System.out.println("\n=== Search Employee ===");
        
        System.out.print("Enter last name (or part of it): ");
        String lastName = scanner.nextLine();
        
        System.out.print("Enter first name (or part of it, leave blank to search by last name only): ");
        String firstName = scanner.nextLine();
        
        String sql;
        if (firstName.isEmpty()) {
            sql = "SELECT * FROM employees WHERE last_name LIKE ? ORDER BY last_name";
        } else {
            sql = "SELECT * FROM employees WHERE last_name LIKE ? AND first_name LIKE ? ORDER BY last_name";
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + lastName + "%");
            
            if (!firstName.isEmpty()) {
                stmt.setString(2, "%" + firstName + "%");
            }
            
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\nSearch Results:");
                System.out.printf("%-15s %-15s %-25s %-15s %10s\n", 
                    "Last Name", "First Name", "Email", "Department", "Salary");
                System.out.println("----------------------------------------------------------------");
                
                boolean found = false;
                while (rs.next()) {
                    found = true;
                    String rsLastName = rs.getString("last_name");
                    String rsFirstName = rs.getString("first_name");
                    String email = rs.getString("email");
                    String department = rs.getString("department");
                    double salary = rs.getDouble("salary");
                    
                    System.out.printf("%-15s %-15s %-25s %-15s %,10.2f\n", 
                        rsLastName, rsFirstName, email, department, salary);
                }
                
                if (!found) {
                    System.out.println("No employees found matching your criteria.");
                }
            }
        }
    }

    private boolean displayEmployee(String lastName, String firstName) throws SQLException {
        String sql = "SELECT * FROM employees WHERE last_name = ? AND first_name = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, lastName);
            stmt.setString(2, firstName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.printf("\n%-15s: %s\n", "Last Name", rs.getString("last_name"));
                    System.out.printf("%-15s: %s\n", "First Name", rs.getString("first_name"));
                    System.out.printf("%-15s: %s\n", "Email", rs.getString("email"));
                    System.out.printf("%-15s: %s\n", "Department", rs.getString("department"));
                    System.out.printf("%-15s: %,.2f\n", "Salary", rs.getDouble("salary"));
                    return true;
                } else {
                    System.out.println("\nEmployee not found: " + firstName + " " + lastName);
                    return false;
                }
            }
        }
    }
	
	private void connectToDatabase() throws SQLException {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream(PROPERTIES_FILE));
			String url = props.getProperty("dburl");
            String user = props.getProperty("user");
            String password = props.getProperty("password");
            
            System.out.println("Connecting to database...");
            System.out.println("Database URL: " + url);
            System.out.println("User: " + user);
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Connection successful!");

            displayDatabaseInfo(); // Display basic database info
			
		}
		catch(Exception e) {
			System.err.println("Error loading properties file." + e.getMessage());
			//Fall back to defaults if properties file not found
			System.out.println("Using default connection parameters...");
			connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/demo","student","student");
		}
		
	}
	private void displayDatabaseInfo() throws SQLException {
		DatabaseMetaData metaData = connection.getMetaData();
        System.out.println("\nDatabase Information:");
        System.out.println("Product name: " + metaData.getDatabaseProductName());
        System.out.println("Product version: " + metaData.getDatabaseProductVersion());
        System.out.println("JDBC Driver name: " + metaData.getDriverName());
        System.out.println("JDBC Driver version: " + metaData.getDriverVersion() + "\n");
	}

}
