package CAC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HighestImportExport {

    public static void main(String[] args) {
        String csvFile = "C:\\Users\\V SRAVANI\\OneDrive\\Desktop\\Java CAC2\\CAC\\fill.csv"; // Update with your CSV file path
        try {
            String[] highestImportDetails = findHighestValue(csvFile, "Import");
            String[] highestExportDetails = findHighestValue(csvFile, "Export");

            System.out.println("Highest Import Value: " + highestImportDetails[0] + " in " + highestImportDetails[1]);
            System.out.println("Highest Export Value: " + highestExportDetails[0] + " in " + highestExportDetails[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[] findHighestValue(String csvFile, String columnName) throws IOException {
        double highestValue = Double.MIN_VALUE;
        int columnIndex = -1;
        String[] highestDetails = new String[2]; // Array to hold the value and its corresponding year/country

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String headerLine = br.readLine(); // Read the header line to get column names
            String[] headers = headerLine.split(",");

            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equalsIgnoreCase(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex == -1) {
                throw new IllegalArgumentException("Column '" + columnName + "' not found in CSV file.");
            }

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > columnIndex) {
                    double value = Double.parseDouble(parts[columnIndex].trim());
                    if (value > highestValue) {
                        highestValue = value;
                        highestDetails[0] = Double.toString(highestValue);
                        // Assuming the first column is year or country name
                        highestDetails[1] = parts[0].trim();
                    }
                }
            }
        }

        return highestDetails;
    }
}







