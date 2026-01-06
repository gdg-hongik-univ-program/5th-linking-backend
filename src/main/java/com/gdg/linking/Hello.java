package com.gdg.linking;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {

    @GetMapping("/")
    public String helloCheck() {
        return "Hello World! 배포 성공!";
    }
}
