package app;

import org.junit.*;

import java.io.*;

import static app.LocalUserSerializer.*;
import static org.junit.Assert.*;

public class LocalUserSerializerTest {

  @Test
  public void serializeThenDeserialize() throws IOException, ClassNotFoundException {
    LocalUser user = new LocalUser("random");
    String serialize = toBase64Serialize(user);
    System.out.println(serialize);
    LocalUser rebuiltUser = toBase64Deserialize(serialize);
    assertEquals(user, rebuiltUser);
  }
}
