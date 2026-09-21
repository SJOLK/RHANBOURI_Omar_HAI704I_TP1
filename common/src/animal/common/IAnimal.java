package animal.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IAnimal extends Remote {

    String getNom() throws RemoteException;

    String getNomMaitre() throws RemoteException;

    String getRace() throws RemoteException;

    Espece getEspece() throws RemoteException;
}