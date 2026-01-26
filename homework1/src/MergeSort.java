package homework1.src;

import java.util.Arrays;

public class MergeSort {

    public static int[] mergeSort(int[] input) {

        //if the input array is empty, break out of the function
        if (input.length == 0)
            return input;

        //if input array is not empty, run the mergeSort function with the input's indices as arguments
        return mergeSort(0, input.length - 1, input);
    }

    public static int[] mergeSort(int low, int high, int[] lst) {

        //if there's only one element in lst, return lst
        if (low == high) {
            return new int[] {lst[low]};
        }

        //if lst is longer than 1 element, find the midpoint index of lst, rounded down
        int mid = (low + high) / 2;

        //create new lst with left half lst and keep splitting it
        int[] left = mergeSort(0, mid, lst);
        //create new lst with right half of lst and keep splitting it
        int[] right = mergeSort(mid + 1, high, lst);

        //after list gets split down to base, merge and sort
        return merge(left, right);
    }

    public static int[] merge(int[] left, int[] right) {

        int[] result = new int[left.length + right.length];
        //placeholder
        int i = 0;
        int j = 0;
        int k = 0;

        while (i < left.length && j < right.length) {

            if (i == left.length) {
                result[k] = right[j];
            } else if (j == right.length) {
                result[k] = left[i];
            } else if (left[i] <= right[j]) {
                result[k] = left[i];
            } else {
                result[k] = right[j];
            }
            k++;
            i++;
            j++;

        }

        return result;

    }
}
