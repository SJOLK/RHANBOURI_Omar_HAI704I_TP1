package animal.server;

import animal.common.IDossierSuivi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class DossierSuiviImpl
        extends UnicastRemoteObject
        implements IDossierSuivi {

    private String etatSante;
    private final List<String> historique;

    public DossierSuiviImpl(String etatSante) throws RemoteException {
        super();
        this.etatSante = etatSante;
        this.historique = new ArrayList<>();
    }

    @Override
    public synchronized String getEtatSante() throws RemoteException {
        return etatSante;
    }

    @Override
    public synchronized void setEtatSante(String etatSante)
            throws RemoteException {
        this.etatSante = etatSante;
    }

    @Override
    public synchronized List<String> getHistorique()
            throws RemoteException {
        return new ArrayList<>(historique);
    }

    @Override
    public synchronized void ajouterObservation(String observation)
            throws RemoteException {
        historique.add(observation);
    }
}