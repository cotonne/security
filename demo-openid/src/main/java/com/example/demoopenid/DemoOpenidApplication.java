package com.example.demoopenid;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.client.*;
import org.springframework.security.oauth2.client.endpoint.*;
import org.springframework.web.client.*;

import javax.annotation.*;
import java.nio.charset.*;
import java.util.*;

@SpringBootApplication
public class DemoOpenidApplication {

  public static void main(String[] args) {
    SpringApplication.run(DemoOpenidApplication.class, args);
  }

}
