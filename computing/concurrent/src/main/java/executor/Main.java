package mdpm.j8c.executor;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class Main {

    public static void main(String[] args) {
        example1();
    }

    static private void example1() {
//      withExecutor(executor -> {
//        executor.submit(() -> {
//          String threadName = Thread.currentThread().getName();
//          System.out.println("Hello " + threadName);
//        });
//      });
      
      try {
        FileReader res = new FileReader(new File("C:\\Users\\normen.mueller\\etc\\jdk1.8.0_121\\tags"));
        @SuppressWarnings("resource")
        Function<FileReader,Optional<?>> f = r -> { 
          System.out.println(r.getEncoding());
          return Optional.empty();
        };
        with(res).apply(f);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
      
    }
    
    static <T extends Closeable> Function<Function<T, Optional<?>>, Optional<?>> with(T resource) {
      return new Function<Function<T, Optional<?>>, Optional<?>>() {

        @Override
        public Optional<?> apply(Function<T, Optional<?>> f) {
          try {  
            return f.apply(resource);
          } finally {
            try { resource.close(); } catch (IOException e) { e.printStackTrace(); }
          }
        }

      };
    }
    
    static void withExecutor(Consumer<ExecutorService> consumer) {
      ExecutorService executor = Executors.newSingleThreadExecutor();
      try {
        consumer.accept(executor);
      } finally {
         gracefulShutdown(executor);
      }
    }

    static void gracefulShutdown(ExecutorService executor) {
       try {
         executor.shutdown();
         executor.awaitTermination(5, TimeUnit.SECONDS);
       }
       catch (InterruptedException e) {
         System.err.println("tasks interrupted");
       }
       finally {
         if (!executor.isTerminated()) {
           System.err.println("cancel non-finished tasks");
         }
         executor.shutdownNow();
       } 
    }

}
