package cn.keepfight.spark;

import org.junit.Test;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.LongBinaryOperator;
import java.util.stream.IntStream;


class XX extends Thread{

    static int count=0;

    @Override
    public void run() {
        test();
    }

    static void test(){

        Integer x = 2;
        synchronized (x){
            System.out.println(" x: "+x);
//            try {
//                TimeUnit.SECONDS.wait(3);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            try {
                x.wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(" x: "+x+" wait over");
        }
    }
}

/**
 * Hello world!
 */
public class App {
    static ReentrantLock lock = new ReentrantLock();
    static String s = "content 1";
    public static void main(String[] args) throws Exception {
        run14();
    }



    @Test
    public void getHostIP() throws UnknownHostException, SocketException {
        InetAddress x = InetAddress.getLocalHost();

        System.out.println(x.getHostName());
        System.out.println(x.getHostAddress());
//        System.out.println(Arrays.asList(x.getAddress()).stream().collect(joining(";")));

        Enumeration net = NetworkInterface.getNetworkInterfaces();

        while (net.hasMoreElements()){
            NetworkInterface ni = (NetworkInterface) net.nextElement();
            Enumeration nt = ni.getInetAddresses();

            while (nt.hasMoreElements()){
                InetAddress ip = (InetAddress) nt.nextElement();
                if (ip.getHostAddress().indexOf(":")==-1){
                    System.out.print("not a ip6! ");
                }
                System.out.println(ip.getHostAddress()+" - "+ip.isSiteLocalAddress()+" - "+ip.isLoopbackAddress());
            }
            System.out.println("en hen! "+ni.getDisplayName());
        }
    }


    //???????????????????????????
    static void run14(){
        LongBinaryOperator op = (v, y) -> (v*2 + y);
        LongAccumulator accumulator = new LongAccumulator(op, 1L);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 10)
                .forEach(i -> executor.submit(() -> accumulator.accumulate(i)));

        stop(executor);

        System.out.println(accumulator.getThenReset());     // => 2539
    }

    static void run13(){
        ExecutorService executor = Executors.newFixedThreadPool(2);

        LongAdder adder = new LongAdder();

        IntStream.range(0, 1000)
                .forEach(i -> executor.submit(adder::increment));

        stop(executor);

        System.out.println(adder.sumThenReset());   // => 1000
    }

    static void run12(){
        AtomicInteger atomicInt = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 1000)
                .forEach(i -> {
                    Runnable task = () ->
                            atomicInt.accumulateAndGet(i, (n, m) -> n + m);
                    executor.submit(task);
                });

        stop(executor);

        System.out.println(atomicInt.get());    // => 499500
    }

    static void run11(){
        AtomicInteger atomicInt = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 1000)
                .forEach(i -> {
                    Runnable task = () ->
                            atomicInt.updateAndGet(n -> n + 2);
                    executor.submit(task);
                });

        stop(executor);

        System.out.println(atomicInt.get());    // => 2000
    }

    static void run10(){
        AtomicInteger atomicInt = new AtomicInteger(0);

        ExecutorService executor = Executors.newFixedThreadPool(2);

        IntStream.range(0, 1000)
                .forEach(i -> executor.submit(atomicInt::incrementAndGet));

        stop(executor);

        System.out.println(atomicInt.get());    // => 1000

    }

    static void run9(){
        ReentrantLock lock = new ReentrantLock();
        lock.lock();
        System.out.println("lock ok");
        try {
            TimeUnit.SECONDS.wait(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        lock.unlock();
    }

    static void run7(){
        synchronized (lock){
            System.out.println("xx");
            run7();
        }
    }

    static void run8(){
        ExecutorService executor = Executors.newFixedThreadPool(2);
        StampedLock lock = new StampedLock();


        executor.submit(() -> {
            long stamp = lock.tryOptimisticRead();
            try {
                System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
                System.out.println(s);

                sleep(1000);
                System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
                System.out.println(s);

                sleep(2000);
                System.out.println("Optimistic Lock Valid: " + lock.validate(stamp));
                System.out.println(s);

            } finally {
                lock.unlock(stamp);
            }
        });

        executor.submit(() -> {
            long stamp = lock.writeLock();
            try {
                System.out.println("Write Lock acquired");
                s = "c2-p1 ->->->";
                sleep(2000);
                s += "c2-p2";
            } finally {
                lock.unlock(stamp);
                System.out.println("Write done");
            }
        });

        executor.shutdown();
    }

    static void run6(){
        try {
            run4();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("run5!"+lock.isLocked());
        run5();
        new Thread(App::run5).start();

    }

    static void run5(){
        System.out.println("it is " + Thread.currentThread().getId()+"  -> "+lock.isLocked()+" by me?"+lock.isHeldByCurrentThread());

        if (lock.tryLock()){
            System.out.println("ok lock");
        }else {
            System.out.println("god! it locked!");
        }
    }

    static void run4(){

        lock.lock();
        if (true)
            throw new RuntimeException("xx");
        System.out.println("run it!");
        lock.unlock();
    }

    static void run3() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                TimeUnit.SECONDS.sleep(2);
                System.out.println("Scheduling: " + System.nanoTime());
            }
            catch (InterruptedException e) {
                System.err.println("task interrupted");
            }
        };

        System.out.println("S: " + System.nanoTime());
        executor.scheduleWithFixedDelay(task, 0, 1, TimeUnit.SECONDS);
    }
    static void run2(){
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());

        int initialDelay = 0;
        int period = 1;
        System.out.println("S: " + System.nanoTime());
        executor.scheduleAtFixedRate(task, initialDelay, period, TimeUnit.SECONDS);
    }

    static void run1() throws InterruptedException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> System.out.println("Scheduling: " + System.nanoTime());
        ScheduledFuture<?> future = executor.schedule(task, 3, TimeUnit.SECONDS);

        TimeUnit.MILLISECONDS.sleep(1000);

        long remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
        System.out.printf("Remaining Delay: %sms \n", remainingDelay);

        TimeUnit.MILLISECONDS.sleep(1000);

        remainingDelay = future.getDelay(TimeUnit.MILLISECONDS);
        System.out.printf("Remaining Delay: %sms \n", remainingDelay);
    }

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

    public static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new IllegalStateException(e);
        }
    }
}
