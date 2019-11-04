package app;

import java.io.*;
import java.util.*;

public class LocalUser implements Serializable {
  private String username;

  public LocalUser(String username) {
    this.username = username;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LocalUser localUser = (LocalUser) o;
    return Objects.equals(username, localUser.username);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username);
  }
}
