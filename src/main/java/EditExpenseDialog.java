import javax.swing.*;
import java.awt.*;

public class EditExpenseDialog extends JDialog {

    public EditExpenseDialog(
            JFrame parent,
            String username,
            int transactionId,
            double amount,
            String category,
            String description) {

        super(parent, "Edit Expense", true);

        setSize(400, 300);
        setLocationRelativeTo(parent);

        setLayout(new GridLayout(4, 2, 10, 10));

        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // --- 1. Create the UI Components ---

        add(new JLabel("Amount (₹):"));
        JTextField amountField = new JTextField(String.valueOf(amount));
        add(amountField);

        add(new JLabel("Category:"));

        // --- NEW: Using a Dropdown (JComboBox) instead of a Text Field ---
        String[] categories = {
                "Groceries",
                "Shopping",
                "Fuel & Transport", // Must match DB exactly!
                "Utility Bills",
                "Entertainment",
                "Other"
        };
        JComboBox<String> categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setSelectedItem(category); // Automatically selects the current category
        add(categoryComboBox);

        add(new JLabel("Description:"));
        JTextField descField = new JTextField(description);
        add(descField);

        JButton cancelBtn = new JButton("Cancel");
        JButton saveBtn = new JButton("Save");

        add(cancelBtn);
        add(saveBtn);

        // --- 2. Button Actions ---

        cancelBtn.addActionListener(e -> dispose());

        saveBtn.addActionListener(e -> {
            try {
                double newAmount = Double.parseDouble(amountField.getText().trim());

                // Get the string value from the dropdown
                String newCategory = (String) categoryComboBox.getSelectedItem();

                String newDescription = descField.getText().trim();

                TransactionDAO dao = new TransactionDAO();

                boolean success = dao.updateTransaction(
                        transactionId,
                        newCategory,
                        newAmount,
                        newDescription
                );

                if (success) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Expense updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    dispose();

                } else {
                    JOptionPane.showMessageDialog(
                            this,
                            "Update failed! Check if category matches exactly.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid amount entered! Please use numbers.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        setVisible(true);
    }
}