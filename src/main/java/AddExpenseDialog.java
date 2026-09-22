import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class AddExpenseDialog extends JDialog {

    private JComboBox<String> categoryComboBox;
    private JTextField amountField;
    private JTextField descriptionField;
    private JButton saveBtn;
    private JButton cancelBtn;

    private String passedUsername;

    public AddExpenseDialog(JFrame parent, String username) {
        super(parent, "Add New Expense", true);
        this.passedUsername = username;

        setSize(380, 420);
        setLocationRelativeTo(parent);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 247, 250));

        Font labelFont = new Font("Arial", Font.BOLD, 13);

        JLabel categoryLabel = new JLabel("Category");
        categoryLabel.setFont(labelFont);
        categoryLabel.setBounds(40, 30, 300, 20);
        add(categoryLabel);

        // FIX: Spelled exactly as it is in the MySQL Database (No spaces around the &)
        String[] categories = {
                "Groceries",
                "Shopping",
                "Fuel & Transport",
                "Utility Bills",
                "Entertainment",
                "Other"
        };
        categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        categoryComboBox.setBounds(40, 55, 300, 40);
        add(categoryComboBox);

        JLabel amountLabel = new JLabel("Amount (₹)");
        amountLabel.setFont(labelFont);
        amountLabel.setBounds(40, 115, 300, 20);
        add(amountLabel);

        amountField = new JTextField();
        styleInputField(amountField);
        amountField.setBounds(40, 140, 300, 40);
        add(amountField);

        JLabel descLabel = new JLabel("Description");
        descLabel.setFont(labelFont);
        descLabel.setBounds(40, 200, 300, 20);
        add(descLabel);

        descriptionField = new JTextField();
        styleInputField(descriptionField);
        descriptionField.setBounds(40, 225, 300, 40);
        add(descriptionField);

        saveBtn = new JButton("SAVE");
        saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
        saveBtn.setBackground(new Color(28, 70, 117));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBounds(40, 300, 140, 45);
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(saveBtn);

        cancelBtn = new JButton("CANCEL");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
        cancelBtn.setBackground(new Color(239, 68, 68));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBounds(200, 300, 140, 45);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(cancelBtn);

        cancelBtn.addActionListener(e -> dispose());

        saveBtn.addActionListener(e -> {
            String amountText = amountField.getText().trim();
            String description = descriptionField.getText().trim();

            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                double amount = Double.parseDouble(amountText);

                // Add a quick check to prevent negative expenses
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String selectedCategoryName = (String) categoryComboBox.getSelectedItem();

                TransactionDAO dao = new TransactionDAO();

                boolean success = dao.addTransaction(passedUsername, selectedCategoryName, amount, description);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Expense recorded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid number format. Please enter a valid decimal or number amount.", "Parsing Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    private void styleInputField(JTextField field) {
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
    }
}