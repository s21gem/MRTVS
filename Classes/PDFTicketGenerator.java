package Classes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class PDFTicketGenerator {
    
    public static byte[] generateTicketPDF(String username, String fromStation, String toStation, int quantity, double rate, double totalCost, String ticketId, String dateTimeStr) throws IOException {
        PDFBuilder pdf = new PDFBuilder();

        // 1. Catalog Object (ID 1)
        // 2. Pages Object (ID 2)
        // 3. Page Object (ID 3)
        // 4. Font 1 Helvetica-Bold (ID 4)
        // 5. Font 2 Helvetica (ID 5)
        // 6. Content Stream (ID 6)

        int catalogId = 1;
        int pagesId = 2;
        int pageId = 3;
        int fontBoldId = 4;
        int fontNormalId = 5;
        int contentId = 6;

        pdf.addObject("<< /Type /Catalog /Pages " + pagesId + " 0 R >>");
        pdf.addObject("<< /Type /Pages /Kids [" + pageId + " 0 R] /Count 1 >>");
        pdf.addObject("<< /Type /Page /Parent " + pagesId + " 0 R /Resources << /Font << /F1 " + fontBoldId + " 0 R /F2 " + fontNormalId + " 0 R >> >> /MediaBox [0 0 350 550] /Contents " + contentId + " 0 R >>");
        pdf.addObject("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold /Encoding /WinAnsiEncoding >>");
        pdf.addObject("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica /Encoding /WinAnsiEncoding >>");

        // Content Stream Builder
        StringBuilder stream = new StringBuilder();
        
        // Draw Header Banner Background (Emerald Green)
        stream.append("0.06 0.72 0.50 rg\n"); // Emerald green (#10B981)
        stream.append("15 470 320 60 re\n");
        stream.append("f\n");

        // Draw Ticket Border (Slate color)
        stream.append("0.2 0.25 0.35 RG\n"); // Dark Slate (#334155)
        stream.append("2 w\n"); // line width 2
        stream.append("15 15 320 520 re\n");
        stream.append("S\n");

        // Text: Header Title
        stream.append("BT\n");
        stream.append("/F1 18 Tf\n"); // Helvetica-Bold, 18pt
        stream.append("1 g\n"); // White text
        stream.append("45 492 Td\n");
        stream.append("(METRO RAIL TICKET) Tj\n");
        stream.append("ET\n");

        // Text: Subtitle
        stream.append("BT\n");
        stream.append("/F2 9 Tf\n"); // Helvetica, 9pt
        stream.append("1 g\n");
        stream.append("45 480 Td\n");
        stream.append("(Dhaka Mass Transit Company Limited) Tj\n");
        stream.append("ET\n");

        // Details Layout
        int y = 420;
        String[][] details = {
            {"Passenger", username},
            {"From Station", fromStation},
            {"To Station", toStation},
            {"Quantity", String.valueOf(quantity) + (quantity > 1 ? " Tickets" : " Ticket")},
            {"Ticket Rate", rate + " BDT"},
            {"Total Cost", totalCost + " BDT"},
            {"Date & Time", dateTimeStr},
            {"Ticket ID", ticketId}
        };

        for (String[] detail : details) {
            String label = detail[0].toUpperCase();
            String value = escapePDFString(detail[1]);

            // Label
            stream.append("BT\n");
            stream.append("/F1 9 Tf\n");
            stream.append("0.4 0.45 0.55 rg\n"); // Slate gray text
            stream.append("35 " + y + " Td\n");
            stream.append("(" + label + ") Tj\n");
            stream.append("ET\n");

            // Value
            stream.append("BT\n");
            stream.append("/F2 11 Tf\n");
            stream.append("0.1 0.1 0.1 rg\n"); // Dark text
            stream.append("140 " + y + " Td\n");
            stream.append("(" + value + ") Tj\n");
            stream.append("ET\n");

            // Separation Line
            stream.append("0.85 0.85 0.85 RG\n"); // Light gray line
            stream.append("1 w\n");
            stream.append("35 " + (y - 12) + " m\n");
            stream.append("315 " + (y - 12) + " l\n");
            stream.append("S\n");

            y -= 40;
        }

        // Cut-here Line (Dashed)
        stream.append("[4 4] 0 d\n"); // Dash: 4 on, 4 off
        stream.append("0.5 0.5 0.5 RG\n");
        stream.append("35 120 m\n");
        stream.append("315 120 l\n");
        stream.append("S\n");
        stream.append("[] 0 d\n"); // Reset to solid

        // Draw Barcode
        stream.append("0 g\n"); // Black fill
        int bx = 55;
        int[] barWidths = {2, 4, 1, 3, 1, 4, 2, 1, 3, 2, 4, 1, 2, 3, 1, 4, 2, 1, 3, 2, 4, 1, 2, 3, 1, 4, 2, 1, 3};
        for (int w : barWidths) {
            stream.append(bx + " 40 " + w + " 60 re\n");
            stream.append("f\n");
            bx += w + 2;
        }

        // Barcode text
        stream.append("BT\n");
        stream.append("/F2 8 Tf\n");
        stream.append("0.3 0.3 0.3 rg\n");
        stream.append("110 25 Td\n");
        stream.append("(" + ticketId + ") Tj\n");
        stream.append("ET\n");

        byte[] streamBytes = stream.toString().getBytes(StandardCharsets.ISO_8859_1);
        String contentObjDict = "<< /Length " + streamBytes.length + " >>\nstream\n";
        ByteArrayOutputStream contentObjStream = new ByteArrayOutputStream();
        contentObjStream.write(contentObjDict.getBytes(StandardCharsets.ISO_8859_1));
        contentObjStream.write(streamBytes);
        contentObjStream.write("\nendstream".getBytes(StandardCharsets.ISO_8859_1));

        pdf.addObject(contentObjStream.toByteArray());

        return pdf.build();
    }

    private static String escapePDFString(String text) {
        if (text == null) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '(' || c == ')') {
                sb.append('\\').append(c);
            } else if (c == '\\') {
                sb.append("\\\\");
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static class PDFBuilder {
        private final List<byte[]> objects = new ArrayList<>();
        private final List<Integer> offsets = new ArrayList<>();
        private final ByteArrayOutputStream out = new ByteArrayOutputStream();

        public int addObject(String content) {
            int index = objects.size() + 1;
            objects.add(content.getBytes(StandardCharsets.ISO_8859_1));
            return index;
        }

        public int addObject(byte[] content) {
            int index = objects.size() + 1;
            objects.add(content);
            return index;
        }

        public byte[] build() throws IOException {
            out.reset();
            offsets.clear();

            // Write header
            out.write("%PDF-1.4\r\n".getBytes(StandardCharsets.ISO_8859_1));
            out.write(new byte[] { '%', (byte)0xE2, (byte)0xE3, (byte)0xCF, (byte)0xD3, '\r', '\n' });

            // Write objects
            for (int i = 0; i < objects.size(); i++) {
                offsets.add(out.size());
                String objHeader = (i + 1) + " 0 obj\r\n";
                out.write(objHeader.getBytes(StandardCharsets.ISO_8859_1));
                out.write(objects.get(i));
                out.write("\r\nendobj\r\n".getBytes(StandardCharsets.ISO_8859_1));
            }

            int xrefOffset = out.size();

            // Write xref
            out.write("xref\r\n".getBytes(StandardCharsets.ISO_8859_1));
            String sectionHeader = "0 " + (objects.size() + 1) + "\r\n";
            out.write(sectionHeader.getBytes(StandardCharsets.ISO_8859_1));

            // Object 0 entry
            out.write("0000000000 65535 f\r\n".getBytes(StandardCharsets.ISO_8859_1));

            for (int i = 0; i < objects.size(); i++) {
                String entry = String.format("%010d 00000 n\r\n", offsets.get(i));
                out.write(entry.getBytes(StandardCharsets.ISO_8859_1));
            }

            // Write trailer
            out.write("trailer\r\n".getBytes(StandardCharsets.ISO_8859_1));
            String trailerDict = "<< /Size " + (objects.size() + 1) + " /Root 1 0 R >>\r\n";
            out.write(trailerDict.getBytes(StandardCharsets.ISO_8859_1));

            out.write("startxref\r\n".getBytes(StandardCharsets.ISO_8859_1));
            out.write((xrefOffset + "\r\n").getBytes(StandardCharsets.ISO_8859_1));
            out.write("%%EOF\r\n".getBytes(StandardCharsets.ISO_8859_1));

            return out.toByteArray();
        }
    }
}
