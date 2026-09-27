package animal.client;

import java.util.List;
import java.util.Scanner;

public class InterfaceConsole {

    private final LogiqueClient logique;
    private final Scanner scanner;

    public InterfaceConsole(LogiqueClient logique) {
        this.logique = logique;
        this.scanner = new Scanner(System.in);
    }

    public void demarrer() {
        boolean continuer = true;

        System.out.println(
                "Bienvenue dans le cabinet vétérinaire."
        );

        while (continuer) {
            afficherMenu();

            String choix = scanner.nextLine().trim();

            try {
                switch (choix) {
                    case "1" -> afficherPatients();
                    case "2" -> rechercherPatient();
                    case "3" -> ajouterPatient();
                    case "4" -> consulterDossier();
                    case "5" -> modifierDossier();
                    case "6" -> abonnerAlertes();
                    case "7" -> desabonnerAlertes();
                    case "0" -> continuer = false;

                    default -> System.out.println(
                            "Choix invalide. Veuillez réessayer."
                    );
                }

            } catch (ErreurClientException e) {
                System.out.println(
                        "Erreur : " + e.getMessage()
                );
            }
        }

        System.out.println("Fermeture du client.");
    }

    private void afficherMenu() {
        System.out.println();
        System.out.println(
                "========== CABINET VÉTÉRINAIRE =========="
        );
        System.out.println("1. Afficher les patients");
        System.out.println("2. Rechercher un patient");
        System.out.println("3. Enregistrer un patient");
        System.out.println("4. Consulter un dossier de suivi");
        System.out.println("5. Modifier un dossier de suivi");
        System.out.println("6. S'abonner aux alertes");
        System.out.println("7. Se désabonner des alertes");
        System.out.println("0. Quitter");
        System.out.print("Votre choix : ");
    }

    private void consulterDossier()
            throws ErreurClientException {

        System.out.print("Nom du patient : ");
        String nomPatient = scanner.nextLine();

        LogiqueClient.DossierInfo dossier =
                logique.consulterDossier(nomPatient);

        System.out.println();
        System.out.println(
                "========== DOSSIER DE SUIVI =========="
        );
        System.out.println(
                "État de santé : " + dossier.etatSante()
        );

        System.out.println("Historique :");

        if (dossier.historique().isEmpty()) {
            System.out.println("- Aucune observation.");
            return;
        }

        for (String observation : dossier.historique()) {
            System.out.println("- " + observation);
        }
    }

    private void modifierDossier()
            throws ErreurClientException {

        System.out.print("Nom du patient : ");
        String nomPatient = scanner.nextLine();

        System.out.print("Nouvel état de santé : ");
        String nouvelEtat = scanner.nextLine();

        System.out.print(
                "Nouvelle observation "
                        + "(laissez vide pour ne rien ajouter) : "
        );
        String observation = scanner.nextLine();

        logique.mettreAJourDossier(
                nomPatient,
                nouvelEtat,
                observation
        );

        System.out.println(
                "Le dossier de "
                        + nomPatient.trim()
                        + " a été mis à jour."
        );
    }

    private void abonnerAlertes()
            throws ErreurClientException {

        if (logique.estAbonne()) {
            System.out.println(
                    "Ce client est déjà abonné aux alertes."
            );
            return;
        }

        System.out.print(
                "Nom à afficher dans les alertes : "
        );
        String nomClient = scanner.nextLine();

        logique.abonner(nomClient);

        System.out.println(
                "Abonnement aux alertes effectué."
        );
    }

    private void desabonnerAlertes()
            throws ErreurClientException {

        if (!logique.estAbonne()) {
            System.out.println(
                    "Ce client n'est pas abonné aux alertes."
            );
            return;
        }

        logique.desabonner();

        System.out.println(
                "Désabonnement des alertes effectué."
        );
    }

    private void afficherPatients()
            throws ErreurClientException {

        List<LogiqueClient.PatientInfo> patients =
                logique.listerPatients();

        System.out.println();
        System.out.println("========== PATIENTS ==========");

        if (patients.isEmpty()) {
            System.out.println("Aucun patient enregistré.");
            return;
        }

        for (LogiqueClient.PatientInfo patient : patients) {
            afficherPatient(patient);
        }
    }

    private void ajouterPatient()
            throws ErreurClientException {

        System.out.println();
        System.out.println(
                "========== NOUVEAU PATIENT =========="
        );

        System.out.print("Nom du patient : ");
        String nom = scanner.nextLine();

        System.out.print("Nom du maître : ");
        String nomMaitre = scanner.nextLine();

        System.out.print("Race : ");
        String race = scanner.nextLine();

        System.out.print("Nom de l'espèce : ");
        String espece = scanner.nextLine();

        int esperanceVie = lireEntierPositif(
                "Espérance de vie moyenne : "
        );

        LogiqueClient.PatientInfo patient =
                logique.ajouterPatient(
                        nom,
                        nomMaitre,
                        race,
                        espece,
                        esperanceVie
                );

        System.out.println("Patient correctement ajouté :");
        afficherPatient(patient);
    }

    private int lireEntierPositif(String message) {
        while (true) {
            System.out.print(message);
            String saisie = scanner.nextLine().trim();

            try {
                int valeur = Integer.parseInt(saisie);

                if (valeur > 0) {
                    return valeur;
                }

                System.out.println(
                        "Veuillez saisir un entier strictement positif."
                );

            } catch (NumberFormatException e) {
                System.out.println(
                        "Saisie invalide : veuillez entrer un nombre entier."
                );
            }
        }
    }

    private void rechercherPatient()
            throws ErreurClientException {

        System.out.print("Nom du patient : ");
        String nom = scanner.nextLine();

        LogiqueClient.PatientInfo patient =
                logique.rechercherPatient(nom);

        if (patient == null) {
            System.out.println("Patient introuvable.");
            return;
        }

        System.out.println("Patient trouvé :");
        afficherPatient(patient);
    }

    private void afficherPatient(
            LogiqueClient.PatientInfo patient
    ) {
        System.out.println(
                "- " + patient.nom()
                        + " | Maître : " + patient.nomMaitre()
                        + " | Race : " + patient.race()
                        + " | Espèce : " + patient.espece()
        );
    }
}