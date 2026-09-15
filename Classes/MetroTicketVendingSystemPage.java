package Classes;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.Calendar;

public class MetroTicketVendingSystemPage extends JFrame implements ActionListener {
    private JComboBox<String> stationDropdown;
    private UIHelper.ModernButton nextButton;
    private JLabel goBackLabel, welcomeUserLabel, stationPromptLabel;
    private UIHelper.RoundedPanel cardPanel;

    public MetroTicketVendingSystemPage() {
        super("Origin Station - Metro Ticket System");
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
        JLabel titleLabel = new JLabel("METRO TICKET TERMINAL", SwingConstants.CENTER);
        titleLabel.setFont(UIHelper.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(200, 50, 800, 50);
        container.add(titleLabel);

        // Welcome Username Indicator
        String username = WelcomePage.getLoggedInUsername();
        if (username == null) {
            username = "Guest";
        }
        welcomeUserLabel = new JLabel("Logged in as: " + username, SwingConstants.RIGHT);
        welcomeUserLabel.setFont(UIHelper.FONT_BODY_BOLD);
        welcomeUserLabel.setForeground(UIHelper.COLOR_PRIMARY);
        welcomeUserLabel.setBounds(850, 15, 300, 30);
        container.add(welcomeUserLabel);

        // Centered Card Panel
        cardPanel = new UIHelper.RoundedPanel(30, UIHelper.COLOR_BG_PANEL);
        cardPanel.setLayout(null);
        cardPanel.setBounds(350, 180, 500, 420);
        container.add(cardPanel);

        // Prompt
        stationPromptLabel = new JLabel("Please Select Your Current Station", SwingConstants.CENTER);
        stationPromptLabel.setFont(UIHelper.FONT_SUBTITLE);
        stationPromptLabel.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        stationPromptLabel.setBounds(30, 50, 440, 40);
        cardPanel.add(stationPromptLabel);

        // Station Dropdown List
        String[] stationNames = {
            "Uttara North", "Uttara Center", "Uttara South", 
            "Pallabi", "Mirpur 11", "Mirpur 10", 
            "Kazipara", "Sheorapara", "Agargaon"
        };
        String[] displayNames = new String[stationNames.length];
        for (int i = 0; i < stationNames.length; i++) {
            if (ServiceStateManager.isStationMaintenance(stationNames[i])) {
                displayNames[i] = stationNames[i] + " (Closed for Maintenance)";
            } else {
                displayNames[i] = stationNames[i];
            }
        }
        stationDropdown = new JComboBox<>(displayNames);
        stationDropdown.setBounds(70, 140, 360, 45);
        stationDropdown.setFont(UIHelper.FONT_BODY_BOLD);
        stationDropdown.setBackground(UIHelper.COLOR_INPUT_BG);
        stationDropdown.setForeground(Color.WHITE);
        stationDropdown.setBorder(BorderFactory.createLineBorder(UIHelper.COLOR_INPUT_BG, 1, true));
        // Simple visual alignment tweak for standard JComboBox drop-down renderer
        ((JLabel)stationDropdown.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        cardPanel.add(stationDropdown);

        // Next Button
        nextButton = new UIHelper.ModernButton("Confirm Origin & Proceed", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        nextButton.setBounds(70, 230, 360, 50);
        nextButton.addActionListener(this);
        cardPanel.add(nextButton);

        // Go Back Label
        goBackLabel = new JLabel("Log Out / Go Back", SwingConstants.CENTER);
        goBackLabel.setFont(UIHelper.FONT_BODY);
        goBackLabel.setForeground(UIHelper.COLOR_TEXT_MUTED);
        goBackLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        goBackLabel.setBounds(50, 310, 400, 25);
        goBackLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                goBackToWelcomePage();
            }

            @Override
            public void mouseEntered(MouseEvent evt) {
                goBackLabel.setForeground(UIHelper.COLOR_DANGER);
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                goBackLabel.setForeground(UIHelper.COLOR_TEXT_MUTED);
            }
        });
        cardPanel.add(goBackLabel);

        // Profile Navigation Button (Top Left)
        UIHelper.ModernButton profileBtn = new UIHelper.ModernButton("My Profile & Trips", UIHelper.COLOR_ACCENT, UIHelper.COLOR_ACCENT_HOVER);
        profileBtn.setBounds(50, 15, 200, 35);
        profileBtn.addActionListener(evt -> {
            dispose();
            UserProfilePage profilePage = new UserProfilePage();
            profilePage.setVisible(true);
        });
        container.add(profileBtn);

        // Station Info & Timings Button
        UIHelper.ModernButton scheduleBtn = new UIHelper.ModernButton("Station Info & Timings", UIHelper.COLOR_ACCENT, UIHelper.COLOR_ACCENT_HOVER);
        scheduleBtn.setBounds(270, 15, 220, 35);
        scheduleBtn.addActionListener(evt -> showScheduleDialog());
        container.add(scheduleBtn);

        // Marquee Panel at the bottom
        final int initialXOffset = screenWidth;
        JPanel marqueePanel = new JPanel() {
            private int xOffset = initialXOffset;
            private String closedStationsAlert = "";
            
            {
                setBackground(new Color(15, 23, 42)); // Slate 900
                setLayout(null);
                
                java.util.List<String> closedStations = ServiceStateManager.getMaintenanceStations();
                StringBuilder alertBuilder = new StringBuilder();
                if (!closedStations.isEmpty()) {
                    for (String station : closedStations) {
                        alertBuilder.append("[ALERT: ").append(station).append(" is closed for maintenance]  |  ");
                    }
                }
                closedStationsAlert = alertBuilder.toString();

                Timer timer = new Timer(30, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        xOffset -= 2;
                        if (xOffset < -2000) {
                            xOffset = getWidth();
                        }
                        repaint();
                    }
                });
                timer.start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setFont(UIHelper.FONT_BODY_BOLD);
                g2d.setColor(UIHelper.COLOR_PRIMARY);
                
                String marqueeText = closedStationsAlert + "System Status: Normal Service  |  Weather: 29\u00b0C Clear  |  Platform 2: Train arriving in 3 mins.  |  Please stand behind the yellow line.  |  Welcome to Dhaka Metro Rail!";
                g2d.drawString(marqueeText, xOffset, 25);
                
                // Add a subtle border line at the top
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.drawLine(0, 0, getWidth(), 0);
            }
        };
        marqueePanel.setBounds(0, screenHeight - 90, screenWidth, 40);
        backgroundPanel.add(marqueePanel);
    }

    private void goBackToWelcomePage() {
        dispose();
        WelcomePage welcomePage = new WelcomePage();
        welcomePage.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == nextButton) {
            if (!ServiceStateManager.isServiceOpen()) {
                String overrideStatus = ServiceStateManager.getOverrideStatus();
                String reason = "Dhaka Metro is currently closed. Standard operating hours are 07:30 AM - 09:00 PM.";
                if ("CLOSED".equals(overrideStatus)) {
                    reason = "Dhaka Metro service is currently suspended by the administration.";
                }
                JOptionPane.showMessageDialog(this, 
                    reason + "\nTicket purchases and pass recharges are locked.", 
                    "Service Closed", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            String selectedItem = (String) stationDropdown.getSelectedItem();
            String currentStation = selectedItem;
            if (selectedItem != null && selectedItem.contains(" (Closed for Maintenance)")) {
                currentStation = selectedItem.substring(0, selectedItem.indexOf(" (Closed for Maintenance)"));
            }
            if (currentStation != null && ServiceStateManager.isStationMaintenance(currentStation)) {
                JOptionPane.showMessageDialog(this, 
                    "Selected station (" + currentStation + ") is currently closed for maintenance. Please choose another station.", 
                    "Station Closed", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            dispose();
            DestinationSelectionPage destPage = new DestinationSelectionPage(currentStation);
            destPage.setVisible(true);
        }
    }

    private void showScheduleDialog() {
        JDialog dialog = new JDialog(this, "Station Schedule & Live Arrivals", true);
        dialog.setSize(750, 580);
        dialog.setUndecorated(true);
        dialog.setLocationRelativeTo(this);
        
        // Border Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(UIHelper.COLOR_BG_DARK);
        mainPanel.setBorder(BorderFactory.createLineBorder(UIHelper.COLOR_PRIMARY, 2));
        dialog.setContentPane(mainPanel);
        
        // Custom Title Bar
        JPanel titleBar = new JPanel();
        titleBar.setLayout(null);
        titleBar.setBackground(UIHelper.COLOR_BG_PANEL);
        titleBar.setBounds(2, 2, 746, 50);
        mainPanel.add(titleBar);
        
        JLabel titleLbl = new JLabel("DHAKA METRO STATION INFO & SCHEDULES");
        titleLbl.setFont(UIHelper.FONT_BODY_BOLD);
        titleLbl.setForeground(Color.WHITE);
        titleLbl.setBounds(20, 15, 450, 20);
        titleBar.add(titleLbl);
        
        // Drag functionality
        final Point[] initialClick = new Point[1];
        titleBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                initialClick[0] = e.getPoint();
            }
        });
        titleBar.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = dialog.getLocation().x;
                int thisY = dialog.getLocation().y;
                int xMoved = e.getX() - initialClick[0].x;
                int yMoved = e.getY() - initialClick[0].y;
                dialog.setLocation(thisX + xMoved, thisY + yMoved);
            }
        });
        
        // Close Button on Title Bar
        UIHelper.ModernButton closeBtn = new UIHelper.ModernButton("X", UIHelper.COLOR_DANGER, UIHelper.COLOR_DANGER_HOVER);
        closeBtn.setBounds(700, 10, 35, 30);
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleBar.add(closeBtn);
        
        // Operating Details Panel
        UIHelper.RoundedPanel infoPanel = new UIHelper.RoundedPanel(15, UIHelper.COLOR_BG_PANEL);
        infoPanel.setLayout(null);
        infoPanel.setBounds(20, 70, 710, 80);
        mainPanel.add(infoPanel);
        
        JLabel opHoursLbl = new JLabel("OPERATING HOURS: 07:30 AM - 09:00 PM");
        opHoursLbl.setFont(UIHelper.FONT_BODY_BOLD);
        opHoursLbl.setForeground(Color.WHITE);
        opHoursLbl.setBounds(15, 15, 350, 20);
        infoPanel.add(opHoursLbl);
        
        JLabel freqLbl = new JLabel("TRAIN FREQUENCY: Peak Hours: 10 mins  |  Off-Peak: 15 mins");
        freqLbl.setFont(UIHelper.FONT_BODY);
        freqLbl.setForeground(UIHelper.COLOR_TEXT_MUTED);
        freqLbl.setBounds(15, 40, 500, 20);
        infoPanel.add(freqLbl);
        
        JLabel peakDescLbl = new JLabel("Peak: 08-11 AM, 04:30-07:30 PM", SwingConstants.RIGHT);
        peakDescLbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        peakDescLbl.setForeground(UIHelper.COLOR_PRIMARY);
        peakDescLbl.setBounds(450, 15, 245, 20);
        infoPanel.add(peakDescLbl);
        
        // Schedule Table
        String[] columns = {"Station Name", "First Train", "Last Train", "Frequency"};
        Object[][] data = {
            {"Uttara North", "07:30 AM", "08:30 PM", "10-15 Min"},
            {"Uttara Center", "07:35 AM", "08:35 PM", "10-15 Min"},
            {"Uttara South", "07:38 AM", "08:38 PM", "10-15 Min"},
            {"Pallabi", "07:42 AM", "08:42 PM", "10-15 Min"},
            {"Mirpur 11", "07:46 AM", "08:46 PM", "10-15 Min"},
            {"Mirpur 10", "07:50 AM", "08:50 PM", "10-15 Min"},
            {"Kazipara", "07:54 AM", "08:54 PM", "10-15 Min"},
            {"Sheorapara", "07:57 AM", "08:57 PM", "10-15 Min"},
            {"Agargaon", "08:02 AM", "09:02 PM", "10-15 Min"}
        };
        
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        
        JTable table = new JTable(model);
        table.setBackground(UIHelper.COLOR_INPUT_BG);
        table.setForeground(Color.WHITE);
        table.setFont(UIHelper.FONT_BODY);
        table.setRowHeight(30);
        table.setGridColor(new Color(255, 255, 255, 10));
        table.setShowGrid(true);
        
        JTableHeader header = table.getTableHeader();
        header.setBackground(UIHelper.COLOR_PRIMARY);
        header.setForeground(Color.WHITE);
        header.setFont(UIHelper.FONT_BODY_BOLD);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 170, 710, 240);
        scrollPane.getViewport().setBackground(UIHelper.COLOR_BG_PANEL);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 15), 1));
        mainPanel.add(scrollPane);
        
        // Live countdown panel at bottom
        UIHelper.RoundedPanel countdownPanel = new UIHelper.RoundedPanel(15, UIHelper.COLOR_BG_PANEL);
        countdownPanel.setLayout(null);
        countdownPanel.setBounds(20, 430, 710, 120);
        mainPanel.add(countdownPanel);
        
        JLabel liveHeader = new JLabel("LIVE ARRIVAL ESTIMATION (Down-line to Agargaon)");
        liveHeader.setFont(UIHelper.FONT_BODY_BOLD);
        liveHeader.setForeground(UIHelper.COLOR_PRIMARY);
        liveHeader.setBounds(15, 10, 400, 20);
        countdownPanel.add(liveHeader);
        
        JLabel selectStationLbl = new JLabel("Select Station:");
        selectStationLbl.setFont(UIHelper.FONT_BODY);
        selectStationLbl.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        selectStationLbl.setBounds(15, 45, 100, 25);
        countdownPanel.add(selectStationLbl);
        
        String[] stationNames = {
            "Uttara North", "Uttara Center", "Uttara South", 
            "Pallabi", "Mirpur 11", "Mirpur 10", 
            "Kazipara", "Sheorapara", "Agargaon"
        };
        JComboBox<String> stationSelect = new JComboBox<>(stationNames);
        stationSelect.setBounds(15, 75, 200, 30);
        stationSelect.setFont(UIHelper.FONT_BODY_BOLD);
        stationSelect.setBackground(UIHelper.COLOR_INPUT_BG);
        stationSelect.setForeground(Color.WHITE);
        ((JLabel)stationSelect.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        countdownPanel.add(stationSelect);
        
        JLabel countdownLbl = new JLabel("Calculating...", SwingConstants.CENTER);
        countdownLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        countdownLbl.setForeground(Color.WHITE);
        countdownLbl.setBounds(230, 40, 465, 60);
        countdownLbl.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 10), 1, true));
        countdownPanel.add(countdownLbl);
        
        // Define the station offsets in minutes from Uttara North
        int[] offsets = {0, 2, 5, 8, 11, 14, 17, 20, 24};
        
        Timer timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int min = cal.get(Calendar.MINUTE);
                int sec = cal.get(Calendar.SECOND);
                
                // Check operating hours using ServiceStateManager
                boolean open = ServiceStateManager.isServiceOpen();
                
                if (!open) {
                    String overrideStatus = ServiceStateManager.getOverrideStatus();
                    if ("CLOSED".equals(overrideStatus)) {
                        countdownLbl.setText("<html><center>Service Closed<br><font size='3' color='#EF4444'>Suspended by Administration</font></center></html>");
                    } else {
                        countdownLbl.setText("<html><center>Service Closed<br><font size='3' color='#94A3B8'>Dhaka Metro operates 07:30 AM - 09:00 PM</font></center></html>");
                    }
                    return;
                }
                
                // Check if peak hour:
                // 8:00 AM - 11:00 AM or 04:30 PM - 07:30 PM
                boolean isPeak = false;
                if (hour >= 8 && hour < 11) {
                    isPeak = true;
                } else if ((hour == 16 && min >= 30) || (hour == 17) || (hour == 18) || (hour == 19 && min < 30)) {
                    isPeak = true;
                }
                
                int frequencyMin = isPeak ? 10 : 15;
                int selectedIndex = stationSelect.getSelectedIndex();
                int offsetMin = offsets[selectedIndex];
                
                int currentTotalSec = min * 60 + sec;
                int freqSec = frequencyMin * 60;
                int offsetSec = offsetMin * 60;
                
                int diff = currentTotalSec - offsetSec;
                int remainingSec = freqSec - (((diff % freqSec) + freqSec) % freqSec);
                
                int remMin = remainingSec / 60;
                int remSec = remainingSec % 60;
                
                String station = (String) stationSelect.getSelectedItem();
                String timeText = String.format("%02d mins %02d secs", remMin, remSec);
                String freqText = isPeak ? "Peak (10m freq)" : "Off-Peak (15m freq)";
                
                countdownLbl.setText("<html><center>Next Train at " + station + " in:<br><font color='#10B981'>" + timeText + "</font> <font size='3' color='#94A3B8'>[" + freqText + "]</font></center></html>");
            }
        });
        
        // Start countdown
        timer.start();
        
        // Action Listeners
        closeBtn.addActionListener(e -> {
            timer.stop();
            dialog.dispose();
        });
        
        stationSelect.addActionListener(e -> {
            // Trigger quick refresh on selection change
            for (ActionListener al : timer.getActionListeners()) {
                al.actionPerformed(null);
            }
        });
        
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MetroTicketVendingSystemPage page = new MetroTicketVendingSystemPage();
            page.setVisible(true);
        });
    }
}
