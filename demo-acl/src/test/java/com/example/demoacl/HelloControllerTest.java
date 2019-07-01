package com.example.demoacl;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.*;
import org.springframework.boot.test.context.*;
import org.springframework.http.*;
import org.springframework.security.test.context.support.*;
import org.springframework.test.context.junit4.*;
import org.springframework.test.web.servlet.*;
import org.springframework.test.web.servlet.setup.*;
import org.springframework.web.context.*;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HelloControllerTest {
  @Autowired
  private WebApplicationContext context;

  private MockMvc mvc;

  @Before
  public void setup() {
    mvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
  }

  @WithMockUser(username = "user")
  @Test
  public void user_should_say_hello() throws Exception {
    mvc.perform(get("/hello?id=101"))
            .andExpect(content().string("Hello! 101"));
  }

  @WithMockUser(username = "user")
  @Test
  public void user_should_not_say_hello_for_102() throws Exception {
    mvc.perform(get("/hello?id=102"))
            .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
  }

  @WithMockUser(username = "other_user")
  @Test
  public void other_user_should_say_hello() throws Exception {
    mvc.perform(get("/hello?id=102"))
            .andDo(print())
            .andExpect(content().string("Hello! 102"));
  }

  @WithMockUser(username = "other_user")
  @Test
  public void other_user_should_say_hello_for_101() throws Exception {
    mvc.perform(get("/hello?id=101"))
            .andExpect(status().is(HttpStatus.FORBIDDEN.value()));
  }

}
