package animal.server;

import animal.common.IAnimal;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {
        try {
            Registry registry = LocateRegistry.createRegistry(1099);

            IAnimal animal = new AnimalImpl(
                    "Rex",
                    "Omar",
                    "Berger Allemand",
                    "Chien"
            );

            registry.rebind("Animal", animal);

            System.out.println("Serveur démarré.");
            System.out.println("Animal publié sous le nom : Animal");

        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e.getMessage());
            e.printStackTrace();
        }
    }
}