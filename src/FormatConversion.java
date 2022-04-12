import java.io.*;
import java.util.Scanner;

public class FormatConversion {
  private String trainingDataFilePath;
  private String testingDataFilePath;

  public FormatConversion(String trainingDataFilePath, String testingDataFilePath) {
    this.trainingDataFilePath = trainingDataFilePath;
    this.testingDataFilePath = testingDataFilePath;
  }

  // Method(s): Retrieve data file paths
  public String getTrainingDataFilePath() {
    return this.trainingDataFilePath;
  }

  public String getTestingDataFilePath() {
    return this.testingDataFilePath;
  }

  // Method(s): Modify data file paths
  private void setTrainingDataFilePath(String trainingDataFilePath) {
    this.trainingDataFilePath = trainingDataFilePath;
  }

  private void setTestingDataFilePath(String testingDataFilePath) {
    this.testingDataFilePath = testingDataFilePath;
  }

  // Method: Standardize format of data set to a new file for algorithm computation
  public void reformatDataFile(String filePath, int variant) {
    try { // Create a new file with a Reformat naming schema
      File fWrite = new File(filePath.substring(0, filePath.length() - 4) + "Reformat.txt");
      if (!fWrite.createNewFile()) { // Overwrite existing file
        fWrite.delete();
        fWrite.createNewFile();
      }
      // Create a BufferedWriter to write to the new file
      FileOutputStream fos = new FileOutputStream(fWrite);
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
      // Create a Scanner to read original file again line by line
      File fRead = new File(filePath);
      Scanner sc = new Scanner(fRead);
      String[] featuresString;
      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        switch (variant) {
          case 1: // Exponent unused format (#2)
            featuresString = line.split("   ");
            for (int i = 0; i < featuresString.length; i++) {
              if (featuresString[i].length() > 0) {
                bw.write(featuresString[i].substring(0, featuresString[i].length() - 4) + "\t");
              }
            }
            break;
          case 2: // Commas & missing values (#3)
            featuresString = line.split(",");
            for (int i = 0; i < featuresString.length; i++) {
              if (!featuresString[i].equals("1000000000")) {
                bw.write(featuresString[i] + "\t");
              } else {
                bw.write("1.00000000000000e+99" + "\t");
              }
            }
            break;
          case 3: // Exponents used (#4 & #5)
            // Replace all spaces with tabs
            line = line.replaceAll("\\s\\s\\s", "\t");
            line = line.replaceAll("\\s\\s", "\t");
            line = line.replaceAll("\\s", "\t");
            featuresString = line.split("\t");
            for (int i = 0; i < featuresString.length; i++) {
              if (featuresString[i].length() > 0) {
                // Convert exponent text to doubles
                char exponentSign = featuresString[i].charAt(featuresString[i].length() - 3);
                int exponentPower = Integer.parseInt(String.valueOf(featuresString[i].charAt(featuresString[i].length() - 1)));
                Double number = Double.parseDouble(featuresString[i].substring(0, featuresString[i].length() - 4));
                if (exponentSign == '+') { // Positive exponent
                  bw.write(number * Math.pow(10, exponentPower) + "\t");
                } else if (exponentSign == '-') { // Negative exponent
                  bw.write(number * Math.pow(10, -exponentPower) + "\t");
                }
              }
            }
            break;
          default: // Variant other than 1, 2, or 3
            System.out.println("[Failure] Unknown method variant. Data set could not be read.");
            break;
        }
        bw.newLine();
      }
      // Close Scanner & BufferedWriter
      sc.close();
      bw.close();
      System.out.println("[Success] File reformat complete.");
    } catch (IOException e) {
      System.out.println("[Failure] An error occurred.");
    }
  }
}
