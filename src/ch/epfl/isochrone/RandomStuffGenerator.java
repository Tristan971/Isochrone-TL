package ch.epfl.isochrone;

import ch.epfl.isochrone.geo.PointOSM;
import ch.epfl.isochrone.geo.PointWGS84;
import ch.epfl.isochrone.tiledmap.*;
import ch.epfl.isochrone.timetable.Date;
import ch.epfl.isochrone.timetable.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static ch.epfl.isochrone.math.Math.modF;

/**
 * Aide aux tests par randomisation des éléments créés (histoire de gagner du temps de glandage)
 * ÇA C'EST LE SWAG
 * @author Tristan Deloche (234045)
 */

//TODO : improve it by any means (since it's cool I think)

public class RandomStuffGenerator {
    private static yoloRandomIsVeryRandom random = new yoloRandomIsVeryRandom();

    /**
     * On génère une chaîne de 5 caractères minuscules dans {a,b,...,y,z}
     */
    public static String generateName() {
        StringBuilder randomStringBuilder = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            randomStringBuilder.append(random.nextChar());
        }

        return randomStringBuilder.toString();
    }

    /**
     * On génère un point OSM. 1 <= zoom <= 19. x < 2^z+8 (idem y). x et y positifs.
     */
    public static PointOSM generatePointOSM() {
        int zoom;
        return new PointOSM(zoom = 1+random.nextInt()%19, random.nextDouble()% java.lang.Math.pow(2, zoom + 8), random.nextDouble()% java.lang.Math.pow(2, zoom + 8));
    }

    /**
     * yolo
     */
    public static PointWGS84 generatePointWGS84() {
        return generatePointOSM().toWGS84();
    }

    /**
     * On fais un jour en accord avec mois et année. Obligés de copypaste daysinmonth et bissextile ici car privés dans date (et obligatoirement privés)
     */
    public static Date generateDate() {
        int month = 1+random.nextInt()%13;

        int year = random.nextSignedInt();

        return new Date(1+random.nextInt()%getNumberOfDaysForMonth(month, year), month, year);
    }

    /**
     * VOIR PLUS HAUT
     */
    public static int getNumberOfDaysForMonth(int month, int year) {
        switch (month) {
        case 1:
            return 31;
        case 2:
            return isBissextile(year) ? 29 : 28;
        case 3:
            return 31;
        case 4:
            return 30;
        case 5:
            return 31;
        case 6:
            return 30;
        case 7:
            return 31;
        case 8:
            return 31;
        case 9:
            return 30;
        case 10:
            return 31;
        case 11:
            return 30;
        case 12:
            return 31;
        default:
            throw new IllegalArgumentException("Month int out of range in getNumberDaysForMonth(month, year)");
        }
    }

    /**
     * Achète un cerveau
     */
    public static boolean isBissextile(int year) {
        return ((modF(year, 4) == 0 && modF(year, 100) != 0) || (modF(year, 400) == 0));
    }

    /**
     * On génère un service. On crée la startingdate. On crée une date relative après (nextint modifié pour être positif).
     * On randomize sur 7 éléments booléens pour les operating days (fuck the police)
     * On ajoute aussi des included dans cet intervalle. On ajoute des excluded tant qu'ils ne sont pas included.
     * On crée notre service.
     */
    public static Service generateService() {
        Date startingDate = generateDate();
        Date endingDate = startingDate.relative(random.nextInt());

        Set<Date.DayOfWeek> opDays = new HashSet<>();
        for (int i = 0; i <= 6; i++) {
            if (random.nextBoolean()) {
                opDays.add(Date.DayOfWeek.values()[i]);
            }
        }

        Set<Date> inclDates = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            if (random.nextInt() % 2 == 0) {
                inclDates.add(startingDate.relative(random.nextInt()%(endingDate.getIntValue() - startingDate.getIntValue())));
            }
        }

        Set<Date> exclDates = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            if (random.nextInt() % 2 == 0) {
                Date a = startingDate.relative(random.nextInt() % (endingDate.getIntValue() - startingDate.getIntValue()));
                if (!inclDates.contains(a)) {
                    exclDates.add(a);
                }
            }
        }

        return new Service(generateName(), startingDate, endingDate, opDays, inclDates, exclDates);
    }

    /**
     * bref...
     */
    public static Stop generateStop() {
        return new Stop(generateName(), generatePointWGS84());
    }

    /**
     * Si generateGraph() marche, generateFPT() aussi. (on fait le graphe, on fait le fpt sur les stops en en choisissant un de départ au hasard.
     * De même l'heure de départ est au hasard dans [1;SPM.Infinite]; (voir mon randomiseur custom!)
     */
    public static FastestPathTree generateFastestPathTree() {
        Graph theGraph = generateGraph();
        Set<Stop> possibleSSTOPS = theGraph.getStops();
        return theGraph.fastestPaths((Stop) possibleSSTOPS.toArray()[random.nextInt() % possibleSSTOPS.size()], random.nextSPM());
    }

    /**
     * J'ai pas dormi depuis 32 heures maintenant. Indulgence svp.
     * En gros on fait un set randomisé de stops randomisés. (on en veut pas plus de 100 pour pas que ce soit trop lourd d'où
     * la valeur max de i choisie (i<10^2)
     *
     * On crée un Graph.BUILDER (schinz vient de mouiller son pantalon là) et on ajoute des tripedge au hasard dedans en respectant arrivée > départ.
     * Je pense qu'on doit avoir plus de conditions ici mais plus la force de réfléchir ^^;
     *
     * On add les walkedges (balélec ça ça se randomise très bien). On part du principe que marcher plus d'une heure est idiot (zoubi andré) et
     * que au-delà de 15km/h on est plus en train de marcher mais plutôt de courir. (d'où le 3600 et le 15)
     *
     * On build (obviously)
     */
    public static Graph generateGraph() {
        Set<Stop> stopSet = new HashSet<>();

        for (int i = 0; i < random.nextInt() % java.lang.Math.pow(10, 2); i++) {
            stopSet.add(generateStop());
        }

        Graph.Builder gB = new Graph.Builder(stopSet);

        for (Stop aStop : stopSet) {
            for (Stop anotherStop : stopSet) {
                int i;
                gB.addTripEdge(aStop, anotherStop, i = random.nextSPM(), i + random.nextInt() % (SecondsPastMidnight.INFINITE));
            }
        }

        gB.addAllWalkEdges(random.nextInt()%3600, random.nextInt()%15);

        return gB.build();
    }

    /**
     * On fait des stops dans un set. (environ 50 car rand de %2==0 sur 100 random integers nous donne 50/50 oui/non
     * sur chaque essai donc en moyenne 50 stops ajoutés ; ça me parait ni trop peu ni trop)
     *
     * On fait une liste de service (là aussi je pense que ça marche pas car on aura pas les bons stops ; j'y réfléchirai plus tard)
     *
     * On envoie le pâté au constructeur et walla
     */
    public static TimeTable generateTimeTable() {
        Set<Stop> stopSet = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            if (random.nextInt() % 2 == 0) {
                stopSet.add(generateStop());
            }
        }

        List<Service> serviceList = new LinkedList<>();
        for (int i = 0; i < 20; i++) {
            if (random.nextInt() % 2 == 0) {
                serviceList.add(generateService());
            }
        }

        return new TimeTable(stopSet, serviceList);
    }

    /**
     * no comment
     */
    public static CachedTileProvider generateCachedTileProvider() {
        return new CachedTileProvider(generateOSMTileProvider());
    }

    /**
     * On crée un nombre < 10 de tranches de couleurs (ça me parait bien) et on génère une map avec des couleurs différentes et randomisées par
     * intervalle de 10 minutes (voir étape 9 du projet).
     * À l'usage les couleurs randoms ça doit arracher les yeux bien comme il faut~
     */
    public static ColorTable generateColorTable() {
        int i = 10;

       LinkedList<Color> integerColorList = new LinkedList<>();

        for (int j = 0; j < i; j++) {
            integerColorList.add(random.nextColor());
        }

        return new ColorTable(5+random.nextSPM()%6, integerColorList);
    }

    /**
     * On fait un ITP. J'ai pas fini de coder la classe donc ça sera probablement à corriger plus tard
     *
     * @throws IOException
     *      Le vendredi de pleine lune et/ou quand java a ses règles...
     */
    public static IsochroneTileProvider generateIsochroneTileProvider() throws IOException {
        return new IsochroneTileProvider(generateFastestPathTree(), generateColorTable(), random.nextInt()%20);
    }

    /**
     * Suis dans le train donc pas les 2 urls des serveurs osm (a.trucmuche et b.trucmuche) donc à changer dès que j'ai du réseau et dormi
     */
    public static OSMTileProvider generateOSMTileProvider() {
        if (random.nextInt() % 2 == 0) {
            return new OSMTileProvider("YOLO.COM");
        } else {
            return new OSMTileProvider("YOLO.FR");
        }
    }

    /**
     * On crée une tile avec une buffered image vide (à corriger plsu tard je pense qu'on en aura jamais besoin car on utilise que des sous-classes
     * de Tile qui est quasiment une interface mais bon...
     * bref on fait zoom random sur [1;19] puis des x et y qui respectent positifs et < 2^(zoom+8)
     */
    public static Tile generateTile() {
        int zoom;
        return new Tile(zoom = random.nextZoom(), random.nextLongitude(zoom), random.nextLatitude(zoom), new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB));
    }

    /**
     * On fait un tilecache que l'on remplit de tiles manuellement au final (d'où l'inutilité de mon générateur de tiles m'enfin...)
     * à adapter selon votre implémentation du triplet de valeurs
     */
    public static TileCache generateTileCache() {
        TileCache a = new TileCache();

        for (int i = 0; i < 100; i++) {
            int j;
            a.put(j = random.nextZoom(), random.nextLongitude(j), random.nextLatitude(j), generateTile());
        }

        return a;
    }

    /**
     * On randomise sur [0;1[ (oui oui j'aimerais avoir le 1 aussi mais je sais pas trop comment faire) la valeur
     * du canal alpha voulu. Puis on crée un TTP avec.
     */
    public static TransparentTileProvider generateTransparentTileProvider() {
        return new TransparentTileProvider(random.nextDouble()%1.0);
    }

    /**
     * DODOOOOOOOOO zZzzzZzzzz(>_<)
     */
}
