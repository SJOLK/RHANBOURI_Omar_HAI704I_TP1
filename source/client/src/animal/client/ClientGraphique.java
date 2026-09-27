package animal.client;

import animal.common.ICabinetVeterinaire;

import javax.swing.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class ClientGraphique {

    public static void main(String[] args) {
        String hote = args.length >= 1
                ? args[0]
                : "localhost";

        int port;

        try {
            port = args.length >= 2
                    ? Integer.parseInt(args[1])
                    : 1099;

        } catch (NumberFormatException e) {
            afficherErreur("Le port doit être un nombre entier.");
            return;
        }

        try {
            Registry registre =
                    LocateRegistry.getRegistry(hote, port);

            ICabinetVeterinaire cabinet =
                    (ICabinetVeterinaire) registre.lookup("Cabinet");

            LogiqueClient logiqueClient =
                    new LogiqueClient(cabinet);

            SwingUtilities.invokeLater(() -> {
                InterfaceGraphique fenetre =
                        new InterfaceGraphique(logiqueClient);

                fenetre.setVisible(true);
            });

        } catch (Exception e) {
            afficherErreur(
                    "Impossible de joindre le serveur RMI sur "
                            + hote
                            + ":"
                            + port
                            + "."
            );
        }
    }

    private static void afficherErreur(String message) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(
                        null,
                        message,
                        "Erreur de connexion",
                        JOptionPane.ERROR_MESSAGE
                )
        );
    }
}