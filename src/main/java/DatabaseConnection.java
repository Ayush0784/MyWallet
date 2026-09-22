import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // 1. The details to find your specific database
    private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker";
    private static final String USER = "root";

    // CHANGE THIS to your actual MySQL password!
    private static final String PASSWORD = "1234";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Success! MyWallet is connected to the database.");
        } catch (SQLException e) {
            System.out.println("Connection failed! Check your password or database name.");
            e.printStackTrace();
        }
        return conn;
    }

    public static void main(String[] args) {
        getConnection();
    }
}