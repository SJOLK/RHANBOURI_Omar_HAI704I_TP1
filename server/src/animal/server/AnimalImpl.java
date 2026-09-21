package animal.server;

import animal.common.IAnimal;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AnimalImpl extends UnicastRemoteObject implements IAnimal {

    private String nom;
    private String nomMaitre;
    private String race;
    private String espece;

    public AnimalImpl(String nom, String nomMaitre, String race, String espece)
            throws RemoteException {
        super();
        this.nom = nom;
        this.nomMaitre = nomMaitre;
        this.race = race;
        this.espece = espece;
    }

    @Override
    public String getNom() throws RemoteException {
        return nom;
    }

    @Override
    public String getNomMaitre() throws RemoteException {
        return nomMaitre;
    }

    @Override
    public String getRace() throws RemoteException {
        return race;
    }

    @Override
    public String getEspece() throws RemoteException {
        return espece;
    }
}