package CAC;

// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;
// import java.io.BufferedReader;
// import java.io.FileReader;
// import java.io.IOException;

// public class CSVVisualization extends JFrame {

//     private JTable table;

//     public CSVVisualization(String csvFile) {
//         super("CSV Visualization");
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setSize(600, 400);

//         table = new JTable();
//         JScrollPane scrollPane = new JScrollPane(table);
//         add(scrollPane, BorderLayout.CENTER);

//         loadCSV(csvFile);
//     }

//     private void loadCSV(String csvFile) {
//         DefaultTableModel model = new DefaultTableModel();
//         try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
//             String line;
//             String[] headers = null;
//             while ((line = br.readLine()) != null) {
//                 String[] data = line.split(",");
//                 if (headers == null) {
//                     headers = data;
//                     model.setColumnIdentifiers(headers);
//                 } else {
//                     model.addRow(data);
//                 }
//             }
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//         table.setModel(model);
//     }

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> {
//             String csvFile = "C:\\Users\\V SRAVANI\\OneDrive\\Desktop\\Java CAC2\\CAC\\fill.csv"; // Update with your CSV file path
//             CSVVisualization frame = new CSVVisualization(csvFile);
//             frame.setVisible(true);
//         });
//     }
// }

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BarGraph {

    public static void main(String[] args) {
        String csvFile = "C:\\Users\\V SRAVANI\\OneDrive\\Desktop\\Java CAC2\\CAC\\fill.csv"; // Update with your CSV file path
        String xColumn = "Country"; // Country column name
        String yColumn = "Export"; // Export column name

        Map<String, Double> countryExportMap = calculateAverageExport(csvFile, xColumn, yColumn);

        JFrame frame = new JFrame("Average Export by Country");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600); // Increased frame width to accommodate labels

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int barWidth = 30; // Reduced bar width for better separation
                int spacing = 20;
                int startX = 50;
                int startY = getHeight() - 80; // Adjusted start y-position

                for (String country : countryExportMap.keySet()) {
                    double export = countryExportMap.get(country);
                    int barHeight = (int) export * 3; // Scaling factor for better visualization
                    int y = startY - barHeight; // Adjust y position to draw from bottom
                    g2d.setColor(Color.BLUE);
                    g2d.fillRect(startX, y, barWidth, barHeight);

                    g2d.setColor(Color.BLACK);
                    g2d.drawString(country, startX - 5, startY + 15); // Adjusted label position

                    startX += barWidth + spacing;
                }
            }
        };

        frame.add(panel);
        frame.setVisible(true);
    }

    public static Map<String, Double> calculateAverageExport(String csvFile, String xColumn, String yColumn) {
        Map<String, Double> countryExportMap = new HashMap<>();
        Map<String, Integer> countryCountMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String headerLine = br.readLine(); // Read the header line to get column names
            String[] headers = headerLine.split(",");

            int xIndex = -1;
            int yIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase(xColumn)) {
                    xIndex = i;
                } else if (headers[i].trim().equalsIgnoreCase(yColumn)) {
                    yIndex = i;
                }
            }

            if (xIndex == -1 || yIndex == -1) {
                throw new IllegalArgumentException("Column not found in CSV file.");
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > Math.max(xIndex, yIndex)) {
                    String country = parts[xIndex].trim();
                    double export = Double.parseDouble(parts[yIndex].trim());

                    double currentExportSum = countryExportMap.getOrDefault(country, 0.0);
                    int currentCount = countryCountMap.getOrDefault(country, 0);

                    countryExportMap.put(country, currentExportSum + export);
                    countryCountMap.put(country, currentCount + 1);
                }
            }

            // Calculate average
            for (String country : countryExportMap.keySet()) {
                double totalExport = countryExportMap.get(country);
                int count = countryCountMap.get(country);
                double averageExport = totalExport / count;
                countryExportMap.put(country, averageExport);
            }

        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return countryExportMap;
    }
}
