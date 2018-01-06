package services;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Remote interface
public interface Greeting extends Remote {
  
  String greet() throws RemoteException;

}