package animal.client;

import animal.common.IObservateurCabinet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Objects;
import java.util.function.Consumer;

public class ObservateurCabinetImpl
        extends UnicastRemoteObject
        implements IObservateurCabinet {

    private final String nomClient;
    private final Consumer<String> gestionnaireAlerte;

    /*
     * Constructeur utilisé par le client console.
     * Les alertes continuent à être affichées dans la console.
     */
    public ObservateurCabinetImpl(String nomClient)
            throws RemoteException {

        this(nomClient, System.out::println);
    }

    /*
     * Constructeur utilisé par l'interface graphique.
     */
    public ObservateurCabinetImpl(
            String nomClient,
            Consumer<String> gestionnaireAlerte
    ) throws RemoteException {

        super();

        this.nomClient = nomClient;
        this.gestionnaireAlerte =
                Objects.requireNonNull(gestionnaireAlerte);
    }

    @Override
    public void notifierFranchissementSeuil(
            int seuil,
            int ancienNombrePatients,
            int nouveauNombrePatients
    ) throws RemoteException {

        String direction =
                nouveauNombrePatients > ancienNombrePatients
                        ? "à la hausse"
                        : "à la baisse";

        String message =
                "========== ALERTE CABINET =========="
                        + System.lineSeparator()
                        + "Client concerné : " + nomClient
                        + System.lineSeparator()
                        + "Seuil de " + seuil
                        + " patients franchi " + direction
                        + System.lineSeparator()
                        + "Nombre de patients : "
                        + ancienNombrePatients
                        + " -> "
                        + nouveauNombrePatients
                        + System.lineSeparator()
                        + "====================================";

        gestionnaireAlerte.accept(message);
    }
}