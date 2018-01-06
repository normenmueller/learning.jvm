package server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import services.Greeting;
import services.impl.Hello;

/* A "Server", in this context, is the class which has a main method that
 * creates an instance of the remote object implementation, exports the remote
 * object, and then binds that instance to a name in a Java RMI registry.
 */
public class Server {

  public static void main(String args[]) {
    try {
      //System.out.println(Greeting.class.getProtectionDomain().getCodeSource().getLocation().toString());
      //System.out.println(Hello.class.getProtectionDomain().getCodeSource().getLocation().toString());
      
      // Create an instance AND export.
      // The latter is due to java.rmi.server.UnicastRemoteObject; recall `rmic` is deprecated.
      Greeting stub = new Hello();
      
      // Bind the remote object's stub in the registry
      
      /* Start the registry beforehand with:
       * ```
       * rmiregistry -J-Djava.rmi.server.codebase="file:/Users/nrm/Sources/learning/java/rmi/services/services.jar"
       * ```
       * Also cf. http://stackoverflow.com/questions/464687/running-rmi-server-classnotfound
       */
      Registry registry = LocateRegistry.getRegistry();
      
      // OR: 
      //Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
      
      registry.bind("Hello", stub);
      
      System.out.println("Server ready!");
      System.out.println(registry.toString());
    } catch (Exception e) {
      System.err.println("Server exception: " + e.toString());
      e.printStackTrace();
    }
  }

}