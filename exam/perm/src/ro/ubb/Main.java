package ro.ubb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

  private static ExecutorService executorService = Executors.newCachedThreadPool();
  private static AtomicInteger count;

  public static void main(String[] args) throws InterruptedException {
    count = new AtomicInteger(0);
    back(new ArrayList<>(), 5, 10);
    executorService.shutdown();
    executorService.awaitTermination(10, TimeUnit.MINUTES);
  }

  private static void back(List<Integer> solution, int n, int availableThreads) {
    if(solution.size() == n) {
      if(solution.get(0) == 1) {
        count.getAndIncrement();
        System.out.println(solution.toString());
      }
    }
    if (availableThreads == 1) {
      for(int i = 0; i < n; i++) {
        if(solution.contains(i)) continue;
        solution.add(i);
        back(solution, n, availableThreads);
        solution.remove(solution.size() - 1);
      }
    } else {
      List<Integer> copy = new ArrayList<>(solution);
      executorService.submit(() -> {
        for(int i = 1; i < n; i += 2) {
          if(copy.contains(i)) continue;
          copy.add(i);
          back(copy, n, availableThreads / 2);
          copy.remove(copy.size() - 1);
        }
      });
      for(int i = 0; i < n; i += 2) {
        if(solution.contains(i)) continue;
        solution.add(i);
        back(solution, n, availableThreads - availableThreads / 2);
        solution.remove(solution.size() - 1);
      }
    }
  }
}
