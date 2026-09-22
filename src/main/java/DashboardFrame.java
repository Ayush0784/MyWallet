import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class DashboardFrame extends JFrame {

    private DefaultTableModel tableModel;
    private String currentUser;
    private boolean isDarkMode = false;

    public DashboardFrame(String username) {
        this.currentUser = username;

        setTitle("MyWallet - Dashboard");
        setTitle("MyWallet - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setMinimumSize(
                new Dimension(
                        1200,
                        700
                )
        );

        setExtendedState(
                JFrame.MAXIMIZED_BOTH
        );;

        JPanel mainContentPanel = new JPanel(new BorderLayout(20, 20));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // --- Header Area ---
        JPanel headerPanel = new JPanel(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Welcome, " + username + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        Icon moonIcon =
                new ImageIcon(
                        "src/main/resources/Dark1-mode.svg"
                );

        Icon sunIcon =
                new ImageIcon(
                        "src/main/resources/Light-mode.svg"
                );
        JButton themeBtn =
                new JButton(
                        " Dark Mode",
                        moonIcon
                );
        themeBtn.setFont(new Font("Arial", Font.BOLD, 14));
        themeBtn.setFocusPainted(false);
        themeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        themeBtn.setPreferredSize(new Dimension(150, 40));

        JButton logoutBtn = createModernButton("Log Out", new Color(163, 117, 117), Color.WHITE);
        logoutBtn.setPreferredSize(new Dimension(130, 40));

        JPanel headerControlsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        headerControlsPanel.setOpaque(false);
        headerControlsPanel.add(themeBtn);
        headerControlsPanel.add(logoutBtn);

        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(headerControlsPanel, BorderLayout.EAST);

        mainContentPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Center Area (The Data Table) ---

        // ADDED a hidden "RealID" column at the end
        String[] columnNames = {"ID", "Date", "Category", "Amount", "Description", "RealID"};

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(35);
        transactionTable.setFont(new Font("Arial", Font.PLAIN, 15));
        transactionTable.setShowGrid(true);

        // --- FIX 1: CENTER ALL CONTENT IN THE TABLE ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Apply the center renderer to every column
        for (int i = 0; i < transactionTable.getColumnCount(); i++) {
            transactionTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // HIDE THE REAL ID COLUMN
        // We set its width to 0 so the user never sees it, but the data is still there!
        transactionTable.getColumnModel().getColumn(5).setMinWidth(0);
        transactionTable.getColumnModel().getColumn(5).setMaxWidth(0);
        transactionTable.getColumnModel().getColumn(5).setWidth(0);

        JTableHeader tableHeader = transactionTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 16));
        tableHeader.setPreferredSize(new Dimension(100, 45));

        // Keep row selected
        transactionTable.setRowSelectionAllowed(true);
        transactionTable.setColumnSelectionAllowed(false);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);

        mainContentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent me) {
                transactionTable.clearSelection();
                mainContentPanel.requestFocus();
            }
        });

        loadTransactionData();

        // --- Bottom Buttons ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));

        JButton addExpenseBtn = createModernButton("Add Expense", new Color(59, 130, 246), Color.WHITE);
        JButton viewReportsBtn = createModernButton("View Reports", new Color(16, 185, 129), Color.WHITE);
        JButton editBtn = createModernButton("Edit Selected", new Color(245, 158, 11), Color.WHITE);
        JButton deleteBtn = createModernButton("Delete Selected", new Color(239, 68, 68), Color.WHITE);

        bottomPanel.add(deleteBtn);
        bottomPanel.add(editBtn);
        bottomPanel.add(viewReportsBtn);
        bottomPanel.add(addExpenseBtn);

        mainContentPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainContentPanel);

        // --- Button Actions ---

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to log out of MyWallet?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginFrame();
            }
        });

        themeBtn.addActionListener(e -> {

            isDarkMode = !isDarkMode;

            try {

                if (isDarkMode) {

                    UIManager.setLookAndFeel(
                            new FlatDarkLaf()
                    );

                    themeBtn.setText(
                            " Light Mode"
                    );

                    themeBtn.setIcon(
                            sunIcon
                    );

                    welcomeLabel.setForeground(
                            Color.WHITE
                    );

                    transactionTable.setForeground(
                            Color.WHITE
                    );

                    tableHeader.setForeground(
                            Color.WHITE
                    );

                } else {

                    UIManager.setLookAndFeel(
                            new FlatLightLaf()
                    );

                    themeBtn.setText(
                            "🌙 Dark Mode"
                    );

                    welcomeLabel.setForeground(
                            Color.BLACK
                    );

                    transactionTable.setForeground(
                            Color.BLACK
                    );

                    tableHeader.setForeground(
                            Color.BLACK
                    );
                }

                SwingUtilities.updateComponentTreeUI(this);

            } catch (Exception ex) {

                System.out.println(
                        "Failed to change theme: "
                                + ex.getMessage()
                );
            }
        });

        addExpenseBtn.addActionListener(e -> {
            new AddExpenseDialog(this, username);
            loadTransactionData();
        });

        viewReportsBtn.addActionListener(e -> {
            new ReportDialog(this, username);
        });

        editBtn.addActionListener(e -> {
            int selectedRow = transactionTable.getSelectedRow();

            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a row first.",
                        "No Row Selected",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // --- USE HIDDEN COLUMN 5 FOR THE REAL DATABASE ID ---
            int transactionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 5).toString());

            String category = tableModel.getValueAt(selectedRow, 2).toString();
            String amountStr = tableModel.getValueAt(selectedRow, 3).toString().replace("₹", "").replace(",", "");
            double amount = Double.parseDouble(amountStr);
            String description = tableModel.getValueAt(selectedRow, 4).toString();

            new EditExpenseDialog(
                    this,
                    currentUser,
                    transactionId,
                    amount,
                    category,
                    description
            );

            loadTransactionData();
        });

        deleteBtn.addActionListener(e -> {
            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please click on an expense to delete first.", "No Row Selected", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this expense?", "Confirm Delete", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                // --- USE HIDDEN COLUMN 5 FOR THE REAL DATABASE ID ---
                int transactionId = Integer.parseInt(tableModel.getValueAt(selectedRow, 5).toString());

                TransactionDAO dao = new TransactionDAO();
                if (dao.deleteTransaction(transactionId)) {
                    loadTransactionData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete expense.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        setVisible(true);
    }

    private JButton createModernButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 45));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadTransactionData() {
        tableModel.setRowCount(0);
        TransactionDAO dao = new TransactionDAO();
        List<Object[]> records = dao.getUserTransactions(currentUser);

        // --- FIX 2: CREATE A SEQUENTIAL COUNTER STARTING AT 1 ---
        int displayId = 1;

        for (Object[] row : records) {
            Object[] formattedRow = new Object[6]; // Need 6 spots now to hold the hidden ID

            formattedRow[0] = displayId++; // Display the 1, 2, 3...
            formattedRow[1] = row[1];
            formattedRow[2] = row[2];

            double rawAmount = Double.parseDouble(row[3].toString());
            formattedRow[3] = "₹" + String.format("%.2f", rawAmount);

            formattedRow[4] = row[4];

            // Store the REAL database ID in the hidden 5th column
            formattedRow[5] = row[0];

            tableModel.addRow(formattedRow);
        }
    }
}