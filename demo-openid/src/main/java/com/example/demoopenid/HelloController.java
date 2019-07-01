package com.example.demoopenid;

import org.springframework.security.access.prepost.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

  @PreAuthorize("hasRole('USER')")
  @GetMapping("/hello")
  public String hello() {
    return "Hello!";
  }
}
