package Classes;

import java.lang.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminLoginPage extends JFrame implements ActionListener {
    UIHelper.RoundedPanel cardPanel;
    JLabel titleLabel, formTitleLabel, adminIdLabel, adminPasswordLabel, goBackLabel;
    UIHelper.ModernTextField adminIdField;
    UIHelper.ModernPasswordField adminPasswordField;
    UIHelper.ModernButton loginButton;

    public AdminLoginPage() {
        super("Administrator Login - Metro Ticket System");
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
        titleLabel.setBounds(200, 60, 800, 50);
        container.add(titleLabel);

        // Card Panel (Glassmorphic)
        cardPanel = new UIHelper.RoundedPanel(30, new Color(15, 23, 42, 195));
        cardPanel.setLayout(null);
        cardPanel.setBounds(350, 160, 500, 450);
        container.add(cardPanel);

        formTitleLabel = new JLabel("Administrator Login", SwingConstants.CENTER);
        formTitleLabel.setFont(UIHelper.FONT_SUBTITLE);
        formTitleLabel.setForeground(UIHelper.COLOR_ACCENT);
        formTitleLabel.setBounds(50, 35, 400, 40);
        cardPanel.add(formTitleLabel);

        // Admin ID
        adminIdLabel = new JLabel("Admin ID");
        adminIdLabel.setFont(UIHelper.FONT_BODY_BOLD);
        adminIdLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        adminIdLabel.setBounds(70, 105, 360, 20);
        cardPanel.add(adminIdLabel);

        adminIdField = new UIHelper.ModernTextField("Enter Admin ID");
        adminIdField.setBounds(70, 130, 360, 40);
        cardPanel.add(adminIdField);

        // Admin Password
        adminPasswordLabel = new JLabel("Admin Password");
        adminPasswordLabel.setFont(UIHelper.FONT_BODY_BOLD);
        adminPasswordLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        adminPasswordLabel.setBounds(70, 185, 360, 20);
        cardPanel.add(adminPasswordLabel);

        adminPasswordField = new UIHelper.ModernPasswordField("Enter Password");
        adminPasswordField.setBounds(70, 210, 360, 40);
        cardPanel.add(adminPasswordField);

        // Login Button
        loginButton = new UIHelper.ModernButton("Login as Admin", UIHelper.COLOR_ACCENT, UIHelper.COLOR_ACCENT_HOVER);
        loginButton.setBounds(70, 280, 360, 45);
        loginButton.addActionListener(this);
        cardPanel.add(loginButton);

        // Go Back Label
        goBackLabel = new JLabel("Go back to User Login", SwingConstants.CENTER);
        goBackLabel.setFont(UIHelper.FONT_BODY);
        goBackLabel.setForeground(UIHelper.COLOR_TEXT_MUTED);
        goBackLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goBackLabel.setBounds(50, 350, 400, 25);
        goBackLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent me) {
                dispose();
                WelcomePage welcomePage = new WelcomePage();
                welcomePage.setVisible(true);
            }
            @Override
            public void mouseEntered(MouseEvent me) {
                goBackLabel.setForeground(UIHelper.COLOR_ACCENT);
            }
            @Override
            public void mouseExited(MouseEvent me) {
                goBackLabel.setForeground(UIHelper.COLOR_TEXT_MUTED);
            }
        });
        cardPanel.add(goBackLabel);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loginButton) {
            String adminId = adminIdField.getText().trim();
            String adminPassword = new String(adminPasswordField.getPassword()).trim();

            if (adminId.equals("admin") && adminPassword.equals("admin")) {
                dispose();
                AdminPage adminPage = new AdminPage();
                adminPage.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Administrator ID or password.", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        AdminLoginPage adminLoginPage = new AdminLoginPage();
        adminLoginPage.setVisible(true);
    }
}
