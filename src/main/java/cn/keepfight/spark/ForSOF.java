package cn.keepfight.spark;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.function.LongBinaryOperator;
import java.util.stream.IntStream;

/**
 * Created by tom on 17-4-13.
 */
public class ForSOF {

    @Test
    public void test(){
        int v = 1;
        for (int i = 0; i < 10; i++) {
            v = x(v, i);
        }
        System.out.println(v);
    }

    int x(int v, int y){
        return v*2+y;
    }

    public static void main(String[] args){
        LongBinaryOperator op = (v, y) -> (v*2 + y);
        LongAccumulator accumulator = new LongAccumulator(op, 1L);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 10).sorted()
                .forEach(i -> {
                    System.out.println(i);
                    executor.submit(() -> accumulator.accumulate(i));
                    try {
                        executor.awaitTermination(1, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });

        stop(executor);

        // 2539 expected, however result does not always be! I had got 2037 before.
        System.out.println(accumulator.getThenReset());
    }

    /**
     * codes for stop the executor, it's insignificant for my issue.
     */
    public static void stop(ExecutorService executor) {
        try {
            executor.shutdown();
            executor.awaitTermination(60, TimeUnit.SECONDS);
        }
        catch (InterruptedException e) {
            System.err.println("termination interrupted");
        }
        finally {
            if (!executor.isTerminated()) {
                System.err.println("killing non-finished tasks");
            }
            executor.shutdownNow();
        }
    }
}
