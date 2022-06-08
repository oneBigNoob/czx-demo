package cc.caozx.ware.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author Czx
 * @Date 2022/5/30 17:35
 */
@Configuration
public class ThreadPool {

    @Bean
    public ThreadPoolExecutor getThreadPoolExecutor() {
        return  new ThreadPoolExecutor(
                20,
                30,
                20L,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(20),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }
}
