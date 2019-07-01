package com.example.demoacl;

import com.example.demoacl.domain.*;
import org.springframework.security.access.prepost.*;
import org.springframework.stereotype.*;

@Service
public class HelloService {

  @PreAuthorize("hasPermission(#hello, 'READ')")
  public String hello(Hello hello) {
    return "Hello! " + hello.getId();
  }
}
