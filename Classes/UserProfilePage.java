package Classes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class UserProfilePage extends JFrame {
    private JLabel headerLabel, backLabel;
    private UIHelper.RoundedPanel profileCard, historyCard;
    private JTable table;
    private DefaultTableModel tableModel;
    private UIHelper.ModernButton reprintButton;

    private JPanel cardWrapperPanel = null;

    public UserProfilePage() {
        super("My Profile & Trips - Metro Ticket System");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setIconImage(new ImageIcon("Images/icon.png").getImage());

        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int screenWidth = maxBounds.width;
        int screenHeight = maxBounds.height;

        // Base gradient background
        UIHelper.GradientPanel backgroundPanel = new UIHelper.GradientPanel(
            UIHelper.COLOR_BG_DARK, 
            new Color(9, 13, 22)
        );
        backgroundPanel.setBounds(0, 0, screenWidth, screenHeight);
        this.add(backgroundPanel);

        // Centered container of size 1200x800
        JPanel container = new JPanel();
        container.setLayout(null);
        container.setOpaque(false);
        container.setBounds((screenWidth - 1200) / 2, (screenHeight - 800) / 2, 1200, 800);
        backgroundPanel.add(container);

        // Header Title
        headerLabel = new JLabel("PASSENGER PORTAL", SwingConstants.CENTER);
        headerLabel.setFont(UIHelper.FONT_TITLE);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBounds(200, 40, 800, 50);
        container.add(headerLabel);

        // Back Label (Top Left)
        backLabel = new JLabel("<- Back to Terminal", SwingConstants.LEFT);
        backLabel.setFont(UIHelper.FONT_BODY_BOLD);
        backLabel.setForeground(UIHelper.COLOR_TEXT_MUTED);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.setBounds(50, 20, 200, 30);
        backLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dispose();
                MetroTicketVendingSystemPage page = new MetroTicketVendingSystemPage();
                page.setVisible(true);
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backLabel.setForeground(UIHelper.COLOR_PRIMARY);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backLabel.setForeground(UIHelper.COLOR_TEXT_MUTED);
            }
        });
        container.add(backLabel);

        // Fetch User Info
        String loggedInUser = WelcomePage.getLoggedInUsername();
        if (loggedInUser == null) {
            loggedInUser = "Guest";
        }
        
        String fullName = loggedInUser;
        String phoneNumber = "N/A";
        String dob = "N/A";
        
        if (!loggedInUser.equals("Guest")) {
            try (BufferedReader reader = new BufferedReader(new FileReader("Data/signup.txt"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] data = line.split(",");
                    if (data.length > 0 && data[0].trim().equalsIgnoreCase(loggedInUser)) {
                        fullName = data[0].trim();
                        if (data.length > 1) phoneNumber = data[1].trim();
                        if (data.length > 2) dob = data[2].trim();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Fetch Transactions
        List<String[]> userTransactions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("Data/transactions.txt"))) {
            String line = reader.readLine(); // Skip header
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 6) {
                    String txUser = data[1].trim();
                    if (txUser.equalsIgnoreCase(loggedInUser)) {
                        userTransactions.add(data);
                    }
                }
            }
        } catch (IOException e) {
            // File might not exist yet if clean install
        }
        Collections.reverse(userTransactions); // Show latest first

        // --- LEFT PANEL: Profile Details & Smart Card ---
        profileCard = new UIHelper.RoundedPanel(25, UIHelper.COLOR_BG_PANEL);
        profileCard.setLayout(null);
        profileCard.setBounds(50, 110, 380, 620); // Taller profile card
        container.add(profileCard);

        JLabel profileTitle = new JLabel("PASSENGER PROFILE", SwingConstants.CENTER);
        profileTitle.setFont(UIHelper.FONT_SUBTITLE);
        profileTitle.setForeground(UIHelper.COLOR_PRIMARY);
        profileTitle.setBounds(20, 20, 340, 30);
        profileCard.add(profileTitle);

        // Vector Avatar Graphic (More compact)
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(UIHelper.COLOR_INPUT_BG);
                g2d.fillOval(5, 5, 80, 80);
                g2d.setColor(UIHelper.COLOR_PRIMARY);
                g2d.setStroke(new BasicStroke(2.5f));
                g2d.drawOval(5, 5, 80, 80);
                
                g2d.setColor(UIHelper.COLOR_TEXT_MUTED);
                g2d.fillOval(30, 20, 30, 30);
                g2d.fillArc(15, 52, 60, 32, 0, 180);
            }
        };
        avatarPanel.setBounds(145, 60, 90, 90);
        avatarPanel.setOpaque(false);
        profileCard.add(avatarPanel);

        // Details labels (More compact vertical spacing)
        addDetailRow(profileCard, "Full Name", fullName, 160);
        addDetailRow(profileCard, "Phone Number", phoneNumber, 210);
        addDetailRow(profileCard, "Date of Birth", dob, 260);

        int totalTrips = 0;
        for (String[] tx : userTransactions) {
            try {
                totalTrips += Integer.parseInt(tx[4].trim());
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        // Render the Smart Card Subsystem Section
        renderMRTPassSection(loggedInUser, totalTrips);

        // --- RIGHT PANEL: Trip History JTable ---
        historyCard = new UIHelper.RoundedPanel(25, UIHelper.COLOR_BG_PANEL);
        historyCard.setLayout(null);
        historyCard.setBounds(460, 110, 690, 620);
        container.add(historyCard);

        JLabel historyTitle = new JLabel("TRIP HISTORY & TRANSACTIONS", SwingConstants.CENTER);
        historyTitle.setFont(UIHelper.FONT_SUBTITLE);
        historyTitle.setForeground(UIHelper.COLOR_PRIMARY);
        historyTitle.setBounds(30, 20, 630, 30);
        historyCard.add(historyTitle);

        // Setup Table
        tableModel = new DefaultTableModel(
            new Object[]{"Date & Time", "Origin", "Destination", "Qty", "Cost"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (String[] tx : userTransactions) {
            tableModel.addRow(new Object[]{
                tx[0],
                tx[2],
                tx[3],
                tx[4],
                tx[5] + " BDT"
            });
        }

        table = new JTable(tableModel);
        table.setBackground(UIHelper.COLOR_INPUT_BG);
        table.setForeground(Color.WHITE);
        table.setFont(UIHelper.FONT_BODY);
        table.setRowHeight(35);
        table.setSelectionBackground(UIHelper.COLOR_PRIMARY_HOVER);
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(new Color(255, 255, 255, 10));
        table.setShowGrid(true);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(UIHelper.COLOR_PRIMARY);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setFont(UIHelper.FONT_BODY_BOLD);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 75, 630, 280);
        scrollPane.getViewport().setBackground(UIHelper.COLOR_BG_PANEL);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 15), 1));
        historyCard.add(scrollPane);

        // Eco-Savings & Stats Panel
        double co2Saved = totalTrips * 0.18;
        double moneySaved = totalTrips * 150.0;

        UIHelper.RoundedPanel ecoStatsPanel = new UIHelper.RoundedPanel(20, UIHelper.COLOR_BG_DARK);
        ecoStatsPanel.setLayout(null);
        ecoStatsPanel.setBounds(30, 375, 630, 150);
        historyCard.add(ecoStatsPanel);

        JLabel ecoHeader = new JLabel("YOUR GREEN COMMUTE IMPACT", SwingConstants.CENTER);
        ecoHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        ecoHeader.setForeground(UIHelper.COLOR_PRIMARY);
        ecoHeader.setBounds(20, 12, 590, 20);
        ecoStatsPanel.add(ecoHeader);

        // CO2 Saved Card
        UIHelper.RoundedPanel co2Card = new UIHelper.RoundedPanel(15, UIHelper.COLOR_INPUT_BG);
        co2Card.setLayout(null);
        co2Card.setBounds(15, 42, 290, 92);
        ecoStatsPanel.add(co2Card);

        JLabel co2Title = new JLabel("CARBON FOOTPRINT REDUCED", SwingConstants.CENTER);
        co2Title.setFont(new Font("Segoe UI", Font.BOLD, 9));
        co2Title.setForeground(UIHelper.COLOR_TEXT_MUTED);
        co2Title.setBounds(10, 15, 270, 15);
        co2Card.add(co2Title);

        JLabel co2Val = new JLabel(String.format(java.util.Locale.US, "%.2f kg CO₂", co2Saved), SwingConstants.CENTER);
        co2Val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        co2Val.setForeground(new Color(52, 211, 153)); // Light emerald green
        co2Val.setBounds(10, 35, 270, 35);
        co2Card.add(co2Val);

        JLabel co2Sub = new JLabel("vs. private vehicle travel", SwingConstants.CENTER);
        co2Sub.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        co2Sub.setForeground(UIHelper.COLOR_TEXT_MUTED);
        co2Sub.setBounds(10, 68, 270, 15);
        co2Card.add(co2Sub);

        // Savings Card
        UIHelper.RoundedPanel saveCard = new UIHelper.RoundedPanel(15, UIHelper.COLOR_INPUT_BG);
        saveCard.setLayout(null);
        saveCard.setBounds(325, 42, 290, 92);
        ecoStatsPanel.add(saveCard);

        JLabel saveTitle = new JLabel("ESTIMATED SAVINGS VS TAXI", SwingConstants.CENTER);
        saveTitle.setFont(new Font("Segoe UI", Font.BOLD, 9));
        saveTitle.setForeground(UIHelper.COLOR_TEXT_MUTED);
        saveTitle.setBounds(10, 15, 270, 15);
        saveCard.add(saveTitle);

        JLabel saveVal = new JLabel(String.format(java.util.Locale.US, "%.0f BDT", moneySaved), SwingConstants.CENTER);
        saveVal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        saveVal.setForeground(new Color(251, 191, 36)); // Amber yellow
        saveVal.setBounds(10, 35, 270, 35);
        saveCard.add(saveVal);

        JLabel saveSub = new JLabel("based on private taxi fares", SwingConstants.CENTER);
        saveSub.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        saveSub.setForeground(UIHelper.COLOR_TEXT_MUTED);
        saveSub.setBounds(10, 68, 270, 15);
        saveCard.add(saveSub);

        // Reprint Action Button
        reprintButton = new UIHelper.ModernButton("Reprint Selected Ticket (PDF)", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        reprintButton.setBounds(30, 545, 630, 45);
        reprintButton.addActionListener(e -> reprintSelectedTicket());
        historyCard.add(reprintButton);
    }

    private void renderMRTPassSection(String username, int totalTrips) {
        if (cardWrapperPanel != null) {
            profileCard.remove(cardWrapperPanel);
        }

        cardWrapperPanel = new JPanel();
        cardWrapperPanel.setLayout(null);
        cardWrapperPanel.setOpaque(false);
        cardWrapperPanel.setBounds(20, 315, 340, 280);
        profileCard.add(cardWrapperPanel);

        MRTPassManager.PassInfo pass = MRTPassManager.getPass(username);

        if (pass == null) {
            UIHelper.RoundedPanel placeholderCard = new UIHelper.RoundedPanel(20, UIHelper.COLOR_INPUT_BG);
            placeholderCard.setLayout(null);
            placeholderCard.setBounds(0, 0, 340, 180);
            cardWrapperPanel.add(placeholderCard);

            JLabel noPassLbl = new JLabel("No MRT Pass Linked", SwingConstants.CENTER);
            noPassLbl.setFont(UIHelper.FONT_BODY_BOLD);
            noPassLbl.setForeground(UIHelper.COLOR_TEXT_MUTED);
            noPassLbl.setBounds(10, 60, 320, 25);
            placeholderCard.add(noPassLbl);

            JLabel infoLbl = new JLabel("Issue a virtual smart card for easy travel", SwingConstants.CENTER);
            infoLbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            infoLbl.setForeground(UIHelper.COLOR_TEXT_MUTED);
            infoLbl.setBounds(10, 90, 320, 20);
            placeholderCard.add(infoLbl);

            UIHelper.ModernButton issueBtn = new UIHelper.ModernButton("Issue Virtual MRT Pass", UIHelper.COLOR_ACCENT, UIHelper.COLOR_ACCENT_HOVER);
            issueBtn.setBounds(0, 200, 340, 45);
            issueBtn.addActionListener(e -> {
                MRTPassManager.createPass(username);
                JOptionPane.showMessageDialog(this, "Virtual MRT Pass issued successfully!\nStarting balance: 0.0 BDT", "Pass Created", JOptionPane.INFORMATION_MESSAGE);
                renderMRTPassSection(username, totalTrips);
                profileCard.repaint();
            });
            cardWrapperPanel.add(issueBtn);
        } else {
            Color cardColor = pass.status.equals("Active") ? new Color(16, 185, 129) : new Color(100, 116, 139);
            UIHelper.RoundedPanel passCard = new UIHelper.RoundedPanel(20, cardColor);
            passCard.setLayout(null);
            passCard.setBounds(0, 0, 340, 180);
            cardWrapperPanel.add(passCard);

            JLabel cardHeader = new JLabel("MRT PASS");
            cardHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
            cardHeader.setForeground(Color.WHITE);
            cardHeader.setBounds(15, 15, 150, 22);
            passCard.add(cardHeader);

            JLabel statusBadge = new JLabel(pass.status.toUpperCase(), SwingConstants.CENTER);
            statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
            statusBadge.setForeground(pass.status.equals("Active") ? new Color(16, 185, 129) : Color.WHITE);
            statusBadge.setOpaque(true);
            statusBadge.setBackground(pass.status.equals("Active") ? Color.WHITE : new Color(239, 68, 68));
            statusBadge.setBounds(255, 15, 70, 22);
            passCard.add(statusBadge);

            JPanel chip = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    GradientPaint gp = new GradientPaint(0, 0, new Color(245, 158, 11), 0, getHeight(), new Color(217, 119, 6));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    
                    g2.setColor(new Color(78, 51, 0, 100));
                    g2.setStroke(new BasicStroke(1f));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                    g2.drawLine(getWidth()/3, 0, getWidth()/3, getHeight());
                    g2.drawLine(2*getWidth()/3, 0, 2*getWidth()/3, getHeight());
                    g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
                    g2.drawOval(getWidth()/3, getHeight()/4, getWidth()/3, getHeight()/2);
                }
            };
            chip.setBounds(15, 48, 38, 28);
            chip.setOpaque(false);
            passCard.add(chip);

            // Vector Tier Badge
            if (totalTrips > 0) {
                final String tierName;
                final Color badgeColor;
                final Color textColor;
                if (totalTrips >= 15) {
                    tierName = "EMERALD";
                    badgeColor = new Color(5, 150, 105); // Emerald Green
                    textColor = Color.WHITE;
                } else if (totalTrips >= 8) {
                    tierName = "GOLD";
                    badgeColor = new Color(245, 158, 11); // Gold yellow
                    textColor = Color.WHITE;
                } else if (totalTrips >= 4) {
                    tierName = "SILVER";
                    badgeColor = new Color(148, 163, 184); // Silver gray
                    textColor = Color.WHITE;
                } else {
                    tierName = "BRONZE";
                    badgeColor = new Color(180, 83, 9); // Bronze orange-brown
                    textColor = Color.WHITE;
                }

                JPanel badgePanel = new JPanel() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g;
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        int w = getWidth();
                        int h = getHeight();

                        // Draw shield shape badge
                        int[] xPoints = { w/2, w - 2, w - 2, w/2, 2, 2 };
                        int[] yPoints = { 2, 10, h - 12, h - 2, h - 12, 10 };
                        g2.setColor(badgeColor);
                        g2.fillPolygon(xPoints, yPoints, 6);

                        // Draw border highlight
                        g2.setColor(Color.WHITE);
                        g2.setStroke(new BasicStroke(1.5f));
                        g2.drawPolygon(xPoints, yPoints, 6);

                        // Draw tier text
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 7));
                        g2.setColor(textColor);
                        FontMetrics fm = g2.getFontMetrics();
                        int textX = (w - fm.stringWidth(tierName)) / 2;
                        int textY = h / 2 + 3;
                        g2.drawString(tierName, textX, textY);
                    }
                };
                badgePanel.setBounds(265, 48, 55, 38);
                badgePanel.setOpaque(false);
                passCard.add(badgePanel);
            }

            String formattedCardNum = pass.cardNumber.substring(0, 4) + " " + pass.cardNumber.substring(4, 8) + " " + pass.cardNumber.substring(8);
            JLabel cardNumLbl = new JLabel(formattedCardNum);
            cardNumLbl.setFont(new Font("Courier New", Font.BOLD, 18));
            cardNumLbl.setForeground(Color.WHITE);
            cardNumLbl.setBounds(15, 90, 310, 25);
            passCard.add(cardNumLbl);

            JLabel cardHolderLbl = new JLabel(pass.username.toUpperCase());
            cardHolderLbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            cardHolderLbl.setForeground(new Color(241, 245, 249));
            cardHolderLbl.setBounds(15, 120, 200, 20);
            passCard.add(cardHolderLbl);

            JLabel balanceLbl = new JLabel(pass.balance + " BDT", SwingConstants.RIGHT);
            balanceLbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
            balanceLbl.setForeground(Color.WHITE);
            balanceLbl.setBounds(150, 140, 175, 28);
            passCard.add(balanceLbl);

            JLabel typeLbl = new JLabel("COMMUTER CARD");
            typeLbl.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            typeLbl.setForeground(new Color(241, 245, 249, 180));
            typeLbl.setBounds(15, 145, 150, 15);
            passCard.add(typeLbl);

            UIHelper.ModernButton rechargeBtn = new UIHelper.ModernButton("Recharge Card", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
            rechargeBtn.setBounds(0, 200, 160, 45);
            rechargeBtn.addActionListener(e -> {
                if (!pass.status.equals("Active")) {
                    JOptionPane.showMessageDialog(this, "Your MRT Pass is currently Blocked. Please activate it first.", "Card Blocked", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                String amtStr = JOptionPane.showInputDialog(this, "Enter Recharge Amount (BDT):", "Recharge MRT Pass", JOptionPane.QUESTION_MESSAGE);
                if (amtStr != null && !amtStr.trim().isEmpty()) {
                    try {
                        double amount = Double.parseDouble(amtStr.trim());
                        if (amount <= 0) {
                            JOptionPane.showMessageDialog(this, "Amount must be greater than zero.", "Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        dispose();
                        PaymentPage paymentPage = new PaymentPage("CARD_RECHARGE", 0, 1, amount, amount);
                    } catch (NumberFormatException nfe) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid numeric amount.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            cardWrapperPanel.add(rechargeBtn);

            String toggleText = pass.status.equals("Active") ? "Block Card" : "Activate Card";
            Color baseColor = pass.status.equals("Active") ? UIHelper.COLOR_DANGER : UIHelper.COLOR_ACCENT;
            Color hoverColor = pass.status.equals("Active") ? UIHelper.COLOR_DANGER_HOVER : UIHelper.COLOR_ACCENT_HOVER;

            UIHelper.ModernButton toggleBtn = new UIHelper.ModernButton(toggleText, baseColor, hoverColor);
            toggleBtn.setBounds(180, 200, 160, 45);
            toggleBtn.addActionListener(e -> {
                pass.status = pass.status.equals("Active") ? "Blocked" : "Active";
                MRTPassManager.savePass(pass);
                JOptionPane.showMessageDialog(this, "Card status updated to: " + pass.status, "Status Updated", JOptionPane.INFORMATION_MESSAGE);
                renderMRTPassSection(username, totalTrips);
                profileCard.repaint();
            });
            cardWrapperPanel.add(toggleBtn);
        }
        profileCard.revalidate();
    }

    private void addDetailRow(JPanel card, String title, String val, int y) {
        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tLbl.setForeground(UIHelper.COLOR_TEXT_MUTED);
        tLbl.setBounds(30, y, 320, 18);
        card.add(tLbl);

        JLabel vLbl = new JLabel(val);
        vLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        vLbl.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        vLbl.setBounds(30, y + 18, 320, 22);
        card.add(vLbl);
    }

    private void reprintSelectedTicket() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a trip from the history list first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dateTimeStr = (String) tableModel.getValueAt(selectedRow, 0);
        String fromStation = (String) tableModel.getValueAt(selectedRow, 1);
        String toStation = (String) tableModel.getValueAt(selectedRow, 2);
        String qtyStr = (String) tableModel.getValueAt(selectedRow, 3);
        String totalCostStr = (String) tableModel.getValueAt(selectedRow, 4);

        int quantity = Integer.parseInt(qtyStr);
        double totalCost = Double.parseDouble(totalCostStr.replace(" BDT", "").trim());
        double rate = totalCost / quantity;

        String timestamp = dateTimeStr.replaceAll("[^0-9]", "");
        String ticketId = "MRTVS-REPRINT-" + timestamp;

        String username = WelcomePage.getLoggedInUsername();
        if (username == null) username = "Guest";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Reprinted PDF Ticket");
        fileChooser.setSelectedFile(new File("metro_ticket_reprint_" + username + "_" + timestamp + ".pdf"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Documents (*.pdf)", "pdf"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(filePath + ".pdf");
            }

            try {
                byte[] pdfBytes = PDFTicketGenerator.generateTicketPDF(
                    username,
                    fromStation,
                    toStation,
                    quantity,
                    rate,
                    totalCost,
                    ticketId,
                    dateTimeStr
                );

                try (FileOutputStream fos = new FileOutputStream(fileToSave)) {
                    fos.write(pdfBytes);
                }

                File ticketsDir = new File("Tickets");
                if (!ticketsDir.exists()) {
                    ticketsDir.mkdirs();
                }
                File ticketCopy = new File(ticketsDir, "metro_ticket_reprint_" + username + "_" + timestamp + ".pdf");
                try (FileOutputStream fosCopy = new FileOutputStream(ticketCopy)) {
                    fosCopy.write(pdfBytes);
                }

                int openChoice = JOptionPane.showConfirmDialog(this,
                    "PDF Ticket reprinted successfully!\nWould you like to open it now?",
                    "Ticket Reprinted",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

                if (openChoice == JOptionPane.YES_OPTION) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop desktop = Desktop.getDesktop();
                        if (desktop.isSupported(Desktop.Action.OPEN)) {
                            desktop.open(fileToSave);
                        } else {
                            JOptionPane.showMessageDialog(this, 
                                "Opening files is not supported on this platform.", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Desktop operations are not supported on this platform.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, 
                    "Failed to reprint PDF Ticket:\n" + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserProfilePage page = new UserProfilePage();
            page.setVisible(true);
        });
    }
}
