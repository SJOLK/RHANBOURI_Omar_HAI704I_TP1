package animal.client;

import animal.common.*;
import animal.common.Chien;

import java.lang.reflect.Proxy;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        try {
            // Connexion au registre RMI lancé sur la machine locale
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            ICabinetVeterinaire cabinet =
                    (ICabinetVeterinaire) registry.lookup("Cabinet");

            String nomClient =
                    args.length > 0 ? args[0] : "Client sans nom";

            IObservateurCabinet observateur =
                    new ObservateurCabinetImpl(nomClient);

            cabinet.abonner(observateur);

            System.out.println(
                    nomClient + " est abonné aux alertes du cabinet."
            );

            System.out.println(
                    "Cabinet est un proxy : "
                            + Proxy.isProxyClass(cabinet.getClass())
            );

            System.out.println("\n========== LISTE DES PATIENTS ==========");

            List<IAnimal> patients = cabinet.getPatients();

            for (IAnimal patient : patients) {
                System.out.println(
                        "- " + patient.getNom()
                                + ", maître : " + patient.getNomMaitre()
                                + ", race : " + patient.getRace()
                );

                System.out.println(
                        "  Est un proxy : "
                                + Proxy.isProxyClass(patient.getClass())
                );
            }

            System.out.println("\n========== AJOUT D'UN PATIENT ==========");

            int nombreAvant = patients.size();

            Espece especeRocky = new Espece("Chien", 12);

            IAnimal nouveauPatient = cabinet.ajouterPatient(
                    "Rocky",
                    "Sophie",
                    "Labrador",
                    especeRocky
            );

            System.out.println(
                    "Patient ajouté : " + nouveauPatient.getNom()
            );
            System.out.println(
                    "Le patient retourné est un proxy : "
                            + Proxy.isProxyClass(nouveauPatient.getClass())
            );

// On redemande la liste au serveur
            List<IAnimal> patientsApresAjout =
                    cabinet.getPatients();

            System.out.println(
                    "Nombre de patients avant : " + nombreAvant
            );
            System.out.println(
                    "Nombre de patients après : "
                            + patientsApresAjout.size()
            );

            System.out.println("Liste après ajout :");

            for (IAnimal patient : patientsApresAjout) {
                System.out.println(
                        "- " + patient.getNom()
                                + " | " + patient.getEspece()
                );
            }

            IAnimal rockyRecherche =
                    cabinet.rechercherPatient("Rocky");

            System.out.println(
                    "Recherche de Rocky : "
                            + (rockyRecherche == null
                            ? "introuvable"
                            : rockyRecherche.getNom() + " trouvé")
            );

            System.out.println("\n========== RECHERCHE ==========");

            IAnimal animal = cabinet.rechercherPatient("rex");

            if (animal == null) {
                System.out.println("Patient introuvable.");
                return;
            }

            System.out.println("Patient trouvé : " + animal.getNom());

            IAnimal patientInexistant =
                    cabinet.rechercherPatient("Patient inconnu");

            System.out.println(
                    "Recherche d'un patient inexistant : "
                            + (patientInexistant == null ? "introuvable" : "trouvé")
            );

            // Appels des méthodes distantes
            System.out.println("Nom : " + animal.getNom());
            System.out.println("Maître : " + animal.getNomMaitre());
            System.out.println("Race : " + animal.getRace());
            System.out.println(
                    "Classe de l'animal : "
                            + animal.getClass().getName()
            );
            System.out.println(
                    "Animal est un proxy : "
                            + Proxy.isProxyClass(animal.getClass())
            );

            // Première récupération : le client reçoit une copie
            Espece premiereCopie = animal.getEspece();

            System.out.println("\n--- Première copie reçue ---");
            System.out.println("Espèce : " + premiereCopie);
            System.out.println(
                    "Identité côté client : "
                            + System.identityHashCode(premiereCopie)
            );

            // Modification uniquement dans la JVM du client
            premiereCopie.setNom("Chat");
            premiereCopie.setEsperanceVieMoyenne(20);

            System.out.println("\n--- Après modification locale ---");
            System.out.println("Copie modifiée : " + premiereCopie);

            // Nouvelle demande au serveur
            Espece deuxiemeCopie = animal.getEspece();

            System.out.println("\n--- Nouvelle copie reçue du serveur ---");
            System.out.println("Espèce : " + deuxiemeCopie);
            System.out.println(
                    "Identité côté client : "
                            + System.identityHashCode(deuxiemeCopie)
            );

            System.out.println("\n========== DOSSIER DE SUIVI ==========");

            // Première récupération du dossier distant
            IDossierSuivi premierDossier = animal.getDossierSuivi();

            System.out.println(
                    "Classe du dossier : "
                            + premierDossier.getClass().getName()
            );
            System.out.println(
                    "Dossier est un proxy : "
                            + Proxy.isProxyClass(premierDossier.getClass())
            );

            System.out.println(
                    "État avant modification : "
                            + premierDossier.getEtatSante()
            );

            // Modification distante du véritable dossier
            premierDossier.setEtatSante("Sous traitement");
            premierDossier.ajouterObservation(
                    "Traitement antibiotique prescrit."
            );

            // On redemande le dossier à l'animal
            IDossierSuivi secondDossier = animal.getDossierSuivi();

            System.out.println(
                    "État après nouvelle récupération : "
                            + secondDossier.getEtatSante()
            );

            System.out.println("Historique :");
            for (String observation : secondDossier.getHistorique()) {
                System.out.println("- " + observation);
            }

            System.out.println(
                    "\n========== A7 : TEST DE SÉRIALISATION =========="
            );

            try {
                cabinet.ajouterPatient(
                        "Rex Junior",
                        "Omar",
                        "Berger Allemand",
                        new Chien(12)
                );

                System.out.println(
                        "Correction validée : Rex Junior a été ajouté."
                );

            } catch (RemoteException e) {
                System.err.println("Échec attendu lors de l'envoi de Chien.");

                Throwable cause = e;

                while (cause != null) {
                    System.err.println(
                            cause.getClass().getName()
                                    + " : "
                                    + cause.getMessage()
                    );

                    cause = cause.getCause();
                }
            }

            System.out.println();
            System.out.println(
                    "Appuyez sur Entrée pour compléter le cabinet jusqu'à 100 patients..."
            );

            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();

            int nombreActuel = cabinet.getPatients().size();

            for (int numero = nombreActuel + 1; numero <= 100; numero++) {
                cabinet.ajouterPatient(
                        "Patient" + numero,
                        "Maitre" + numero,
                        "Race test",
                        new Espece("Espèce test", 10)
                );
            }

            System.out.println(
                    "Nombre final de patients : "
                            + cabinet.getPatients().size()
            );

            System.out.println(
                    "Le client reste actif pour recevoir les alertes."
            );

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
            e.printStackTrace();
        }
    }
}