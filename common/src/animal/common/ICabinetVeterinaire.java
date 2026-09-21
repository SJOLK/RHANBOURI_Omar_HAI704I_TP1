package animal.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ICabinetVeterinaire extends Remote {

    List<IAnimal> getPatients() throws RemoteException;

    IAnimal rechercherPatient(String nom) throws RemoteException;

    IAnimal ajouterPatient(
            String nom,
            String nomMaitre,
            String race,
            Espece espece
    ) throws RemoteException;
}