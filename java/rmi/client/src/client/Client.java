package client;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import services.Greeting;

public class Client {

  public Client() {
  }

  public static void main(String[] args) {
    String host = (args.length < 1) ? "localhost" : args[0];
    try {
        //Registry registry = LocateRegistry.getRegistry(host);
        //Greeting stub = (Greeting) registry.lookup("Hello");
        //OR:
        Greeting stub = (Greeting) Naming.lookup("//"+host+":1099/Hello");
        
        String response = stub.greet();
        System.out.println("response: " + response);
    } catch (Exception e) {
        System.err.println("Client exception: " + e.toString());
        e.printStackTrace();
    }
  }

}