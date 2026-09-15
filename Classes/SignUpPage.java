package Classes;

import java.lang.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SignUpPage extends JFrame implements ActionListener {
    UIHelper.RoundedPanel cardPanel;
    JLabel titleLabel, formTitleLabel, nameLabel, phoneNumberLabel, dobLabel, newPasswordLabel, goBackLabel;
    UIHelper.ModernTextField nameField, phoneNumberField, dobField;
    UIHelper.ModernPasswordField newPasswordField;
    UIHelper.ModernButton registerButton;

    public SignUpPage() {
        super("Sign Up - Metro Ticket System");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon("Images/icon.png").getImage());

        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int screenWidth = maxBounds.width;
        int screenHeight = maxBounds.height;

        // Base image background
        UIHelper.ImagePanel backgroundPanel = new UIHelper.ImagePanel("Images/metro_rail_bg.png");
        backgroundPanel.setBounds(0, 0, screenWidth, screenHeight);
        this.add(backgroundPanel);

        // Centered container of size 1200x800
        JPanel container = new JPanel();
        container.setLayout(null);
        container.setOpaque(false);
        container.setBounds((screenWidth - 1200) / 2, (screenHeight - 800) / 2, 1200, 800);
        backgroundPanel.add(container);

        // Header Title
        titleLabel = new JLabel("METRO RAIL TICKET SYSTEM", SwingConstants.CENTER);
        titleLabel.setFont(UIHelper.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(200, 40, 800, 50);
        container.add(titleLabel);

        // Centered Card Panel (Glassmorphic)
        cardPanel = new UIHelper.RoundedPanel(30, new Color(15, 23, 42, 195));
        cardPanel.setLayout(null);
        cardPanel.setBounds(350, 120, 500, 540);
        container.add(cardPanel);

        formTitleLabel = new JLabel("Register Account", SwingConstants.CENTER);
        formTitleLabel.setFont(UIHelper.FONT_SUBTITLE);
        formTitleLabel.setForeground(UIHelper.COLOR_PRIMARY);
        formTitleLabel.setBounds(50, 30, 400, 45);
        cardPanel.add(formTitleLabel);

        // Name
        nameLabel = new JLabel("Full Name");
        nameLabel.setFont(UIHelper.FONT_BODY_BOLD);
        nameLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        nameLabel.setBounds(70, 95, 360, 20);
        cardPanel.add(nameLabel);

        nameField = new UIHelper.ModernTextField("Enter full name");
        nameField.setBounds(70, 120, 360, 40);
        cardPanel.add(nameField);

        // Phone Number
        phoneNumberLabel = new JLabel("Phone Number");
        phoneNumberLabel.setFont(UIHelper.FONT_BODY_BOLD);
        phoneNumberLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        phoneNumberLabel.setBounds(70, 175, 360, 20);
        cardPanel.add(phoneNumberLabel);

        phoneNumberField = new UIHelper.ModernTextField("Enter phone number");
        phoneNumberField.setBounds(70, 200, 360, 40);
        cardPanel.add(phoneNumberField);

        // DOB
        dobLabel = new JLabel("Date of Birth (YYYY-MM-DD)");
        dobLabel.setFont(UIHelper.FONT_BODY_BOLD);
        dobLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        dobLabel.setBounds(70, 255, 360, 20);
        cardPanel.add(dobLabel);

        dobField = new UIHelper.ModernTextField("YYYY-MM-DD");
        dobField.setBounds(70, 280, 360, 40);
        cardPanel.add(dobField);

        // Password
        newPasswordLabel = new JLabel("New Password");
        newPasswordLabel.setFont(UIHelper.FONT_BODY_BOLD);
        newPasswordLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        newPasswordLabel.setBounds(70, 335, 360, 20);
        cardPanel.add(newPasswordLabel);

        newPasswordField = new UIHelper.ModernPasswordField("Enter new password");
        newPasswordField.setBounds(70, 360, 360, 40);
        cardPanel.add(newPasswordField);

        // Register button
        registerButton = new UIHelper.ModernButton("Register", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        registerButton.setBounds(70, 430, 360, 45);
        registerButton.addActionListener(this);
        cardPanel.add(registerButton);

        // Go Back Label
        goBackLabel = new JLabel("Go back to User Login", SwingConstants.CENTER);
        goBackLabel.setFont(UIHelper.FONT_BODY);
        goBackLabel.setForeground(UIHelper.COLOR_TEXT_MUTED);
        goBackLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goBackLabel.setBounds(50, 490, 400, 25);
        goBackLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                dispose();
                WelcomePage welcomePage = new WelcomePage();
                welcomePage.setVisible(true);
            }
            @Override
            public void mouseEntered(MouseEvent me) {
                goBackLabel.setForeground(UIHelper.COLOR_PRIMARY);
            }
            @Override
            public void mouseExited(MouseEvent me) {
                goBackLabel.setForeground(UIHelper.COLOR_TEXT_MUTED);
            }
        });
        cardPanel.add(goBackLabel);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == registerButton) {
            String name = nameField.getText().trim();
            String phone = phoneNumberField.getText().trim();
            String dob = dobField.getText().trim();
            String password = new String(newPasswordField.getPassword()).trim();

            if (name.isEmpty() || phone.isEmpty() || dob.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all input fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Simple validation: Phone number format
            if (!phone.matches("\\d{11}")) {
                JOptionPane.showMessageDialog(this, "Phone Number must be exactly 11 digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Simple validation: DOB YYYY-MM-DD
            if (!dob.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "Date of Birth must be in YYYY-MM-DD format.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if username already exists in signup.txt
            try (BufferedReader reader = new BufferedReader(new FileReader("Data/signup.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length > 0 && data[0].trim().equalsIgnoreCase(name)) {
                        JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different name.", "Duplicate Account", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
            } catch (IOException e) {
                // Ignore if file doesn't exist yet
            }

            // Write registration info
            try {
                File dir = new File("Data");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter("Data/signup.txt", true));
                writer.write(name + "," + phone + "," + dob + "," + password);
                writer.newLine();
                writer.close();

                JOptionPane.showMessageDialog(this, "Account registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                WelcomePage welcomePage = new WelcomePage();
                welcomePage.setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to save registration details.", "System Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SignUpPage signUpPage = new SignUpPage();
        signUpPage.setVisible(true);
    }
}
