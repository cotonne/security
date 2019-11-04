package app;

import app.*;

import java.io.*;
import java.util.*;

public class LocalUserSerializer {
  public static String toBase64Serialize(LocalUser user) throws IOException {
    byte[] src = serialize(user);
    return Base64.getEncoder().encodeToString(src);
  }

  public static byte[] serialize(LocalUser user) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
    objectOutputStream.writeObject(user);
    return out.toByteArray();
  }

  public static LocalUser toBase64Deserialize(String savedUser) throws IOException, ClassNotFoundException {
    byte[] bytes = Base64.getDecoder().decode(savedUser);
    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes));
    return (LocalUser) in.readObject();
  }
}
