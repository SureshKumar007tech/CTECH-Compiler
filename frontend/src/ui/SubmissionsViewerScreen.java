package ui;

import com.formdev.flatlaf.FlatClientProperties;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SubmissionsViewerScreen extends JFrame {

    public SubmissionsViewerScreen(JSONArray submissions) {
        setTitle("All Student Submissions");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- Title ---
        JLabel titleLabel = new JLabel("Submission Results", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // --- Table of Results ---
        String[] columnNames = {"Student", "Snippet Title", "Viva Score", "Viva Question", "Viva Answer", "AI Feedback"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setDefaultEditor(Object.class, null); // Make table read-only

        // Populate the table with data from the API
        for (int i = 0; i < submissions.length(); i++) {
            JSONObject sub = submissions.getJSONObject(i);
            Object[] row = new Object[]{
                    sub.optString("studentUsername", "N/A"),
                    sub.optString("snippetTitle", "N/A"),
                    sub.optInt("vivaScore", 0),
                    sub.optString("vivaQuestion", "-"),
                    sub.optString("vivaAnswer", "-"),
                    sub.optString("vivaFeedback", "-")
            };
            tableModel.addRow(row);
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Bottom Panel with Export Button ---
        JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        southPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JButton exportBtn = new JButton("Export to Excel (Coming Soon)");
        exportBtn.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        southPanel.add(exportBtn);
        add(southPanel, BorderLayout.SOUTH);

        exportBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Export to Excel functionality will be added in the final step!", "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
        });
    }
}
