import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Date;

public class PDFExporter {

    public static void generateReport(String username, List<Object[]> reportData) {
        // Safe guard check
        if (reportData == null || reportData.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No transaction data available to export!", "Report Empty", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Report As...");
        fileChooser.setSelectedFile(new File(username + "_Detailed_Spending_Report.pdf"));

        int userSelection = fileChooser.showSaveDialog(null);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            try {
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();

                // Load Arial Font to support the ₹ currency symbol natively
                BaseFont arial = BaseFont.createFont("C:/Windows/Fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font titleFont = new Font(arial, 20, Font.BOLD, BaseColor.BLACK);
                Font headerFont = new Font(arial, 11, Font.BOLD, BaseColor.BLACK);
                Font tableFont = new Font(arial, 11, Font.NORMAL, BaseColor.BLACK);
                Font totalFont = new Font(arial, 14, Font.BOLD, new BaseColor(220, 53, 69)); // Modern crimson

                // Report Title
                Paragraph title = new Paragraph("Detailed Spending Report for " + username, titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(5f);
                document.add(title);

                // Generated Timestamp
                Paragraph dateParagraph = new Paragraph("Generated on: " + new Date().toString());
                dateParagraph.setAlignment(Element.ALIGN_CENTER);
                dateParagraph.setSpacingAfter(25f);
                document.add(dateParagraph);

                // --- FORCED 5-COLUMN TABLE LAYOUT ---
                PdfPTable table = new PdfPTable(5);
                table.setWidthPercentage(100);

                // Column proportions: ID(10%), Date(20%), Category(20%), Amount(15%), Description(35%)
                table.setWidths(new float[]{1.0f, 2.0f, 2.0f, 1.5f, 3.5f});

                // Table Header Row
                String[] headers = {"ID", "Date", "Category", "Amount", "Description"};
                for (String headerText : headers) {
                    PdfPCell headerCell = new PdfPCell(new Phrase(headerText, headerFont));
                    headerCell.setBackgroundColor(new BaseColor(230, 235, 245)); // Soft gray-blue highlight
                    headerCell.setPadding(8f);
                    table.addCell(headerCell);
                }

                double grandTotal = 0;

                // --- IMPLEMENTED SEQUENTIAL ID ---
                int serialNo = 1;

                // Populate row data
                for (Object[] row : reportData) {

                    // PDF ID starts from 1 and increments sequentially
                    table.addCell(createFormattedCell(String.valueOf(serialNo++), tableFont));

                    // The rest of the data array indexes shift naturally
                    table.addCell(createFormattedCell(row[1].toString(), tableFont));
                    table.addCell(createFormattedCell(row[2].toString(), tableFont));

                    // Parse numeric cost for summation
                    double amount = Double.parseDouble(row[3].toString());
                    grandTotal += amount;
                    table.addCell(createFormattedCell("₹" + String.format("%.2f", amount), tableFont));

                    // Safe verification fallback for text description notes fields
                    String descText = (row[4] != null) ? row[4].toString() : "";
                    table.addCell(createFormattedCell(descText, tableFont));
                }

                document.add(table);

                // Bottom Total Section
                Paragraph grandTotalText = new Paragraph("Grand Total: ₹" + String.format("%.2f", grandTotal), totalFont);
                grandTotalText.setAlignment(Element.ALIGN_RIGHT);
                grandTotalText.setSpacingBefore(20f);
                document.add(grandTotalText);

                document.close();
                JOptionPane.showMessageDialog(null, "PDF successfully saved to:\n" + fileToSave.getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error generating PDF layout: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static PdfPCell createFormattedCell(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(6f);
        return cell;
    }
}