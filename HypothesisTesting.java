package CAC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HypothesisTesting {

    public static void main(String[] args) {
        String inputFileName = "C:\\Users\\V SRAVANI\\OneDrive\\Desktop\\Java CAC2\\CAC\\fill.csv"; // Update this path

        List<String[]> data = readCSVData(inputFileName);
        if (data.isEmpty()) {
            System.out.println("No data found in the file or file not found.");
            return;
        }

        // Test hypothesis between column 1 and column 2
        int col1Index = 1;
        int col2Index = 2;

        List<Double> col1Data = extractColumnData(data, col1Index);
        List<Double> col2Data = extractColumnData(data, col2Index);

        if (col1Data.isEmpty() || col2Data.isEmpty()) {
            System.out.println("One or both columns are empty or contain non-numeric data.");
            return;
        }

        double tStatistic = calculateTStatistic(col1Data, col2Data);
        double pValue = calculatePValue(tStatistic, col1Data.size(), col2Data.size());

        System.out.println("T-Statistic: " + tStatistic);
        System.out.println("P-Value: " + pValue);

        interpretResults(pValue);
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

    public static double calculateTStatistic(List<Double> col1, List<Double> col2) {
        double mean1 = calculateMean(col1);
        double mean2 = calculateMean(col2);
        double variance1 = calculateVariance(col1, mean1);
        double variance2 = calculateVariance(col2, mean2);

        int n1 = col1.size();
        int n2 = col2.size();

        return (mean1 - mean2) / Math.sqrt((variance1 / n1) + (variance2 / n2));
    }

    public static double calculatePValue(double tStatistic, int n1, int n2) {
        int df = n1 + n2 - 2; // Degrees of freedom
        return 2 * (1 - tDistCDF(Math.abs(tStatistic), df)); // Two-tailed test
    }

    public static double calculateMean(List<Double> data) {
        double sum = 0.0;
        for (double value : data) {
            sum += value;
        }
        return sum / data.size();
    }

    public static double calculateVariance(List<Double> data, double mean) {
        double sumSquaredDifferences = 0.0;
        for (double value : data) {
            sumSquaredDifferences += Math.pow(value - mean, 2);
        }
        return sumSquaredDifferences / (data.size() - 1);
    }

    public static double tDistCDF(double t, int df) {
        double x = (t + Math.sqrt(t * t + df)) / (2 * Math.sqrt(t * t + df));
        double betaInc = betaIncomplete(x, df / 2.0, 0.5);
        return betaInc;
    }

    public static double betaIncomplete(double x, double a, double b) {
        double bt = (x == 0.0 || x == 1.0) ? 0.0 : Math.exp(gammln(a + b) - gammln(a) - gammln(b) + a * Math.log(x) + b * Math.log(1.0 - x));
        if (x < (a + 1.0) / (a + b + 2.0)) {
            return bt * betacf(x, a, b) / a;
        } else {
            return 1.0 - bt * betacf(1.0 - x, b, a) / b;
        }
    }

    public static double betacf(double x, double a, double b) {
        int maxIterations = 100;
        double eps = 3.0e-7;
        double am = 1.0, bm = 1.0, az = 1.0, qab = a + b, qap = a + 1.0, qam = a - 1.0;
        double bz = 1.0 - qab * x / qap;
        double em, tem, d, ap, bp, app, bpp;

        for (int m = 1; m <= maxIterations; m++) {
            em = m;
            tem = em + em;
            d = em * (b - em) * x / ((qam + tem) * (a + tem));
            ap = az + d * am;
            bp = bz + d * bm;
            d = -(a + em) * (qab + em) * x / ((a + tem) * (qap + tem));
            app = ap + d * az;
            bpp = bp + d * bz;
            am = ap / bpp;
            bm = bp / bpp;
            az = app / bpp;
            bz = 1.0;
            if (Math.abs(az - app) < (eps * Math.abs(az))) {
                return az;
            }
        }
        return az;
    }

    public static double gammln(double x) {
        double[] cof = {76.18009172947146, -86.50532032941677, 24.01409824083091,
                -1.231739572450155, 0.001208650973866179, -0.000005395239384953};
        double y = x;
        double tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (int j = 0; j <= 5; j++) {
            ser += cof[j] / ++y;
        }
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }

    public static void interpretResults(double pValue) {
        double alpha = 0.05; // Significance level
        if (pValue < alpha) {
            System.out.println("Reject the null hypothesis (H0). There is a significant difference between the two columns.");
        } else {
            System.out.println("Fail to reject the null hypothesis (H0). There is no significant difference between the two columns.");
        }
    }
}


