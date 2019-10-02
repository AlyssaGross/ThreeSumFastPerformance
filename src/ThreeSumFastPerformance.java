import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.*;
import java.util.Arrays;

public class ThreeSumFastPerformance {
    static ThreadMXBean bean = ManagementFactory.getThreadMXBean( );

    /* define constants */
    static long MAXVALUE = 2000000000;
    static long MINVALUE = -2000000000;
    static int numberOfTrials = 50;
    static int MAXINPUTSIZE = (int) Math.pow(2,15);
    static int MININPUTSIZE = 1;

    //set up variable to hold folder path and FileWriter/PrintWriter for printing results to a file
    static String ResultsFolderPath = "/home/alyssa/Results/"; // pathname to results folder 
    static FileWriter resultsFile;
    static PrintWriter resultsWriter;


    public static void main (String[] args)
    {
        verifyThreeSumFast();                                   //verify that ThreeSumFast is working correctly

        // run the whole experiment five times, and expect to throw away the data from the earlier runs, before java has fully optimized 
       System.out.println("Running first full experiment...");
       runFullExperiment("ThreeSumFast-ExpRun1-ThrowAway.txt");
       System.out.println("Running second full experiment...");
       runFullExperiment("ThreeSumFast-ExpRun2.txt");
       System.out.println("Running third full experiment...");
       runFullExperiment("ThreeSumFast-ExpRun3.txt");
       System.out.println("Running fourth full experiment...");
       runFullExperiment("ThreeSumFast-ExpRun4.txt");
        System.out.println("Running fifth    full experiment...");
        runFullExperiment("ThreeSumFast-ExpRun5.txt");

    }

    // ThreeSumFast finds triples that sum to zero and returns the number of triples found
    // The fast algorithm includes sorting the array, finding the sum of each couple of numbers in the list
    // Then calling binarysearch to determine if the opposite of that sum exists in the rest of the list
    // If the opposite is found, that means there is a triple that sums to zero and the count is incremented
    public static int threeSumFast(long [] testList) {
        Arrays.sort(testList);

        int len = testList.length;
        int count = 0;
        for(int i = 0; i<len-2; i++)                                                        // control index of the first element in pair
            for(int j = i +1; j< len; j++)                                                  // control index of the second element in pair
                   if (Arrays.binarySearch(testList, -testList[i]-testList[j])> j)     // use binary search to check if the third element that completes the triple exists in the list
                        count++;                                                            // increment count if true

        return count;
    }

    //create a random list of integers of a specified length
    static long[] createRandomIntegerList(int size)
    {
        long [] newList = new long[size];
        for(int j=0; j<size; j++)
        {
            newList[j] = (long)(MINVALUE + Math.random() * (MAXVALUE - MINVALUE));
        }
        return newList;
    }

    // verifies the ThreeSumFast function works as is expected
    // creates three lists with known amount of triples that sum to zero, calls the threeSumFast function and if the
    // result matches the number of known triples, the function is properly working.
    static void verifyThreeSumFast()
    {
        int count;
        System.out.println("Verification for ThreeSum");
        //contains 5 triples that sum to 0
        long [] verifyList1 = {-33, 4333, 335, -540, 8274, 483, -2300, -450, 9843, -2668, 6000, 736, 1120, -9010, 2333, -5460};
            System.out.println("Verification 1 :");
            System.out.println("List : " + Arrays.toString(verifyList1));
            count = threeSumFast(verifyList1);
            System.out.println("  ThreeSum count:   " + count + "\n");
        //contains 3 triples that sum to 0
        long [] verifyList2 = {786, 122, -934, 39048, 2304, 324, 23422,  1022,-558, 9830, 929, 901, 394, -24351, 33, 234};
            System.out.println("Verification 2 :");
            System.out.println("List : " + Arrays.toString(verifyList2));
            count = threeSumFast(verifyList2);
            System.out.println("  ThreeSum count:   " + count + "\n");
        //contains 7 triples that some to 0
        long [] verifyList3 = {-39, 10, -405, 27, 540, 2111, 378, -598, -1706, 12, 9202, -3114, 3102, -12304, 1003, 2935};
            System.out.println("Verification 3 :");
            System.out.println("List : " + Arrays.toString(verifyList3));
            count = threeSumFast(verifyList3);
            System.out.println("  ThreeSum count:   " + count + "\n");
    }

    // runs the threeSumFast function for every input size for the specified number of trials
    // times the amount of time each trial took, and calculates the average for the input size
    // prints the input size along with the average time taken to run threeSumFast
    static void runFullExperiment(String resultsFileName){

        int count;
        try {
            resultsFile = new FileWriter(ResultsFolderPath + resultsFileName);
            resultsWriter = new PrintWriter(resultsFile);
        } catch(Exception e) {
            System.out.println("*****!!!!!  Had a problem opening the results file "+ResultsFolderPath+resultsFileName);
            return;
        }


        ThreadCpuStopWatch TrialStopwatch = new ThreadCpuStopWatch();                                   // create stopwatch for timing an individual trial 

        resultsWriter.println("#InputSize    AverageTime");                                             // # marks a comment in gnuplot data 
        resultsWriter.flush();

        for(int inputSize=MININPUTSIZE;inputSize<=MAXINPUTSIZE; inputSize*= 2) {                        // for each size of input we want to test: in this case starting small and doubling the size each time

            System.out.println("Running test for input size "+inputSize+" ... ");                       // progress message... 
            System.out.print("    Running trial batch...");
            long batchElapsedTime = 0;                                                                  // reset elapsed time for the batch to 0

            System.gc();                                                                                // force garbage collection before each batch of trials run so it is not included in the time

                                                                                                        // repeat for desired number of trials (for a specific size of input)...
            for (long trial = 0; trial < numberOfTrials; trial++) {                                     // run the trials 

                long[] testList = createRandomIntegerList(inputSize);                                   // generate a list to use for input in the threeSumFast function 

                TrialStopwatch.start();                                                                 // begin timing
                count = threeSumFast(testList);                                                         // run the threeSumFast on the trial input
                batchElapsedTime = batchElapsedTime + TrialStopwatch.elapsedTime();                     // stop timer and add to the total time elapsed for the batch of trials
            }
            double averageTimePerTrialInBatch = (double) batchElapsedTime / (double)numberOfTrials;     // calculate the average time taken for each trial of the batch

            resultsWriter.printf("%12d  %15.2f \n",inputSize, averageTimePerTrialInBatch);              // print data for this size of input
            resultsWriter.flush();
            System.out.println(" ....done.");
        }
    }


}
