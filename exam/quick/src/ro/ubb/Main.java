package ro.ubb;

import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<Integer> array = Arrays.asList(4, 3, 2, 1);
        quicksort(array, 0, array.size());
        array.forEach(s -> System.out.print(s + " "));
    }

    private static void quicksort(List<Integer> array, int start, int end) {
        if (start < end) {
            int p = partition(array, start, end);
            quicksort(array, start, p);
            quicksort(array, p, end);
        }
    }

    private static int partition(List<Integer> array, int start, int end) {
        int pivot = array.get(end);
        int i = start - 1;
        for(int j = start; j < end; j++) {
            if (array.get(j)) {
                i++;
            }
        }
    }
}
