package ch.epfl.isochrone;

import java.awt.*;
import java.util.Random;

/**
 * BECAUSE YOWLOW
 * @author Tristan Deloche (234045)
 */
public class yoloRandomIsVeryRandom extends Random {
    private Random random;
    private String Alphabet = "az";

    public yoloRandomIsVeryRandom() {
        this.random = new Random();
    }

    /**
     * On a besoin quasiment que de naturels, donc fuck les relatifs
     */
    @Override
    public int nextInt() {
        return Math.abs(random.nextInt());
    }

    /**
     * Pour les rares cas où on veut aussi avoir des négatifs des fois (genre l'année d'une Date)
     */
    public int nextSignedInt() {
        return random.nextInt();
    }

    /**
     * Plutôt que de chaque fois copier-coller ce %SPM.Midnight autant en faire une fonction
     */
    public int nextSPM() {
        return this.nextInt() % 107999;
    }

    /**
     * Idem que nextSPM()
     */
    public int nextZoom() {
        return this.nextInt() % 20;
    }

    /**
     * Idem nextZoom() et nextSPM()
     */
    public int nextLatitude(int zoom) {
        return this.nextInt()% (int) Math.pow(2,zoom);
    }

    /**
     * Useless techniquement mais rend plus clair le code on va dire? (sinon idem nextLatitude())
     */
    public int nextLongitude(int zoom) {
        return this.nextInt() % (int) Math.pow(2,zoom);
    }

    /**
     * Crée une couleur en randomisant R, G et B sur [0;256]
     */
    public Color nextColor() {
        return new Color(random.nextInt()%257, random.nextInt()%257, random.nextInt()%257);
    }

    /**
     * Se sert de l'immonde système de cast de java pour générer des lettres minuscules aléatoirement
     * (en gros on veut un numéro de lettre Unicode inférieur à a-z. Puis on lui additionne celui de a (32 de mémoire, ou un truc du genre) et donc
     * en y additionnant le numéro randomisé on choppe une lettre dans le bon intervalle.
     * Possible de d'ajouter les majuscules avec un petit if. Demain... peut-être... ou pas... :p
     */
    public char nextChar() {
        return (char) (random.nextInt()%(int)Alphabet.charAt(1) - (int)Alphabet.charAt(0) + (int)Alphabet.charAt(1));
    }
}
