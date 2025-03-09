package kr.yuns;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class KoreanguideApiApplication {

    private static final String TIMEZONE_KST = "Asia/Seoul";

    public static void main(String[] args) {
        SpringApplication.run(KoreanguideApiApplication.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone(TIMEZONE_KST));
    }
}
