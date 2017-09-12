package services.writer.simple;

import org.springframework.stereotype.Service;

import services.writer.Writer;

//Service Implementation
@Service
public class WriterService implements Writer {
  public void writer (String s) {
    System.out.println(s);
  }
}

