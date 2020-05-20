package tarinatilasto;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Luokka SQLite yhteyden muodostamiseksi.
 * Tavoite on että yhdelle nimelle on vain yksi tällainen Kanta-olio.
 * Luokkaa on tarkoitus käyttää seuraavasti:
 * <pre>
 *    Kanta kanta = Kanta.alustaKanta("tiedot");
 *    ...
 *    try ( Connection con = kanta.annaKantayhteys() ) {
 *      ...
 *    }  
 * </pre>
 * @author vesal
 * @version 7.2.2015
 *
 */
public class Kanta {
    
    private static HashMap<String, Kanta> kannat = new HashMap<String, Kanta>();
    private String tiedostonPerusNimi = "";
    
    /** 
     * Alustetaan kanta
     * @param nimi tiedoston perusnimi
     */
    private Kanta(String nimi) {
        setTiedosto(nimi);
    }

    
    /**
     * Alustetaan kantayhteys
     * @param nimi kannan nimi
     * @return kannan tiedot jolla voidaan operoida
     */
    public static Kanta alustaKanta(String nimi) {
        if ( kannat.containsKey(nimi) ) return kannat.get(nimi); 
        Kanta uusi = new Kanta(nimi);
        kannat.put(nimi, uusi);
        return uusi;
    }
    
    
    /**
     * Asettaa kannan perusnimen
     * @param nimi uusi nimi
     */
    public void setTiedosto(String nimi) {
        // SQLite käyttää yhtä tiedostoa koko tietokannalle.
        tiedostonPerusNimi = nimi;
    }
    
    
    /**
     * Palauttaa tiedoston nimen, jota käytetään tallennukseen
     * @return tallennustiedoston nimi
     */
    public String getTiedostonNimi() {
        return tiedostonPerusNimi + ".db";
    }
    
    
    /**
     * Antaa tietokantayhteyden
     * @return tietokantayhteys
     * @throws SQLException Jos tietokantayhteyden avaamisessa on ongelmia
     */
    public Connection annaKantayhteys() throws SQLException {
        String sDriver = "org.sqlite.JDBC";
        try {
            Class.forName(sDriver);
        } catch (ClassNotFoundException e) {
            System.err.println("Virhe luokan " + sDriver + "lataamisessa: " + e.getMessage());
        }
        return DriverManager.getConnection("jdbc:sqlite:" + getTiedostonNimi());
    }
}
