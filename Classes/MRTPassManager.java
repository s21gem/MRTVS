package Classes;

import java.io.*;
import java.util.Random;

public class MRTPassManager {
    private static final String PASS_FILE = "Data/mrt_passes.txt";

    public static class PassInfo {
        public String username;
        public String cardNumber;
        public double balance;
        public String status; // "Active", "Blocked"

        public PassInfo(String username, String cardNumber, double balance, String status) {
            this.username = username;
            this.cardNumber = cardNumber;
            this.balance = balance;
            this.status = status;
        }
    }

    public static PassInfo getPass(String username) {
        File file = new File(PASS_FILE);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    if (parts[0].trim().equalsIgnoreCase(username)) {
                        return new PassInfo(
                            parts[0].trim(),
                            parts[1].trim(),
                            Double.parseDouble(parts[2].trim()),
                            parts[3].trim()
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PassInfo getPassByCard(String cardNumber) {
        File file = new File(PASS_FILE);
        if (!file.exists()) return null;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    if (parts[1].trim().equals(cardNumber.trim())) {
                        return new PassInfo(
                            parts[0].trim(),
                            parts[1].trim(),
                            Double.parseDouble(parts[2].trim()),
                            parts[3].trim()
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PassInfo createPass(String username) {
        String cardNumber;
        Random rand = new Random();
        do {
            long num = 1000000000L + (long)(rand.nextDouble() * 8999999999L);
            cardNumber = String.valueOf(num);
        } while (getPassByCard(cardNumber) != null);

        PassInfo pass = new PassInfo(username, cardNumber, 0.0, "Active");
        savePass(pass);
        return pass;
    }

    public static void savePass(PassInfo pass) {
        File file = new File(PASS_FILE);
        File temp = new File("Data/mrt_passes_temp.txt");
        
        File dir = new File("Data");
        if (!dir.exists()) {
            dir.mkdirs();
        }

        boolean updated = false;
        try (BufferedReader reader = file.exists() ? new BufferedReader(new FileReader(file)) : null;
             BufferedWriter writer = new BufferedWriter(new FileWriter(temp))) {
            
            if (reader != null) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length >= 4 && parts[0].trim().equalsIgnoreCase(pass.username)) {
                        writer.write(pass.username + "," + pass.cardNumber + "," + pass.balance + "," + pass.status + "\n");
                        updated = true;
                    } else {
                        writer.write(line + "\n");
                    }
                }
            }
            
            if (!updated) {
                writer.write(pass.username + "," + pass.cardNumber + "," + pass.balance + "," + pass.status + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (file.exists()) {
            file.delete();
        }
        temp.renameTo(file);
    }
}
