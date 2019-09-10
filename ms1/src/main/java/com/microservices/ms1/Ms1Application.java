package com.microservices.ms1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class Ms1Application {
    public static void main(String[] args) {
        SpringApplication.run(Ms1Application.class, args);
    }
}

@org.springframework.web.bind.annotation.RestController
class RestController {

    @GetMapping(path = "/api1")
    public ResponseEntity<String> getInfo() {
        return ResponseEntity.ok("hello");
    }

    @GetMapping(path = "/invoke3rdparty/api")
    public ResponseEntity<String> invoke3rdParty() {
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject("https://jsonplaceholder.typicode.com/todos/1", String.class);
        return ResponseEntity.ok(response);
    }
}


