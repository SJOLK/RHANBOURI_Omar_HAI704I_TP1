package animal.server;

import animal.common.IAnimal;
import animal.common.ICabinetVeterinaire;
import animal.common.Espece;
import animal.common.IDossierSuivi;
import animal.common.IObservateurCabinet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class CabinetVeterinaireImpl
        extends UnicastRemoteObject
        implements ICabinetVeterinaire {

    private final List<IAnimal> patients;
    private final CopyOnWriteArrayList<IObservateurCabinet> observateurs =
            new CopyOnWriteArrayList<>();
    private static final int[] SEUILS_ALERTES = {100, 500, 1000};

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

        int ancienNombrePatients = patients.size();

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

        int nouveauNombrePatients = patients.size();

        System.out.println(
                "Nouveau patient ajouté : " + nom
                        + " | Nombre de patients : " + nouveauNombrePatients
        );

        notifierSiFranchissement(
                ancienNombrePatients,
                nouveauNombrePatients
        );

        return nouveauPatient;
    }

    @Override
    public void abonner(IObservateurCabinet observateur)
            throws RemoteException {

        if (observateur != null) {
            observateurs.addIfAbsent(observateur);

            System.out.println(
                    "Client abonné aux alertes. Nombre d'abonnés : "
                            + observateurs.size()
            );
        }
    }

    @Override
    public void desabonner(IObservateurCabinet observateur)
            throws RemoteException {

        observateurs.remove(observateur);

        System.out.println(
                "Client désabonné des alertes. Nombre d'abonnés : "
                        + observateurs.size()
        );
    }

    private void notifierSiFranchissement(
            int ancienNombrePatients,
            int nouveauNombrePatients
    ) {
        for (int seuil : SEUILS_ALERTES) {

            boolean franchissementHausse =
                    ancienNombrePatients < seuil
                            && nouveauNombrePatients >= seuil;

            boolean franchissementBaisse =
                    ancienNombrePatients >= seuil
                            && nouveauNombrePatients < seuil;

            if (franchissementHausse || franchissementBaisse) {
                notifierObservateurs(
                        seuil,
                        ancienNombrePatients,
                        nouveauNombrePatients
                );
            }
        }
    }

    private void notifierObservateurs(
            int seuil,
            int ancienNombrePatients,
            int nouveauNombrePatients
    ) {
        for (IObservateurCabinet observateur : observateurs) {
            try {
                observateur.notifierFranchissementSeuil(
                        seuil,
                        ancienNombrePatients,
                        nouveauNombrePatients
                );
            } catch (RemoteException e) {
                observateurs.remove(observateur);

                System.err.println(
                        "Client abonné injoignable : suppression de l'abonnement."
                );
            }
        }
    }
}