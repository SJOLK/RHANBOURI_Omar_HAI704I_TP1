package animal.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ICabinetVeterinaire extends Remote {

    List<IAnimal> getPatients() throws RemoteException;

    IAnimal rechercherPatient(String nom) throws RemoteException;
}