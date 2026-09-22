import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class ReportDialog extends JDialog {

    private ChartPanel chartPanel;
    private DefaultPieDataset pieDataset;
    private DefaultCategoryDataset barDataset;

    public ReportDialog(JFrame parent, String username) {
        super(parent, "Spending Report", true);
        setSize(1000, 600);
        setLocationRelativeTo(parent);

        // Main container with generous padding
        JPanel mainContentPanel = new JPanel(new BorderLayout(20, 20));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        TransactionDAO dao = new TransactionDAO();

        // --- 1. Populate the Table with INDIVIDUAL Transactions ---
        List<Object[]> allTransactions = dao.getUserTransactions(username);
        String[] tableColumns = {"Date", "Category", "Description", "Amount"};
        DefaultTableModel tableModel = new DefaultTableModel(tableColumns, 0);

        for (Object[] row : allTransactions) {
            Object date = row[1];
            Object category = row[2];
            Object description = row[4];
            Object amount = row[3];

            tableModel.addRow(new Object[]{date, category, description, amount});
        }

        // --- 2. Populate the Graphs with AGGREGATED Totals ---
        List<Object[]> categoryTotals = dao.getCategoryTotals(username);
        pieDataset = new DefaultPieDataset();
        barDataset = new DefaultCategoryDataset();

        for (Object[] row : categoryTotals) {
            String category = (String) row[0];
            String amountStr = (String) row[1];

            // Clean the string to do math for the graphs
            double amount = Double.parseDouble(amountStr.replace("₹", "").trim());

            // Feed both datasets so we can switch between them instantly
            pieDataset.setValue(category, amount);
            barDataset.addValue(amount, "Spent", category);
        }

        // --- Left Side: The Modern Table ---
        JPanel tablePanel = new JPanel(new BorderLayout(0, 10));

        JLabel titleLabel = new JLabel("Individual Expenses", SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        tablePanel.add(titleLabel, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        // Enabled grid lines and set their color to match the dashboard
        table.setShowGrid(true);
        table.setGridColor(new Color(200, 200, 200));

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));
        tableHeader.setPreferredSize(new Dimension(100, 40));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // --- Right Side: The Graph & Dropdown ---
        JPanel graphPanel = new JPanel(new BorderLayout(0, 10));

        JPanel graphHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel selectLabel = new JLabel("Graph Type: ");
        selectLabel.setFont(new Font("Arial", Font.BOLD, 14));

        String[] chartOptions = {"Pie Chart", "Bar Chart"};
        JComboBox<String> chartSelector = new JComboBox<>(chartOptions);
        chartSelector.setFont(new Font("Arial", Font.PLAIN, 14));

        graphHeaderPanel.add(selectLabel);
        graphHeaderPanel.add(chartSelector);
        graphPanel.add(graphHeaderPanel, BorderLayout.NORTH);

        JFreeChart initialChart = ChartFactory.createPieChart("Spending Breakdown", pieDataset, true, true, false);
        chartPanel = new ChartPanel(initialChart);
        chartPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        graphPanel.add(chartPanel, BorderLayout.CENTER);

        // Swap graphs when the dropdown changes
        chartSelector.addActionListener(e -> {
            String selected = (String) chartSelector.getSelectedItem();
            JFreeChart newChart;
            if ("Bar Chart".equals(selected)) {
                newChart = ChartFactory.createBarChart("Spending Breakdown", "Category", "Amount (₹)", barDataset, PlotOrientation.VERTICAL, false, true, false);
            } else {
                newChart = ChartFactory.createPieChart("Spending Breakdown", pieDataset, true, true, false);
            }
            chartPanel.setChart(newChart);
        });

        // Combine Left and Right side
        JPanel centerSplitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        centerSplitPanel.add(tablePanel);
        centerSplitPanel.add(graphPanel);
        mainContentPanel.add(centerSplitPanel, BorderLayout.CENTER);

        // --- Bottom Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton closeBtn = createModernButton("Close", new Color(100, 116, 139), Color.WHITE);
        JButton exportPdfBtn = createModernButton("Download PDF", new Color(16, 185, 129), Color.WHITE);

        closeBtn.addActionListener(e -> dispose());

        // --- FIXED: Now passes the complete 5-column 'allTransactions' list ---
        exportPdfBtn.addActionListener(e -> {
            PDFExporter.generateReport(username, allTransactions);
        });

        buttonPanel.add(closeBtn);
        buttonPanel.add(exportPdfBtn);
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainContentPanel);
        setVisible(true);
    }

    // Modern Button Helper Method
    private JButton createModernButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}