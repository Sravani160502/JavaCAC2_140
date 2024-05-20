
package CAC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CountryTradeAnalysis {

    public static void main(String[] args) {
        String inputFileName = "C:\\Users\\V SRAVANI\\OneDrive\\Desktop\\Java CAC2\\CAC\\fill.csv"; // Update this path

        List<String[]> data = readCSVData(inputFileName);
        if (data.isEmpty()) {
            System.out.println("No data found in the file or file not found.");
            return;
        }

        int countryIndex = getColumnIndex(data.get(0), "Country");
        int exportIndex = getColumnIndex(data.get(0), "Export");
        int importIndex = getColumnIndex(data.get(0), "Import");
        int totalTradeIndex = getColumnIndex(data.get(0), "Total Trade");
        int tradeBalanceIndex = getColumnIndex(data.get(0), "Trade Balance");

        if (countryIndex == -1 || exportIndex == -1 || importIndex == -1 || totalTradeIndex == -1 || tradeBalanceIndex == -1) {
            System.out.println("One or more required columns not found in the dataset.");
            return;
        }

        Map<String, List<Double>> exportData = new HashMap<>();
        Map<String, List<Double>> importData = new HashMap<>();
        Map<String, List<Double>> totalTradeData = new HashMap<>();
        Map<String, List<Double>> tradeBalanceData = new HashMap<>();

        for (int i = 1; i < data.size(); i++) {
            String[] row = data.get(i);
            String country = row[countryIndex];

            exportData.putIfAbsent(country, new ArrayList<>());
            importData.putIfAbsent(country, new ArrayList<>());
            totalTradeData.putIfAbsent(country, new ArrayList<>());
            tradeBalanceData.putIfAbsent(country, new ArrayList<>());

            try {
                // Remove commas from the string and then parse it as a double
                double export = Double.parseDouble(row[exportIndex].replaceAll(",", ""));
                double importVal = Double.parseDouble(row[importIndex].replaceAll(",", ""));
                double totalTrade = Double.parseDouble(row[totalTradeIndex].replaceAll(",", ""));
                double tradeBalance = Double.parseDouble(row[tradeBalanceIndex].replaceAll(",", ""));

                exportData.get(country).add(export);
                importData.get(country).add(importVal);
                totalTradeData.get(country).add(totalTrade);
                tradeBalanceData.get(country).add(tradeBalance);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                // Skip non-numeric or missing values
                System.out.println("Error parsing data for country " + country + ": " + e.getMessage());
                System.out.println("Data: " + row[exportIndex] + ", " + row[importIndex] + ", " + row[totalTradeIndex] + ", " + row[tradeBalanceIndex]);
            }
        }

        // Sort countries alphabetically
        List<String> sortedCountries = new ArrayList<>(exportData.keySet());
        Collections.sort(sortedCountries);

        // Print averages for each country in alphabetical order
        for (String country : sortedCountries) {
            double averageExport = calculateMean(exportData.get(country));
            double averageImport = calculateMean(importData.get(country));
            double averageTotalTrade = calculateMean(totalTradeData.get(country));
            double averageTradeBalance = calculateMean(tradeBalanceData.get(country));

            System.out.println("Country: " + country);
            System.out.println("Average Export: " + averageExport);
            System.out.println("Average Import: " + averageImport);
            System.out.println("Average Total Trade: " + averageTotalTrade);
            System.out.println("Average Trade Balance: " + averageTradeBalance);
            System.out.println();
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

    public static double calculateMean(List<Double> data) {
        double sum = 0.0;
        for (double value : data) {
            sum += value;
        }
        return data.isEmpty() ? 0 : sum / data.size();
    }

    public static int getColumnIndex(String[] headers, String columnName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(columnName)) {
                return i;
            }
        }
        return -1; // Column not found
    }
}

