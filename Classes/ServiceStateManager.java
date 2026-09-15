package Classes;

import java.io.*;
import java.util.Calendar;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceStateManager {
    private static final String STATUS_FILE = "Data/service_status.txt";
    private static final String STATION_STATUS_FILE = "Data/station_status.txt";
    private static final String FARE_MULTIPLIER_FILE = "Data/fare_multiplier.txt";

    public static String getOverrideStatus() {
        File file = new File(STATUS_FILE);
        if (!file.exists()) {
            return "AUTO"; // default
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) {
                return line.trim().toUpperCase();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "AUTO";
    }

    public static void setOverrideStatus(String status) {
        File dir = new File("Data");
        if (!dir.exists()) dir.mkdirs();
        try (FileWriter writer = new FileWriter(STATUS_FILE)) {
            writer.write(status.trim().toUpperCase());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isServiceOpen() {
        String status = getOverrideStatus();
        if (status.equals("OPEN")) {
            return true;
        }
        if (status.equals("CLOSED")) {
            return false;
        }
        
        // AUTO status: check operating hours (07:30 AM - 09:00 PM)
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);

        if (hour < 7 || (hour == 7 && min < 30) || hour >= 21) {
            return false;
        }
        return true;
    }

    public static double getFareMultiplier() {
        File file = new File(FARE_MULTIPLIER_FILE);
        if (!file.exists()) {
            return 1.0;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            if (line != null) {
                return Double.parseDouble(line.trim());
            }
        } catch (Exception e) {
            // fallback
        }
        return 1.0;
    }

    public static void setFareMultiplier(double multiplier) {
        File dir = new File("Data");
        if (!dir.exists()) dir.mkdirs();
        try (FileWriter writer = new FileWriter(FARE_MULTIPLIER_FILE)) {
            writer.write(String.format(java.util.Locale.US, "%.1f", multiplier));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isStationMaintenance(String stationName) {
        File file = new File(STATION_STATUS_FILE);
        if (!file.exists()) {
            return false;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    String status = parts[1].trim();
                    if (name.equalsIgnoreCase(stationName.trim()) && status.equalsIgnoreCase("Maintenance")) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void setStationMaintenance(String stationName, boolean underMaintenance) {
        File dir = new File("Data");
        if (!dir.exists()) dir.mkdirs();
        
        Map<String, String> statusMap = new HashMap<>();
        File file = new File(STATION_STATUS_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 2) {
                        statusMap.put(parts[0].trim().toLowerCase(), parts[1].trim());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        String key = stationName.trim().toLowerCase();
        if (underMaintenance) {
            statusMap.put(key, "Maintenance");
        } else {
            statusMap.put(key, "Operational");
        }
        
        String[] stationNames = {
            "Uttara North", "Uttara Center", "Uttara South", 
            "Pallabi", "Mirpur 11", "Mirpur 10", 
            "Kazipara", "Sheorapara", "Agargaon"
        };
        try (FileWriter writer = new FileWriter(STATION_STATUS_FILE)) {
            for (String name : stationNames) {
                String status = statusMap.get(name.toLowerCase());
                if (status == null) {
                    status = "Operational";
                }
                writer.write(name + "," + status + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getMaintenanceStations() {
        List<String> maintenanceList = new ArrayList<>();
        File file = new File(STATION_STATUS_FILE);
        if (!file.exists()) {
            return maintenanceList;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    String status = parts[1].trim();
                    if (status.equalsIgnoreCase("Maintenance")) {
                        maintenanceList.add(name);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maintenanceList;
    }
}
