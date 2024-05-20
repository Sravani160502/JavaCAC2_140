package CAC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CorrelationCalculator {

    public static void main(String[] args) {
        String inputFileName = "C:\\Users\\V SRAVANI\\OneDrive\\Desktop\\Java CAC2\\CAC\\fill.csv"; // Update this path
        int col1Index = 3; // Index of the third column (zero-based)
        int col2Index = 4; // Index of the four column (zero-based)

        List<String[]> data = readCSVData(inputFileName);
        if (data.isEmpty() || data.size() <= Math.max(col1Index, col2Index)) {
            System.out.println("Insufficient data or invalid column indices.");
            return;
        }

        List<Double> col1Data = extractColumnData(data, col1Index);
        List<Double> col2Data = extractColumnData(data, col2Index);

        if (col1Data.isEmpty() || col2Data.isEmpty()) {
            System.out.println("One or both columns are empty or contain non-numeric data.");
            return;
        }

        double correlation = calculateCorrelation(col1Data, col2Data);
        System.out.println("Pearson correlation coefficient: " + correlation);

        if (correlation > 0.7 || correlation < -0.7) {
            System.out.println("There is a strong correlation between the two variables.");
        } else if (correlation > 0.3 || correlation < -0.3) {
            System.out.println("There is a moderate correlation between the two variables.");
        } else {
            System.out.println("There is a weak or no correlation between the two variables.");
        }
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

    public static List<Double> extractColumnData(List<String[]> data, int colIndex) {
        List<Double> columnData = new ArrayList<>();
        boolean isFirstRow = true;

        for (String[] row : data) {
            if (isFirstRow) {
                isFirstRow = false;
                continue; // Skip header row
            }
            try {
                columnData.add(Double.parseDouble(row[colIndex]));
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                // Skip non-numeric or missing values
            }
        }
        return columnData;
    }

    public static double calculateCorrelation(List<Double> col1, List<Double> col2) {
        double mean1 = calculateMean(col1);
        double mean2 = calculateMean(col2);
        double covariance = calculateCovariance(col1, col2, mean1, mean2);
        double variance1 = calculateVariance(col1, mean1);
        double variance2 = calculateVariance(col2, mean2);

        return covariance / (Math.sqrt(variance1) * Math.sqrt(variance2));
    }

    public static double calculateMean(List<Double> data) {
        double sum = 0.0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.size();
    }

    public static double calculateCovariance(List<Double> col1, List<Double> col2, double mean1, double mean2) {
        double sum = 0.0;
        int n = Math.min(col1.size(), col2.size()); // Use the smaller size to avoid out of bounds access

        for (int i = 0; i < n; i++) {
            sum += (col1.get(i) - mean1) * (col2.get(i) - mean2);
        }
        return sum / (n - 1);
    }

    public static double calculateVariance(List<Double> data, double mean) {
        double sumSquaredDifferences = 0.0;
        for (double value : data) {
            sumSquaredDifferences += Math.pow(value - mean, 2);
        }
        return sumSquaredDifferences / (data.size() - 1);
    }
}
