import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/demo";
    private static final String USER = "student";
    private static final String PASS = "student";
    
    private Connection connection;
    private Scanner scanner;

    public DatabaseManager() {
        scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        DatabaseManager manager = new DatabaseManager();
        manager.run();
    }

    public void run() {
        try {
            connectToDatabase();
            showMainMenu();
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        } finally {
            closeConnection();
            scanner.close();
        }
    }

    private void connectToDatabase() throws SQLException {
        System.out.println("Connecting to database...");
        connection = DriverManager.getConnection(DB_URL, USER, PASS);
        System.out.println("Connection successful!");
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
            System.out.println("\n=== Database Management System ===");
            System.out.println("1. BLOB & CLOB Operations");
            System.out.println("2. Transaction Management");
            System.out.println("3. Stored Procedure Operations");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1:
                        showBlobClobMenu();
                        break;
                    case 2:
                        showTransactionMenu();
                        break;
                    case 3:
                        showStoredProcedureMenu();
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

    // BLOB and CLOB Operations Menu
    private void showBlobClobMenu() throws SQLException, IOException {
        while (true) {
            System.out.println("\n=== BLOB & CLOB Operations ===");
            System.out.println("1. Read BLOB from database");
            System.out.println("2. Write BLOB to database");
            System.out.println("3. Read CLOB from database");
            System.out.println("4. Write CLOB to database");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    readBlob();
                    break;
                case 2:
                    writeBlob();
                    break;
                case 3:
                    readClob();
                    break;
                case 4:
                    writeClob();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Transaction Management Menu
    private void showTransactionMenu() throws SQLException {
        while (true) {
            System.out.println("\n=== Transaction Management ===");
            System.out.println("1. Execute Transaction Example");
            System.out.println("2. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    executeTransactionExample();
                    break;
                case 2:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // Stored Procedure Operations Menu
    private void showStoredProcedureMenu() throws SQLException {
        while (true) {
            System.out.println("\n=== Stored Procedure Operations ===");
            System.out.println("1. Call Procedure with IN Parameters");
            System.out.println("2. Call Procedure with OUT Parameters");
            System.out.println("3. Call Procedure with INOUT Parameters");
            System.out.println("4. Call Procedure that Returns Result Set");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    callProcedureWithInParams();
                    break;
                case 2:
                    callProcedureWithOutParams();
                    break;
                case 3:
                    callProcedureWithInOutParams();
                    break;
                case 4:
                    callProcedureReturningResultSet();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // BLOB and CLOB methods (same as before)
    private void readBlob() throws SQLException, IOException {
        System.out.print("Enter email to search for: ");
        String email = scanner.nextLine();
        System.out.print("Enter column name containing BLOB: ");
        String columnName = scanner.nextLine();
        System.out.print("Enter output file path: ");
        String filePath = scanner.nextLine();

        String sql = "SELECT " + columnName + " FROM employees WHERE email=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    try (InputStream input = rs.getBinaryStream(columnName);
                         FileOutputStream output = new FileOutputStream(filePath)) {
                        
                        System.out.println("Reading BLOB from database...");
                        
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                        
                        System.out.println("Saved to file: " + new File(filePath).getAbsolutePath());
                        System.out.println("Completed successfully!");
                    }
                } else {
                    System.out.println("No record found for email: " + email);
                }
            }
        }
    }

    private void writeBlob() throws SQLException, IOException {
        System.out.print("Enter email to update: ");
        String email = scanner.nextLine();
        System.out.print("Enter column name for BLOB: ");
        String columnName = scanner.nextLine();
        System.out.print("Enter input file path: ");
        String filePath = scanner.nextLine();

        String sql = "UPDATE employees SET " + columnName + "=? WHERE email=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             FileInputStream input = new FileInputStream(filePath)) {
            
            stmt.setBinaryStream(1, input);
            stmt.setString(2, email);
            
            System.out.println("Reading input file: " + new File(filePath).getAbsolutePath());
            System.out.println("Storing BLOB in database...");
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("BLOB stored successfully for email: " + email);
            } else {
                System.out.println("No record updated. Email not found: " + email);
            }
        }
    }

    private void readClob() throws SQLException, IOException {
        System.out.print("Enter email to search for: ");
        String email = scanner.nextLine();
        System.out.print("Enter column name containing CLOB: ");
        String columnName = scanner.nextLine();
        System.out.print("Enter output file path: ");
        String filePath = scanner.nextLine();

        String sql = "SELECT " + columnName + " FROM employees WHERE email=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    try (Reader reader = rs.getCharacterStream(columnName);
                         FileWriter writer = new FileWriter(filePath)) {
                        
                        System.out.println("Reading CLOB from database...");
                        
                        char[] buffer = new char[1024];
                        int charsRead;
                        while ((charsRead = reader.read(buffer)) != -1) {
                            writer.write(buffer, 0, charsRead);
                        }
                        
                        System.out.println("Saved to file: " + new File(filePath).getAbsolutePath());
                        System.out.println("Completed successfully!");
                    }
                } else {
                    System.out.println("No record found for email: " + email);
                }
            }
        }
    }

    private void writeClob() throws SQLException, IOException {
        System.out.print("Enter email to update: ");
        String email = scanner.nextLine();
        System.out.print("Enter column name for CLOB: ");
        String columnName = scanner.nextLine();
        System.out.print("Enter input file path: ");
        String filePath = scanner.nextLine();

        String sql = "UPDATE employees SET " + columnName + "=? WHERE email=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             FileReader reader = new FileReader(filePath)) {
            
            stmt.setCharacterStream(1, reader);
            stmt.setString(2, email);
            
            System.out.println("Reading input file: " + new File(filePath).getAbsolutePath());
            System.out.println("Storing CLOB in database...");
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("CLOB stored successfully for email: " + email);
            } else {
                System.out.println("No record updated. Email not found: " + email);
            }
        }
    }

    // Transaction Management Methods
    private void executeTransactionExample() throws SQLException {
        try {
            // Turn off auto commit
            connection.setAutoCommit(false);

            System.out.println("\n=== Transaction Example ===");
            System.out.println("This example will:");
            System.out.println("1. Delete all HR employees");
            System.out.println("2. Set salaries to 300000 for all Engineering employees");
            
            // Show salaries BEFORE
            System.out.println("\nSalaries BEFORE:");
            showDepartmentSalaries("HR");
            showDepartmentSalaries("Engineering");

            // Transaction Step 1: Delete all HR employees
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DELETE FROM employees WHERE department='HR'");
            }

            // Transaction Step 2: Set salaries to 300000 for all Engineering employees
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("UPDATE employees SET salary=300000 WHERE department='Engineering'");
            }

            System.out.println("\n>> Transaction steps are ready.");

            // Ask user if it is okay to save
            System.out.print("\nIs it okay to save? (yes/no): ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("yes")) {
                // store in database
                connection.commit();
                System.out.println("\n>> Transaction COMMITTED.");
            } else {
                // discard
                connection.rollback();
                System.out.println("\n>> Transaction ROLLED BACK.");
            }

            // Show salaries AFTER
            System.out.println("\nSalaries AFTER:");
            showDepartmentSalaries("HR");
            showDepartmentSalaries("Engineering");

        } finally {
            // Reset auto commit to true
            connection.setAutoCommit(true);
        }
    }

    private void showDepartmentSalaries(String department) throws SQLException {
        String sql = "SELECT * FROM employees WHERE department=?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, department);
            
            try (ResultSet rs = stmt.executeQuery()) {
                System.out.println("\nDepartment: " + department);
                System.out.println("------------------------");
                
                if (!rs.isBeforeFirst()) {
                    System.out.println("No employees found");
                } else {
                    while (rs.next()) {
                        String lastName = rs.getString("last_name");
                        String firstName = rs.getString("first_name");
                        double salary = rs.getDouble("salary");
                        
                        System.out.printf("%s, %s, %.2f\n", lastName, firstName, salary);
                    }
                }
            }
        }
    }

    // Stored Procedure Methods
    private void callProcedureWithInParams() throws SQLException {
        System.out.print("Enter department name: ");
        String department = scanner.nextLine();
        System.out.print("Enter salary increase amount: ");
        int increaseAmount = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Show salaries BEFORE
        System.out.println("\nSalaries BEFORE:");
        showDepartmentSalaries(department);

        // Prepare the stored procedure call
        try (CallableStatement stmt = connection.prepareCall(
                "{call increase_salaries_for_department(?, ?)}")) {
            
            // Set the parameters
            stmt.setString(1, department);
            stmt.setDouble(2, increaseAmount);

            // Call stored procedure
            System.out.println("\nCalling stored procedure: increase_salaries_for_department('" + 
                              department + "', " + increaseAmount + ")");
            stmt.execute();
            System.out.println("Finished calling stored procedure");

            // Show salaries AFTER
            System.out.println("\nSalaries AFTER:");
            showDepartmentSalaries(department);
        }
    }

    private void callProcedureWithOutParams() throws SQLException {
        System.out.print("Enter department name to count employees: ");
        String department = scanner.nextLine();

        // Prepare the stored procedure call
        try (CallableStatement stmt = connection.prepareCall(
                "{call get_count_for_department(?, ?)}")) {
            
            // Set the parameters
            stmt.setString(1, department);
            stmt.registerOutParameter(2, Types.INTEGER);

            // Call stored procedure
            System.out.println("\nCalling stored procedure: get_count_for_department('" + 
                              department + "', ?)");
            stmt.execute();
            System.out.println("Finished calling stored procedure");

            // Get the value of the OUT parameter
            int count = stmt.getInt(2);
            System.out.println("\nNumber of employees in " + department + 
                              " department: " + count);
        }
    }

    private void callProcedureWithInOutParams() throws SQLException {
        System.out.print("Enter department name to greet: ");
        String department = scanner.nextLine();

        // Prepare the stored procedure call
        try (CallableStatement stmt = connection.prepareCall(
                "{call greet_the_department(?)}")) {
            
            // Set the parameters
            stmt.registerOutParameter(1, Types.VARCHAR);
            stmt.setString(1, department);

            // Call stored procedure
            System.out.println("\nCalling stored procedure: greet_the_department('" + 
                              department + "')");
            stmt.execute();
            System.out.println("Finished calling stored procedure");

            // Get the value of the INOUT parameter
            String result = stmt.getString(1);
            System.out.println("\nGreeting: " + result);
        }
    }

    private void callProcedureReturningResultSet() throws SQLException {
        System.out.print("Enter department name to list employees: ");
        String department = scanner.nextLine();

        // Prepare the stored procedure call
        try (CallableStatement stmt = connection.prepareCall(
                "{call get_employees_for_department(?)}")) {
            
            // Set the parameter
            stmt.setString(1, department);

            // Call stored procedure
            System.out.println("\nCalling stored procedure: get_employees_for_department('" + 
                              department + "')");
            stmt.execute();
            System.out.println("Finished calling stored procedure.\n");

            // Get the result set
            try (ResultSet rs = stmt.getResultSet()) {
                System.out.println("Employees in " + department + " department:");
                System.out.println("----------------------------------");
                
                while (rs.next()) {
                    String lastName = rs.getString("last_name");
                    String firstName = rs.getString("first_name");
                    double salary = rs.getDouble("salary");
                    
                    System.out.printf("%s, %s, %.2f\n", lastName, firstName, salary);
                }
            }
        }
    }
}