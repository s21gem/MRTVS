package Classes;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AdminPage extends JFrame implements ActionListener {
    private JTabbedPane tabbedPane;

    // Tab 1: User Management
    private JPanel userManagementPanel;
    private DefaultTableModel userTableModel;
    private JTable userTable;
    private TableRowSorter<DefaultTableModel> userRowSorter;
    private UIHelper.ModernTextField searchField;
    private UIHelper.ModernButton updateButton;
    private UIHelper.ModernButton deleteButton;
    private UIHelper.ModernButton signoutButton;

    // Tab 2: Analytics & Reports
    private JPanel analyticsPanel;
    private DefaultTableModel transTableModel;
    private JTable transTable;
    private JLabel totalRevenueLabel, totalTicketsLabel, totalTransLabel;

    // Tab 3: Operations Control
    private JPanel operationsControlPanel;
    private JLabel currentStatusBadge, currentModeLabel, systemTimeLabel;

    // Tab 4: Station & Fare Settings
    private JPanel stationFareSettingsPanel;
    private DefaultTableModel stationMaintenanceModel;
    private JTable stationMaintenanceTable;
    private JLabel currentMultiplierLabel;
    private double currentMultiplierValue = 1.0;
    private UIHelper.ModernButton btnDecreaseMultiplier, btnIncreaseMultiplier, btnSaveMultiplier;
    private UIHelper.ModernButton btnToggleMaintenance, btnRefreshStations;

    public AdminPage() {
        super("Administrator Dashboard - Metro Ticket System");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setIconImage(new ImageIcon("Images/icon.png").getImage());
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int screenWidth = maxBounds.width;
        int screenHeight = maxBounds.height;

        // Main layout using Slate theme
        getContentPane().setBackground(UIHelper.COLOR_BG_DARK);
        this.setLayout(null);

        // Header Title
        JLabel titleLabel = new JLabel("METRO RAIL ADMINISTRATION DASHBOARD", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 20, screenWidth - 100, 40);
        add(titleLabel);

        // Create Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIHelper.FONT_BODY_BOLD);
        tabbedPane.setBounds(50, 80, screenWidth - 100, screenHeight - 160);

        // Build Tabs
        buildUserManagementTab(screenWidth, screenHeight);
        buildAnalyticsTab(screenWidth, screenHeight);
        buildOperationsControlTab(screenWidth, screenHeight);
        buildStationFareSettingsTab(screenWidth, screenHeight);

        add(tabbedPane);
    }

    private void buildUserManagementTab(int screenWidth, int screenHeight) {
        userManagementPanel = new JPanel();
        userManagementPanel.setBackground(UIHelper.COLOR_BG_PANEL);
        userManagementPanel.setLayout(null);

        // Search Bar Section
        JLabel searchLabel = new JLabel("Filter Directory:");
        searchLabel.setFont(UIHelper.FONT_BODY_BOLD);
        searchLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        searchLabel.setBounds(40, 25, 120, 30);
        userManagementPanel.add(searchLabel);

        searchField = new UIHelper.ModernTextField("Enter username or phone number to search...");
        searchField.setBounds(170, 20, screenWidth - 100 - 210, 40);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterUserTable(); }
            public void removeUpdate(DocumentEvent e) { filterUserTable(); }
            public void changedUpdate(DocumentEvent e) { filterUserTable(); }
        });
        userManagementPanel.add(searchField);

        // User Directory Table
        userTableModel = new DefaultTableModel(new Object[]{"Name", "Phone Number", "Date of Birth", "Password"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Table cells are read-only; editing is done via dialog
            }
        };
        userTable = new JTable(userTableModel);
        userTable.setFont(UIHelper.FONT_BODY);
        userTable.setRowHeight(25);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setBackground(UIHelper.COLOR_BG_DARK);
        userTable.setForeground(Color.WHITE);
        userTable.setGridColor(UIHelper.COLOR_INPUT_BG);

        userRowSorter = new TableRowSorter<>(userTableModel);
        userTable.setRowSorter(userRowSorter);

        loadUserData();

        userTable.getSelectionModel().addListSelectionListener(e -> {
            boolean selected = userTable.getSelectedRow() != -1;
            updateButton.setEnabled(selected);
            deleteButton.setEnabled(selected);
        });

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBounds(40, 80, screenWidth - 100 - 80, screenHeight - 160 - 220);
        userManagementPanel.add(scrollPane);

        // Control Buttons
        updateButton = new UIHelper.ModernButton("Update Selected User", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        updateButton.setBounds(40, screenHeight - 160 - 100, 220, 45);
        updateButton.setEnabled(false);
        updateButton.addActionListener(this);
        userManagementPanel.add(updateButton);

        deleteButton = new UIHelper.ModernButton("Delete Selected User", UIHelper.COLOR_DANGER, UIHelper.COLOR_DANGER_HOVER);
        deleteButton.setBounds(280, screenHeight - 160 - 100, 220, 45);
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(this);
        userManagementPanel.add(deleteButton);

        signoutButton = new UIHelper.ModernButton("Sign Out Dashboard", UIHelper.COLOR_INPUT_BG, new Color(71, 85, 105));
        signoutButton.setBounds(screenWidth - 100 - 260, screenHeight - 160 - 100, 220, 45);
        signoutButton.addActionListener(this);
        userManagementPanel.add(signoutButton);

        tabbedPane.addTab("User Management Directory", userManagementPanel);
    }

    private void buildAnalyticsTab(int screenWidth, int screenHeight) {
        analyticsPanel = new JPanel();
        analyticsPanel.setBackground(UIHelper.COLOR_BG_PANEL);
        analyticsPanel.setLayout(null);

        // Calculate card layout to dynamically center them
        int cardWidth = 300;
        int cardGap = 20;
        int totalCardsWidth = 3 * cardWidth + 2 * cardGap;
        int tabContentWidth = screenWidth - 100;
        int startX = (tabContentWidth - totalCardsWidth) / 2;

        // Stat Card 1: Revenue
        UIHelper.RoundedPanel card1 = new UIHelper.RoundedPanel(15, UIHelper.COLOR_BG_DARK);
        card1.setLayout(null);
        card1.setBounds(startX, 25, cardWidth, 110);
        analyticsPanel.add(card1);

        JLabel revTitle = new JLabel("TOTAL SALES REVENUE", SwingConstants.CENTER);
        revTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        revTitle.setForeground(UIHelper.COLOR_PRIMARY);
        revTitle.setBounds(10, 15, 280, 20);
        card1.add(revTitle);

        totalRevenueLabel = new JLabel("0.0 BDT", SwingConstants.CENTER);
        totalRevenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalRevenueLabel.setForeground(Color.WHITE);
        totalRevenueLabel.setBounds(10, 45, 280, 40);
        card1.add(totalRevenueLabel);

        // Stat Card 2: Tickets
        UIHelper.RoundedPanel card2 = new UIHelper.RoundedPanel(15, UIHelper.COLOR_BG_DARK);
        card2.setLayout(null);
        card2.setBounds(startX + cardWidth + cardGap, 25, cardWidth, 110);
        analyticsPanel.add(card2);

        JLabel tktTitle = new JLabel("TOTAL TICKETS SOLD", SwingConstants.CENTER);
        tktTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tktTitle.setForeground(UIHelper.COLOR_ACCENT);
        tktTitle.setBounds(10, 15, 280, 20);
        card2.add(tktTitle);

        totalTicketsLabel = new JLabel("0", SwingConstants.CENTER);
        totalTicketsLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalTicketsLabel.setForeground(Color.WHITE);
        totalTicketsLabel.setBounds(10, 45, 280, 40);
        card2.add(totalTicketsLabel);

        // Stat Card 3: Completed Transactions
        UIHelper.RoundedPanel card3 = new UIHelper.RoundedPanel(15, UIHelper.COLOR_BG_DARK);
        card3.setLayout(null);
        card3.setBounds(startX + 2 * (cardWidth + cardGap), 25, cardWidth, 110);
        analyticsPanel.add(card3);

        JLabel transTitle = new JLabel("COMPLETED TRANSACTIONS", SwingConstants.CENTER);
        transTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        transTitle.setForeground(Color.ORANGE);
        transTitle.setBounds(10, 15, 280, 20);
        card3.add(transTitle);

        totalTransLabel = new JLabel("0", SwingConstants.CENTER);
        totalTransLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        totalTransLabel.setForeground(Color.WHITE);
        totalTransLabel.setBounds(10, 45, 280, 40);
        card3.add(totalTransLabel);

        // Transaction History Table
        JLabel tblTitle = new JLabel("Audit Logs & Transaction History");
        tblTitle.setFont(UIHelper.FONT_BODY_BOLD);
        tblTitle.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        tblTitle.setBounds(40, 160, 400, 25);
        analyticsPanel.add(tblTitle);

        transTableModel = new DefaultTableModel(new Object[]{"Date/Time", "Username", "Origin", "Destination", "Qty", "Total Cost (BDT)"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        transTable = new JTable(transTableModel);
        transTable.setFont(UIHelper.FONT_BODY);
        transTable.setRowHeight(25);
        transTable.setBackground(UIHelper.COLOR_BG_DARK);
        transTable.setForeground(Color.WHITE);
        transTable.setGridColor(UIHelper.COLOR_INPUT_BG);

        loadTransactionData();

        JScrollPane transScroll = new JScrollPane(transTable);
        transScroll.setBounds(40, 195, screenWidth - 100 - 80, screenHeight - 160 - 250);
        analyticsPanel.add(transScroll);

        tabbedPane.addTab("Sales & Revenue Reports", analyticsPanel);

        // Dynamic stats reload when tab is selected
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                loadTransactionData();
            }
        });
    }

    private void filterUserTable() {
        String filterText = searchField.getText().trim();
        if (filterText.isEmpty() || filterText.equals("Enter username or phone number to search...")) {
            userRowSorter.setRowFilter(null);
        } else {
            userRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + filterText));
        }
    }

    private void loadUserData() {
        userTableModel.setRowCount(0);
        try (BufferedReader reader = new BufferedReader(new FileReader("Data/signup.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length >= 4) {
                    userTableModel.addRow(userData);
                }
            }
        } catch (IOException e) {
            // signup.txt may not exist yet if no signups occurred
        }
    }

    private void saveUserData() {
        try (FileWriter writer = new FileWriter("Data/signup.txt")) {
            for (int i = 0; i < userTableModel.getRowCount(); i++) {
                for (int j = 0; j < userTableModel.getColumnCount(); j++) {
                    writer.write(userTableModel.getValueAt(i, j).toString());
                    if (j < userTableModel.getColumnCount() - 1) {
                        writer.write(",");
                    }
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error occurred while saving updated details.", "Write Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTransactionData() {
        transTableModel.setRowCount(0);
        double totalRev = 0;
        int totalTickets = 0;
        int transCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader("Data/transactions.txt"))) {
            String header = reader.readLine(); // Skip header line
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 8) {
                    transCount++;
                    // Columns: Date/Time, User, Origin, Dest, Qty, Cost
                    transTableModel.addRow(new Object[]{parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]});
                    
                    try {
                        totalTickets += Integer.parseInt(parts[4]);
                        totalRev += Double.parseDouble(parts[5]);
                    } catch (NumberFormatException nfe) {
                        // ignore malformed metrics
                    }
                }
            }
        } catch (IOException e) {
            // transactions log doesn't exist yet
        }

        totalRevenueLabel.setText(totalRev + " BDT");
        totalTicketsLabel.setText(String.valueOf(totalTickets));
        totalTransLabel.setText(String.valueOf(transCount));
    }

    private void updateUserInfo() {
        int selectedModelRow = userTable.getSelectedRow();
        if (selectedModelRow != -1) {
            // Map row indices correctly through sorter filter
            int actualRow = userTable.convertRowIndexToModel(selectedModelRow);

            String currentName = userTableModel.getValueAt(actualRow, 0).toString();
            String currentPhone = userTableModel.getValueAt(actualRow, 1).toString();
            String currentDob = userTableModel.getValueAt(actualRow, 2).toString();
            String currentPass = userTableModel.getValueAt(actualRow, 3).toString();

            // Dialog for editing fields
            JTextField nameEdit = new JTextField(currentName);
            JTextField phoneEdit = new JTextField(currentPhone);
            JTextField dobEdit = new JTextField(currentDob);
            JTextField passEdit = new JTextField(currentPass);

            Object[] fields = {
                "Edit Full Name:", nameEdit,
                "Edit Phone Number:", phoneEdit,
                "Edit DOB (YYYY-MM-DD):", dobEdit,
                "Edit Password:", passEdit
            };

            int option = JOptionPane.showConfirmDialog(this, fields, "Update User Account Info", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String name = nameEdit.getText().trim();
                String phone = phoneEdit.getText().trim();
                String dob = dobEdit.getText().trim();
                String pass = passEdit.getText().trim();

                if (name.isEmpty() || phone.isEmpty() || dob.isEmpty() || pass.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All input fields must be filled.", "Validation Alert", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                userTableModel.setValueAt(name, actualRow, 0);
                userTableModel.setValueAt(phone, actualRow, 1);
                userTableModel.setValueAt(dob, actualRow, 2);
                userTableModel.setValueAt(pass, actualRow, 3);

                saveUserData();
                JOptionPane.showMessageDialog(this, "Account details successfully updated.", "Updated", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void deleteSelectedUser() {
        int selectedModelRow = userTable.getSelectedRow();
        if (selectedModelRow != -1) {
            int actualRow = userTable.convertRowIndexToModel(selectedModelRow);
            String name = userTableModel.getValueAt(actualRow, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to permanently delete the user account for '" + name + "'?", 
                "Confirm Account Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                userTableModel.removeRow(actualRow);
                saveUserData();
                JOptionPane.showMessageDialog(this, "Account has been deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void signout() {
        dispose();
        WelcomePage welcomePage = new WelcomePage();
        welcomePage.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == updateButton) {
            updateUserInfo();
        } else if (ae.getSource() == deleteButton) {
            deleteSelectedUser();
        } else if (ae.getSource() == signoutButton) {
            signout();
        }
    }

    private void buildOperationsControlTab(int screenWidth, int screenHeight) {
        operationsControlPanel = new JPanel();
        operationsControlPanel.setBackground(UIHelper.COLOR_BG_PANEL);
        operationsControlPanel.setLayout(null);

        // Subtitle/Title inside Tab
        JLabel title = new JLabel("METRO RAIL SYSTEM GATEWAYS & SERVICE WINDOWS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(40, 20, 600, 30);
        operationsControlPanel.add(title);

        // Status Card showing current settings
        UIHelper.RoundedPanel statusCard = new UIHelper.RoundedPanel(20, UIHelper.COLOR_BG_DARK);
        statusCard.setLayout(null);
        statusCard.setBounds(40, 70, screenWidth - 100 - 80, 150);
        operationsControlPanel.add(statusCard);

        JLabel statusTitle = new JLabel("LIVE SERVICE STATUS INDICATOR");
        statusTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusTitle.setForeground(UIHelper.COLOR_TEXT_MUTED);
        statusTitle.setBounds(30, 20, 250, 20);
        statusCard.add(statusTitle);

        JLabel badgeTitle = new JLabel("GATEWAY STATUS:");
        badgeTitle.setFont(UIHelper.FONT_BODY_BOLD);
        badgeTitle.setForeground(Color.WHITE);
        badgeTitle.setBounds(30, 60, 150, 30);
        statusCard.add(badgeTitle);

        currentStatusBadge = new JLabel("...", SwingConstants.CENTER);
        currentStatusBadge.setFont(new Font("Segoe UI", Font.BOLD, 16));
        currentStatusBadge.setOpaque(true);
        currentStatusBadge.setBounds(180, 57, 150, 36);
        statusCard.add(currentStatusBadge);

        JLabel modeTitle = new JLabel("CURRENT OVERRIDE MODE:");
        modeTitle.setFont(UIHelper.FONT_BODY_BOLD);
        modeTitle.setForeground(Color.WHITE);
        modeTitle.setBounds(30, 110, 220, 20);
        statusCard.add(modeTitle);

        currentModeLabel = new JLabel("AUTO (SCHEDULED)");
        currentModeLabel.setFont(UIHelper.FONT_BODY_BOLD);
        currentModeLabel.setForeground(UIHelper.COLOR_ACCENT);
        currentModeLabel.setBounds(250, 110, 250, 20);
        statusCard.add(currentModeLabel);

        // System Time
        JLabel timeTitle = new JLabel("SYSTEM LIVE TIME:");
        timeTitle.setFont(UIHelper.FONT_BODY_BOLD);
        timeTitle.setForeground(Color.WHITE);
        timeTitle.setBounds(screenWidth - 100 - 80 - 380, 60, 150, 20);
        statusCard.add(timeTitle);

        systemTimeLabel = new JLabel("HH:mm:ss");
        systemTimeLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        systemTimeLabel.setForeground(Color.WHITE);
        systemTimeLabel.setBounds(screenWidth - 100 - 80 - 230, 55, 200, 30);
        statusCard.add(systemTimeLabel);

        // Quick status updater timer
        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("hh:mm:ss a");
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                systemTimeLabel.setText(timeFormat.format(new java.util.Date()));
                updateLiveStatusUI();
            }
        });
        timer.start();

        // 3 Cards for configuration options
        int cardWidth = (screenWidth - 100 - 160) / 3;
        int cardHeight = 260;
        int cardY = 250;

        // Card 1: AUTO
        UIHelper.RoundedPanel autoCard = new UIHelper.RoundedPanel(15, UIHelper.COLOR_BG_DARK);
        autoCard.setLayout(null);
        autoCard.setBounds(40, cardY, cardWidth, cardHeight);
        operationsControlPanel.add(autoCard);

        JLabel autoTitle = new JLabel("AUTO TIMING POLICY", SwingConstants.CENTER);
        autoTitle.setFont(UIHelper.FONT_BODY_BOLD);
        autoTitle.setForeground(UIHelper.COLOR_PRIMARY);
        autoTitle.setBounds(20, 20, cardWidth - 40, 25);
        autoCard.add(autoTitle);

        JTextArea autoDesc = new JTextArea("Locks and unlocks system ticket purchases automatically based on standard operational times.\n\n- Open: 07:30 AM\n- Close: 09:00 PM");
        autoDesc.setFont(UIHelper.FONT_BODY);
        autoDesc.setForeground(UIHelper.COLOR_TEXT_MUTED);
        autoDesc.setBackground(new Color(0,0,0,0));
        autoDesc.setEditable(false);
        autoDesc.setLineWrap(true);
        autoDesc.setWrapStyleWord(true);
        autoDesc.setBounds(20, 60, cardWidth - 40, 120);
        autoCard.add(autoDesc);

        UIHelper.ModernButton btnAuto = new UIHelper.ModernButton("Activate Scheduled Policy", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        btnAuto.setBounds(20, 195, cardWidth - 40, 45);
        btnAuto.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServiceStateManager.setOverrideStatus("AUTO");
                updateLiveStatusUI();
                JOptionPane.showMessageDialog(AdminPage.this, "Scheduled operational policy activated.", "Policy Updated", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        autoCard.add(btnAuto);

        // Card 2: FORCE OPEN
        UIHelper.RoundedPanel openCard = new UIHelper.RoundedPanel(15, UIHelper.COLOR_BG_DARK);
        openCard.setLayout(null);
        openCard.setBounds(40 + cardWidth + 40, cardY, cardWidth, cardHeight);
        operationsControlPanel.add(openCard);

        JLabel openTitle = new JLabel("FORCE SERVICE OPEN", SwingConstants.CENTER);
        openTitle.setFont(UIHelper.FONT_BODY_BOLD);
        openTitle.setForeground(UIHelper.COLOR_ACCENT);
        openTitle.setBounds(20, 20, cardWidth - 40, 25);
        openCard.add(openTitle);

        JTextArea openDesc = new JTextArea("Forces the ticket purchasing gateways to be active 24/7. Overrides the regular system closing hours for holidays or testing purposes.");
        openDesc.setFont(UIHelper.FONT_BODY);
        openDesc.setForeground(UIHelper.COLOR_TEXT_MUTED);
        openDesc.setBackground(new Color(0,0,0,0));
        openDesc.setEditable(false);
        openDesc.setLineWrap(true);
        openDesc.setWrapStyleWord(true);
        openDesc.setBounds(20, 60, cardWidth - 40, 120);
        openCard.add(openDesc);

        UIHelper.ModernButton btnOpen = new UIHelper.ModernButton("Force Open Gateways", UIHelper.COLOR_ACCENT, UIHelper.COLOR_ACCENT_HOVER);
        btnOpen.setBounds(20, 195, cardWidth - 40, 45);
        btnOpen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServiceStateManager.setOverrideStatus("OPEN");
                updateLiveStatusUI();
                JOptionPane.showMessageDialog(AdminPage.this, "Operational gateways forced to OPEN state.", "Policy Updated", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        openCard.add(btnOpen);

        // Card 3: FORCE CLOSED
        UIHelper.RoundedPanel closeCard = new UIHelper.RoundedPanel(15, UIHelper.COLOR_BG_DARK);
        closeCard.setLayout(null);
        closeCard.setBounds(40 + 2 * (cardWidth + 40), cardY, cardWidth, cardHeight);
        operationsControlPanel.add(closeCard);

        JLabel closeTitle = new JLabel("FORCE SERVICE CLOSED", SwingConstants.CENTER);
        closeTitle.setFont(UIHelper.FONT_BODY_BOLD);
        closeTitle.setForeground(UIHelper.COLOR_DANGER);
        closeTitle.setBounds(20, 20, cardWidth - 40, 25);
        closeCard.add(closeTitle);

        JTextArea closeDesc = new JTextArea("Forces the ticket purchasing gateways to lock immediately. Users will not be able to purchase tickets or recharge passes until reopened.");
        closeDesc.setFont(UIHelper.FONT_BODY);
        closeDesc.setForeground(UIHelper.COLOR_TEXT_MUTED);
        closeDesc.setBackground(new Color(0,0,0,0));
        closeDesc.setEditable(false);
        closeDesc.setLineWrap(true);
        closeDesc.setWrapStyleWord(true);
        closeDesc.setBounds(20, 60, cardWidth - 40, 120);
        closeCard.add(closeDesc);

        UIHelper.ModernButton btnClose = new UIHelper.ModernButton("Force Close Gateways", UIHelper.COLOR_DANGER, UIHelper.COLOR_DANGER_HOVER);
        btnClose.setBounds(20, 195, cardWidth - 40, 45);
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ServiceStateManager.setOverrideStatus("CLOSED");
                updateLiveStatusUI();
                JOptionPane.showMessageDialog(AdminPage.this, "Operational gateways forced to CLOSED state.", "Policy Updated", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        closeCard.add(btnClose);

        tabbedPane.addTab("Operations Control", operationsControlPanel);
        updateLiveStatusUI();
    }

    private void updateLiveStatusUI() {
        if (currentStatusBadge == null) return;
        boolean open = ServiceStateManager.isServiceOpen();
        String overrideStatus = ServiceStateManager.getOverrideStatus();

        if (open) {
            currentStatusBadge.setText("SERVICE OPEN");
            currentStatusBadge.setBackground(UIHelper.COLOR_PRIMARY);
            currentStatusBadge.setForeground(Color.WHITE);
        } else {
            currentStatusBadge.setText("SERVICE CLOSED");
            currentStatusBadge.setBackground(UIHelper.COLOR_DANGER);
            currentStatusBadge.setForeground(Color.WHITE);
        }

        if (overrideStatus.equals("AUTO")) {
            currentModeLabel.setText("AUTO (SCHEDULED)");
            currentModeLabel.setForeground(UIHelper.COLOR_PRIMARY);
        } else if (overrideStatus.equals("OPEN")) {
            currentModeLabel.setText("FORCE OPEN (OVERRIDE)");
            currentModeLabel.setForeground(UIHelper.COLOR_ACCENT);
        } else {
            currentModeLabel.setText("FORCE CLOSED (OVERRIDE)");
            currentModeLabel.setForeground(UIHelper.COLOR_DANGER);
        }
    }

    private void buildStationFareSettingsTab(int screenWidth, int screenHeight) {
        stationFareSettingsPanel = new JPanel();
        stationFareSettingsPanel.setBackground(UIHelper.COLOR_BG_PANEL);
        stationFareSettingsPanel.setLayout(null);

        JLabel title = new JLabel("STATION MAINTENANCE & FARE ADJUSTMENT CONTROLS");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(40, 20, 800, 30);
        stationFareSettingsPanel.add(title);

        int leftWidth = (screenWidth - 100 - 120) * 6 / 10;
        int rightWidth = (screenWidth - 100 - 120) * 4 / 10;
        int rightX = 40 + leftWidth + 40;
        int tabHeight = screenHeight - 160;

        JLabel tblTitle = new JLabel("Station Operational Directory & Statuses");
        tblTitle.setFont(UIHelper.FONT_BODY_BOLD);
        tblTitle.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        tblTitle.setBounds(40, 70, 400, 25);
        stationFareSettingsPanel.add(tblTitle);

        stationMaintenanceModel = new DefaultTableModel(new Object[]{"Station Name", "Maintenance Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        stationMaintenanceTable = new JTable(stationMaintenanceModel);
        stationMaintenanceTable.setFont(UIHelper.FONT_BODY);
        stationMaintenanceTable.setRowHeight(30);
        stationMaintenanceTable.setBackground(UIHelper.COLOR_BG_DARK);
        stationMaintenanceTable.setForeground(Color.WHITE);
        stationMaintenanceTable.setGridColor(UIHelper.COLOR_INPUT_BG);
        stationMaintenanceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader tableHeader = stationMaintenanceTable.getTableHeader();
        tableHeader.setBackground(UIHelper.COLOR_PRIMARY);
        tableHeader.setForeground(Color.WHITE);
        tableHeader.setFont(UIHelper.FONT_BODY_BOLD);

        loadStationMaintenanceData();

        JScrollPane scrollPane = new JScrollPane(stationMaintenanceTable);
        scrollPane.setBounds(40, 105, leftWidth, tabHeight - 250);
        stationFareSettingsPanel.add(scrollPane);

        btnToggleMaintenance = new UIHelper.ModernButton("Toggle Maintenance Status", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        btnToggleMaintenance.setBounds(40, tabHeight - 125, 260, 45);
        btnToggleMaintenance.addActionListener(e -> toggleMaintenanceStatus());
        stationFareSettingsPanel.add(btnToggleMaintenance);

        btnRefreshStations = new UIHelper.ModernButton("Refresh Statuses", UIHelper.COLOR_INPUT_BG, new Color(71, 85, 105));
        btnRefreshStations.setBounds(320, tabHeight - 125, 200, 45);
        btnRefreshStations.addActionListener(e -> loadStationMaintenanceData());
        stationFareSettingsPanel.add(btnRefreshStations);

        UIHelper.RoundedPanel rightCard = new UIHelper.RoundedPanel(20, UIHelper.COLOR_BG_DARK);
        rightCard.setLayout(null);
        rightCard.setBounds(rightX, 105, rightWidth, tabHeight - 250);
        stationFareSettingsPanel.add(rightCard);

        JLabel rightTitle = new JLabel("FARE MULTIPLIER CONFIG", SwingConstants.CENTER);
        rightTitle.setFont(UIHelper.FONT_BODY_BOLD);
        rightTitle.setForeground(UIHelper.COLOR_ACCENT);
        rightTitle.setBounds(20, 25, rightWidth - 40, 25);
        rightCard.add(rightTitle);

        JTextArea descText = new JTextArea("Adjust the network-wide fare multiplier to dynamically configure pricing surcharges or off-peak discounts.\n\n- 1.0x: Standard Fare Rates\n- 1.2x: 20% Peak Surcharge\n- 0.8x: 20% Off-Peak Discount\nAllowed range is 0.5x to 2.0x.");
        descText.setFont(UIHelper.FONT_BODY);
        descText.setForeground(UIHelper.COLOR_TEXT_MUTED);
        descText.setBackground(new Color(0, 0, 0, 0));
        descText.setEditable(false);
        descText.setLineWrap(true);
        descText.setWrapStyleWord(true);
        descText.setBounds(30, 70, rightWidth - 60, 120);
        rightCard.add(descText);

        currentMultiplierValue = ServiceStateManager.getFareMultiplier();
        currentMultiplierLabel = new JLabel(String.format(java.util.Locale.US, "%.1fx", currentMultiplierValue), SwingConstants.CENTER);
        currentMultiplierLabel.setFont(new Font("Segoe UI", Font.BOLD, 48));
        currentMultiplierLabel.setForeground(Color.WHITE);
        currentMultiplierLabel.setBounds(20, 200, rightWidth - 40, 60);
        rightCard.add(currentMultiplierLabel);

        int btnWidth = 80;
        int btnGap = 20;
        int adjustX = (rightWidth - (2 * btnWidth + btnGap)) / 2;

        btnDecreaseMultiplier = new UIHelper.ModernButton("-0.1", UIHelper.COLOR_DANGER, UIHelper.COLOR_DANGER_HOVER);
        btnDecreaseMultiplier.setFont(UIHelper.FONT_BODY_BOLD);
        btnDecreaseMultiplier.setBounds(adjustX, 280, btnWidth, 40);
        btnDecreaseMultiplier.addActionListener(e -> adjustMultiplier(-0.1));
        rightCard.add(btnDecreaseMultiplier);

        btnIncreaseMultiplier = new UIHelper.ModernButton("+0.1", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        btnIncreaseMultiplier.setFont(UIHelper.FONT_BODY_BOLD);
        btnIncreaseMultiplier.setBounds(adjustX + btnWidth + btnGap, 280, btnWidth, 40);
        btnIncreaseMultiplier.addActionListener(e -> adjustMultiplier(0.1));
        rightCard.add(btnIncreaseMultiplier);

        btnSaveMultiplier = new UIHelper.ModernButton("Save & Apply Multiplier", UIHelper.COLOR_ACCENT, UIHelper.COLOR_ACCENT_HOVER);
        btnSaveMultiplier.setBounds(rightX, tabHeight - 125, rightWidth, 45);
        btnSaveMultiplier.addActionListener(e -> saveMultiplierValue());
        stationFareSettingsPanel.add(btnSaveMultiplier);

        tabbedPane.addTab("Station & Fare Settings", stationFareSettingsPanel);
    }

    private void loadStationMaintenanceData() {
        stationMaintenanceModel.setRowCount(0);
        String[] stationNames = {
            "Uttara North", "Uttara Center", "Uttara South", 
            "Pallabi", "Mirpur 11", "Mirpur 10", 
            "Kazipara", "Sheorapara", "Agargaon"
        };
        for (String name : stationNames) {
            boolean closed = ServiceStateManager.isStationMaintenance(name);
            stationMaintenanceModel.addRow(new Object[]{name, closed ? "Under Maintenance" : "Operational"});
        }
    }

    private void toggleMaintenanceStatus() {
        int selectedRow = stationMaintenanceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a station from the directory first.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String stationName = (String) stationMaintenanceModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) stationMaintenanceModel.getValueAt(selectedRow, 1);
        boolean isClosed = currentStatus.equals("Under Maintenance");

        boolean newClosed = !isClosed;
        ServiceStateManager.setStationMaintenance(stationName, newClosed);
        
        loadStationMaintenanceData();
        
        stationMaintenanceTable.setRowSelectionInterval(selectedRow, selectedRow);

        String msg = stationName + " is now " + (newClosed ? "UNDER MAINTENANCE (Closed)" : "OPERATIONAL (Open)");
        JOptionPane.showMessageDialog(this, msg, "Station Status Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void adjustMultiplier(double diff) {
        currentMultiplierValue += diff;
        if (currentMultiplierValue < 0.5) currentMultiplierValue = 0.5;
        if (currentMultiplierValue > 2.0) currentMultiplierValue = 2.0;

        currentMultiplierLabel.setText(String.format(java.util.Locale.US, "%.1fx", currentMultiplierValue));
    }

    private void saveMultiplierValue() {
        ServiceStateManager.setFareMultiplier(currentMultiplierValue);
        JOptionPane.showMessageDialog(this, 
            "Fare Multiplier successfully saved and applied network-wide!\nNew Multiplier: " + String.format(java.util.Locale.US, "%.1f", currentMultiplierValue) + "x", 
            "Pricing Policy Saved", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AdminPage adminPage = new AdminPage();
            adminPage.setVisible(true);
        });
    }
}
