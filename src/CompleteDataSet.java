import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;

public class CompleteDataSet {
  private String trainingDataFilePath;
  private String testingDataFilePath;
  private String trainingLabelFilePath;
  private ArrayList<ArrayList<Double>> trainingData;
  private ArrayList<ArrayList<Double>> testingData;
  private ArrayList<Integer> trainingLabels;
  private HashSet<Integer> classLabels;
  private ArrayList<ArrayList<Double>> euclideanDistancesSet;

  public CompleteDataSet(String trainingDataFilePath, String testingDataFilePath, String trainingLabelFilePath) {
    this.trainingDataFilePath = trainingDataFilePath;
    this.testingDataFilePath = testingDataFilePath;
    this.trainingLabelFilePath = trainingLabelFilePath;
  }

  // Method(s): Retrieve data file paths
  public String getTrainingDataFilePath() {
    return this.trainingDataFilePath;
  }

  public String getTestingDataFilePath() {
    return this.testingDataFilePath;
  }

  public String getTrainingLabelFilePath() {
    return this.trainingLabelFilePath;
  }

  // Method(s): Retrieve data sets
  public ArrayList<ArrayList<Double>> getTrainingData() {
    return this.trainingData;
  }

  public ArrayList<ArrayList<Double>> getTestingData() {
    return this.testingData;
  }

  public ArrayList<Integer> getTrainingLabels() {
    return this.trainingLabels;
  }

  public HashSet<Integer> getClassLabels() {
    return this.classLabels;
  }

  public ArrayList<ArrayList<Double>> getEuclideanDistancesSet() {
    return this.euclideanDistancesSet;
  }

  // Method(s): Set data file paths
  private void setTrainingDataFilePath(String trainingDataFilePath) {
    this.trainingDataFilePath = trainingDataFilePath;
  }

  private void setTestingDataFilePath(String testingDataFilePath) {
    this.testingDataFilePath = testingDataFilePath;
  }

  private void setTrainingLabelFilePath(String trainingLabelFilePath) {
    this.trainingLabelFilePath = trainingLabelFilePath;
  }

  // Method(s): Set data
  private void setTrainingData(ArrayList<ArrayList<Double>> trainingData) {
    this.trainingData = trainingData;
  }

  private void setTestingData(ArrayList<ArrayList<Double>> testingData) {
    this.testingData = testingData;
  }

  private void setTrainingLabels(ArrayList<Integer> trainingLabels) {
    this.trainingLabels = trainingLabels;
  }

  private void setClassLabels(HashSet<Integer> classLabels) {
    this.classLabels = classLabels;
  }

  private void setEuclideanDistancesSet(ArrayList<ArrayList<Double>> euclideanDistancesSet) {
    this.euclideanDistancesSet = euclideanDistancesSet;
  }

  // Method: Read and store data set as arrays of arrays for training (1) and testing (2) data and array as labels (3)
  public void readDataSet(String filePath, int variant) {
    try { // Create a Scanner to read the file line by line
      File f = new File(filePath);
      Scanner sc = new Scanner(f);
      // Determine which storage space to use after reading the file
      switch (variant) {
        case 1, 2: // Training or testing data
          // Data sets have multiple samples
          ArrayList<ArrayList<Double>> dataSet = new ArrayList<ArrayList<Double>>();
          while (sc.hasNextLine()) {
            String line = sc.nextLine();
            // Samples have multiple features split by tabs on each line
            ArrayList<Double> sample = new ArrayList<Double>();
            String[] featuresString = line.split("\t");
            // Add each unique feature to unique sample
            for (int i = 0; i < featuresString.length; i++) {
              sample.add(Double.parseDouble(featuresString[i]));
            }
            // Add complete unique sample to data set
            dataSet.add(sample);
          }
          if (variant == 1) { // Store data set into training data
            setTrainingData(dataSet);
            System.out.println("[Success] Read {Training Data} file.");
          } else { // Store data into testing data
            setTestingData(dataSet);
            System.out.println("[Success] Read {Testing Data} file.");
          }
          // Close Scanner
          sc.close();
          break;
        case 3: // Training labels
          ArrayList<Integer> trainingLabels = new ArrayList<Integer>();
          HashSet<Integer> classLabels = new HashSet<Integer>();
          while (sc.hasNextLine()) {
            String line = sc.nextLine();
            int classType = Integer.parseInt(line);
            trainingLabels.add(classType);
            // Store unique class labels
            if (!classLabels.contains(classType)) {
              classLabels.add(classType);
            }
          }
          // Store data into training labels
          setTrainingLabels(trainingLabels);
          setClassLabels(classLabels);
          System.out.println("[Success] Read {Training Labels} file.");
          // Close Scanner
          sc.close();
          break;
        default: // Variant other than 1, 2, or 3
          System.out.println("[Failure] Unknown method variant. Data set could not be read.");
          break;
      }
    } catch (FileNotFoundException e) { // File does not exist
      System.out.println("[Failure] File does not exist.");
    }
  }

