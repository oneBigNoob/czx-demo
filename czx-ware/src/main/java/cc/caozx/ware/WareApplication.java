package cc.caozx.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class WareApplication {
    public static void main(String[] args) {
        SpringApplication.run(WareApplication.class, args);
    }
}
