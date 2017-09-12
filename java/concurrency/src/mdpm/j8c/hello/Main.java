package mdpm.j8c.hello;

import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
      example2();
    }

    @SuppressWarnings("unused")
    static private void example1() {
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Hello " + threadName);
        };

        task.run();

        Thread thread = new Thread(task);
        thread.start();

        System.out.println("Done!");
    }

    static private void example2() {
        Runnable task = () -> {
          try {
            String threadName = Thread.currentThread().getName();

            System.out.println("Foo " + threadName);
            TimeUnit.SECONDS.sleep(1);
            System.out.println("Bar " + threadName);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        };

        Thread thread = new Thread(task);
        thread.start();

        System.out.println("Done!");
    }
}
