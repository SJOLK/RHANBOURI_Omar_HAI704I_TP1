package animal.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IDossierSuivi extends Remote {

    String getEtatSante() throws RemoteException;

    void setEtatSante(String etatSante) throws RemoteException;

    List<String> getHistorique() throws RemoteException;

    void ajouterObservation(String observation) throws RemoteException;
}