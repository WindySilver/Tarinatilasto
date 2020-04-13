package tarinatilasto;

/**
 * @author Noora Jokela ja Janne Taipalus
 * @version 2.5.2019
 *
 */
public interface Tietue {

    /**
     * @return Osan kenttien lukumäärä.
     */
    public abstract int getKenttia();
    
    
    /**
     * @return Ensimmäisen käyttäjän syötettävän kentän indeksi.
     */
    public abstract int ekaKentta();
    
    
    /**
     * @param k Minkä kentän kysymys halutaan.
     * @return Valitun kentän kysymysteksti.
     */
    public abstract String getKysymys(int k);
    
    
    /**
     * @param k Minkä kentän sisältö halutaan.
     * @return Valitun kentän sisältö.
     * @example
     * <pre name="test>
     * Osa osa = new Osa();
     * osa.parse("3  |1|9   |Salainen Puutarha  |  2405| 10");
     * osa.anna(0) === 3;
     * osa.anna(1) === 1;
     * osa.anna(2) === 9;
     * osa.anna(3) === "Salainen Puutarha";
     * osa.anna(4) === 2405;
     * osa.anna(5) === 10;
     * </pre>
     */
    public abstract String getAvain(int k);
    
    
    /**
     * Asetetaan valitun kentän sisältö.
     * @param k Mihin kenttään sisältö asetetaan.
     * @param s Asetettava sisältö merkkijonona.
     * @return Null jos ok, muuten virheteksti.
     * @example
     * <pre name="test">
     * Osa osa = new Osa();
     * osa.parse("3  |1|9   |Salainen Puutarha  |  2405| 10");
     * osa.aseta(3, "kukka") === null;
     * osa.aseta(4, "2") === null;
     * osa.aseta(4, "kukka") === Sanamäärä on väärin jono=\"kukka\"";
     */
    public abstract String aseta(int k, String s);
    
    
    /**
     * Tehdään osasta identtinen klooni.
     * @return Kloonattu osa.
     * @throws CloneNotSupportedException Jos kloonausta ei tueta.
     * @example
     * <pre name="test>
     * Osa osa = new Osa();
     * osa.parse("3  |1|9   |Salainen Puutarha  |  2405| 10");
     * Object kopio = osa.clone();
     * kopio.toString() === osa.toString();
     * osa.parse("2  |1|9   |Salainen Puutarha  |  2405| 10");
     * kopio.toString().equals(osa.toString)) === false;
     * kopio instanceof Osa === true;
     * </pre>
     */
    public abstract Tietue clone() throws CloneNotSupportedException;
    
    
    /**
     * Palauttaa osan tiedot merkkijonona, jonka voi tallentaa tiedostoon.
     * @return osa tolppaeroteltuna merkkijonona
     * @example
     * <pre name="test">
     *  Osa testi = new Osa();
     *  testi.parse("    5   |   10   |   13  |   testaus   |     205     |       365     ");
     *  testi.toString() === "5|10|13|testaus|205.0|365";
     * </pre>
     */
    @Override
    public abstract String toString();
}