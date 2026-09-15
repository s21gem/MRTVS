package Classes;

import java.lang.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class ForgotPasswordPage extends JFrame implements ActionListener {
    UIHelper.RoundedPanel cardPanel;
    JLabel titleLabel, formTitleLabel, usernameLabel, dobLabel, newPasswordLabel, goBackLabel;
    UIHelper.ModernTextField usernameField, dobField;
    UIHelper.ModernPasswordField newPasswordField;
    UIHelper.ModernButton changePasswordButton;

    private static class PasswordUpdateResult {
        boolean userFound;
        boolean success;
    }

    public ForgotPasswordPage() {
        super("Forgot Password - Metro Ticket System");
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
        titleLabel.setBounds(200, 50, 800, 50);
        container.add(titleLabel);

        // Card Panel (Glassmorphic)
        cardPanel = new UIHelper.RoundedPanel(30, new Color(15, 23, 42, 195));
        cardPanel.setLayout(null);
        cardPanel.setBounds(350, 140, 500, 480);
        container.add(cardPanel);

        formTitleLabel = new JLabel("Reset Password", SwingConstants.CENTER);
        formTitleLabel.setFont(UIHelper.FONT_SUBTITLE);
        formTitleLabel.setForeground(UIHelper.COLOR_PRIMARY);
        formTitleLabel.setBounds(50, 30, 400, 40);
        cardPanel.add(formTitleLabel);

        // Username
        usernameLabel = new JLabel("Username");
        usernameLabel.setFont(UIHelper.FONT_BODY_BOLD);
        usernameLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        usernameLabel.setBounds(70, 95, 360, 20);
        cardPanel.add(usernameLabel);

        usernameField = new UIHelper.ModernTextField("Enter username");
        usernameField.setBounds(70, 120, 360, 40);
        cardPanel.add(usernameField);

        // DOB
        dobLabel = new JLabel("Date of Birth (YYYY-MM-DD)");
        dobLabel.setFont(UIHelper.FONT_BODY_BOLD);
        dobLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        dobLabel.setBounds(70, 175, 360, 20);
        cardPanel.add(dobLabel);

        dobField = new UIHelper.ModernTextField("YYYY-MM-DD");
        dobField.setBounds(70, 200, 360, 40);
        cardPanel.add(dobField);

        // New Password
        newPasswordLabel = new JLabel("New Password");
        newPasswordLabel.setFont(UIHelper.FONT_BODY_BOLD);
        newPasswordLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        newPasswordLabel.setBounds(70, 255, 360, 20);
        cardPanel.add(newPasswordLabel);

        newPasswordField = new UIHelper.ModernPasswordField("Enter new password");
        newPasswordField.setBounds(70, 280, 360, 40);
        cardPanel.add(newPasswordField);

        // Change Password Button
        changePasswordButton = new UIHelper.ModernButton("Change Password", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        changePasswordButton.setBounds(70, 350, 360, 45);
        changePasswordButton.addActionListener(this);
        cardPanel.add(changePasswordButton);

        // Go Back Label
        goBackLabel = new JLabel("Go back to User Login", SwingConstants.CENTER);
        goBackLabel.setFont(UIHelper.FONT_BODY);
        goBackLabel.setForeground(UIHelper.COLOR_TEXT_MUTED);
        goBackLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goBackLabel.setBounds(50, 415, 400, 25);
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

    private PasswordUpdateResult updatePasswordInFile(String enteredUsername, String enteredDOB, String newPassword) {
        PasswordUpdateResult result = new PasswordUpdateResult();
        File inputFile = new File("Data/signup.txt");
        File tempFile = new File("Data/temp.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length >= 4) {
                    String storedUsername = userData[0].trim();
                    String storedDOB = userData[2].trim();

                    if (enteredUsername.equalsIgnoreCase(storedUsername) && enteredDOB.equals(storedDOB)) {
                        userData[3] = newPassword;
                        line = String.join(",", userData);
                        result.userFound = true;
                    }
                }
                writer.write(line + System.lineSeparator());
            }
            result.success = true;
        } catch (IOException e) {
            e.printStackTrace();
            result.success = false;
        }

        if (result.success) {
            if (!inputFile.delete() || !tempFile.renameTo(inputFile)) {
                result.success = false;
            }
        }
        return result;
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == changePasswordButton) {
            String enteredUsername = usernameField.getText().trim();
            String enteredDOB = dobField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();

            if (enteredUsername.isEmpty() || enteredDOB.isEmpty() || newPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All input fields must be filled.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            PasswordUpdateResult updateResult = updatePasswordInFile(enteredUsername, enteredDOB, newPassword);

            if (!updateResult.userFound) {
                JOptionPane.showMessageDialog(this, "User details matching username and date of birth could not be found.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!updateResult.success) {
                JOptionPane.showMessageDialog(this, "An error occurred while updating the password.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Password updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                WelcomePage welcomePage = new WelcomePage();
                welcomePage.setVisible(true);
            }
        }
    }

    public static void main(String[] args) {
        ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();
        forgotPasswordPage.setVisible(true);
    }
}
