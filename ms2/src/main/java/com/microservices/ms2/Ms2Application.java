package com.microservices.ms2;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import java.util.function.Supplier;


@SpringBootApplication
public class Ms2Application {
    public static void main(String[] args) {
        SpringApplication.run(Ms2Application.class, args);
    }
}

@org.springframework.web.bind.annotation.RestController
@Slf4j
class RestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestController.class);

    @GetMapping(path = "/api2")
    public ResponseEntity<String> getInfo() {
        return ResponseEntity.ok("hello");
    }

    @GetMapping(path = "/invoke3rdparty/api2")
    public ResponseEntity<String> invoke3rdParty() {
        RestTemplate restTemplate = new RestTemplate();
        double rand = (int) (Math.random() * (100 - 1)) + 1;
        Supplier<Long> longSupplier = () -> {
            return (new Double(rand)).longValue();
        };
        System.out.println("random value" + longSupplier.get());
        try {
            Thread.sleep(longSupplier.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = restTemplate.getForObject("https://jsonplaceholder.typicode.com/todos/1", String.class);
        System.out.println(response);
        return ResponseEntity.ok(response);
    }
}