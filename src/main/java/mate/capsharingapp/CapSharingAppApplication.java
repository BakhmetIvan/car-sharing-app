package mate.capsharingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class CapSharingAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(CapSharingAppApplication.class, args);
    }

}
