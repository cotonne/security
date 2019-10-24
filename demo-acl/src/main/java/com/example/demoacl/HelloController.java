package com.example.demoacl;


import com.example.demoacl.domain.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloController {

  @Autowired
  private HelloService service;

  @GetMapping(path = "hello")
  public String hello(@RequestParam int id) {
    return service.hello(new Hello(id));
  }

  @GetMapping(path = "is-allowed-as-user")
  @PreAuthorize("hasRole('ROLE_USER')")
  public String is_allowed_as_user() {
    return "yes";
  }

  @GetMapping(path = "is-also-allowed-as-user")
  public String is_also_allowed_as_user() {
    return "yes";
  }

  @GetMapping(path = "isallowed-as-admin")
  public String isallowed_as_admin() {
    return "yes";
  }

}
