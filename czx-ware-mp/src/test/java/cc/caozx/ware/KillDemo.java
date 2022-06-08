package cc.caozx.ware;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 */
@Slf4j
public class KillDemo {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        KillDemo killDemo = new KillDemo();
        killDemo.mergeJob();
        Thread.sleep(2000);

        List<Future<R>> futureList = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(1000);

        for (int i = 0; i < 1000; i++) {
            final long orderId = i + 100L;
            final long userId = Long.valueOf(i);
            Future<R> future = executorService.submit(() -> {
                countDownLatch.countDown();
                countDownLatch.await(1000, TimeUnit.MILLISECONDS);
                UserRequest userRequest = new UserRequest(orderId, userId, 1);
                return killDemo.operate(userRequest);
            });
            futureList.add(future);
        }

        futureList.forEach(future -> {
            try {
                R result = future.get(300, TimeUnit.MILLISECONDS);
                log.info(Thread.currentThread().getName() + ":客户端请求响应:{}", result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private Integer stock = 100;

    private BlockingQueue<RequestPromise> queue = new LinkedBlockingQueue<>(1000);


    /**
     * 用户库存扣减
     *
     * @return
     */
    public R operate(UserRequest userRequest) throws InterruptedException {
        // TODO 阈值判断
        // TODO 队列创建
        RequestPromise requestPromise = new RequestPromise(userRequest);
        synchronized (requestPromise) {
            boolean enqueueSucess = queue.offer(requestPromise, 100, TimeUnit.MILLISECONDS);
            if (!enqueueSucess) {
                return R.fail("系统繁忙");
            }
            try {
                requestPromise.wait(200);
                if (requestPromise.getResult() == null) {
                    return R.fail("等待超时");
                }
            } catch (InterruptedException e) {
                log.error("线程 异常,", e);
            }
        }
        return requestPromise.getResult();
    }

    public void mergeJob() {
        new Thread(() -> {
            List<RequestPromise> list = new ArrayList<>();
            while (true) {
                if (queue.isEmpty()) {
                    try {
                        Thread.sleep(10);
                        continue;
                    } catch (InterruptedException e) {
                        log.error("线程休眠异常,", e);
                    }
                }

                int batchSize = queue.size();
                for (int i = 0; i < batchSize; i++) {
                    list.add(queue.poll());
                }
                log.info(Thread.currentThread().getName() + ":合并扣减库存:{}", list);

                int sum = list.stream().mapToInt(e -> e.getUserRequest().getCount()).sum();
                if (sum <= stock) {
                    stock -= sum;
                    // notify user
                    list.forEach(requestPromise -> {
                        requestPromise.setResult(
                                R.success("ok")
                        );
                        synchronized (requestPromise) {
                            requestPromise.notify();
                        }
                    });
                    continue;
                }
                for (RequestPromise requestPromise : list) {
                    int count = requestPromise.getUserRequest().getCount();
                    if (count <= stock) {
                        stock -= count;
                        requestPromise.setResult(R.success("ok"));
                    } else {
                        requestPromise.setResult(R.fail("库存不足"));
                    }
                    synchronized (requestPromise) {
                        requestPromise.notify();
                    }
                }
                list.clear();
            }
        }, "mergeThread").start();
    }
}

@Data
@ToString
@AllArgsConstructor
class RequestPromise {
    private UserRequest userRequest;
    private R result;

    public RequestPromise(UserRequest userRequest) {
        this.userRequest = userRequest;
    }
}

@Data
@ToString
@AllArgsConstructor
class UserRequest {
    private Long orderId;
    private Long userId;
    private Integer count;
}