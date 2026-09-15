package Classes;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class DestinationSelectionPage extends JFrame implements ActionListener {
    private String currentStation;
    private int selectedDestination = -1;
    private int selectedTicketQuantity = 1;
    private double ticketFare = 0;
    private double totalCost = 0;

    private RouteMapPanel routeMapPanel;
    private UIHelper.ModernButton okButton, backButton;
    private UIHelper.ModernButton[] quantityButtons;
    private JLabel summaryTitle, summaryOrigin, summaryDest, summaryPrice, summaryQty, summaryTotal;
    private UIHelper.RoundedPanel summaryPanel, qtyPanel;
    private JLabel transitTitle, transitStopsCount, transitTime, transitRoute;

    private static final String[] STATION_NAMES = {
            "Uttara North", "Uttara Center", "Uttara South", 
            "Pallabi", "Mirpur 11", "Mirpur 10", 
            "Kazipara", "Sheorapara", "Agargaon"
    };

    private static final double[][] FARE_MATRIX = {
            {0, 20, 20, 30, 30, 40, 40, 50, 60},
            {20, 0, 20, 20, 30, 30, 40, 40, 50},
            {20, 20, 0, 20, 20, 30, 30, 40, 40},
            {30, 20, 20, 0, 20, 20, 20, 30, 30},
            {30, 30, 20, 20, 0, 20, 20, 20, 30},
            {40, 30, 30, 20, 20, 0, 20, 20, 20},
            {40, 40, 30, 20, 20, 20, 0, 20, 20},
            {50, 40, 40, 30, 20, 20, 20, 0, 20},
            {60, 50, 40, 30, 30, 20, 20, 20, 0}
    };

    public DestinationSelectionPage(String currentStation) {
        super("Destination Selection - Metro Ticket System");
        this.currentStation = currentStation;
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
        JLabel titleLabel = new JLabel("SELECT DESTINATION STATION", SwingConstants.CENTER);
        titleLabel.setFont(UIHelper.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(200, 40, 800, 50);
        container.add(titleLabel);

        // Interactive visual route map panel (horizontal line)
        routeMapPanel = new RouteMapPanel();
        routeMapPanel.setBounds(50, 120, 1100, 240);
        container.add(routeMapPanel);

        // Quantity Panel
        qtyPanel = new UIHelper.RoundedPanel(20, UIHelper.COLOR_BG_PANEL);
        qtyPanel.setLayout(null);
        qtyPanel.setBounds(50, 390, 500, 240);
        container.add(qtyPanel);

        JLabel qtyLabel = new JLabel("Number of Tickets", SwingConstants.CENTER);
        qtyLabel.setFont(UIHelper.FONT_SUBTITLE);
        qtyLabel.setForeground(UIHelper.COLOR_PRIMARY);
        qtyLabel.setBounds(50, 30, 400, 30);
        qtyPanel.add(qtyLabel);

        // Quantity Buttons (1, 2, 3)
        quantityButtons = new UIHelper.ModernButton[3];
        for (int i = 0; i < 3; i++) {
            final int qty = i + 1;
            quantityButtons[i] = new UIHelper.ModernButton(String.valueOf(qty), UIHelper.COLOR_INPUT_BG, UIHelper.COLOR_INPUT_BG);
            quantityButtons[i].setFont(new Font("Segoe UI", Font.BOLD, 22));
            quantityButtons[i].setBounds(100 + i * 110, 100, 90, 60);
            quantityButtons[i].addActionListener(e -> {
                selectedTicketQuantity = qty;
                updateQuantityButtons();
                updateFareSummary();
            });
            qtyPanel.add(quantityButtons[i]);
        }
        updateQuantityButtons(); // Highlight initial choice

        // Summary Panel
        summaryPanel = new UIHelper.RoundedPanel(20, UIHelper.COLOR_BG_PANEL);
        summaryPanel.setLayout(null);
        summaryPanel.setBounds(650, 390, 500, 240);
        container.add(summaryPanel);

        summaryTitle = new JLabel("Booking Summary", SwingConstants.LEFT);
        summaryTitle.setFont(UIHelper.FONT_SUBTITLE);
        summaryTitle.setForeground(UIHelper.COLOR_PRIMARY);
        summaryTitle.setBounds(30, 20, 215, 30);
        summaryPanel.add(summaryTitle);

        summaryOrigin = new JLabel("Origin: " + currentStation);
        summaryOrigin.setFont(UIHelper.FONT_BODY);
        summaryOrigin.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        summaryOrigin.setBounds(30, 60, 215, 25);
        summaryPanel.add(summaryOrigin);

        summaryDest = new JLabel("Destination: Select Map");
        summaryDest.setFont(UIHelper.FONT_BODY);
        summaryDest.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        summaryDest.setBounds(30, 90, 215, 25);
        summaryPanel.add(summaryDest);

        summaryPrice = new JLabel("Price: -- BDT");
        summaryPrice.setFont(UIHelper.FONT_BODY);
        summaryPrice.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        summaryPrice.setBounds(30, 120, 215, 25);
        summaryPanel.add(summaryPrice);

        summaryQty = new JLabel("Quantity: 1 ticket");
        summaryQty.setFont(UIHelper.FONT_BODY);
        summaryQty.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        summaryQty.setBounds(30, 150, 215, 25);
        summaryPanel.add(summaryQty);

        summaryTotal = new JLabel("Total: 0 BDT");
        summaryTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        summaryTotal.setForeground(UIHelper.COLOR_PRIMARY);
        summaryTotal.setBounds(30, 185, 215, 30);
        summaryPanel.add(summaryTotal);

        // Divider Line in summaryPanel
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(255, 255, 255, 20));
                g.drawLine(0, 0, 0, getHeight());
            }
        };
        divider.setBounds(255, 20, 1, 200);
        divider.setOpaque(false);
        summaryPanel.add(divider);

        // Right column (Transit Details)
        transitTitle = new JLabel("Transit Pathfinder", SwingConstants.LEFT);
        transitTitle.setFont(UIHelper.FONT_SUBTITLE);
        transitTitle.setForeground(UIHelper.COLOR_ACCENT);
        transitTitle.setBounds(275, 20, 200, 30);
        summaryPanel.add(transitTitle);

        transitStopsCount = new JLabel("Stops Count: --");
        transitStopsCount.setFont(UIHelper.FONT_BODY);
        transitStopsCount.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        transitStopsCount.setBounds(275, 60, 200, 25);
        summaryPanel.add(transitStopsCount);

        transitTime = new JLabel("Duration: --");
        transitTime.setFont(UIHelper.FONT_BODY);
        transitTime.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        transitTime.setBounds(275, 90, 200, 25);
        summaryPanel.add(transitTime);

        JLabel routeLabel = new JLabel("Route Path:");
        routeLabel.setFont(UIHelper.FONT_BODY_BOLD);
        routeLabel.setForeground(UIHelper.COLOR_TEXT_MUTED);
        routeLabel.setBounds(275, 120, 200, 20);
        summaryPanel.add(routeLabel);

        transitRoute = new JLabel("<html><i>Select destination...</i></html>");
        transitRoute.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        transitRoute.setForeground(UIHelper.COLOR_TEXT_LIGHT);
        transitRoute.setVerticalAlignment(SwingConstants.TOP);
        transitRoute.setBounds(275, 142, 215, 80);
        summaryPanel.add(transitRoute);

        // Buttons
        backButton = new UIHelper.ModernButton("Go Back", UIHelper.COLOR_DANGER, UIHelper.COLOR_DANGER_HOVER);
        backButton.setBounds(50, 670, 220, 50);
        backButton.addActionListener(this);
        container.add(backButton);

        okButton = new UIHelper.ModernButton("Proceed to Payment", UIHelper.COLOR_PRIMARY, UIHelper.COLOR_PRIMARY_HOVER);
        okButton.setBounds(930, 670, 220, 50);
        okButton.setEnabled(false); // Enable only when destination is chosen
        okButton.addActionListener(this);
        container.add(okButton);
    }

    private int getStationIndex(String stationName) {
        for (int i = 0; i < STATION_NAMES.length; i++) {
            if (STATION_NAMES[i].equalsIgnoreCase(stationName)) {
                return i;
            }
        }
        return -1;
    }

    private void updateQuantityButtons() {
        for (int i = 0; i < 3; i++) {
            if (selectedTicketQuantity == (i + 1)) {
                quantityButtons[i].setBackground(UIHelper.COLOR_PRIMARY);
                quantityButtons[i].setForeground(Color.WHITE);
            } else {
                quantityButtons[i].setBackground(UIHelper.COLOR_INPUT_BG);
                quantityButtons[i].setForeground(UIHelper.COLOR_TEXT_MUTED);
            }
        }
    }

    private void updateFareSummary() {
        if (selectedDestination != -1) {
            int originIndex = getStationIndex(currentStation);
            double multiplier = ServiceStateManager.getFareMultiplier();
            double baseFare = FARE_MATRIX[originIndex][selectedDestination];
            ticketFare = baseFare * multiplier;
            totalCost = ticketFare * selectedTicketQuantity;

            summaryDest.setText("Destination: " + STATION_NAMES[selectedDestination]);
            if (multiplier != 1.0) {
                summaryPrice.setText(String.format(java.util.Locale.US, "Price: %.1f BDT (%.1fx rate)", ticketFare, multiplier));
            } else {
                summaryPrice.setText("Price: " + baseFare + " BDT");
            }
            summaryQty.setText("Quantity: " + selectedTicketQuantity + (selectedTicketQuantity > 1 ? " tickets" : " ticket"));
            summaryTotal.setText("Total: " + totalCost + " BDT");

            // Transit Pathfinder Calculations
            int stopsCount = Math.abs(selectedDestination - originIndex);
            double duration = stopsCount * 2.5;

            transitStopsCount.setText("Stops Count: " + stopsCount + (stopsCount > 1 ? " stops" : " stop"));
            transitTime.setText(String.format(java.util.Locale.US, "Duration: %.1f mins", duration));

            // Build Route Path string
            StringBuilder routePath = new StringBuilder("<html>");
            int direction = (selectedDestination > originIndex) ? 1 : -1;
            for (int i = originIndex; i != selectedDestination; i += direction) {
                routePath.append(STATION_NAMES[i]).append("<br>&rarr; ");
            }
            routePath.append("<b>").append(STATION_NAMES[selectedDestination]).append("</b></html>");
            transitRoute.setText(routePath.toString());

            okButton.setEnabled(true);
        } else {
            summaryDest.setText("Destination: Select Map");
            summaryPrice.setText("Price: -- BDT");
            summaryQty.setText("Quantity: " + selectedTicketQuantity + (selectedTicketQuantity > 1 ? " tickets" : " ticket"));
            summaryTotal.setText("Total: 0 BDT");

            transitStopsCount.setText("Stops Count: --");
            transitTime.setText("Duration: --");
            transitRoute.setText("<html><i>Select destination...</i></html>");

            okButton.setEnabled(false);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            dispose();
            MetroTicketVendingSystemPage startPage = new MetroTicketVendingSystemPage();
            startPage.setVisible(true);
        } else if (e.getSource() == okButton) {
            if (selectedDestination != -1) {
                try {
                    PaymentPage paymentPage = new PaymentPage(currentStation, selectedDestination + 1, selectedTicketQuantity, ticketFare, totalCost);
                    dispose();
                } catch (Throwable ex) {
                    ex.printStackTrace();
                    String msg = ex.getMessage();
                    if (msg == null) {
                        msg = ex.toString();
                    }
                    JOptionPane.showMessageDialog(this, 
                        "Failed to open Payment screen:\n" + msg + "\nCheck terminal logs for full stack trace.", 
                        "Navigation Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Custom Route Map Drawing Component
    private class RouteMapPanel extends JPanel {
        private final int numStations = 9;
        private final int nodeRadius = 15;
        private final int lineY = 100;
        private final int startX = 80;
        private final int stepX = 118; // Spreads them evenly within 1100 width
        private int hoveredIndex = -1;
        private final boolean[] maintenanceStatuses = new boolean[numStations];

        public RouteMapPanel() {
            setOpaque(false);
            for (int i = 0; i < numStations; i++) {
                maintenanceStatuses[i] = ServiceStateManager.isStationMaintenance(STATION_NAMES[i]);
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int clicked = getStationAt(e.getPoint());
                    int originIndex = getStationIndex(currentStation);
                    if (clicked != -1 && clicked != originIndex) {
                        selectedDestination = clicked;
                        updateFareSummary();
                        repaint();
                    }
                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int prevHover = hoveredIndex;
                    hoveredIndex = getStationAt(e.getPoint());
                    int originIndex = getStationIndex(currentStation);
                    if (hoveredIndex == originIndex) {
                        hoveredIndex = -1; // Cannot select origin
                    }
                    if (hoveredIndex != prevHover) {
                        if (hoveredIndex != -1) {
                            setCursor(new Cursor(Cursor.HAND_CURSOR));
                        } else {
                            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        }
                        repaint();
                    }
                }
            });
        }

        private int getStationAt(Point p) {
            for (int i = 0; i < numStations; i++) {
                int cx = startX + i * stepX;
                int cy = lineY;
                double dist = p.distance(cx, cy);
                if (dist <= 25) { // Clickable radius
                    if (maintenanceStatuses[i]) {
                        return -1;
                    }
                    return i;
                }
            }
            return -1;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw Slate Panel Background
            g2.setColor(UIHelper.COLOR_BG_PANEL);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);

            // Draw Railway Line Track
            g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(UIHelper.COLOR_INPUT_BG);
            g2.drawLine(startX, lineY, startX + (numStations - 1) * stepX, lineY);

            // Draw active/completed route track in glowing green if a destination is chosen
            int originIndex = getStationIndex(currentStation);
            if (selectedDestination != -1) {
                int left = Math.min(originIndex, selectedDestination);
                int right = Math.max(originIndex, selectedDestination);
                
                // Outer glow
                g2.setStroke(new BasicStroke(16, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(16, 185, 129, 80)); // Semi-transparent emerald
                g2.drawLine(startX + left * stepX, lineY, startX + right * stepX, lineY);
                
                // Inner solid line
                g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(UIHelper.COLOR_PRIMARY); // Solid emerald
                g2.drawLine(startX + left * stepX, lineY, startX + right * stepX, lineY);
            }

            // Draw Nodes
            for (int i = 0; i < numStations; i++) {
                int cx = startX + i * stepX;
                int cy = lineY;
                boolean isMaintenance = maintenanceStatuses[i];

                // Node coloring & highlighting
                if (isMaintenance) {
                    // Grayed out maintenance node with X
                    g2.setColor(new Color(71, 85, 105)); // Slate 600
                    g2.fillOval(cx - nodeRadius, cy - nodeRadius, nodeRadius * 2, nodeRadius * 2);
                    g2.setColor(new Color(148, 163, 184)); // Slate 400
                    g2.setStroke(new BasicStroke(2));
                    g2.drawLine(cx - 7, cy - 7, cx + 7, cy + 7);
                    g2.drawLine(cx - 7, cy + 7, cx + 7, cy - 7);
                } else if (i == originIndex) {
                    // Origin station: Coral Red node with label
                    g2.setColor(UIHelper.COLOR_DANGER);
                    g2.fillOval(cx - nodeRadius, cy - nodeRadius, nodeRadius * 2, nodeRadius * 2);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawOval(cx - nodeRadius, cy - nodeRadius, nodeRadius * 2, nodeRadius * 2);
                } else if (i == selectedDestination) {
                    // Selected destination: Glowing emerald node
                    g2.setColor(UIHelper.COLOR_PRIMARY);
                    g2.fillOval(cx - nodeRadius, cy - nodeRadius, nodeRadius * 2, nodeRadius * 2);
                    
                    // Draw outer ring highlight
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawOval(cx - nodeRadius - 3, cy - nodeRadius - 3, (nodeRadius + 3) * 2, (nodeRadius + 3) * 2);
                } else if (selectedDestination != -1 && ((i > originIndex && i < selectedDestination) || (i < originIndex && i > selectedDestination))) {
                    // Intermediate station: color node in emerald green
                    g2.setColor(UIHelper.COLOR_PRIMARY);
                    g2.fillOval(cx - (nodeRadius - 2), cy - (nodeRadius - 2), (nodeRadius - 2) * 2, (nodeRadius - 2) * 2);
                    g2.setColor(Color.WHITE);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawOval(cx - (nodeRadius - 2), cy - (nodeRadius - 2), (nodeRadius - 2) * 2, (nodeRadius - 2) * 2);
                } else if (i == hoveredIndex) {
                    // Hovered node
                    g2.setColor(UIHelper.COLOR_ACCENT);
                    g2.fillOval(cx - nodeRadius, cy - nodeRadius, nodeRadius * 2, nodeRadius * 2);
                } else {
                    // Ordinary node
                    g2.setColor(UIHelper.COLOR_TEXT_MUTED);
                    g2.fillOval(cx - (nodeRadius - 3), cy - (nodeRadius - 3), (nodeRadius - 3) * 2, (nodeRadius - 3) * 2);
                }

                // Node text labels (alternating above and below line)
                g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                String stationName = STATION_NAMES[i];
                String displayName = stationName;
                if (isMaintenance) {
                    displayName = stationName + " (Closed)";
                }
                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(displayName);

                if (i % 2 == 0) {
                    // Draw above line
                    g2.setColor(isMaintenance ? new Color(148, 163, 184) : UIHelper.COLOR_TEXT_LIGHT);
                    g2.drawString(displayName, cx - textWidth / 2, cy - 25);
                    
                    // Draw origin tag if applicable
                    if (i == originIndex) {
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                        g2.setColor(UIHelper.COLOR_DANGER);
                        g2.drawString("ORIGIN", cx - g2.getFontMetrics().stringWidth("ORIGIN") / 2, cy - 40);
                    }
                } else {
                    // Draw below line
                    g2.setColor(isMaintenance ? new Color(148, 163, 184) : UIHelper.COLOR_TEXT_LIGHT);
                    g2.drawString(displayName, cx - textWidth / 2, cy + 32);
                    
                    if (i == originIndex) {
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 9));
                        g2.setColor(UIHelper.COLOR_DANGER);
                        g2.drawString("ORIGIN", cx - g2.getFontMetrics().stringWidth("ORIGIN") / 2, cy + 47);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        DestinationSelectionPage p = new DestinationSelectionPage("Uttara North");
        p.setVisible(true);
    }
}
