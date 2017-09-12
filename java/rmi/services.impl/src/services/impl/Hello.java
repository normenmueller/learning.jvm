package services.impl;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;

import services.Greeting;

public class Hello extends UnicastRemoteObject implements Greeting {

  // java.rmi.server.RemoteObject implements Serializable to serialize and
  // send the respective stub over the network
  private static final long serialVersionUID = 79007848308807906L;

  public Hello() throws RemoteException {
    super();
  }

  public Hello(int port) throws RemoteException {
    super(port);
  }

  public Hello(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
    super(port, csf, ssf);
  }

  @Override
  public String greet() throws RemoteException {
    return "Hello!";
  }

}