  // Method: Calculate distance between each training and testing sample
  public void calculateEuclideanDistances() {
    // Set of distance sets
    ArrayList<ArrayList<Double>> euclideanDistancesSet = new ArrayList<ArrayList<Double>>();
    for (int i = 0; i < getTestingData().size(); i++) { // As many testing samples
      // Set of distances with respect to testing sample
      ArrayList<Double> euclideanDistances = new ArrayList<Double>();
      for (int j = 0; j < getTrainingData().size(); j++) { // As many training samples
        // Distance between testing and training sample
        Double euclideanDistance = 0.0;
        for (int k = 0; k < getTestingData().get(0).size(); k++) { // As many sample features
          euclideanDistance += Math.pow(getTestingData().get(i).get(k) - getTrainingData().get(j).get(k), 2);
        }
        euclideanDistances.add(Math.sqrt(euclideanDistance));
      }
      euclideanDistancesSet.add(euclideanDistances);
    }
    setEuclideanDistancesSet(euclideanDistancesSet);
    System.out.println("[Success] Calculated Euclidean Distances between training and testing samples.");
  }

  // Method: Calculate closest neighbors to testing sample
  public void calculateKNN() {
    try { // Create a new file to output classification results
      File fWrite = new File(getTestingDataFilePath().substring(0, testingDataFilePath.length() - 4) + "Label.txt");
      if (!fWrite.createNewFile()) { // Overwrite existing file
        fWrite.delete();
        fWrite.createNewFile();
      }
      // Create a BufferedWriter to write to the new file
      FileOutputStream fos = new FileOutputStream(fWrite);
      BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
      // K value is set to the square root of training data samples
      int kValue = (int) Math.sqrt(getTrainingData().size());
      // Access distances in distance set
      for (int i = 0; i < getEuclideanDistancesSet().size(); i++) {
        // Create a deep copy of euclidean distances with respect to testing sample
        // to allow sorting without affecting order they appear in the original structure
        ArrayList<Double> sortedEuclideanDistances = new ArrayList<Double>();
        for (int j = 0; j < getEuclideanDistancesSet().get(i).size(); j++) {
          sortedEuclideanDistances.add(getEuclideanDistancesSet().get(i).get(j));
        }
        // Sort euclidean distances to find k minimum distances
        Collections.sort(sortedEuclideanDistances);
        HashSet<Double> nearestNeighbors = new HashSet<Double>();
        for (int index = 0; index < kValue; index++) {
          nearestNeighbors.add(sortedEuclideanDistances.get(index));
        }
        // Determine k nearest neighbors' indices
        ArrayList<Integer> nearestNeighborsIndices = new ArrayList<Integer>();
        int l = 0;
        while (!nearestNeighbors.isEmpty()) {
          if (nearestNeighbors.contains(getEuclideanDistancesSet().get(i).get(l))) {
            nearestNeighbors.remove(getEuclideanDistancesSet().get(i).get(l));
            nearestNeighborsIndices.add(l);
          }
          l++;
        }
        // Calculate mode of k nearest neighbors' class types
        ArrayList<Integer> classFrequency = new ArrayList<Integer>();
        for (int index = 0; index < Collections.max(getClassLabels()); index++) {
          classFrequency.add(0);
        }
        for (int index = 0; index < nearestNeighborsIndices.size(); index++) {
          int classType = getTrainingLabels().get(nearestNeighborsIndices.get(index));
          classFrequency.set(classType - 1, classFrequency.get(classType - 1) + 1);
        }
        int max = Integer.MIN_VALUE;
        int maxIndex = Integer.MIN_VALUE;
        for (int index = 0; index < classFrequency.size(); index++) {
          if (max < classFrequency.get(index)) {
            max = classFrequency.get(index);
            maxIndex = index;
          }
        }
        // Write classification
        bw.write(String.valueOf(maxIndex+1));
        bw.newLine();
      }
      // Close BufferedWriter
      bw.close();
    } catch (IOException e) {
      System.out.println("[Failure] An error occurred.");
    }
    System.out.println("[Success] Classified testing samples based on k-nearest neighbors.");
  }
}
