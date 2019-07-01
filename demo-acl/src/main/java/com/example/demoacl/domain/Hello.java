package com.example.demoacl.domain;

public class Hello {
  private final int id;

  public Hello(int id) {
    this.id = id;
  }

  // Required by spring-ACL
  public int getId() {
    return id;
  }
}
