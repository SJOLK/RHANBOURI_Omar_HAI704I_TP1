package animal.client;

import animal.common.ICabinetVeterinaire;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public static void main(String[] args) {

        String hote =
                args.length >= 1
                        ? args[0]
                        : "localhost";

        int port = 1099;

        if (args.length >= 2) {
            try {
                port = Integer.parseInt(args[1]);

                if (port < 1 || port > 65535) {
                    System.err.println(
                            "Le port doit être compris entre 1 et 65535."
                    );
                    return;
                }

            } catch (NumberFormatException e) {
                System.err.println(
                        "Le port doit être un nombre entier."
                );
                return;
            }
        }

        LogiqueClient logique = null;

        try {
            Registry registry =
                    LocateRegistry.getRegistry(hote, port);

            ICabinetVeterinaire cabinet =
                    (ICabinetVeterinaire)
                            registry.lookup("Cabinet");

            logique = new LogiqueClient(cabinet);

            System.out.println(
                    "Connexion au cabinet réussie : "
                            + hote
                            + ":"
                            + port
            );

            InterfaceConsole interfaceConsole =
                    new InterfaceConsole(logique);

            interfaceConsole.demarrer();

        } catch (NotBoundException e) {
            System.err.println(
                    "Le service « Cabinet » n'est pas publié "
                            + "dans le registre RMI."
            );

        } catch (RemoteException e) {
            System.err.println(
                    "Impossible de joindre le serveur RMI sur "
                            + hote
                            + ":"
                            + port
                            + "."
            );
            System.err.println(
                    "Vérifiez que le serveur est démarré."
            );

        } catch (ClassCastException e) {
            System.err.println(
                    "L'objet « Cabinet » publié dans le registre "
                            + "n'utilise pas l'interface attendue."
            );

        } finally {
            if (logique != null) {
                logique.fermer();
            }
        }
    }
}