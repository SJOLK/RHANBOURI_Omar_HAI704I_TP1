package animal.server;

import animal.common.Espece;
import animal.common.IAnimal;
import animal.common.IDossierSuivi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class AnimalImpl extends UnicastRemoteObject implements IAnimal {

    private String nom;
    private String nomMaitre;
    private String race;
    private Espece espece;
    private IDossierSuivi dossierSuivi;

    public AnimalImpl(
            String nom,
            String nomMaitre,
            String race,
            Espece espece,
            IDossierSuivi dossierSuivi
    ) throws RemoteException {
        super();
        this.nom = nom;
        this.nomMaitre = nomMaitre;
        this.race = race;
        this.espece = espece;
        this.dossierSuivi = dossierSuivi;
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
    public Espece getEspece() throws RemoteException {
        System.out.println(
                "Identité de l'espèce côté serveur : "
                        + System.identityHashCode(espece)
        );

        return espece;
    }
    @Override
    public IDossierSuivi getDossierSuivi() throws RemoteException {
        return dossierSuivi;
    }
}