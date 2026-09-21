package animal.client;

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
            System.out.println("Espèce : " + animal.getEspece());

            // Vérification que l'objet reçu est un proxy RMI
            System.out.println("Classe reçue : " + animal.getClass().getName());
            System.out.println(
                    "Est un proxy : "
                            + Proxy.isProxyClass(animal.getClass())
            );

        } catch (Exception e) {
            System.err.println("Erreur client : " + e.getMessage());
            e.printStackTrace();
        }
    }
}