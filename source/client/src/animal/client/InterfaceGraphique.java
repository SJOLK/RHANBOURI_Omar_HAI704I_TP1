package animal.client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.concurrent.ExecutionException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class InterfaceGraphique extends JFrame {

    private final LogiqueClient logiqueClient;

    private final DefaultTableModel modelePatients;
    private final JTable tablePatients;

    private final JTextField champPatientDossier;
    private final JTextField champEtatSante;
    private final JTextArea zoneHistorique;

    private final JTextField champNomClient;
    private final JTextArea zoneAlertes;

    public InterfaceGraphique(LogiqueClient logiqueClient) {
        this.logiqueClient = logiqueClient;

        modelePatients = new DefaultTableModel(
                new String[]{
                        "Nom",
                        "Maître",
                        "Race",
                        "Espèce"
                },
                0
        );

        tablePatients = new JTable(modelePatients);

        champPatientDossier = new JTextField(20);
        champEtatSante = new JTextField(20);
        zoneHistorique = new JTextArea();

        champNomClient = new JTextField(20);
        zoneAlertes = new JTextArea();

        initialiserFenetre();
        initialiserComposants();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                fermerApplication();
            }
        });
    }

    private void initialiserFenetre() {
        setTitle("Cabinet vétérinaire");
        setSize(1000, 650);
        setMinimumSize(new Dimension(800, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }

    private void initialiserComposants() {
        JTabbedPane onglets = new JTabbedPane();

        onglets.addTab("Patients", creerOngletPatients());
        onglets.addTab("Dossier de suivi", creerOngletDossier());
        onglets.addTab("Alertes", creerOngletAlertes());

        add(onglets, BorderLayout.CENTER);
    }

    private JPanel creerOngletPatients() {

        JPanel panneau = new JPanel(new BorderLayout(10, 10));
        panneau.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JPanel barreActions = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton boutonActualiser = new JButton("Actualiser");
        boutonActualiser.addActionListener(e -> chargerPatients());
        JButton boutonRechercher = new JButton("Rechercher");
        boutonRechercher.addActionListener(e -> rechercherPatient());
        JButton boutonAjouter = new JButton("Ajouter un patient");
        boutonAjouter.addActionListener(e -> afficherFormulaireAjout());

        barreActions.add(boutonActualiser);
        barreActions.add(boutonRechercher);
        barreActions.add(boutonAjouter);

        tablePatients.setFillsViewportHeight(true);
        tablePatients.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        panneau.add(barreActions, BorderLayout.NORTH);
        panneau.add(new JScrollPane(tablePatients), BorderLayout.CENTER);

        return panneau;
    }

    private JPanel creerOngletDossier() {
        JPanel panneau = new JPanel(new BorderLayout(10, 10));
        panneau.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JPanel formulaire = new JPanel(new GridLayout(3, 2, 10, 10));

        formulaire.add(new JLabel("Nom du patient :"));
        formulaire.add(champPatientDossier);

        formulaire.add(new JLabel("État de santé :"));
        formulaire.add(champEtatSante);

        JButton boutonConsulter = new JButton("Consulter");
        boutonConsulter.addActionListener(e -> consulterDossier());
        JButton boutonModifier = new JButton("Modifier");
        boutonModifier.addActionListener(e -> modifierDossier());

        formulaire.add(boutonConsulter);
        formulaire.add(boutonModifier);

        zoneHistorique.setEditable(false);
        zoneHistorique.setLineWrap(true);
        zoneHistorique.setWrapStyleWord(true);

        JPanel panneauHistorique = new JPanel(new BorderLayout(5, 5));
        panneauHistorique.setBorder(
                BorderFactory.createTitledBorder("Historique")
        );
        panneauHistorique.add(
                new JScrollPane(zoneHistorique),
                BorderLayout.CENTER
        );

        panneau.add(formulaire, BorderLayout.NORTH);
        panneau.add(panneauHistorique, BorderLayout.CENTER);

        return panneau;
    }

    private JPanel creerOngletAlertes() {
        JPanel panneau = new JPanel(new BorderLayout(10, 10));
        panneau.setBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        );

        JPanel formulaire = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton boutonAbonner = new JButton("S'abonner");
        JButton boutonDesabonner = new JButton("Se désabonner");
        JButton boutonTestSeuil = new JButton("Test seuil 100");
        boutonAbonner.addActionListener(e -> abonnerAlertes());
        boutonDesabonner.addActionListener(e -> desabonnerAlertes());
        boutonTestSeuil.addActionListener(
                e -> testerSeuil100(boutonTestSeuil)
        );

        formulaire.add(new JLabel("Nom du client :"));
        formulaire.add(champNomClient);
        formulaire.add(boutonAbonner);
        formulaire.add(boutonDesabonner);
        formulaire.add(boutonTestSeuil);

        zoneAlertes.setEditable(false);
        zoneAlertes.setLineWrap(true);
        zoneAlertes.setWrapStyleWord(true);

        JPanel panneauAlertes = new JPanel(new BorderLayout());
        panneauAlertes.setBorder(
                BorderFactory.createTitledBorder("Alertes reçues")
        );
        panneauAlertes.add(
                new JScrollPane(zoneAlertes),
                BorderLayout.CENTER
        );

        panneau.add(formulaire, BorderLayout.NORTH);
        panneau.add(panneauAlertes, BorderLayout.CENTER);

        return panneau;
    }

    private void chargerPatients() {
        modelePatients.setRowCount(0);

        new SwingWorker<
                java.util.List<LogiqueClient.PatientInfo>,
                Void
                >() {

            @Override
            protected java.util.List<LogiqueClient.PatientInfo>
            doInBackground() throws ErreurClientException {
                return logiqueClient.listerPatients();
            }

            @Override
            protected void done() {
                try {
                    for (LogiqueClient.PatientInfo patient : get()) {
                        modelePatients.addRow(new Object[]{
                                patient.nom(),
                                patient.nomMaitre(),
                                patient.race(),
                                patient.espece()
                        });
                    }

                } catch (Exception e) {
                    Throwable cause = e.getCause();

                    afficherErreur(
                            cause != null
                                    ? cause.getMessage()
                                    : e.getMessage()
                    );
                }
            }
        }.execute();
    }

    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog(
                this,
                message,
                "Erreur",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void rechercherPatient() {
        String nom = JOptionPane.showInputDialog(
                this,
                "Nom du patient :",
                "Rechercher un patient",
                JOptionPane.QUESTION_MESSAGE
        );

        // L'utilisateur a fermé la boîte de dialogue.
        if (nom == null) {
            return;
        }

        new SwingWorker<LogiqueClient.PatientInfo, Void>() {

            @Override
            protected LogiqueClient.PatientInfo doInBackground()
                    throws ErreurClientException {

                return logiqueClient.rechercherPatient(nom);
            }

            @Override
            protected void done() {
                try {
                    LogiqueClient.PatientInfo patient = get();

                    if (patient == null) {
                        JOptionPane.showMessageDialog(
                                InterfaceGraphique.this,
                                "Aucun patient nommé "
                                        + nom.trim()
                                        + " n'a été trouvé.",
                                "Patient introuvable",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                        return;
                    }

                    afficherPatient(patient);

                } catch (Exception e) {
                    Throwable cause = e.getCause();

                    afficherErreur(
                            cause != null
                                    ? cause.getMessage()
                                    : e.getMessage()
                    );
                }
            }
        }.execute();
    }

    private void afficherPatient(
            LogiqueClient.PatientInfo patient
    ) {
        String message =
                "Nom : " + patient.nom()
                        + "\nMaître : " + patient.nomMaitre()
                        + "\nRace : " + patient.race()
                        + "\nEspèce : " + patient.espece();

        JOptionPane.showMessageDialog(
                this,
                message,
                "Patient trouvé",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void afficherFormulaireAjout() {
        JTextField champNom = new JTextField();
        JTextField champMaitre = new JTextField();
        JTextField champRace = new JTextField();
        JTextField champEspece = new JTextField();

        JSpinner champEsperanceVie = new JSpinner(
                new SpinnerNumberModel(
                        1,      // valeur initiale
                        1,      // minimum
                        200,    // maximum
                        1       // pas
                )
        );

        JPanel formulaire = new JPanel(
                new GridLayout(5, 2, 10, 10)
        );

        formulaire.add(new JLabel("Nom du patient :"));
        formulaire.add(champNom);

        formulaire.add(new JLabel("Nom du maître :"));
        formulaire.add(champMaitre);

        formulaire.add(new JLabel("Race :"));
        formulaire.add(champRace);

        formulaire.add(new JLabel("Nom de l'espèce :"));
        formulaire.add(champEspece);

        formulaire.add(new JLabel("Espérance de vie :"));
        formulaire.add(champEsperanceVie);

        int resultat = JOptionPane.showConfirmDialog(
                this,
                formulaire,
                "Ajouter un patient",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (resultat != JOptionPane.OK_OPTION) {
            return;
        }

        int esperanceVie =
                (Integer) champEsperanceVie.getValue();

        ajouterPatient(
                champNom.getText(),
                champMaitre.getText(),
                champRace.getText(),
                champEspece.getText(),
                esperanceVie
        );
    }

    private void ajouterPatient(
            String nom,
            String nomMaitre,
            String race,
            String nomEspece,
            int esperanceVie
    ) {
        new SwingWorker<LogiqueClient.PatientInfo, Void>() {

            @Override
            protected LogiqueClient.PatientInfo doInBackground()
                    throws ErreurClientException {

                return logiqueClient.ajouterPatient(
                        nom,
                        nomMaitre,
                        race,
                        nomEspece,
                        esperanceVie
                );
            }

            @Override
            protected void done() {
                try {
                    LogiqueClient.PatientInfo patient = get();

                    JOptionPane.showMessageDialog(
                            InterfaceGraphique.this,
                            "Patient correctement ajouté :\n\n"
                                    + "Nom : " + patient.nom()
                                    + "\nMaître : " + patient.nomMaitre()
                                    + "\nRace : " + patient.race()
                                    + "\nEspèce : " + patient.espece(),
                            "Ajout réussi",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    // Recharge automatiquement la table.
                    chargerPatients();

                } catch (Exception e) {
                    Throwable cause = e.getCause();

                    afficherErreur(
                            cause != null
                                    ? cause.getMessage()
                                    : e.getMessage()
                    );
                }
            }
        }.execute();
    }

    private void consulterDossier() {
        String nomPatient = champPatientDossier.getText();

        new SwingWorker<LogiqueClient.DossierInfo, Void>() {

            @Override
            protected LogiqueClient.DossierInfo doInBackground()
                    throws ErreurClientException {

                return logiqueClient.consulterDossier(nomPatient);
            }

            @Override
            protected void done() {
                try {
                    LogiqueClient.DossierInfo dossier = get();

                    champEtatSante.setText(dossier.etatSante());

                    StringBuilder historique = new StringBuilder();

                    for (String observation : dossier.historique()) {
                        historique.append("- ")
                                .append(observation)
                                .append(System.lineSeparator());
                    }

                    zoneHistorique.setText(historique.toString());
                    zoneHistorique.setCaretPosition(0);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    afficherErreur("La consultation du dossier a été interrompue.");

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();

                    afficherErreur(
                            cause != null && cause.getMessage() != null
                                    ? cause.getMessage()
                                    : "Impossible de consulter le dossier."
                    );
                }
            }
        }.execute();
    }

    private void modifierDossier() {
        String nomPatient = champPatientDossier.getText();
        String nouvelEtat = champEtatSante.getText();

        if (nomPatient == null || nomPatient.isBlank()) {
            afficherErreur("Saisissez le nom du patient.");
            return;
        }

        if (nouvelEtat == null || nouvelEtat.isBlank()) {
            afficherErreur("Saisissez le nouvel état de santé.");
            return;
        }

        String observation = JOptionPane.showInputDialog(
                this,
                "Nouvelle observation (facultative) :",
                "Modifier le dossier",
                JOptionPane.QUESTION_MESSAGE
        );

        // L'utilisateur a cliqué sur Annuler.
        if (observation == null) {
            return;
        }

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground()
                    throws ErreurClientException {

                logiqueClient.mettreAJourDossier(
                        nomPatient,
                        nouvelEtat,
                        observation
                );

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();

                    JOptionPane.showMessageDialog(
                            InterfaceGraphique.this,
                            "Le dossier de " + nomPatient + " a été mis à jour.",
                            "Modification réussie",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    // Recharge le dossier pour afficher les nouvelles données.
                    consulterDossier();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    afficherErreur("La modification a été interrompue.");

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();

                    afficherErreur(
                            cause != null && cause.getMessage() != null
                                    ? cause.getMessage()
                                    : "Impossible de modifier le dossier."
                    );
                }
            }
        }.execute();
    }

    private void afficherAlerte(String message) {
        SwingUtilities.invokeLater(() -> {
            zoneAlertes.append(message);
            zoneAlertes.append(System.lineSeparator());
            zoneAlertes.append(System.lineSeparator());

            zoneAlertes.setCaretPosition(
                    zoneAlertes.getDocument().getLength()
            );
        });
    }

    private void abonnerAlertes() {
        String nomClient = champNomClient.getText();

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground()
                    throws ErreurClientException {

                logiqueClient.abonner(
                        nomClient,
                        InterfaceGraphique.this::afficherAlerte
                );

                return null;
            }

            @Override
            protected void done() {
                try {
                    get();

                    zoneAlertes.append(
                            "Abonnement aux alertes effectué."
                                    + System.lineSeparator()
                                    + System.lineSeparator()
                    );

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    afficherErreur("L'abonnement a été interrompu.");

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();

                    afficherErreur(
                            cause != null && cause.getMessage() != null
                                    ? cause.getMessage()
                                    : "Impossible de s'abonner aux alertes."
                    );
                }
            }
        }.execute();
    }

    private void desabonnerAlertes() {
        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground()
                    throws ErreurClientException {

                logiqueClient.desabonner();
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();

                    zoneAlertes.append(
                            "Désabonnement des alertes effectué."
                                    + System.lineSeparator()
                                    + System.lineSeparator()
                    );

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    afficherErreur("Le désabonnement a été interrompu.");

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();

                    afficherErreur(
                            cause != null && cause.getMessage() != null
                                    ? cause.getMessage()
                                    : "Impossible de se désabonner des alertes."
                    );
                }
            }
        }.execute();
    }
    private void fermerApplication() {
        setEnabled(false);

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() {
                logiqueClient.fermer();
                return null;
            }

            @Override
            protected void done() {
                dispose();
                System.exit(0);
            }
        }.execute();
    }

    private void testerSeuil100(JButton boutonTest) {
        if (!logiqueClient.estAbonne()) {
            afficherErreur(
                    "Abonnez-vous aux alertes avant de lancer le test."
            );
            return;
        }

        boutonTest.setEnabled(false);

        zoneAlertes.append(
                "Ajout de patients jusqu'au seuil de 100..."
                        + System.lineSeparator()
        );

        new SwingWorker<Integer, Void>() {

            @Override
            protected Integer doInBackground()
                    throws ErreurClientException {

                int nombrePatients =
                        logiqueClient.listerPatients().size();

                while (nombrePatients < 100) {
                    int numero = nombrePatients + 1;

                    logiqueClient.ajouterPatient(
                            "PatientGUI" + numero,
                            "Maître test",
                            "Race test",
                            "Espèce test",
                            10
                    );

                    nombrePatients++;
                }

                return nombrePatients;
            }

            @Override
            protected void done() {
                boutonTest.setEnabled(true);

                try {
                    int nombreFinal = get();

                    zoneAlertes.append(
                            "Test terminé. Nombre de patients : "
                                    + nombreFinal
                                    + System.lineSeparator()
                                    + System.lineSeparator()
                    );

                    chargerPatients();

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    afficherErreur("Le test du seuil a été interrompu.");

                } catch (ExecutionException e) {
                    Throwable cause = e.getCause();

                    afficherErreur(
                            cause != null && cause.getMessage() != null
                                    ? cause.getMessage()
                                    : "Impossible d'effectuer le test du seuil."
                    );
                }
            }
        }.execute();
    }
}