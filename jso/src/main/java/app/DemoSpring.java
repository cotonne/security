package app;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.web.servlet.config.annotation.*;

@SpringBootApplication()
@EnableWebMvc
public class DemoSpring {
  public static void main(String[] args) throws Exception {
    SpringApplication.run(DemoSpring.class, args);
  }
}
