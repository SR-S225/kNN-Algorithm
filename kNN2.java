import java.util.*;
import java.io.*;
import java.util.stream.IntStream;



/*
 * Possible improvements:
 * 1. Use a different distance measure like manhattan distance
 * It increased the accuracy from 96 matches to 110 matches.
 *  
 * 2. Use a different k value
 * Changing the k value to 6 increased the accuracy from 110 matches to 119 matches.
 * 
 * 3. Scaling the data using Min-Max scaling
 * This increased the data from 119 matches to 193 matches. 
 *
 */
public class kNN2 {

    public static void main(String[] args) throws IOException {

        // This takes the size of each data set. 
        // The train size is 200 for the rows and feature size is 61 for the amount of data in each row. 
        // Test size is 200 because of the final output being 200. 
        int TRAIN_SIZE = 200;
        int FEATURE_SIZE = 61;
        int TEST_SIZE = 200;

        // Creating the arrays for the data and labels.
        // train and test are both doubles because of the data
        // the labels are integers because they're only 0 or 1.
        double[][] train = new double[TRAIN_SIZE][FEATURE_SIZE];
        double[][] test = new double[TEST_SIZE][FEATURE_SIZE];
        int[] train_label = new int[TRAIN_SIZE];
        int[] test_label = new int[TEST_SIZE];


        int[] predicted_label = new int[TEST_SIZE];

        // Reading the data from the files and adding to array.
        Scanner trainDscanner = new Scanner(new File("train_data.txt"));

        // Loops through the rows and each piece of data and adds to the array. 
        for (int i=0; i < TRAIN_SIZE; i++) {
            for (int j=0; j< FEATURE_SIZE; j++) {
                if (trainDscanner.hasNextDouble()) {
                    train[i][j] = trainDscanner.nextDouble();
                }
            }
        }
        trainDscanner.close();


        
        // Same as above but for the test data instead.
        Scanner testDscanner = new Scanner(new File("test_data.txt"));

        for (int i=0; i < TEST_SIZE; i++) {
            for (int j=0; j< FEATURE_SIZE; j++) {
                if (testDscanner.hasNextDouble()) {
                    test[i][j] = testDscanner.nextDouble();
                }
            }
        }
        testDscanner.close();

        // This is the same as above but for the labels instead.
        // However it only needs to loop once 
        // Because there is only one row of 200 pieces of data. 
        Scanner trainLscanner = new Scanner(new File("train_label.txt"));

        for (int i=0; i < TRAIN_SIZE; i++) {
            if (trainLscanner.hasNextInt()) {
                train_label[i] = trainLscanner.nextInt();
            }
        }
        trainLscanner.close();

        // Same as above but for the test labels instead.
        Scanner testLscanner = new Scanner(new File("test_label.txt"));

        for (int i=0; i < TEST_SIZE; i++) {
            if (testLscanner.hasNextInt()) {
                test_label[i] = testLscanner.nextInt();
            }
        }
        testLscanner.close();




        // Scaling the data to be between 0 and 1.
        // Here we will calculate the miniumum and maximum values for each feature. 
        double[] minValues = new double[FEATURE_SIZE];
        double[] maxValues = new double[FEATURE_SIZE];
        // This loops through each piece of data. 
        for (int i = 0; i < FEATURE_SIZE; i++) {
            minValues[i] = Double.MAX_VALUE;
            maxValues[i] = Double.MIN_VALUE;
            for (int j = 0; j < TRAIN_SIZE; j++) {
                // Using min and max to find the min and max values for each feature.
                minValues[i] = Math.min(minValues[i], train[j][i]);
                maxValues[i] = Math.max(maxValues[i], train[j][i]);
            }
        }

        // Here we know scale the training data to be between 0 and 1.
        // So we loop through each piece of data 
        // and subtract the minimum value and divide by the difference 
        // between the max and min values.
        for (int i = 0; i < TRAIN_SIZE; i++) {
            for (int j = 0; j < FEATURE_SIZE; j++) {
                train[i][j] = (train[i][j] - minValues[j]) / (maxValues[j] - minValues[j]);
            }
        }

        // Here we do the same as above but for the test data instead. 
        for (int i = 0; i < TEST_SIZE; i++) {
            for (int j = 0; j < FEATURE_SIZE; j++) {
                test[i][j] = (test[i][j] - minValues[j]) / (maxValues[j] - minValues[j]);
            }
        }



        


        // Calculating the Manhattan  distance between the test data and train data.
        // Here you can set your k value. The optimal value is 3 for this data. 
        int k = 6; 

        // You start by looping through the test and train data.
        for (int i=0; i < TEST_SIZE; i++) {
            // Set a new array to store the distances.
            double[] distances = new double[TRAIN_SIZE];
            for (int j=0; j< TRAIN_SIZE; j++) {
                double distance = 0;
                for (int l=0; l< FEATURE_SIZE; l++) {
                    distance += Math.abs(test[i][l] - train[j][l]);
                }
                distances[j] = distance;
            }

            
            // This is where we find k nearest neighbours.
            // An array is created to the size of k. 
            int[] nearestIndices = new int[k];
            // This fills the array with -1 so we can check if it has been filled or not.
            Arrays.fill(nearestIndices, -1);
            
            // This loops through the distances and finds the k nearest neighbours.
            for (int index = 0; index < distances.length; index++) {
                // Sets a condition to know when to break out the loop.
                boolean inserted = false;
                for (int j = 0; j < k && !inserted; j++) {
                    // If the nearestIndices is -1 or the distance is less than the distance at the index
                    if (nearestIndices[j] == -1 || distances[index] < distances[nearestIndices[j]]) {
                        // This shifts the array to the right to make space for the new index.
                        for (int l = k - 1; l > j; l--) {
                            nearestIndices[l] = nearestIndices[l - 1];
                        }
                        // This inserts the new index into the array.
                        nearestIndices[j] = index;
                        inserted = true;
                    }
                }
            }


            // This is where we find the majority vote.
            // Start by creating an array to store the counts.            
            int[] counts = new int[TRAIN_SIZE];
            // This loops through the nearest indices and adds to the counts array.
            for (int index : nearestIndices) {
                counts[train_label[index]]++;
            }

            // This finds the max index in the counts array.
            int maxIndex = 0;
            // This loops through the counts array and finds the max index. 
            for (int j=1; j<counts.length; j++) {
                if (counts[j] > counts[maxIndex]) {
                    maxIndex = j;
                }
            }

            // This sets the predicted label to the max index.
            predicted_label[i] = maxIndex;
        }   

        

        
        /* Code for testing matches. 
        int count = 0;
        for (int i=0; i< predicted_label.length; i++) {
            if (predicted_label[i] == test_label[i]) {
                count++;
            }
        }
        

        System.out.println("There are " + count + " matches");
        */
        



        // Writing data to the files.
        try {
            // Sets the new file to write to.
            PrintWriter writer = new PrintWriter("output2.txt");
             // Loops through the preidcted labels and writes them to the file.
            for (int i=0; i< TEST_SIZE; i++) {
                writer.print(predicted_label[i]+ " ");
            }
            // Closes the file
            writer.close();
        } catch (Exception e) {
            System.out.println("Error");
        }
    }



}