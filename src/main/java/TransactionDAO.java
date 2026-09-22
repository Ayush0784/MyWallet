import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // --- 1. SECURE SAVE Method ---
    public boolean addTransaction(String username, String categoryName, double amount, String description) {
        if (username != null) {
            username = username.trim();
        }

        String sql = "INSERT INTO transactions (user_id, category_id, amount, transaction_date, description) " +
                "VALUES (" +
                "(SELECT id FROM users WHERE username = ?), " +
                "(SELECT id FROM categories WHERE LOWER(TRIM(name)) = LOWER(TRIM(?))), " +
                "?, CURDATE(), ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, categoryName);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, description);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("\n=== MyWallet SQL Exception ===");
            System.err.println("Username : " + username);
            System.err.println("Category : " + categoryName);
            System.err.println("Error    : " + e.getMessage());
            System.err.println("==============================\n");

            String extraHint = "";
            if (e.getMessage().contains("category_id")) {
                extraHint = "\n\nHint: The database could not find '" + categoryName + "' in the categories table. Make sure you ran the SQL script to insert all 6 categories!";
            } else if (e.getMessage().contains("user_id")) {
                extraHint = "\n\nHint: The database could not match the session name to a valid user.";
            }

            javax.swing.JOptionPane.showMessageDialog(null,
                    "Database Error Details:\n" + e.getMessage() + extraHint,
                    "SQL Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // --- 2. LOAD Method ---
    public List<Object[]> getUserTransactions(String username) {
        List<Object[]> transactions = new ArrayList<>();
        String sql = "SELECT t.id, t.transaction_date, c.name AS category_name, t.amount, t.description " +
                "FROM transactions t " +
                "JOIN categories c ON t.category_id = c.id " +
                "JOIN users u ON t.user_id = u.id " +
                "WHERE u.username = ? " +
                "ORDER BY t.transaction_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    transactions.add(new Object[]{
                            rs.getInt("id"),
                            rs.getString("transaction_date"),
                            rs.getString("category_name"),
                            rs.getDouble("amount"),
                            rs.getString("description")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // --- 3. CALCULATE Totals ---
    public List<Object[]> getCategoryTotals(String username) {
        List<Object[]> totals = new ArrayList<>();
        String sql = "SELECT c.name, SUM(t.amount) FROM transactions t " +
                "JOIN categories c ON t.category_id = c.id " +
                "JOIN users u ON t.user_id = u.id " +
                "WHERE u.username = ? GROUP BY c.name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    totals.add(new Object[]{rs.getString(1), "₹" + rs.getDouble(2)});
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totals;
    }

    // --- 4. DELETE Method ---
    public boolean deleteTransaction(int transactionId) {
        String sql = "DELETE FROM transactions WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, transactionId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- 5. EDIT / UPDATE Method (FIXED) ---
    public boolean updateTransaction(int transactionId, String category, double amount, String description) {
        // We use a subquery to guarantee the new category gets mapped to the right ID
        String sql = "UPDATE transactions " +
                "SET category_id = (SELECT id FROM categories WHERE LOWER(TRIM(name)) = LOWER(TRIM(?))), " +
                "amount = ?, " +
                "description = ? " +
                "WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, description);
            pstmt.setInt(4, transactionId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}