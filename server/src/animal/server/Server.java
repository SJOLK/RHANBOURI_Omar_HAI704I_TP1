package animal.server;

import animal.common.Espece;
import animal.common.IAnimal;
import animal.common.IDossierSuivi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);

            Espece espece = new Espece("Chien", 13);

            IDossierSuivi dossier =
                    new DossierSuiviImpl("Bonne santé");

            dossier.ajouterObservation(
                    "Première consultation : aucun problème détecté."
            );

            IAnimal animal = new AnimalImpl(
                    "Rex",
                    "Omar",
                    "Berger Allemand",
                    espece,
                    dossier
            );

            registry.rebind("Animal", animal);

            System.out.println("Serveur démarré.");
            System.out.println("Animal publié sous le nom : Animal");
            System.out.println(
                    "Identité initiale de l'espèce côté serveur : "
                            + System.identityHashCode(espece)
            );

        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}