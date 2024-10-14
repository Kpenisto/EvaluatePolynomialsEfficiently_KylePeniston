import java.math.BigInteger;
import java.io.*;
import java.util.Scanner;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        //Demonstrate overflow
        demonstrateOverflow();
        //demonstrateOverflowBigInteger();

        //First round of experiments
        firstRoundExperiments();

        //Second round of experiments using file handling
        secondRoundExperiments();
    }

    public static void demonstrateOverflow() {
        int n = 25;
        long x = 123;
        long[] coefficients = new long[n + 1];

        //coefficients as 0, 1, 2, ..., n
        for (int i = 0; i <= n; i++) {
            coefficients[i] = i;
        }

        //Evaluate polynomial using the brute force method
        long bruteForceResult = evaluateBruteForceOverflow(coefficients, x);

        System.out.println("Brute Force Overflow Demonstration (n=25, x=123): " + bruteForceResult);
    }

    public static long evaluateBruteForceOverflow(long[] coefficients, long x) {
        long result = 0;
        long powerOfX = 1;

        for (int i = 0; i < coefficients.length; i++) {
            //Add the term ai * x^i
            result += coefficients[i] * powerOfX;
            powerOfX *= x;
        }

        return result;
    }

    public static void demonstrateOverflowBigInteger() {
        int n = 25;
        BigInteger x = BigInteger.valueOf(123);
        BigInteger[] coefficients = new BigInteger[n + 1];
        for (int i = 0; i <= n; i++) {
            coefficients[i] = BigInteger.valueOf(i);
        }

        BigInteger bruteForceResult = evaluateBruteForceBigInt(coefficients, x);
        System.out.println("Brute Force Overflow Demonstration (n=25, x=123): " + bruteForceResult);
    }

    public static BigInteger evaluateBruteForceBigInt(BigInteger[] coefficients, BigInteger x) {
        //Default BigInteger result
        BigInteger result = BigInteger.ZERO;
        //Represents x^k
        BigInteger powerOfX = BigInteger.ONE;

        //Multiply each coefficient by the appropriate power of x
        //Adding up all these terms to get the final result
        for (int i = 0; i < coefficients.length; i++) {
            result = result.add(coefficients[i].multiply(powerOfX));
            powerOfX = powerOfX.multiply(x);
        }

        return result;
    }

    //First round with small values
    public static void firstRoundExperiments() {
        /*Hard coding variables "n", "d", "x"
        int n = 5; Degree of the polynomial (n + 1), aka length of coefficients array
        int d = 2; Number of digits for coefficients
        BigInteger x = BigInteger.valueOf(2) Positive integer
        Formula P "sub" n of (X) = Pn(x)
        */

        //x with 2 digits (d = 2)
        BigInteger x = BigInteger.valueOf(25);

        //Coefficients with 2 digits (d = 2)
        BigInteger[] coefficients = {
            BigInteger.valueOf(10), BigInteger.valueOf(20), BigInteger.valueOf(30),
            BigInteger.valueOf(40), BigInteger.valueOf(50), BigInteger.valueOf(60)
        };

        //Brute Force
        long startTime = System.nanoTime();
        BigInteger result1 = evaluateBruteForceBigInt(coefficients, x);
        long endTime = System.nanoTime();
        System.out.println("Brute Force Result: " + result1 + " Time: " + (endTime - startTime) + "nsec");
        System.out.println("Brute Force Result: " + result1 + " Time: " + (endTime - startTime) / 1_000_000 + "ms");

        //Repeated Squaring
        startTime = System.nanoTime();
        BigInteger result2 = evaluateRepeatedSquaring(coefficients, x);
        endTime = System.nanoTime();
        System.out.println("Repeated Squaring Result: " + result2 + " Time: " + (endTime - startTime) + "nsec");
        System.out.println("Repeated Squaring Result: " + result1 + " Time: " + (endTime - startTime) / 1_000_000 + "ms");

        //Horner's Rule
        startTime = System.nanoTime();
        BigInteger result3 = evaluateHorner(coefficients, x);
        endTime = System.nanoTime();
        System.out.println("Horner's Rule Result: " + result3 + " Time: " + (endTime - startTime) + "nsec");
        System.out.println("Horner's Rule Result: " + result1 + " Time: " + (endTime - startTime) / 1_000_000 + "ms");

        //Compare Results
        System.out.println("Results are the same: " + compareResults(result1, result2, result3));

        //Output Results to file FirstRoundResults.txt
        writeResultsToFirstRoundFile(result1, result2, result3);
    }

    public static BigInteger evaluateRepeatedSquaring(BigInteger[] coefficients, BigInteger x) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < coefficients.length; i++) {
            result = result.add(coefficients[i].multiply(repeatedSquare(x, i)));
        }

        return result;
    }

    public static BigInteger repeatedSquare(BigInteger x, int power) {
        //Use recursion to calculate x^n based on whether n (the power) is even or odd
        if (power == 0) return BigInteger.ONE;

        //If the power is even, the function calculates x^(n/2) (half the exponent), and then squares the result
        if (power % 2 == 0) {
            BigInteger halfPower = repeatedSquare(x, power / 2);
            return halfPower.multiply(halfPower);
        } else {
            //If the power is odd, the function reduces the problem by one
            //This turns the odd power into an even power which can then be handled by the even case.
            return x.multiply(repeatedSquare(x, power - 1));
        }
    }

    public static BigInteger evaluateHorner(BigInteger[] coefficients, BigInteger x) {
        BigInteger result = BigInteger.ZERO;

        //Start from the last coefficient and move towards the first coefficient
        //For each iteration, we multiply the current result by x and then add the current coefficient.
        for (int i = coefficients.length - 1; i >= 0; i--) {
            result = result.multiply(x).add(coefficients[i]);
        }

        return result;
    }

    public static boolean compareResults(BigInteger result1, BigInteger result2, BigInteger result3) {
        return result1.equals(result2) && result2.equals(result3);
    }

    public static void writeResultsToFirstRoundFile(BigInteger result1, BigInteger result2, BigInteger result3) {
        String fileName = "FirstRoundResults.txt";

        // Create a timestamp
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String timestamp = dtf.format(now);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write the timestamp as the first line
            writer.write("Timestamp: " + timestamp);
            writer.newLine();
            writer.write("Brute Force Result: " + result1.toString());
            writer.newLine();
            writer.write("Repeated Squaring Result: " + result2.toString());
            writer.newLine();
            writer.write("Horner's Rule Result: " + result3.toString());
            writer.newLine();
            writer.write("Results are the same: " + compareResults(result1, result2, result3));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void writeResultsToSecondRoundFile(BigInteger result1, BigInteger result2, BigInteger result3, long time1, long time2, long time3) {
        String fileName = "SecondRoundResults.txt";

        // Create a timestamp
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        String timestamp = dtf.format(now);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            // Write the timestamp as the first line
            writer.write("Timestamp: " + timestamp);
            writer.newLine();
            writer.write("Brute Force Result: " + result1.toString());
            writer.newLine();
            writer.write("Brute Force Time: " + time1 + "nsec");
            writer.newLine();
            writer.write("Repeated Squaring Result: " + result2.toString());
            writer.newLine();
            writer.write("Repeated Squaring Time: " + time2 + "nsec");
            writer.newLine();
            writer.write("Horner's Rule Result: " + result3.toString());
            writer.newLine();
            writer.write("Horner's Rule Time: " + time3 + "nsec");
            writer.newLine();
            writer.write("Results are the same: " + compareResults(result1, result2, result3));
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void secondRoundExperiments() {
        String filename = "SecondRoundDataFile.txt";

        //Load data from file before running the algorithms
        ExperimentData data = loadDataFromFile(filename);

        System.out.println("n: " + data.n);
        System.out.println("d: " + data.d);
        System.out.println("x: " + data.x);
        System.out.println("Coefficients: " + data.coefficients.length);

        int n = data.n;
        BigInteger x = data.x;
        BigInteger[] coefficients = data.coefficients;

        //Brute Force
        long startTime = System.nanoTime();
        BigInteger result1 = evaluateBruteForceBigInt(coefficients, x);
        long endTime = System.nanoTime();
        long time1 = endTime - startTime;
        System.out.println("Brute Force Result: " + result1 + " Time: " + (endTime - startTime) + "nsec");

        //Repeated Squaring
        startTime = System.nanoTime();
        BigInteger result2 = evaluateRepeatedSquaring(coefficients, x);
        endTime = System.nanoTime();
        long time2 = endTime - startTime;
        System.out.println("Repeated Squaring Result: " + result2 + " Time: " + (endTime - startTime) + "nsec");

        //Horner's Rule
        startTime = System.nanoTime();
        BigInteger result3 = evaluateHorner(coefficients, x);
        endTime = System.nanoTime();
        long time3 = endTime - startTime;
        System.out.println("Horner's Rule Result: " + result3 + " Time: " + (endTime - startTime) + "nsec");

        //Compare Results
        System.out.println("Results are the same: " + compareResults(result1, result2, result3));

        ////Output Results to file SecondRoundResults.txt
        writeResultsToSecondRoundFile(result1, result2, result3, time1, time2, time3);

    }

    public static ExperimentData loadDataFromFile(String filename) {
        int n = 0;
        int d = 0;
        BigInteger x = BigInteger.ZERO;
        BigInteger[] coefficients = null;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("n:")) {
                    n = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("d:")) {
                    d = Integer.parseInt(line.split(":")[1].trim());
                } else if (line.startsWith("x:")) {
                    x = BigInteger.valueOf(Long.parseLong(line.split(":")[1].trim()));
                } else if (line.startsWith("coefficients:")) {
                    String[] coeffStrings = line.split(":")[1].trim().split(" ");
                    coefficients = new BigInteger[coeffStrings.length];
                    for (int i = 0; i < coeffStrings.length; i++) {
                        coefficients[i] = BigInteger.valueOf(Long.parseLong(coeffStrings[i]));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ExperimentData(n, d, x, coefficients);
    }

    static class ExperimentData {
        int n;
        int d;
        BigInteger x;
        BigInteger[] coefficients;

        public ExperimentData(int n, int d, BigInteger x, BigInteger[] coefficients) {
            this.n = n;
            this.d = d;
            this.x = x;
            this.coefficients = coefficients;
        }
    }
}