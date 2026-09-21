package animal.client;

import animal.common.Espece;
import animal.common.IAnimal;
import animal.common.IDossierSuivi;
import animal.common.ICabinetVeterinaire;

import java.lang.reflect.Proxy;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class Client {

    public static void main(String[] args) {
        try {
            // Connexion au registre RMI lancé sur la machine locale
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            ICabinetVeterinaire cabinet =
                    (ICabinetVeterinaire) registry.lookup("Cabinet");

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

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
            e.printStackTrace();
        }
    }
}