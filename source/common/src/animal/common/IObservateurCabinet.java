package animal.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IObservateurCabinet extends Remote {

    void notifierFranchissementSeuil(
            int seuil,
            int ancienNombrePatients,
            int nouveauNombrePatients
    ) throws RemoteException;
}