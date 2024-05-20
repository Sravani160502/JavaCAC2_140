package CAC;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AnovaManual {

    public static void main(String[] args) {
        String inputFileName = "C:\\Users\\V SRAVANI\\OneDrive\\Desktop\\Java CAC2\\CAC\\fill.csv"; // Update this path

        List<List<Double>> data = readCSVData(inputFileName);
        if (data.isEmpty()) {
            System.out.println("No data found in the file or file not found.");
            return;
        }

        AnovaResult result = performAnova(data);
        printAnovaTable(result);

        double significanceLevel = 0.05;
        boolean isSignificant = result.pValue < significanceLevel;
        System.out.println("Significance Level: " + significanceLevel);
        System.out.println("Is there a significant difference between the groups? " + (isSignificant ? "Yes" : "No"));
    }

    public static List<List<Double>> readCSVData(String fileName) {
        List<List<Double>> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                for (int i = 0; i < values.length; i++) {
                    if (data.size() <= i) {
                        data.add(new ArrayList<>());
                    }
                    try {
                        data.get(i).add(Double.parseDouble(values[i]));
                    } catch (NumberFormatException e) {
                        // Skip non-numeric or missing values without printing anything
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static AnovaResult performAnova(List<List<Double>> data) {
        int numGroups = data.size();
        int totalNumSamples = data.stream().mapToInt(List::size).sum();

        // Calculate means
        double[] means = new double[numGroups];
        double grandMean = 0;
        for (int i = 0; i < numGroups; i++) {
            means[i] = calculateMean(data.get(i));
            grandMean += means[i] * data.get(i).size();
        }
        grandMean /= totalNumSamples;

        // Calculate SS Between
        double ssBetween = 0;
        for (int i = 0; i < numGroups; i++) {
            ssBetween += data.get(i).size() * Math.pow(means[i] - grandMean, 2);
        }

        // Calculate SS Within
        double ssWithin = 0;
        for (int i = 0; i < numGroups; i++) {
            for (double value : data.get(i)) {
                ssWithin += Math.pow(value - means[i], 2);
            }
        }

        // Calculate degrees of freedom
        int dfBetween = numGroups - 1;
        int dfWithin = totalNumSamples - numGroups;

        // Calculate MS Between and MS Within
        double msBetween = ssBetween / dfBetween;
        double msWithin = ssWithin / dfWithin;

        // Calculate F-value
        double fValue = msBetween / msWithin;

        // Calculate p-value
        double pValue = fDistributionCdf(fValue, dfBetween, dfWithin);

        return new AnovaResult(ssBetween, dfBetween, msBetween, ssWithin, dfWithin, msWithin, fValue, pValue);
    }

    public static double calculateMean(List<Double> data) {
        double sum = 0.0;
        for (double value : data) {
            sum += value;
        }
        return data.isEmpty() ? 0 : sum / data.size();
    }

    public static void printAnovaTable(AnovaResult result) {
        System.out.println("Source of Variation   SS      df      MS      F      p-value");
        System.out.printf("Between Groups        %.4f  %d  %.4f  %.4f  %.6f%n",
                result.ssBetween, result.dfBetween, result.msBetween, result.fValue, result.pValue);
        System.out.printf("Within Groups         %.4f  %d  %.4f%n",
                result.ssWithin, result.dfWithin, result.msWithin);
    }

    public static double fDistributionCdf(double fValue, int df1, int df2) {
        // Approximate the p-value using the F-distribution
        // This method uses the incomplete beta function to calculate the cumulative distribution function (CDF)
        // for the F-distribution. This is a simplified approach.

        // Beta incomplete function
        double x = (df1 * fValue) / (df1 * fValue + df2);
        return 1 - betaIncompleteFunction(df1 / 2.0, df2 / 2.0, x);
    }

    public static double betaIncompleteFunction(double a, double b, double x) {
        // This method approximates the incomplete beta function using a series expansion
        double bt = (x == 0.0 || x == 1.0) ? 0.0 : Math.exp(gammaLn(a + b) - gammaLn(a) - gammaLn(b) + a * Math.log(x) + b * Math.log(1.0 - x));
        if (x < (a + 1.0) / (a + b + 2.0)) {
            return bt * betaContinuedFraction(a, b, x) / a;
        } else {
            return 1.0 - bt * betaContinuedFraction(b, a, 1.0 - x) / b;
        }
    }

    public static double betaContinuedFraction(double a, double b, double x) {
        // This method computes the continued fraction representation of the beta function
        int maxIterations = 100;
        double epsilon = 3.0e-7;
        double am = 1.0;
        double bm = 1.0;
        double az = 1.0;
        double qab = a + b;
        double qap = a + 1.0;
        double qam = a - 1.0;
        double bz = 1.0 - qab * x / qap;

        for (int m = 1; m <= maxIterations; m++) {
            int em = m + m;
            double d = em * (b - m) * x / ((qam + em) * (a + em));
            double ap = az + d * am;
            double bp = bz + d * bm;
            d = -(a + m) * (qab + m) * x / ((qap + em) * (a + em));
            double app = ap + d * az;
            double bpp = bp + d * bz;
            double aold = az;
            am = ap;
            bm = bp;
            az = app;
            bz = bpp;
            if (Math.abs(az - aold) < epsilon * Math.abs(az)) {
                return az;
            }
        }
        return az;
    }

    public static double gammaLn(double x) {
        // This method approximates the natural logarithm of the gamma function
        double[] coefficients = {
                76.18009172947146, -86.50532032941677,
                24.01409824083091, -1.231739572450155,
                0.1208650973866179e-2, -0.5395239384953e-5
        };
        double y = x;
        double tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (double coefficient : coefficients) {
            ser += coefficient / ++y;
        }
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }

    static class AnovaResult {
        double ssBetween;
        int dfBetween;
        double msBetween;
        double ssWithin;
        int dfWithin;
        double msWithin;
        double fValue;
        double pValue;

        AnovaResult(double ssBetween, int dfBetween, double msBetween, double ssWithin, int dfWithin, double msWithin, double fValue, double pValue) {
            this.ssBetween = ssBetween;
            this.dfBetween = dfBetween;
            this.msBetween = msBetween;
            this.ssWithin = ssWithin;
            this.dfWithin = dfWithin;
            this.msWithin = msWithin;
            this.fValue = fValue;
            this.pValue = pValue;
        }
    }
}

