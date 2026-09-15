package Classes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentPage extends JFrame implements ActionListener {
    private static final String[] STATION_NAMES = {
            "Uttara North", "Uttara Center", "Uttara South", 
            "Pallabi", "Mirpur 11", "Mirpur 10", 
            "Kazipara", "Sheorapara", "Agargaon"
    };

    private String currentStation;
    private int selectedDestination;
    private int selectedTicketQuantity;
    private double ticketFare;
    private double totalCost;
    private double amountInserted;
    private double returnedAmount;

    // GUI components
    private UIHelper.RoundedPanel cashPanel, ticketCardPanel;
    private JLabel headerLabel, userGreetLabel;
    private JLabel totalCostLabel, insertedLabel, refundLabel, statusLabel;
    private UIHelper.ModernButton tk10Button, tk20Button, tk50Button, tk100Button, tk500Button;
    private UIHelper.ModernButton payButton, cancelButton;

    // Multi-Channel Payment components
    private JPanel cashSubPanel, digitalSubPanel, passSubPanel;
    private UIHelper.ModernButton btnCashMode, btnDigitalMode, btnPassMode;
    private UIHelper.ModernButton btnBkash, btnNagad, btnCard;
    private UIHelper.ModernTextField txtNumber;
    private UIHelper.ModernPasswordField txtPin;
    private UIHelper.ModernButton btnDigitalPay;
    private JLabel lblNumber, lblPin;
    private int digitalMethod = 0; // 0=bKash, 1=Nagad, 2=Card

    // On-screen Receipt components
    private JLabel recOrigin, recDest, recQty, recRate, recTotal, recInserted, recChange;

    public PaymentPage(String currentStation, int selectedDestination, int selectedTicketQuantity, double ticketFare, double totalCost) {
        super("Secure Payment - Metro Ticket System");
        this.currentStation = currentStation;
        this.selectedDestination = selectedDestination;
        this.selectedTicketQuantity = selectedTicketQuantity;
        this.ticketFare = ticketFare;
        this.totalCost = totalCost;
        this.amountInserted = 0;
        this.returnedAmount = 0;

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
        headerLabel = new JLabel("TICKET PAYMENT TERMINAL", SwingConstants.CENTER);
        headerLabel.setFont(UIHelper.FONT_TITLE);
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBounds(200, 40, 800, 50);
        container.add(headerLabel);

        // Greet Logged-in User
        String username = WelcomePage.getLoggedInUsername();
        if (username == null) username = "Guest";
        userGreetLabel = new JLabel("Passenger: " + username, SwingConstants.RIGHT);
        userGreetLabel.setFont(UIHelper.FONT_BODY_BOLD);
        userGreetLabel.setForeground(UIHelper.COLOR_PRIMARY);
        userGreetLabel.setBounds(850, 15, 300, 30);
        container.add(userGreetLabel);

        // --- LEFT PANEL: Payment Channel Terminal ---
        cashPanel = new UIHelper.RoundedPanel(25, UIHelper.COLOR_BG_PANEL);
        cashPanel.setLayout(null);
        cashPanel.setBounds(70, 130, 500, 480);
        container.add(cashPanel);

        MRTPassManager.PassInfo pass = MRTPassManager.getPass(username);
        boolean hasActivePass = (pass != null && pass.status.equals("Active"));

        // Toggle buttons for Cash vs Digital vs MRT Pass modes
        if (hasActivePass && !currentStation.equals("CARD_RECHARGE")) {
            btnCashMode = new UIHelper.ModernButton("Cash Payment", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
            btnCashMode.setBounds(20, 15, 145, 40);
            btnCashMode.addActionListener(e -> switchPaymentMode(0));
            cashPanel.add(btnCashMode);

            btnDigitalMode = new UIHelper.ModernButton("Digital Wallet", UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
            btnDigitalMode.setBounds(180, 15, 145, 40);
            btnDigitalMode.addActionListener(e -> switchPaymentMode(1));
            cashPanel.add(btnDigitalMode);

            btnPassMode = new UIHelper.ModernButton("MRT Pass", UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
            btnPassMode.setBounds(340, 15, 145, 40);
            btnPassMode.addActionListener(e -> switchPaymentMode(2));
            cashPanel.add(btnPassMode);
        } else {
            btnCashMode = new UIHelper.ModernButton("Cash Payment", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
            btnCashMode.setBounds(20, 15, 220, 40);
            btnCashMode.addActionListener(e -> switchPaymentMode(0));
            cashPanel.add(btnCashMode);

            btnDigitalMode = new UIHelper.ModernButton("Digital Wallet", UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
            btnDigitalMode.setBounds(260, 15, 220, 40);
            btnDigitalMode.addActionListener(e -> switchPaymentMode(1));
            cashPanel.add(btnDigitalMode);
        }

        // --- SUB-PANEL 1: Cash Payment Details ---
        cashSubPanel = new JPanel();
        cashSubPanel.setLayout(null);
        cashSubPanel.setOpaque(false);
        cashSubPanel.setBounds(0, 65, 500, 285);
        cashPanel.add(cashSubPanel);

        JLabel cashTitle = new JLabel("Insert Banknotes (BDT)", SwingConstants.CENTER);
        cashTitle.setFont(UIHelper.FONT_BODY_BOLD);
        cashTitle.setForeground(UIHelper.COLOR_PRIMARY);
        cashTitle.setBounds(50, 5, 400, 25);
        cashSubPanel.add(cashTitle);

        tk10Button = new UIHelper.ModernButton("10 ৳", new Color(180, 83, 9), new Color(146, 64, 14));
        tk10Button.setBounds(60, 40, 170, 50);
        tk10Button.addActionListener(this);
        cashSubPanel.add(tk10Button);

        tk20Button = new UIHelper.ModernButton("20 ৳", new Color(3, 105, 161), new Color(2, 132, 199));
        tk20Button.setBounds(270, 40, 170, 50);
        tk20Button.addActionListener(this);
        cashSubPanel.add(tk20Button);

        tk50Button = new UIHelper.ModernButton("50 ৳", new Color(109, 40, 217), new Color(91, 33, 182));
        tk50Button.setBounds(60, 100, 170, 50);
        tk50Button.addActionListener(this);
        cashSubPanel.add(tk50Button);

        tk100Button = new UIHelper.ModernButton("100 ৳", new Color(15, 118, 110), new Color(13, 148, 136));
        tk100Button.setBounds(270, 100, 170, 50);
        tk100Button.addActionListener(this);
        cashSubPanel.add(tk100Button);

        tk500Button = new UIHelper.ModernButton("500 ৳", new Color(190, 24, 74), new Color(157, 23, 77));
        tk500Button.setBounds(60, 160, 380, 50);
        tk500Button.addActionListener(this);
        cashSubPanel.add(tk500Button);

        // --- SUB-PANEL 2: Digital Payment Details ---
        digitalSubPanel = new JPanel();
        digitalSubPanel.setLayout(null);
        digitalSubPanel.setOpaque(false);
        digitalSubPanel.setBounds(0, 65, 500, 285);
        digitalSubPanel.setVisible(false);
        cashPanel.add(digitalSubPanel);

        btnBkash = new UIHelper.ModernButton("bKash", new Color(225, 20, 110), new Color(190, 10, 90));
        btnBkash.setBounds(40, 5, 130, 35);
        btnBkash.addActionListener(e -> selectDigitalMethod(0));
        digitalSubPanel.add(btnBkash);

        btnNagad = new UIHelper.ModernButton("Nagad", UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
        btnNagad.setBounds(185, 5, 130, 35);
        btnNagad.addActionListener(e -> selectDigitalMethod(1));
        digitalSubPanel.add(btnNagad);

        btnCard = new UIHelper.ModernButton("Card", UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
        btnCard.setBounds(330, 5, 130, 35);
        btnCard.addActionListener(e -> selectDigitalMethod(2));
        digitalSubPanel.add(btnCard);

        QRCodePanel qrCodePanel = new QRCodePanel();
        qrCodePanel.setBounds(40, 55, 100, 100);
        digitalSubPanel.add(qrCodePanel);

        lblNumber = new JLabel("bKash Account Number");
        lblNumber.setFont(UIHelper.FONT_BODY_BOLD);
        lblNumber.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        lblNumber.setBounds(160, 50, 300, 18);
        digitalSubPanel.add(lblNumber);

        txtNumber = new UIHelper.ModernTextField("Enter 11-digit mobile number");
        txtNumber.setBounds(160, 70, 300, 32);
        digitalSubPanel.add(txtNumber);

        lblPin = new JLabel("bKash PIN");
        lblPin.setFont(UIHelper.FONT_BODY_BOLD);
        lblPin.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        lblPin.setBounds(160, 107, 300, 18);
        digitalSubPanel.add(lblPin);

        txtPin = new UIHelper.ModernPasswordField("PIN");
        txtPin.setBounds(160, 127, 300, 32);
        digitalSubPanel.add(txtPin);

        btnDigitalPay = new UIHelper.ModernButton("Verify & Pay " + totalCost + " BDT", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        btnDigitalPay.setBounds(40, 175, 420, 45);
        btnDigitalPay.addActionListener(e -> processDigitalPayment());
        digitalSubPanel.add(btnDigitalPay);

        // --- SUB-PANEL 3: MRT Pass Payment Details (Direct Deduct) ---
        passSubPanel = new JPanel();
        passSubPanel.setLayout(null);
        passSubPanel.setOpaque(false);
        passSubPanel.setBounds(0, 65, 500, 285);
        passSubPanel.setVisible(false);
        cashPanel.add(passSubPanel);

        JLabel passTitle = new JLabel("MRT Pass Card Checkout", SwingConstants.CENTER);
        passTitle.setFont(UIHelper.FONT_BODY_BOLD);
        passTitle.setForeground(UIHelper.COLOR_PRIMARY);
        passTitle.setBounds(50, 10, 400, 25);
        passSubPanel.add(passTitle);

        JLabel lblCardNum = new JLabel("Card Number: " + (pass != null ? (pass.cardNumber.substring(0, 4) + " " + pass.cardNumber.substring(4, 8) + " " + pass.cardNumber.substring(8)) : "N/A"));
        lblCardNum.setFont(UIHelper.FONT_BODY_BOLD);
        lblCardNum.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        lblCardNum.setBounds(60, 50, 380, 25);
        passSubPanel.add(lblCardNum);

        JLabel lblCardBal = new JLabel("Card Balance: " + (pass != null ? pass.balance : 0.0) + " BDT");
        lblCardBal.setFont(UIHelper.FONT_BODY_BOLD);
        lblCardBal.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        lblCardBal.setBounds(60, 80, 380, 25);
        passSubPanel.add(lblCardBal);

        JLabel lblDeduct = new JLabel("Deduction   : -" + totalCost + " BDT");
        lblDeduct.setFont(UIHelper.FONT_BODY_BOLD);
        lblDeduct.setForeground(UIHelper.COLOR_DANGER);
        lblDeduct.setBounds(60, 110, 380, 25);
        passSubPanel.add(lblDeduct);

        JLabel lblPassStatus = new JLabel();
        lblPassStatus.setFont(UIHelper.FONT_BODY_BOLD);
        lblPassStatus.setBounds(60, 145, 380, 25);
        passSubPanel.add(lblPassStatus);

        UIHelper.ModernButton btnPassPay = new UIHelper.ModernButton("Confirm & Pay via MRT Pass", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        btnPassPay.setBounds(40, 190, 420, 45);
        passSubPanel.add(btnPassPay);

        if (pass != null) {
            if (pass.balance >= totalCost) {
                lblPassStatus.setText("Status: SUFFICIENT BALANCE");
                lblPassStatus.setForeground(UIHelper.COLOR_PRIMARY);
                btnPassPay.setEnabled(true);
            } else {
                lblPassStatus.setText("Status: INSUFFICIENT BALANCE (Recharge Required)");
                lblPassStatus.setForeground(UIHelper.COLOR_DANGER);
                btnPassPay.setEnabled(false);
            }
        }

        btnPassPay.addActionListener(evt -> {
            if (pass != null && pass.balance >= totalCost) {
                pass.balance -= totalCost;
                MRTPassManager.savePass(pass);
                
                amountInserted = totalCost;
                insertedLabel.setText("Paid with MRT Pass: " + totalCost + " BDT");
                recInserted.setText("INSERTED     : " + totalCost + " BDT");
                returnedAmount = 0.0;
                refundLabel.setText("Change Due: 0.0 BDT");
                recChange.setText("CHANGE DUE   : 0.0 BDT");
                
                statusLabel.setText("PAID");
                statusLabel.setForeground(UIHelper.COLOR_PRIMARY);
                statusLabel.setBackground(new Color(209, 250, 229));
                
                payButton.setEnabled(true);
                btnPassPay.setEnabled(false);
                btnPassPay.setText("Paid Successfully!");
                
                JOptionPane.showMessageDialog(this, 
                    "MRT Pass Payment Successful!\nNew Card Balance: " + pass.balance + " BDT\nProceed to Print Ticket.", 
                    "Payment Approved", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Numeric billing summary on Cash panel (always visible at bottom)
        totalCostLabel = new JLabel("Total Required: " + totalCost + " BDT");
        totalCostLabel.setFont(UIHelper.FONT_BODY_BOLD);
        totalCostLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        totalCostLabel.setBounds(60, 355, 380, 25);
        cashPanel.add(totalCostLabel);

        insertedLabel = new JLabel("Amount Inserted: 0.0 BDT");
        insertedLabel.setFont(UIHelper.FONT_BODY_BOLD);
        insertedLabel.setForeground(UIHelper.COLOR_ACCENT);
        insertedLabel.setBounds(60, 385, 380, 25);
        cashPanel.add(insertedLabel);

        refundLabel = new JLabel("Change Due: 0.0 BDT");
        refundLabel.setFont(UIHelper.FONT_BODY_BOLD);
        refundLabel.setForeground(UIHelper.COLOR_PRIMARY);
        refundLabel.setBounds(60, 415, 380, 25);
        cashPanel.add(refundLabel);

        // --- RIGHT PANEL: Live Digital Ticket Mockup ---
        ticketCardPanel = new UIHelper.RoundedPanel(25, Color.WHITE);
        ticketCardPanel.setLayout(null);
        ticketCardPanel.setBounds(630, 130, 500, 480);
        container.add(ticketCardPanel);

        // Ticket visual header
        JLabel tHeader = new JLabel(currentStation.equals("CARD_RECHARGE") ? "MRT PASS RECHARGE" : "METRO RAIL TICKET", SwingConstants.CENTER);
        tHeader.setFont(new Font("Courier New", Font.BOLD, 26));
        tHeader.setForeground(Color.BLACK);
        tHeader.setBounds(20, 25, 460, 30);
        ticketCardPanel.add(tHeader);

        JLabel tSubHeader = new JLabel("---------------------------------------------", SwingConstants.CENTER);
        tSubHeader.setFont(new Font("Courier New", Font.PLAIN, 14));
        tSubHeader.setForeground(Color.GRAY);
        tSubHeader.setBounds(20, 55, 460, 15);
        ticketCardPanel.add(tSubHeader);

        // Ticket Details
        if (currentStation.equals("CARD_RECHARGE")) {
            recOrigin = new JLabel("PASSENGER    : " + username);
            recDest = new JLabel("CARD NUMBER  : " + (pass != null ? pass.cardNumber : "N/A"));
            recQty = new JLabel("RECHARGE BDT : " + totalCost + " BDT");
            recRate = new JLabel("STATUS       : ACTIVE");
        } else {
            recOrigin = new JLabel("FROM         : " + currentStation);
            recDest = new JLabel("TO           : " + getStationName(selectedDestination));
            recQty = new JLabel("QUANTITY     : " + selectedTicketQuantity + (selectedTicketQuantity > 1 ? " Tickets" : " Ticket"));
            recRate = new JLabel("RATE         : " + ticketFare + " BDT");
        }

        styleTicketLabel(recOrigin, 90);
        ticketCardPanel.add(recOrigin);

        styleTicketLabel(recDest, 130);
        ticketCardPanel.add(recDest);

        styleTicketLabel(recQty, 170);
        ticketCardPanel.add(recQty);

        styleTicketLabel(recRate, 210);
        ticketCardPanel.add(recRate);

        recTotal = new JLabel(currentStation.equals("CARD_RECHARGE") ? "TOTAL PAID   : " + totalCost + " BDT" : "TOTAL COST   : " + totalCost + " BDT");
        recTotal.setFont(new Font("Courier New", Font.BOLD, 18));
        recTotal.setForeground(Color.BLACK);
        recTotal.setBounds(40, 255, 420, 25);
        ticketCardPanel.add(recTotal);

        recInserted = new JLabel("INSERTED     : 0.0 BDT");
        styleTicketLabel(recInserted, 290);
        ticketCardPanel.add(recInserted);

        recChange = new JLabel("CHANGE DUE   : 0.0 BDT");
        styleTicketLabel(recChange, 320);
        ticketCardPanel.add(recChange);

        statusLabel = new JLabel("UNPAID", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        statusLabel.setForeground(UIHelper.COLOR_DANGER);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(254, 226, 226));
        statusLabel.setBounds(150, 380, 200, 45);
        ticketCardPanel.add(statusLabel);

        // --- BOTTOM ACTIONS ---
        cancelButton = new UIHelper.ModernButton("Cancel & Refund", UIHelper.COLOR_DANGER, UIHelper.COLOR_DANGER_HOVER);
        cancelButton.setBounds(70, 650, 250, 50);
        cancelButton.addActionListener(this);
        container.add(cancelButton);

        payButton = new UIHelper.ModernButton(currentStation.equals("CARD_RECHARGE") ? "Complete Recharge" : "Pay & Print Ticket", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        payButton.setBounds(880, 650, 250, 50);
        payButton.setEnabled(false);
        payButton.addActionListener(this);
        container.add(payButton);

        this.setVisible(true);
    }

    private void styleTicketLabel(JLabel label, int y) {
        label.setFont(new Font("Courier New", Font.BOLD, 16));
        label.setForeground(new Color(55, 65, 81));
        label.setBounds(40, y, 420, 25);
    }

    private String getStationName(int stationNumber) {
        return STATION_NAMES[stationNumber - 1];
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == tk10Button) {
            insertCash(10);
        } else if (e.getSource() == tk20Button) {
            insertCash(20);
        } else if (e.getSource() == tk50Button) {
            insertCash(50);
        } else if (e.getSource() == tk100Button) {
            insertCash(100);
        } else if (e.getSource() == tk500Button) {
            insertCash(500);
        } else if (e.getSource() == payButton) {
            processPayment();
        } else if (e.getSource() == cancelButton) {
            cancelTransaction();
        }
    }

    private void insertCash(double val) {
        amountInserted += val;
        insertedLabel.setText("Amount Inserted: " + amountInserted + " BDT");
        recInserted.setText("INSERTED     : " + amountInserted + " BDT");

        returnedAmount = amountInserted - totalCost;
        double displayChange = returnedAmount < 0 ? 0 : returnedAmount;
        refundLabel.setText("Change Due: " + displayChange + " BDT");
        recChange.setText("CHANGE DUE   : " + displayChange + " BDT");

        if (amountInserted >= totalCost) {
            statusLabel.setText("PAID");
            statusLabel.setForeground(UIHelper.COLOR_PRIMARY);
            statusLabel.setBackground(new Color(209, 250, 229));
            payButton.setEnabled(true);
        } else {
            statusLabel.setText("UNPAID");
            statusLabel.setForeground(UIHelper.COLOR_DANGER);
            statusLabel.setBackground(new Color(254, 226, 226));
            payButton.setEnabled(false);
        }
    }

    private void processPayment() {
        if (amountInserted >= totalCost) {
            String username = WelcomePage.getLoggedInUsername();
            if (username == null) username = "Guest";

            if (currentStation.equals("CARD_RECHARGE")) {
                MRTPassManager.PassInfo pass = MRTPassManager.getPass(username);
                if (pass != null) {
                    pass.balance += totalCost;
                    MRTPassManager.savePass(pass);
                }
                printRechargeReceiptToNotepad();
                JOptionPane.showMessageDialog(this,
                    "Recharge Successful!\n" + totalCost + " BDT has been credited to your card.\nNew Balance: " + (pass != null ? pass.balance : 0) + " BDT.",
                    "Recharge Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                UserProfilePage profilePage = new UserProfilePage();
                profilePage.setVisible(true);
                return;
            }

            // 1. Generate text receipt for passenger
            printTicketToNotepad();

            // 2. Log transaction in audit log file
            logTransactionToDatabase();

            // 3. PDF Ticket Generation Flow (Automatically downloaded to Tickets destination folder)
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String dateTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            String ticketId = "MRTVS-" + timestamp;

            File ticketsDir = new File("Tickets");
            if (!ticketsDir.exists()) {
                ticketsDir.mkdirs();
            }
            File fileToSave = new File(ticketsDir, "metro_ticket_" + username + "_" + timestamp + ".pdf");

            try {
                byte[] pdfBytes = PDFTicketGenerator.generateTicketPDF(
                    username,
                    currentStation,
                    getStationName(selectedDestination),
                    selectedTicketQuantity,
                    ticketFare,
                    totalCost,
                    ticketId,
                    dateTimeStr
                );

                try (java.io.FileOutputStream fos = new java.io.FileOutputStream(fileToSave)) {
                    fos.write(pdfBytes);
                }

                int openChoice = JOptionPane.showConfirmDialog(this,
                    "Payment Successful!\nPDF Ticket downloaded automatically to: " + fileToSave.getPath() + "\nChange returned: " + returnedAmount + " BDT\n\nWould you like to open it now?",
                    "Ticket Saved & Change Returned",
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
                    "Failed to generate or save PDF Ticket:\n" + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }

            dispose();
            MetroTicketVendingSystemPage page = new MetroTicketVendingSystemPage();
            page.setVisible(true);
        }
    }

    private void cancelTransaction() {
        if (amountInserted > 0) {
            JOptionPane.showMessageDialog(this, 
                "Transaction Cancelled!\nRefunding inserted cash: " + amountInserted + " BDT", 
                "Refund Initiated", JOptionPane.WARNING_MESSAGE);
        }
        dispose();
        if (currentStation.equals("CARD_RECHARGE")) {
            UserProfilePage profilePage = new UserProfilePage();
            profilePage.setVisible(true);
        } else {
            DestinationSelectionPage destPage = new DestinationSelectionPage(currentStation);
            destPage.setVisible(true);
        }
    }

    private void printTicketToNotepad() {
        try {
            String username = WelcomePage.getLoggedInUsername();
            if (username == null) username = "Guest";
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            
            File folder = new File("Tickets");
            if (!folder.exists()) {
                folder.mkdir();
            }

            String filename = "Tickets/metro_ticket_" + username + "_" + timestamp + ".txt";
            try (PrintWriter printWriter = new PrintWriter(new FileWriter(filename))) {
                printWriter.println("***********************************");
                printWriter.println("       METRO RAIL TRANSIT SYSTEM    ");
                printWriter.println("***********************************");
                printWriter.println("\nPassenger         : " + username);
                printWriter.println("From              : " + currentStation);
                printWriter.println("To                : " + getStationName(selectedDestination));
                printWriter.println("Number of Tickets : " + selectedTicketQuantity);
                printWriter.println("Total Cost        : " + totalCost + " BDT");
                printWriter.println("Amount Inserted   : " + amountInserted + " BDT");
                printWriter.println("Returned Amount   : " + returnedAmount + " BDT");
                printWriter.println("\nDate and Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                printWriter.println("\n***********************************");
                printWriter.println("    THANK YOU FOR RIDING METRO!    ");
                printWriter.println("***********************************");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printRechargeReceiptToNotepad() {
        try {
            String username = WelcomePage.getLoggedInUsername();
            if (username == null) username = "Guest";
            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            MRTPassManager.PassInfo pass = MRTPassManager.getPass(username);
            
            File folder = new File("Tickets");
            if (!folder.exists()) {
                folder.mkdir();
            }

            String filename = "Tickets/recharge_receipt_" + username + "_" + timestamp + ".txt";
            try (PrintWriter printWriter = new PrintWriter(new FileWriter(filename))) {
                printWriter.println("***********************************");
                printWriter.println("    MRT PASS RECHARGE RECEIPT      ");
                printWriter.println("***********************************");
                printWriter.println("\nPassenger         : " + username);
                printWriter.println("Card Number       : " + (pass != null ? pass.cardNumber : "N/A"));
                printWriter.println("Recharge Amount   : " + totalCost + " BDT");
                printWriter.println("New Card Balance  : " + (pass != null ? pass.balance : 0) + " BDT");
                printWriter.println("Payment Mode      : " + (digitalSubPanel.isVisible() ? "Digital Wallet" : "Cash"));
                printWriter.println("\nDate and Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                printWriter.println("\n***********************************");
                printWriter.println("    THANK YOU FOR RIDING METRO!    ");
                printWriter.println("***********************************");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void logTransactionToDatabase() {
        try {
            File dir = new File("Data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File("Data/transactions.txt");
            boolean newFile = !file.exists();

            try (FileWriter fw = new FileWriter(file, true);
                 PrintWriter pw = new PrintWriter(fw)) {
                if (newFile) {
                    pw.println("Timestamp,Username,From,To,Quantity,TotalCost,Inserted,Returned");
                }
                String username = WelcomePage.getLoggedInUsername();
                if (username == null) username = "Guest";
                String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                pw.println(String.join(",", 
                    timestamp,
                    username,
                    currentStation,
                    getStationName(selectedDestination),
                    String.valueOf(selectedTicketQuantity),
                    String.valueOf(totalCost),
                    String.valueOf(amountInserted),
                    String.valueOf(returnedAmount)
                ));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void switchPaymentMode(int mode) {
        if (mode == 0) {
            cashSubPanel.setVisible(true);
            digitalSubPanel.setVisible(false);
            if (passSubPanel != null) passSubPanel.setVisible(false);
            btnCashMode.setColors(UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
            btnDigitalMode.setColors(UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
            if (btnPassMode != null) btnPassMode.setColors(UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
        } else if (mode == 1) {
            cashSubPanel.setVisible(false);
            digitalSubPanel.setVisible(true);
            if (passSubPanel != null) passSubPanel.setVisible(false);
            btnCashMode.setColors(UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
            btnDigitalMode.setColors(UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
            if (btnPassMode != null) btnPassMode.setColors(UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
        } else if (mode == 2) {
            cashSubPanel.setVisible(false);
            digitalSubPanel.setVisible(false);
            if (passSubPanel != null) passSubPanel.setVisible(true);
            btnCashMode.setColors(UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
            btnDigitalMode.setColors(UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
            if (btnPassMode != null) btnPassMode.setColors(UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        }

        amountInserted = 0;
        insertedLabel.setText(mode == 0 ? "Amount Inserted: 0.0 BDT" : (mode == 1 ? "Digital Payment Selected" : "MRT Pass Selected"));
        recInserted.setText("INSERTED     : 0.0 BDT");
        refundLabel.setText("Change Due: 0.0 BDT");
        recChange.setText("CHANGE DUE   : 0.0 BDT");
        statusLabel.setText("UNPAID");
        statusLabel.setForeground(UIHelper.COLOR_DANGER);
        statusLabel.setBackground(new Color(254, 226, 226));
        payButton.setEnabled(false);
    }

    private void selectDigitalMethod(int method) {
        this.digitalMethod = method;
        txtNumber.setCleanText("");
        txtPin.setText("");
        
        btnBkash.setColors(UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
        btnNagad.setColors(UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
        btnCard.setColors(UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
        
        if (method == 0) {
            btnBkash.setColors(new Color(225, 20, 110), new Color(190, 10, 90));
            lblNumber.setText("bKash Account Number");
            lblPin.setText("bKash PIN");
            txtNumber.setCleanText("Enter 11-digit mobile number");
            txtNumber.setForeground(UIHelper.COLOR_TEXT_MUTED);
        } else if (method == 1) {
            btnNagad.setColors(new Color(245, 130, 32), new Color(210, 100, 20));
            lblNumber.setText("Nagad Account Number");
            lblPin.setText("Nagad PIN");
            txtNumber.setCleanText("Enter 11-digit mobile number");
            txtNumber.setForeground(UIHelper.COLOR_TEXT_MUTED);
        } else if (method == 2) {
            btnCard.setColors(UIHelper.COLOR_ACCENT, UIHelper.COLOR_ACCENT_HOVER);
            lblNumber.setText("Card Number (16-digit)");
            lblPin.setText("CVV / PIN");
            txtNumber.setCleanText("Enter 16-digit card number");
            txtNumber.setForeground(UIHelper.COLOR_TEXT_MUTED);
        }
        btnDigitalPay.setText("Verify & Pay " + totalCost + " BDT");
    }

    private void processDigitalPayment() {
        String num = txtNumber.getText().trim();
        String pin = new String(txtPin.getPassword()).trim();
        
        if (num.isEmpty() || num.equals("Enter 11-digit mobile number") || num.equals("Enter 16-digit card number")) {
            JOptionPane.showMessageDialog(this, "Please enter your account/card number.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (pin.isEmpty() || pin.equals("PIN")) {
            JOptionPane.showMessageDialog(this, "Please enter your PIN / CVV code.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (digitalMethod == 2) {
            if (!num.matches("\\d{16}")) {
                JOptionPane.showMessageDialog(this, "Card number must be exactly 16 digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } else {
            if (!num.matches("01\\d{9}")) {
                JOptionPane.showMessageDialog(this, "Mobile number must be a valid 11-digit Bangladeshi number (starting with 01).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        if (!pin.matches("\\d{3,4}")) {
            JOptionPane.showMessageDialog(this, "PIN / CVV must be 3 or 4 digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        btnDigitalPay.setEnabled(false);
        btnDigitalPay.setText("Processing Transaction... Please wait.");
        
        Timer timer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                amountInserted = totalCost;
                insertedLabel.setText("Amount Paid: " + totalCost + " BDT (Digital)");
                recInserted.setText("INSERTED     : " + totalCost + " BDT");
                returnedAmount = 0.0;
                refundLabel.setText("Change Due: 0.0 BDT");
                recChange.setText("CHANGE DUE   : 0.0 BDT");
                
                statusLabel.setText("PAID");
                statusLabel.setForeground(UIHelper.COLOR_PRIMARY);
                statusLabel.setBackground(new Color(209, 250, 229));
                
                payButton.setEnabled(true);
                btnDigitalPay.setEnabled(true);
                btnDigitalPay.setText("Payment Successful (PAID)");
                
                JOptionPane.showMessageDialog(PaymentPage.this, 
                    "Digital Payment Authorized Successfully!\nProceed to Print Ticket.", 
                    "Payment Approved", JOptionPane.INFORMATION_MESSAGE);
                
                ((Timer)evt.getSource()).stop();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    private static class QRCodePanel extends JPanel {
        public QRCodePanel() {
            setPreferredSize(new Dimension(100, 100));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(Color.BLACK);
            drawFinderPattern(g2d, 5, 5);
            drawFinderPattern(g2d, 75, 5);
            drawFinderPattern(g2d, 5, 75);
            
            g2d.fillRect(35, 5, 10, 10);
            g2d.fillRect(55, 5, 15, 5);
            g2d.fillRect(45, 20, 5, 15);
            g2d.fillRect(60, 25, 20, 10);
            g2d.fillRect(35, 45, 10, 20);
            g2d.fillRect(55, 45, 15, 10);
            g2d.fillRect(75, 45, 10, 15);
            g2d.fillRect(5, 55, 15, 5);
            g2d.fillRect(20, 65, 10, 10);
            g2d.fillRect(40, 75, 25, 5);
            g2d.fillRect(70, 75, 15, 15);
        }

        private void drawFinderPattern(Graphics2D g2d, int x, int y) {
            g2d.fillRect(x, y, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(x + 3, y + 3, 14, 14);
            g2d.setColor(Color.BLACK);
            g2d.fillRect(x + 6, y + 6, 8, 8);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PaymentPage page = new PaymentPage("Uttara North", 3, 2, 20, 40);
            page.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            page.setVisible(true);
        });
    }
}
