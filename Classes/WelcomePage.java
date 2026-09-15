package Classes;

import java.lang.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomePage extends JFrame implements MouseListener, ActionListener {
    UIHelper.RoundedPanel cardPanel;
    JLabel titleLabel, userLoginLabel, usernameLabel, passwordLabel, forgotPasswordLabel, adminLoginLabel;
    UIHelper.ModernTextField usernameField;
    UIHelper.ModernPasswordField passwordField;
    UIHelper.ModernButton loginButton, signupButton;

    // Store logged-in user
    private static String loggedInUsername;

    public WelcomePage() {
        super("Metro Rail Ticket Vending System");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon("Images/icon.png").getImage());

        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int screenWidth = maxBounds.width;
        int screenHeight = maxBounds.height;

        // Base image background (Dhaka Metro Rail)
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
        titleLabel.setBounds(200, 60, 800, 50);
        container.add(titleLabel);

        // Glassmorphism Card Panel (Centered, Semi-Transparent Rounded Panel)
        cardPanel = new UIHelper.RoundedPanel(30, new Color(15, 23, 42, 195)); // Semi-transparent Slate 900
        cardPanel.setLayout(null);
        cardPanel.setBounds(350, 160, 500, 480);
        container.add(cardPanel);

        // Card Subtitle
        userLoginLabel = new JLabel("User Login", SwingConstants.CENTER);
        userLoginLabel.setFont(UIHelper.FONT_SUBTITLE);
        userLoginLabel.setForeground(UIHelper.COLOR_PRIMARY);
        userLoginLabel.setBounds(50, 40, 400, 40);
        cardPanel.add(userLoginLabel);

        // Username
        usernameLabel = new JLabel("Username");
        usernameLabel.setFont(UIHelper.FONT_BODY_BOLD);
        usernameLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        usernameLabel.setBounds(70, 110, 360, 25);
        cardPanel.add(usernameLabel);

        usernameField = new UIHelper.ModernTextField("Enter username");
        usernameField.setBounds(70, 140, 360, 45);
        cardPanel.add(usernameField);

        // Password
        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(UIHelper.FONT_BODY_BOLD);
        passwordLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        passwordLabel.setBounds(70, 200, 360, 25);
        cardPanel.add(passwordLabel);

        passwordField = new UIHelper.ModernPasswordField("Enter password");
        passwordField.setBounds(70, 230, 360, 45);
        cardPanel.add(passwordField);

        // Login & Signup Buttons
        loginButton = new UIHelper.ModernButton("Login", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        loginButton.setBounds(70, 300, 170, 45);
        loginButton.addActionListener(this);
        cardPanel.add(loginButton);

        signupButton = new UIHelper.ModernButton("Register", UIHelper.COLOR_ACCENT, UIHelper.COLOR_ACCENT_HOVER);
        signupButton.setBounds(260, 300, 170, 45);
        signupButton.addActionListener(this);
        cardPanel.add(signupButton);

        // Forgot Password link
        forgotPasswordLabel = new JLabel("Forgot Password?", SwingConstants.CENTER);
        forgotPasswordLabel.setFont(UIHelper.FONT_BODY);
        forgotPasswordLabel.setForeground(UIHelper.COLOR_TEXT_MUTED);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.setBounds(50, 370, 400, 25);
        forgotPasswordLabel.addMouseListener(this);
        cardPanel.add(forgotPasswordLabel);

        // Admin login link
        adminLoginLabel = new JLabel("Administrator Login", SwingConstants.CENTER);
        adminLoginLabel.setFont(UIHelper.FONT_BODY);
        adminLoginLabel.setForeground(UIHelper.COLOR_TEXT_MUTED);
        adminLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminLoginLabel.setBounds(50, 405, 400, 25);
        adminLoginLabel.addMouseListener(this);
        cardPanel.add(adminLoginLabel);
    }

    public void setLoggedInUsername(String username) {
        loggedInUsername = username;
    }

    private boolean isValidCredentials(String enteredUsername, String enteredPassword) {
        try (BufferedReader reader = new BufferedReader(new FileReader("Data/signup.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length >= 4) {
                    String storedUsername = userData[0].trim();
                    String storedPassword = userData[3].trim();
                    if (enteredUsername.equals(storedUsername) && enteredPassword.equals(storedPassword)) {
                        loggedInUsername = enteredUsername;
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error reading database records.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    public static String getLoggedInUsername() {
        return loggedInUsername;
    }

    public void mouseClicked(MouseEvent me) {
        if (me.getSource() == forgotPasswordLabel) {
            dispose();
            ForgotPasswordPage forgotPasswordPage = new ForgotPasswordPage();
            forgotPasswordPage.setVisible(true);
        } else if (me.getSource() == adminLoginLabel) {
            dispose();
            AdminLoginPage adminLoginPage = new AdminLoginPage();
            adminLoginPage.setVisible(true);
        }
    }

    public void mousePressed(MouseEvent me) {}
    public void mouseReleased(MouseEvent me) {}

    public void mouseEntered(MouseEvent me) {
        if (me.getSource() == forgotPasswordLabel || me.getSource() == adminLoginLabel) {
            ((JLabel) me.getSource()).setForeground(UIHelper.COLOR_PRIMARY);
        }
    }

    public void mouseExited(MouseEvent me) {
        if (me.getSource() == forgotPasswordLabel || me.getSource() == adminLoginLabel) {
            ((JLabel) me.getSource()).setForeground(UIHelper.COLOR_TEXT_MUTED);
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == signupButton) {
            dispose();
            SignUpPage signUpPage = new SignUpPage();
            signUpPage.setVisible(true);
        } else if (ae.getSource() == loginButton) {
            String enteredUsername = usernameField.getText().trim();
            String enteredPassword = new String(passwordField.getPassword());

            if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Username and password fields cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (isValidCredentials(enteredUsername, enteredPassword)) {
                JOptionPane.showMessageDialog(this, "Welcome back, " + enteredUsername + "! Login Successful.", "Success", JOptionPane.INFORMATION_MESSAGE);
                setLoggedInUsername(enteredUsername);
                dispose();
                MetroTicketVendingSystemPage systemPage = new MetroTicketVendingSystemPage();
                systemPage.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Access Denied", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        WelcomePage welcomePage = new WelcomePage();
        welcomePage.setVisible(true);
    }
}