package animal.common;

import java.io.Serializable;

public class Espece implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nom;
    private int esperanceVieMoyenne;

    public Espece(String nom, int esperanceVieMoyenne) {
        this.nom = nom;
        this.esperanceVieMoyenne = esperanceVieMoyenne;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getEsperanceVieMoyenne() {
        return esperanceVieMoyenne;
    }

    public void setEsperanceVieMoyenne(int esperanceVieMoyenne) {
        this.esperanceVieMoyenne = esperanceVieMoyenne;
    }

    @Override
    public String toString() {
        return nom + " (espérance de vie moyenne : "
                + esperanceVieMoyenne + " ans)";
    }
}