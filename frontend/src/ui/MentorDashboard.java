package ui;

import api.ApiService;
import com.formdev.flatlaf.FlatClientProperties;
import org.json.JSONArray;

import javax.swing.*;
import java.awt.*;

public class MentorDashboard extends JFrame {
    private String username;
    private String name;

    public MentorDashboard(String username, String name) {
        this.username = username;
        this.name = name;
        setTitle("Mentor Dashboard");
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Welcome Mentor, " + name, SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(title, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(3, 1, 10, 20));
        center.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        JButton btnCreateStudent = new JButton("Create Student Account");
        JButton btnAssignTest = new JButton("Assign Code Snippet to All");
        JButton btnViewResults = new JButton("View/Extract Results");

        // Apply modern button styling
        btnCreateStudent.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnAssignTest.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);
        btnViewResults.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_ROUND_RECT);

        center.add(btnCreateStudent);
        center.add(btnAssignTest);
        center.add(btnViewResults);
        add(center, BorderLayout.CENTER);

        // --- Action Listeners ---

        // Listener for creating a student account
        btnCreateStudent.addActionListener(e -> handleCreateStudent());

        // Listener for assigning a snippet to all students
        btnAssignTest.addActionListener(e -> handleAssignSnippet());

        // Listener for viewing results
        btnViewResults.addActionListener(e -> {
            // Use a SwingWorker to fetch data without freezing the UI
            new SwingWorker<JSONArray, Void>() {
                @Override
                protected JSONArray doInBackground() throws Exception {
                    // Call our new API service method
                    String response = ApiService.getAllSubmissions();
                    return new JSONArray(response);
                }

                @Override
                protected void done() {
                    try {
                        JSONArray submissions = get(); // Get the result from the background thread
                        if (submissions.length() == 0) {
                            JOptionPane.showMessageDialog(MentorDashboard.this, "No student submissions have been recorded yet.", "No Data", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        // If we have data, create and show the new results screen
                        new SubmissionsViewerScreen(submissions).setVisible(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(MentorDashboard.this, "Failed to fetch submissions: " + ex.getMessage(), "API Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
    }

    private void handleCreateStudent() {
        JPanel p = new JPanel(new GridLayout(4, 2, 10, 10));
        JTextField su = new JTextField();
        JPasswordField sp = new JPasswordField();
        JTextField sname = new JTextField();
        JPasswordField mentorPass = new JPasswordField();
        p.add(new JLabel("Student Username:")); p.add(su);
        p.add(new JLabel("Student Password:")); p.add(sp);
        p.add(new JLabel("Student Name:")); p.add(sname);
        p.add(new JLabel("Your Mentor Password (confirm):")); p.add(mentorPass);

        int ok = JOptionPane.showConfirmDialog(this, p, "Create Student Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                ApiService.registerStudent(this.username, new String(mentorPass.getPassword()).trim(),
                        su.getText().trim(), new String(sp.getPassword()).trim(), sname.getText().trim());
                JOptionPane.showMessageDialog(this, "Student created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to create student: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleAssignSnippet() {
        JPanel p = new JPanel(new BorderLayout(10, 10));

        JTextField titleField = new JTextField();
        JTextArea snippetArea = new JTextArea(10, 40);
        snippetArea.setLineWrap(true);
        snippetArea.setWrapStyleWord(true);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        formPanel.add(new JLabel("Snippet Title:"));
        formPanel.add(titleField);

        p.add(formPanel, BorderLayout.NORTH);
        p.add(new JLabel("Code Snippet:"), BorderLayout.CENTER);
        p.add(new JScrollPane(snippetArea), BorderLayout.SOUTH);

        int ok = JOptionPane.showConfirmDialog(this, p, "Assign Snippet to ALL Students", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (ok == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String snippet = snippetArea.getText().trim();

            if (title.isEmpty() || snippet.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Title and snippet cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                ApiService.assignSnippet(title, snippet);
                JOptionPane.showMessageDialog(this, "Snippet assigned successfully to all students!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to assign snippet: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

