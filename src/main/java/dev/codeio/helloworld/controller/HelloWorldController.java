package dev.codeio.helloworld.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class HelloWorldController {
    @GetMapping("/hello")
    String sayHelloWorld(){
        return "Hello World";
    }
}
