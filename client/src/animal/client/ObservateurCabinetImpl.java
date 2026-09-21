package animal.client;

import animal.common.IObservateurCabinet;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ObservateurCabinetImpl
        extends UnicastRemoteObject
        implements IObservateurCabinet {

    private final String nomClient;

    public ObservateurCabinetImpl(String nomClient)
            throws RemoteException {
        super();
        this.nomClient = nomClient;
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

        System.out.println();
        System.out.println("========== ALERTE CABINET ==========");
        System.out.println("Client concerné : " + nomClient);
        System.out.println(
                "Seuil de " + seuil
                        + " patients franchi " + direction
        );
        System.out.println(
                "Nombre de patients : "
                        + ancienNombrePatients
                        + " -> "
                        + nouveauNombrePatients
        );
        System.out.println("====================================");
    }
}