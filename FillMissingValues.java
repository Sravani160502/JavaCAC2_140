package CAC;


import java.io.*;
import java.util.*;

public class FillMissingValues {
    public static void main(String[] args) {
        String inputFileName = "C:\\Users\\V SRAVANI\\OneDrive\\Desktop\\Java CAC2\\CAC\\CAC2.csv"; // Ensure this path is correct
        String outputFileName = "C:\\Users\\V SRAVANI\\OneDrive\\Desktop\\Java CAC2\\fill.csv";

        List<String[]> data = readCSVData(inputFileName);
        if (data.isEmpty()) {
            System.out.println("No data found in the file or file not found.");
            return;
        }

        fillMissingValues(data);
        writeCSVData(outputFileName, data);

        System.out.println("Missing values filled with column means and saved as " + outputFileName);
    }

    public static List<String[]> readCSVData(String fileName) {
        List<String[]> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                data.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static void fillMissingValues(List<String[]> data) {
        int numColumns = data.get(0).length;

        // Calculate sums and counts for each column
        int[] columnCounts = new int[numColumns];
        double[] columnSums = new double[numColumns];

        // Skip the header row
        boolean isFirstRow = true;

        for (String[] row : data) {
            if (isFirstRow) {
                isFirstRow = false;
                continue; // Skip header row
            }

            for (int i = 0; i < numColumns; i++) {
                if (!row[i].trim().isEmpty()) {
                    try {
                        columnSums[i] += Double.parseDouble(row[i].trim());
                        columnCounts[i]++;
                    } catch (NumberFormatException e) {
                        // Skip non-numeric values
                    }
                }
            }
        }

        // Calculate means for each column
        double[] columnMeans = new double[numColumns];
        for (int i = 0; i < numColumns; i++) {
            if (columnCounts[i] > 0) {
                columnMeans[i] = columnSums[i] / columnCounts[i];
            } else {
                columnMeans[i] = 0; // Or some default value, if no valid data points are available
            }
        }

        // Fill missing values with column means
        isFirstRow = true;
        for (String[] row : data) {
            if (isFirstRow) {
                isFirstRow = false;
                continue; // Skip header row
            }

            for (int i = 0; i < numColumns; i++) {
                if (row[i].trim().isEmpty()) {
                    row[i] = String.valueOf(columnMeans[i]);
                }
            }
        }
    }

    public static void writeCSVData(String fileName, List<String[]> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}







