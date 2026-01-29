package homework1.src;

import java.util.Random;

public class MergeSort {

    public static void main(String[] args) {

        //create new array
        int[] input = new int[1000];
        //create new random instance
        Random rand = new Random();

        //create random numbers from 0-5000 to populate the array
        for (int i = 0; i < 1000; i++) {
            input[i] = rand.nextInt(5000);
        }

        //sorted array
        mergeSort(input);

    }


    public static int[] mergeSort(int[] input) {

        //if the input array is empty or just one element, just return the array
        if (input.length < 2)
            return input;

        //if input array is larger, run the mergeSort method with the input's indices as arguments
        return mergeSort(0, input.length - 1, input);
    }

    public static int[] mergeSort(int low, int high, int[] lst) {

        //if there's only one element in lst, return lst to enter merge() method
        if (low == high) {
            return new int[] {lst[low]};
        }

        //if lst is longer than 1 element, find the midpoint index of lst, rounded down
        int mid = (low + high) / 2;

        //create new lst with left half lst and keep splitting it
        int[] left = mergeSort(low, mid, lst);
        //create new lst with right half of lst and keep splitting it
        int[] right = mergeSort(mid + 1, high, lst);

        //after list gets split down to base, merge and sort
        return merge(left, right);
    }

    public static int[] merge(int[] left, int[] right) {

        //placeholder for sorted array
        int[] result = new int[left.length + right.length];

        //placeholder index for left array
        int i = 0;
        //placeholder index for right array
        int j = 0;
        //placeholder index for sorted result
        int k = 0;

        //as long as the index for both lists are less than the length, enter the while loop
        //compare each element from each array and add the lesser value to the result array
        while (i < left.length && j < right.length) {
            //if left array element less than or equal to right element, add left array element to result array
            if (left[i] <= right[j]) {
                result[k] = left[i];
                i++; //increment left array index
                k++; //increment result array index

            //else add right array element to result array
            } else {
                result[k] = right[j];
                j++; //increment right array index
                k++; //increment result array index
            }
        }

        //add the remaining elements of left array to result array
        while (i < left.length) {
            result[k] = left[i];
            i++;
            k++;
        }

      //add the remaining elements of right array to result array
      while (j < right.length) {
            result[k] = right[j];
            j++;
            k++;
        }

        //return sorted array
        return result;

    }
}
