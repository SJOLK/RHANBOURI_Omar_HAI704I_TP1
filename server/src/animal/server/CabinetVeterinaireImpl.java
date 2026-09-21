package animal.server;

import animal.common.IAnimal;
import animal.common.ICabinetVeterinaire;
import animal.common.Espece;
import animal.common.IDossierSuivi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class CabinetVeterinaireImpl
        extends UnicastRemoteObject
        implements ICabinetVeterinaire {

    private final List<IAnimal> patients;

    public CabinetVeterinaireImpl(List<IAnimal> patients)
            throws RemoteException {
        super();
        this.patients = new ArrayList<>(patients);
    }

    @Override
    public synchronized List<IAnimal> getPatients()
            throws RemoteException {
        return new ArrayList<>(patients);
    }

    @Override
    public synchronized IAnimal rechercherPatient(String nom)
            throws RemoteException {

        if (nom == null) {
            return null;
        }

        for (IAnimal patient : patients) {
            if (patient.getNom().equalsIgnoreCase(nom.trim())) {
                return patient;
            }
        }

        return null;
    }

    @Override
    public synchronized IAnimal ajouterPatient(
            String nom,
            String nomMaitre,
            String race,
            Espece espece
    ) throws RemoteException {

        // Ces objets sont créés dans la JVM du serveur
        IDossierSuivi dossier =
                new DossierSuiviImpl("À examiner");

        dossier.ajouterObservation(
                "Patient ajouté au cabinet."
        );

        IAnimal nouveauPatient = new AnimalImpl(
                nom,
                nomMaitre,
                race,
                espece,
                dossier
        );

        patients.add(nouveauPatient);

        System.out.println(
                "Nouveau patient ajouté : " + nom
                        + " | Nombre de patients : " + patients.size()
        );

        return nouveauPatient;
    }
}