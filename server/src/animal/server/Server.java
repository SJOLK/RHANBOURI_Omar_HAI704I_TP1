package animal.server;

import animal.common.Espece;
import animal.common.IAnimal;
import animal.common.IDossierSuivi;
import animal.common.ICabinetVeterinaire;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.List;

public class Server {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);
            // Premier patient
            IDossierSuivi dossierRex =
                    new DossierSuiviImpl("Bonne santé");

            dossierRex.ajouterObservation(
                    "Première consultation : aucun problème détecté."
            );

            IAnimal rex = new AnimalImpl(
                    "Rex",
                    "Omar",
                    "Berger Allemand",
                    new Espece("Chien", 13),
                    dossierRex
            );

            // Deuxième patient
            IDossierSuivi dossierMimi =
                    new DossierSuiviImpl("Fatiguée");

            dossierMimi.ajouterObservation(
                    "Repos conseillé pendant quelques jours."
            );

            IAnimal mimi = new AnimalImpl(
                    "Mimi",
                    "Alice",
                    "Européen",
                    new Espece("Chat", 15),
                    dossierMimi
            );

            // Création du cabinet avec ses patients
            ICabinetVeterinaire cabinet =
                    new CabinetVeterinaireImpl(List.of(rex, mimi));

            // Seul le cabinet est publié
            registry.rebind("Cabinet", cabinet);

            System.out.println("Serveur démarré.");
            System.out.println("Cabinet publié sous le nom : Cabinet");
            System.out.println(
                    "Objets publiés dans le registre : "
                            + Arrays.toString(registry.list())
            );

        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}