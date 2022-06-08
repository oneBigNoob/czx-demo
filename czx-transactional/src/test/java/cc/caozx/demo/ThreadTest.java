package cc.caozx.demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest {
    public static ExecutorService executor  =  Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main....start....");

//        ----------------- 最简单的方法开启一个异步任务（不考虑任务的返回值） ---------------------
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果:" + i);
//        }, executor);
//        System.out.println("main....end....");


//        -------------------- 开启一个异步任务，并且获得返回值或异常 ---------------------------
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int i = 10 / 2;
            System.out.println("运行结果:" + i);
            return i;
        }, executor).whenComplete((res, exception) -> { //虽然能得到异常信息，但是没法修改返回数据
            System.out.println("异步任务成功完成了...结果是:" + res + "；异常信息是" + exception);
        }).exceptionally(throwable -> {  //可以感知异常，同时返回默认值
            return 10;
        }); //成功以后干啥事
//        ------------------------ 立刻获得结果，没用得到结果使用指定的默认值 --------------------------
//        System.out.println(future.getNow(6));

        System.out.println("main....end....");
    }
}
