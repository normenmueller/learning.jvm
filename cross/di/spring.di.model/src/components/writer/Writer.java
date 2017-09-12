package components.writer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Writer {
  // Dependency declaration
  private services.writer.Writer writer;

  @Autowired
  public void setWriter(services.writer.Writer writer) {
    this.writer = writer;
  }

  public void run() {
    String s = "This is my test";
    writer.writer(s);
  }
}

