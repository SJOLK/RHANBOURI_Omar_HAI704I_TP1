package animal.client;

import animal.common.Espece;
import animal.common.IAnimal;

import java.lang.reflect.Proxy;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public static void main(String[] args) {
        try {
            // Connexion au registre RMI lancé sur la machine locale
            Registry registry = LocateRegistry.getRegistry("localhost", 1099);

            // Récupération de l'objet distant publié sous le nom "Animal"
            IAnimal animal = (IAnimal) registry.lookup("Animal");

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

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
            e.printStackTrace();
        }
    }
}