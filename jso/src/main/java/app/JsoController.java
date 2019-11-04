package app;

import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.*;
import java.io.*;

@RestController
public class JsoController {

  @GetMapping("/hello")
  public String hello(@CookieValue(name = "USER", required = false) String savedUser, HttpServletResponse request) throws IOException, ClassNotFoundException {
    LocalUser user;
    if (savedUser == null) {
      user = new LocalUser("bob");
      request.addCookie(new Cookie("USER", LocalUserSerializer.toBase64Serialize(user)));
    } else {
      user = LocalUserSerializer.toBase64Deserialize(savedUser);
    }
    return "OK = " + user.getUsername();
  }

  @GetMapping("/download")
  public ResponseEntity<InputStreamResource> download() throws IOException, ClassNotFoundException {
    LocalUser user;
    user = new LocalUser("bob");
    byte[] data = LocalUserSerializer.serialize(user);

    return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(data.length)
            .body(new InputStreamResource(new ByteArrayInputStream(data)));
  }
}
