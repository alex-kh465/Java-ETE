import java.awt.*;
import java.sql.*;
import javax.swing.*;

public class Main extends JFrame {
    private JTextField nameField, emailField, mobileField, countryField, addressField;
    private JComboBox<String> genderBox, designationBox;
    private JLabel regIdLabel, feeLabel, confirmLabel;
    private JButton addButton, editButton, deleteButton, displayButton, searchButton, exitButton;

    public Main() {
        setTitle("Conference Registration");
        setLayout(new BorderLayout(10, 10));
        JPanel inputPanel = new JPanel(new GridBagLayout());
        JPanel outputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        JPanel buttonPanel = new JPanel(new GridLayout(1, 6, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addToInputPanel(inputPanel, gbc, "Name:", nameField = new JTextField(), 0);
        addToInputPanel(inputPanel, gbc, "Gender:", genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"}), 1);
        addToInputPanel(inputPanel, gbc, "Designation:", designationBox = new JComboBox<>(new String[]{"General", "Student"}), 2);
        addToInputPanel(inputPanel, gbc, "Email:", emailField = new JTextField(), 3);
        addToInputPanel(inputPanel, gbc, "Mobile:", mobileField = new JTextField(), 4);
        addToInputPanel(inputPanel, gbc, "Country:", countryField = new JTextField(), 5);
        addToInputPanel(inputPanel, gbc, "Address:", addressField = new JTextField(), 6);

        outputPanel.add(new JLabel("Registration ID:"));
        regIdLabel = new JLabel("N/A");
        outputPanel.add(regIdLabel);

        outputPanel.add(new JLabel("Total Fee:"));
        feeLabel = new JLabel("N/A");
        outputPanel.add(feeLabel);

        outputPanel.add(new JLabel("Confirmation:"));
        confirmLabel = new JLabel("N/A");
        outputPanel.add(confirmLabel);

        addButton = new JButton("Add");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        displayButton = new JButton("Display");
        searchButton = new JButton("Search");
        exitButton = new JButton("Exit");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(displayButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(exitButton);

        // Add panels to frame
        add(inputPanel, BorderLayout.CENTER);
        add(outputPanel, BorderLayout.SOUTH);
        add(buttonPanel, BorderLayout.NORTH);

        // Button Actions
        addButton.addActionListener(e -> addRegistration());
        exitButton.addActionListener(e -> System.exit(0));

        // Frame Settings
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void addToInputPanel(JPanel panel, GridBagConstraints gbc, String labelText, JComponent inputField, int row) {
        gbc.gridx = 0;
        gbc.gridy = row;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        panel.add(inputField, gbc);
    }

    private void addRegistration() {
        String name = nameField.getText();
        String gender = (String) genderBox.getSelectedItem();
        String designation = (String) designationBox.getSelectedItem();
        String email = emailField.getText();
        String mobile = mobileField.getText();
        String country = countryField.getText();
        String address = addressField.getText();
        int fee = 0;
        if (designation == "General"){
            fee = 100;
        }
        else{
            fee = 75;
        }

        if (!mobile.matches("\\d{10}")) {
            confirmLabel.setText("Invalid mobile number!");
            return;
        }

        try (Connection conn = DBConnect.connect();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO registration (name, gender, designation, email, mobile, country, address, registration_fee) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, gender);
            ps.setString(3, designation);
            ps.setString(4, email);
            ps.setString(5, mobile);
            ps.setString(6, country);
            ps.setString(7, address);
            ps.setInt(8, fee * 74);

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int regId = rs.getInt(1);
                    regIdLabel.setText(String.valueOf(regId));
                    feeLabel.setText("INR " + (fee * 74));
                    confirmLabel.setText("Registration Successful!");
                }

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}


//Database SQL
// CREATE TABLE registration ( registration_id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(100) NOT NULL, gender VARCHAR(10) NOT NULL, designation VARCHAR(50) NOT NULL, email VARCHAR(100) NOT NULL UNIQUE, mobile VARCHAR(15) NOT NULL UNIQUE, country VARCHAR(50) NOT NULL, address TEXT NOT NULL, registration_fee INT NOT NULL ); 
