public class Main {
  public static void main(String[] args) {
    // Use this section as necessary to convert supported data file formats
    /*
    String trainingDataFileConvertPath = "";
    String testDataFileConvertPath = "";
    FormatConversion fc = new FormatConversion(trainingDataFileConvertPath, testDataFileConvertPath);
    // 1 - Unused Exponents, 2 - Commas with Missing Values, 3 - Exponents Used
    fc.reformatDataFile(fc.getTrainingDataFilePath(), 1);
    fc.reformatDataFile(fc.getTestingDataFilePath(), 1);
     */

    // Location of training and testing data files & training label file
    String trainingDataFilePath = "";
    String testingDataFilePath = "";
    String trainingLabelFilePath = "";

    // Scan over data files for possible missing values & complete them by imputation
    MissingValuesDataSet mvds = new MissingValuesDataSet(trainingDataFilePath, testingDataFilePath);
    mvds.completeDataSet();

    // Location of complete training and testing data files
    trainingDataFilePath = trainingDataFilePath.substring(0, trainingDataFilePath.length() - 4) + "Complete.txt";
    testingDataFilePath = testingDataFilePath.substring(0, testingDataFilePath.length() - 4) + "Complete.txt";

    // Read complete data files
    CompleteDataSet cds = new CompleteDataSet(trainingDataFilePath, testingDataFilePath, trainingLabelFilePath);
    cds.readDataSet(cds.getTrainingDataFilePath(), 1);
    cds.readDataSet(cds.getTestingDataFilePath(), 2);
    cds.readDataSet(cds.getTrainingLabelFilePath(), 3);
    cds.calculateEuclideanDistances();
    cds.calculateKNN();
  }
}
