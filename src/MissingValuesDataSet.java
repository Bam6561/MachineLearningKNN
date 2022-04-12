import java.io.*;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MissingValuesDataSet {
  private String trainingDataFilePath;
  private String testingDataFilePath;

  public MissingValuesDataSet(String trainingDataFilePath, String testingDataFilePath) {
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

  // Method: Create a complete training & testing data set with no missing values
  public void completeDataSet() {
    try { // Average training data with respect to unique features
      ArrayList<Double> trainingDataUniqueFeaturesAverages = new ArrayList<Double>(averageUniqueFeatures(trainingDataFilePath));
      System.out.println("[Success] Averaged unique feature values in {Training Data} file.");
      imputeDataSetMissingValues(trainingDataFilePath, trainingDataUniqueFeaturesAverages);
      System.out.println("[Success] Imputed potential missing values in {Training Data} file.");
    } catch (NullPointerException e) { // trainingDataUniqueFeaturesAverages is null
      System.out.println("[Failure] Unable to average unique features. Missing data could not be imputed.");
    }
    try { // Average testing data with respect to unique features
      ArrayList<Double> testingDataUniqueFeaturesAverages = new ArrayList<Double>(averageUniqueFeatures(testingDataFilePath));
      System.out.println("[Success] Averaged unique feature values in {Testing Data} file.");
      imputeDataSetMissingValues(testingDataFilePath, testingDataUniqueFeaturesAverages);
      System.out.println("[Success] Imputed potential missing values in {Testing Data} file.");
    } catch (NullPointerException e) { // testingDataUniqueFeaturesAverages is null
      System.out.println("[Failure] Unable to average unique features. Missing data could not be imputed.");
    }
  }

  // Method: Read the data set and average unique features' values
  private static ArrayList<Double> averageUniqueFeatures(String filePath) {
    try { // Create a Scanner to read the file line by line
      File f = new File(filePath);
      Scanner sc = new Scanner(f);
      // Split the line's text by tabs to individualize sample's features
      String line = sc.nextLine();
      int sampleCount = 1;
      String[] featuresString = line.split("\t");
      // Initialize an array to store sums of samples' unique features
      ArrayList<Double> featuresSums = new ArrayList<Double>();
      // Iterate through sample's features to create starting values
      for (int i = 0; i < featuresString.length; i++) {
        // Existing value
        if (!featuresString[i].equals("1.00000000000000e+99")) {
          try {
            featuresSums.add(Double.parseDouble(featuresString[i]));
          } catch (NumberFormatException e) { // Value is not a double
            System.out.println("[Failure] An error occurred parsing value '" + featuresString[i] + "' on line [" + sampleCount + "].");
            return null;
          }
        } else { // Missing value
          featuresSums.add(0.0);
        }
      }
      // Continue reading file for remaining lines until end of file
      while (sc.hasNextLine()) {
        line = sc.nextLine();
        sampleCount++;
        featuresString = line.split("\t");
        for (int i = 0; i < featuresString.length; i++) {
          // Add to feature's sum if valid value
          if (!featuresString[i].equals("1.00000000000000e+99")) {
            featuresSums.set(i, featuresSums.get(i) + (Double.parseDouble(featuresString[i])));
          }
        }
      }
      // Average all features' sums with respect to number of samples
      for (int i = 0; i < featuresSums.size(); i++) {
        featuresSums.set(i, featuresSums.get(i) / sampleCount);
      }
      // Close Scanner & return unique features' averages
      sc.close();
      return featuresSums;
    } catch (FileNotFoundException e) { // File does not exist
      System.out.println("[Failure] File not found.");
      return null;
    } catch (NoSuchElementException e) { // Empty file
      System.out.println("[Failure] File is empty.");
      return null;
    }
  }

  // Method: Impute data set's missing values with average of unique features
  private void imputeDataSetMissingValues(String filePath, ArrayList<Double> uniqueFeaturesAverages) {
    try { // Create a new file with a Complete naming schema
      File fWrite = new File(filePath.substring(0, filePath.length() - 4) + "Complete.txt");
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
      while (sc.hasNextLine()) {
        String line = sc.nextLine();
        String[] featuresString = line.split("\t");
        for (int i = 0; i < featuresString.length; i++) {
          // Copy existing data to new file
          if (!featuresString[i].equals("1.00000000000000e+99")) {
            bw.write(featuresString[i] + "\t");
          } else { // Replace missing data with average value of unique feature
            bw.write(uniqueFeaturesAverages.get(i) + "\t");
          }
        }
        bw.newLine();
      }
      // Close Scanner & BufferedWriter
      sc.close();
      bw.close();
    } catch (IOException e) {
      System.out.println("[Failure] An error occurred.");
    }
  }
}
