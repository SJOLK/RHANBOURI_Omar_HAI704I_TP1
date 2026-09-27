package animal.client;

import animal.common.Espece;
import animal.common.IAnimal;
import animal.common.ICabinetVeterinaire;
import animal.common.IDossierSuivi;


import java.rmi.RemoteException;
import java.rmi.NoSuchObjectException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class LogiqueClient {

    private final ICabinetVeterinaire cabinet;

    private ObservateurCabinetImpl observateur;

    public LogiqueClient(ICabinetVeterinaire cabinet) {
        this.cabinet = cabinet;
    }

    public record PatientInfo(
            String nom,
            String nomMaitre,
            String race,
            String espece
    ) {
    }

    public record DossierInfo(
            String etatSante,
            List<String> historique
    ) {
    }

    public List<PatientInfo> listerPatients()
            throws ErreurClientException {

        try {
            List<PatientInfo> resultat = new ArrayList<>();

            for (IAnimal patient : cabinet.getPatients()) {
                resultat.add(convertirPatient(patient));
            }

            return resultat;

        } catch (RemoteException e) {
            throw erreurCommunication(e);
        }
    }

    public PatientInfo rechercherPatient(String nom)
            throws ErreurClientException {

        if (nom == null || nom.isBlank()) {
            throw new ErreurClientException(
                    "Le nom du patient ne peut pas être vide."
            );
        }

        try {
            IAnimal patient =
                    cabinet.rechercherPatient(nom.trim());

            if (patient == null) {
                return null;
            }

            return convertirPatient(patient);

        } catch (RemoteException e) {
            throw erreurCommunication(e);
        }
    }

    public PatientInfo ajouterPatient(
            String nom,
            String nomMaitre,
            String race,
            String nomEspece,
            int esperanceVie
    ) throws ErreurClientException {

        if (nom == null || nom.isBlank()
                || nomMaitre == null || nomMaitre.isBlank()
                || race == null || race.isBlank()
                || nomEspece == null || nomEspece.isBlank()) {

            throw new ErreurClientException(
                    "Toutes les informations du patient sont obligatoires."
            );
        }

        if (esperanceVie <= 0) {
            throw new ErreurClientException(
                    "L'espérance de vie doit être strictement positive."
            );
        }

        try {
            Espece espece =
                    new Espece(nomEspece.trim(), esperanceVie);

            IAnimal patient = cabinet.ajouterPatient(
                    nom.trim(),
                    nomMaitre.trim(),
                    race.trim(),
                    espece
            );

            return convertirPatient(patient);

        } catch (RemoteException e) {
            throw erreurCommunication(e);
        }
    }

    public DossierInfo consulterDossier(String nomPatient)
            throws ErreurClientException {

        if (nomPatient == null || nomPatient.isBlank()) {
            throw new ErreurClientException(
                    "Le nom du patient ne peut pas être vide."
            );
        }

        try {
            IAnimal patient =
                    cabinet.rechercherPatient(nomPatient.trim());

            if (patient == null) {
                throw new ErreurClientException(
                        "Aucun patient nommé "
                                + nomPatient.trim()
                                + " n'a été trouvé."
                );
            }

            IDossierSuivi dossier =
                    patient.getDossierSuivi();

            return new DossierInfo(
                    dossier.getEtatSante(),
                    List.copyOf(dossier.getHistorique())
            );

        } catch (RemoteException e) {
            throw erreurCommunication(e);
        }
    }

    public void mettreAJourDossier(
            String nomPatient,
            String nouvelEtat,
            String observation
    ) throws ErreurClientException {

        if (nomPatient == null || nomPatient.isBlank()) {
            throw new ErreurClientException(
                    "Le nom du patient ne peut pas être vide."
            );
        }

        if (nouvelEtat == null || nouvelEtat.isBlank()) {
            throw new ErreurClientException(
                    "Le nouvel état de santé ne peut pas être vide."
            );
        }

        try {
            IAnimal patient =
                    cabinet.rechercherPatient(nomPatient.trim());

            if (patient == null) {
                throw new ErreurClientException(
                        "Aucun patient nommé "
                                + nomPatient.trim()
                                + " n'a été trouvé."
                );
            }

            IDossierSuivi dossier =
                    patient.getDossierSuivi();

            dossier.setEtatSante(nouvelEtat.trim());

            if (observation != null && !observation.isBlank()) {
                dossier.ajouterObservation(
                        observation.trim()
                );
            }

        } catch (RemoteException e) {
            throw erreurCommunication(e);
        }
    }

    public boolean estAbonne() {
        return observateur != null;
    }

    public void abonner(String nomClient)
            throws ErreurClientException {

        // Version utilisée par le client console.
        abonner(nomClient, System.out::println);
    }

    public void abonner(
            String nomClient,
            Consumer<String> gestionnaireAlerte
    ) throws ErreurClientException {

        if (observateur != null) {
            throw new ErreurClientException(
                    "Ce client est déjà abonné aux alertes."
            );
        }

        if (gestionnaireAlerte == null) {
            throw new ErreurClientException(
                    "Le gestionnaire d'alertes ne peut pas être absent."
            );
        }

        ObservateurCabinetImpl nouvelObservateur = null;

        try {
            String nom =
                    nomClient == null || nomClient.isBlank()
                            ? "Client"
                            : nomClient.trim();

            nouvelObservateur =
                    new ObservateurCabinetImpl(
                            nom,
                            gestionnaireAlerte
                    );

            cabinet.abonner(nouvelObservateur);
            observateur = nouvelObservateur;

        } catch (RemoteException e) {
            detruireObservateur(nouvelObservateur);
            throw erreurCommunication(e);
        }
    }

    public void desabonner()
            throws ErreurClientException {

        if (observateur == null) {
            throw new ErreurClientException(
                    "Ce client n'est pas abonné aux alertes."
            );
        }

        ObservateurCabinetImpl observateurActuel =
                observateur;

        try {
            cabinet.desabonner(observateurActuel);

        } catch (RemoteException e) {
            throw erreurCommunication(e);

        } finally {
            detruireObservateur(observateurActuel);
            observateur = null;
        }
    }

    public void fermer() {
        if (observateur == null) {
            return;
        }

        ObservateurCabinetImpl observateurActuel =
                observateur;

        try {
            cabinet.desabonner(observateurActuel);

        } catch (RemoteException e) {
            System.err.println(
                    "Le serveur est inaccessible pendant la fermeture."
            );

        } finally {
            detruireObservateur(observateurActuel);
            observateur = null;
        }
    }

    private void detruireObservateur(
            ObservateurCabinetImpl observateur
    ) {
        if (observateur == null) {
            return;
        }

        try {
            UnicastRemoteObject.unexportObject(
                    observateur,
                    true
            );
        } catch (NoSuchObjectException ignored) {
            // L'observateur était déjà désenregistré localement.
        }
    }

    private PatientInfo convertirPatient(IAnimal patient)
            throws RemoteException {

        return new PatientInfo(
                patient.getNom(),
                patient.getNomMaitre(),
                patient.getRace(),
                patient.getEspece().toString()
        );
    }

    private ErreurClientException erreurCommunication(
            RemoteException cause
    ) {
        return new ErreurClientException(
                "Impossible de communiquer avec le serveur.",
                cause
        );
    }
}