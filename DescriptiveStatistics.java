package CAC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DescriptiveStatistics {

    public static void main(String[] args) {
        String inputFileName = "C:\\Users\\V SRAVANI\\OneDrive\\Desktop\\Java CAC2\\CAC\\fill.csv"; // Update this path

        List<String[]> data = readCSVData(inputFileName);
        if (data.isEmpty()) {
            System.out.println("No data found in the file or file not found.");
            return;
        }

        calculateAndPrintStatistics(data);
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

    public static void calculateAndPrintStatistics(List<String[]> data) {
        int numColumns = data.get(0).length;

        // Initialize lists for each column
        List<List<Double>> columns = new ArrayList<>();
        for (int i = 1; i < 5; i++) { // Columns 2 to 5
            columns.add(new ArrayList<>());
        }

        // Skip the header row
        boolean isFirstRow = true;

        for (String[] row : data) {
            if (isFirstRow) {
                isFirstRow = false;
                continue;
            }

            for (int i = 1; i < 5; i++) { // Columns 2 to 5
                try {
                    double value = Double.parseDouble(row[i]);
                    columns.get(i - 1).add(value); // Subtract 1 from index to align with list index
                } catch (NumberFormatException | IndexOutOfBoundsException e) {
                    // Skip non-numeric values or index out of bounds
                }
            }
        }

        // Calculate and print statistics for each column
        for (int i = 0; i < columns.size(); i++) {
            List<Double> column = columns.get(i);
            if (!column.isEmpty()) {
                double mean = calculateMean(column);
                double median = calculateMedian(column);
                double stdDev = calculateStandardDeviation(column, mean);
                double min = Collections.min(column);
                double max = Collections.max(column);

                System.out.println("Column " + (i + 2) + " Statistics:"); // Add 2 to index to align with column number
                System.out.println("Mean: " + mean);
                System.out.println("Median: " + median);
                System.out.println("Standard Deviation: " + stdDev);
                System.out.println("Min: " + min);
                System.out.println("Max: " + max);
                System.out.println();
            }
        }
    }

    public static double calculateMean(List<Double> column) {
        double sum = 0.0;
        for (double value : column) {
            sum += value;
        }
        return sum / column.size();
    }

    public static double calculateMedian(List<Double> column) {
        Collections.sort(column);
        int size = column.size();
        if (size % 2 == 0) {
            return (column.get(size / 2 - 1) + column.get(size / 2)) / 2.0;
        } else {
            return column.get(size / 2);
        }
    }

    public static double calculateStandardDeviation(List<Double> column, double mean) {
        double sumSquaredDifferences = 0.0;
        for (double value : column) {
            sumSquaredDifferences += Math.pow(value - mean, 2);
        }
        return Math.sqrt(sumSquaredDifferences / column.size());
    }
}
