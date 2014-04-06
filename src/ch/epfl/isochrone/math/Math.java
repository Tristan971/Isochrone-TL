package ch.epfl.isochrone.math;

final public class Math {

    /**
     * Constructeur privé vide. Ne doit pas être utilisé.
     */
    private Math() {}

    /**
     * Fonction arcsinus hyperbolique
     * @param x
     *      Antécédent dont on cherche à calculer l'image par arcsinh
     * @return L'arcsinus hyperbolique de "x"
     */
    public static double asinh(double x){
        return java.lang.Math.log(x + java.lang.Math.sqrt(1 + java.lang.Math.pow(x, 2)));
    }

    /**
     * Fonction demi sinus verse, servant à calculer la distance entre deux points sur une sphère une fois réutilisée comme dans PointWGS84.java:26.
     * @param x
     *      Différence entre les longitudes ou latitudes des points dont on veut connaître la distance.
     * @return Le sinus de l'angle formé par les points avec le centre de la sphère. Couplé à arcsinus et deux fois le rayon de la sphère, il permet d'obetnir la distance à la surface.
     */
    public static double haversin(double x){
        return java.lang.Math.pow(java.lang.Math.sin(x/2),2);
    }

    /**
     * Méthode calculant le quotient division par défaut
     * @param n
     *      Dividende
     * @param d
     *      Diviseur
     * @return Quotient
     */
    public static int divF(int n, int d) {
        int i;

        int rt = n%d;

        if (java.lang.Integer.signum(rt) == -1*java.lang.Integer.signum(d)) {
            i = 1;
        } else {
            i = 0;
        }

        return (int)(java.lang.Math.floor(n/d)-i);

    }

    /**
     * Méthode calculant le reste d'une division par défaut
     * @param n
     *      Dividende
     * @param d
     *      Diviseur
     * @return Reste
     */
    public static int modF(int n, int d) {
        return n-d*divF(n,d);
    }
}
