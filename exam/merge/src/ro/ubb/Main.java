package ro.ubb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Main {
  private static final ExecutorService EXECUTOR_SERVICE = Executors.newCachedThreadPool();

  public static void main(String[] args) throws InterruptedException, ExecutionException {
        mergeSort(Arrays.asList(4, 5, 12, 1, 3, 5, 1, 5, 29, 22), 100)
            .forEach(v -> System.out.print(v + " "));
    EXECUTOR_SERVICE.shutdown();
    EXECUTOR_SERVICE.awaitTermination(10, TimeUnit.MINUTES);
  }

  private static List<Integer> mergeSort(List<Integer> numbers, int threads)
      throws ExecutionException, InterruptedException {
    return mergeSortUtil(numbers, threads);
  }

  private static List<Integer> mergeSortUtil(List<Integer> numbers, int threads)
      throws ExecutionException, InterruptedException {
    if (numbers.size() <= 1) return numbers;
    if (threads >= 2) {
      Future<List<Integer>> future1 =
          EXECUTOR_SERVICE.submit(
              () -> mergeSortUtil(numbers.subList(0, numbers.size() / 2), threads / 2));
      Future<List<Integer>> future2 =
          EXECUTOR_SERVICE.submit(
              () ->
                  mergeSortUtil(
                      numbers.subList(numbers.size() / 2, numbers.size()), threads - threads / 2));
      List<Integer> result1 = future1.get();
      List<Integer> result2 = future2.get();
      return merge(result1, result2);
    } else {
      List<Integer> result1 = mergeSortUtil(numbers.subList(0, numbers.size() / 2), threads);
      List<Integer> result2 =
          mergeSortUtil(numbers.subList(numbers.size() / 2, numbers.size()), threads);
      return merge(result1, result2);
    }
  }

  private static List<Integer> merge(List<Integer> list1, List<Integer> list2) {
    List<Integer> result = new ArrayList<>();
    int i = 0, j = 0;
    while (i < list1.size() && j < list2.size()) {
      if (list1.get(i) < list2.get(j)) {
        result.add(list1.get(i));
        i++;
      } else {
        result.add(list2.get(j));
        j++;
      }
    }
    result.addAll(list1.subList(i, list1.size()));
    result.addAll(list2.subList(j, list2.size()));
    return result;
  }
//
//  private static List<Integer> mergeRecursive(
//      List<Integer> list1,
//      int start1,
//      int end1,
//      List<Integer> list2,
//      int start2,
//      int end2,
//      int threads)
//      throws ExecutionException, InterruptedException {
//    if (threads <= 1 || end1 <= start1 || end2 <= start2) {
//      return merge(list1, list2);
//    }
//    if (end1 - start1 < end2 - start2) {
//      return mergeRecursive(list2, start2, end2, list1, start1, end1, threads);
//    }
//
//    int indexInFirstList = start1 + (end1 - start1) / 2;
//    int element = list1.get(indexInFirstList);
//    int indexInSecondList = lowerBound(list2, start2, end2, element);
//    Future<List<Integer>> future1 =
//        EXECUTOR_SERVICE.submit(
//            () ->
//                mergeRecursive(
//                    list1, start1, indexInFirstList,
//                    list2, start2, indexInSecondList,
//                    threads / 2));
//    Future<List<Integer>> future2 =
//        EXECUTOR_SERVICE.submit(
//            () ->
//                mergeRecursive(
//                    list1, indexInFirstList, end1,
//                    list2, indexInSecondList, end2,
//                    threads - threads / 2));
//    List<Integer> result = future1.get();
//    result.addAll(future2.get());
//    return result;
//  }
//
//  private static int lowerBound(List<Integer> list, int low, int high, int element) {
//    while (low < high) {
//      int middle = low + (high - low) / 2;
//      if (element > list.get(middle)) {
//        low = middle + 1;
//      } else {
//        high = middle;
//      }
//    }
//    return low;
//  }
}
