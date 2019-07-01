package com.example.demoacl;


import com.example.demoacl.domain.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

  @Autowired
  private HelloService service;

  @GetMapping(path = "hello")
  public String hello(@RequestParam int id) {
    return service.hello(new Hello(id));
  }
}
