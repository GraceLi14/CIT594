package homework1.src;

import java.util.LinkedList;
import java.util.Random;


public class JavierMergeSort {

    public static void main(String[] args) {

        int[] input = new int[1000];
        Random rand = new Random();

        for (int i = 0; i < 1000; i++) {
            input[i] = rand.nextInt(5000);
        }
        long start = System.nanoTime(); // start testing runtime
        mergeSort(input);
        long end = System.nanoTime(); // end testing runtime

        //add new runtime to totalRunTime
        long totalRunTime = (end-start);
        System.out.println("Total run time: " + totalRunTime);
    }


    public static int[] mergeSort(int[] input) {
        // Create an empty list of arrays
        LinkedList<int[]> queue = new LinkedList<>();


        // Turn every number into a {num} array and add to queue
        for (int num : input) {
            int[] singleElementArray = {num};
            queue.add(singleElementArray);
        }


        // Pick the first two arrays in the list and merge them
        // We check > 1, so we know it is safe to call remove() twice inside.
        while (queue.size() > 1) {
            int[] first = queue.remove(0);  // gets first value and removes it
            int[] second = queue.remove(0); // gets next value


            int[] combined = merge(first, second);
            queue.add(combined); // add to the back of the line
        }


        // Return the result after it’s the only thing left
        if (queue.isEmpty()) {
            return new int[0]; // Return empty array if input was empty
        } else {
            return queue.remove();
        }
    }


    // Merges two sorted arrays into one new sorted array
    private static int[] merge(int[] a, int[] b) {
        int[] result = new int[a.length + b.length];
        int i = 0; // index for a
        int j = 0; // index for b
        int k = 0; // index for result


        // Compare elements and add the smaller one
        while (i < a.length && j < b.length) {
            if (a[i] <= b[j]) {
                result[k] = a[i]; // add val from a if smaller
                i++;
            } else {
                result[k] = b[j]; // add val from b if smaller
                j++;
            }
            k++;
        }


        // Add remaining elements from a (if any)
        while (i < a.length) {
            result[k] = a[i];
            i++;
            k++;
        }
        
        // Add remaining elements from b (if any)
        while (j < b.length) {
            result[k] = b[j];
            j++;
            k++;
        }


        return result;
    }
}

