package tarinatilasto;

import java.io.*;
import java.sql.*;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Tarinan osan/lukun (käytetään nimeä osa) luokka.
 * @author Janne Taipalus & Noora Jokela
 * @version 18.5.2020
 */
public class Osa implements Cloneable, Tietue {
    
    private int tunnusNro;
    private int tarinaNro;
    private String nimi = "";
    private double sanamaara;
    private int numero;
    private int sivumaara;
    
    private static int seuraavaNro = 0;
    
    
    //******
    //SQL alkaa
    
    
    /**
     * Antaa tietokannan luontilausekkeen osataululle
     * @return Osataulun luontilauseke
     */
    public String annaLuontilauseke() {
        return "CREATE TABLE Osat (" +
                "tunnusNro INTEGER PRIMARY KEY AUTOINCREMENT , " +
                "tarinaNro INTEGER NOT NULL, " +
                "nimi VARCHAR(100) NOT NULL, " +
                "sanamaara DOUBLE, " +
                "numero INTEGER NOT NULL, " +
                "sivumaara INTEGER" +
                ")";
    }
    
    
    /**
     * Antaa osan lisäyslausekkeen
     * @param con tietokantayhteys
     * @return osan lisäyslauseke
     * @throws SQLException Jos lausekkeen luonnissa on ongelmia
     */
    public PreparedStatement annaLisayslauseke(Connection con)
            throws SQLException {
        PreparedStatement sql = con.prepareStatement("INSERT INTO Osat" +
                "(tunnusNro, tarinaNro, nimi, sanamaara, " +
                "numero, sivumaara) " +
                "VALUES (?, ?, ?, ?, ?, ?)");
        
        // Syötetään kentät näin välttääksemme SQL-injektiot.
        // Käyttäjän syötteitä ei ikinä vain kirjoiteta kyselymerkkijonoon
        // tarkistamatta niitä SQL-injektioiden varalta!
        if ( tunnusNro != 0 ) sql.setInt(1, tunnusNro); else sql.setString(1, null);
        sql.setInt(2, tarinaNro);
        sql.setString(3, nimi);
        sql.setDouble(4, sanamaara);
        sql.setInt(5, numero);
        sql.setInt(6, sivumaara);
        
        return sql;
    }
    
    
    /**
     * Tarkistetaan onko id muuttunut lisäyksessä
     * @param rs lisäyslauseen ResultSet
     * @throws SQLException jos tulee jotakin vikaa
     */
    public void tarkistaId(ResultSet rs) throws SQLException {
        if ( !rs.next() ) return;
        int id = rs.getInt(1);
        if ( id == tunnusNro ) return;
        setTunnusNro(id);
    }
    
    
    /**
     * Ottaa osan tiedot ResultSetitsä
     * @param tulokset Tulokset joista tiedot otetaan
     * @throws SQLException Jos jokin menee väärin
     */
    public void parse(ResultSet tulokset) throws SQLException{
        setTunnusNro(tulokset.getInt("tunnusNro"));
        tarinaNro = tulokset.getInt("tarinaNro");
        nimi = tulokset.getString("nimi");
        sanamaara = tulokset.getDouble("sanamaara");
        numero = tulokset.getInt("numero");
        sivumaara = tulokset.getInt("sivumaara");
    }
    
    
    //******
    //SQL loppuu
    
    
    /**
     * Alustetaan osa.
     */
    public Osa() {
        //
    }
    
    
    /**
     * @return Osan kenttien lukumäärä.
     */
    @Override
    public int getKenttia() {
        return 6;
    }
    
    
    /**
     * @return Ensimmäisen käyttäjän syötettävän kentän indeksi.
     */
    @Override
    public int ekaKentta() {
        return 2;
    }
    
    
    /**
     * @param k Minkä kentän kysymys halutaan.
     * @return Valitun kentän kysymysteksti.
     */
    @Override
    public String getKysymys(int k) {
        switch(k) {
        case 0: return "Tunnusnumero";
        case 1: return "TarinaID";
        case 2: return "Numero";
        case 3: return "Nimi";
        case 4: return "Sanamäärä";
        case 5: return "Sivumäärä";
        default: return "?";
        }
    }
    
    
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
    @Override
    public String getAvain(int k) {
        switch(k) {
        case 0: return "" + tunnusNro;
        case 1: return "" + tarinaNro;
        case 2: return "" + numero;
        case 3: return "" + nimi;
        case 4: return "" + sanamaara;
        case 5: return "" + sivumaara;
        default: return "?";
        }
    }
    
    
    /**
     * Tehdään osasta identtinen klooni.
     * @return Kloonattu osa.
     * @example
     * <pre name="test>
     * Osa osa = new Osa();
     * osa.parse("3  |1|9   |Salainen Puutarha  |  2405| 10");
     * Object kopio = osa.clone();
     * kopio.toString() === osa.toString();
     * osa.parse("2  |1|9   |Salainen Puutarha  |  2405| 10");
     * kopio.toString().equals(osa.toString)) === false;
     * kopio instanceof Osa === true;
     */
    @Override
    public Osa clone() throws CloneNotSupportedException{
        return (Osa)super.clone();
    }
    
    
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
    @Override
    public String aseta(int k, String s) {
        String st = s.trim();
        StringBuffer sb = new StringBuffer(st);
        switch (k) {
        case 0: setTunnusNro(Mjonot.erota(sb, '$', getTunnusNro())); return null;
        case 1: tarinaNro = Mjonot.erota(sb, '$', tarinaNro); return null;
        case 2: try{
            numero = Mjonot.erotaEx(sb, '$', numero);
        }catch (NumberFormatException ex) {
            return "Osan numero on väärin " + ex.getMessage();
        } return null;
        case 3: nimi = st; if(st.contentEquals("")) return "Nimi ei saa olla tyhjä!"; return null;
        case 4: try{
            sanamaara = Mjonot.erotaEx(sb, '$', sanamaara);
        }catch (NumberFormatException ex) {
            return "Sanamäärä on väärin " + ex.getMessage();
        } return null;
        case 5: try{ sivumaara = Mjonot.erotaEx(sb, '$', sivumaara);        }catch (NumberFormatException ex) {
            return "Sivumäärä on väärin " + ex.getMessage();
        } return null;
        default: return "Väärä kentän indeksi!";
        }
    }
    
    
    @Override
    public boolean equals(Object osa) {
        return this.toString().equals(osa.toString());
    }
    
    
    /**
     * Alustetaan tietyn tarinan osa.
     * @param tarinaNro Tarinan viitenumero.
     */
    public Osa(int tarinaNro) {
        this.tarinaNro = tarinaNro;
    }
    
    
    /**
     * Apumetodi jolla saadaan täytettyä testiarvot osalle.
     * @param nro Viite tarinaan, jonka osasta on kyse.
     */
    public void lisaaTiedot(int nro) {
        tarinaNro = nro;
        nimi = "Isä, poika ja Leviathan-kirves";
        sanamaara = rand(500, 2000);
        numero = rand(1,25);
        sivumaara=rand(1, 10);
    }
    
    
     /**
     * Arvotaan satunnainen kokonaisluku välille [ala,yla].
     * @param ala Arvonnan alaraja.
     * @param yla Arvonnan yläraja.
     * @return Satunnainen luku väliltä [ala,yla].
     */
    public static int rand(int ala, int yla) {
         double n = (yla-ala)*Math.random() + ala;
         return (int)Math.round(n);
    }
    
    
    /**
     * Tulostetaan osan tiedot.
     * @param out Tietovirta, johon tulostetaan.
     */
    public void tulosta(PrintStream out) {
        out.println("Osa " + numero);
        out.println("Nimi: " + nimi);
        out.println(sanamaara + " sanaa");
        out.println(sivumaara + " sivua");
    }
    
    
    /**
     * Tulostetaan tiedot.
     * @param os Tietovirta, johon tulostetaan.
     */
    public void tulosta(OutputStream os) {
        tulosta(new PrintStream(os));
    }
    
    
    /**
     * Antaa osalle tunnusnumeron.
     * @return Osan tunnusnumero.
     * @example
     * <pre name="test">
     *   Osa osa1 = new Osa();
     *   osa1.getTunnusNro() === 0;
     *   osa1.rekisteroi();
     *   Osa osa2 = new Osa();
     *   osa2.rekisteroi();
     *   int n1 = osa1.getTunnusNro();
     *   int n2 = osa2.getTunnusNro();
     *   n1 === n2-1;
     * </pre>
     */
    public int rekisteroi() {
        tunnusNro = seuraavaNro;
        seuraavaNro++;
        return tunnusNro;
    }
    
    
    /**
     * Palauttaa osan tiedot merkkijonona, jonka voi tallentaa tiedostoon.
     * @return Psa tolppaeroteltuna merkkijonona.
     * @example
     * <pre name="test">
     *  Osa testi = new Osa();
     *  testi.parse("    5   |   10   |   13  |   testaus   |     205     |       365     ");
     *  testi.toString() === "5|10|13|testaus|205.0|365";
     * </pre>
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        String erotin = "";
        for (int k = 0; k<getKenttia(); k++) {
            sb.append(erotin);
            sb.append(getAvain(k));
            erotin = "|";
        }
        return sb.toString();
    }
    
    
    /**
     * Palautetaan osan oma id-numero.
     * @return Osan id-numero.
     */
    public int getTunnusNro() {
        return tunnusNro;
    }
    
    
    /**
     * Palautetaa,n mihin tarinaan osa kuuluu.
     * @return Osan tarinan id-numero.
     * @example
     * <pre name="test">
     *  Osa testi1 = new Osa(5), testi2 = new Osa(9);
     *  testi1.getTarinaNro() === 5;
     *  testi2.getTarinaNro() === 9;
     * </pre>
     */
    public int getTarinaNro() {
        return tarinaNro;
    }
    
    
    /**
     * Asettaa tunnusnumeron ja samalla varmistaa, että seuraavaNumero on aina suurempi kuin tähän mennessä suurin numero.
     * @param nro Asetettava tunnusnumero.
     */
    public void setTunnusNro(int nro)
    {
        tunnusNro = nro;
        if (tunnusNro >= seuraavaNro) seuraavaNro = tunnusNro + 1;
    }


    /**
     * Parsii osan tiedot |-merkillä erotellusta merkkijonosta ja pitää huolen, että seuraavaNro on suurempi kuin tuleva tunnusNumero.
     * @param rivi Rivi, josta osan tiedot otetaan.
     * @example
     * <pre name="test">
     *  Osa testi = new Osa();
     *  testi.parse("    5   |   10   |   13  |   testaus   |     205     |       365     |");
     *  testi.getTarinaNro() === 10;
     *  testi.toString() === "5|10|13|testaus|205.0|365";
     *  
     *  testi.rekisteroi();
     *  int n = testi.getTunnusNro();
     *  testi.parse(""+(n+20));
     *  testi.rekisteroi();
     *  testi.getTunnusNro() === n+20+1;
     *  testi.toString() === "" + (n+20+1) + "|10|13|testaus|205.0|365";
     * </pre>
     */
    public void parse(String rivi) {
        StringBuffer sb = new StringBuffer (rivi);
        for (int k = 0; k<getKenttia();k++) {
            aseta(k, Mjonot.erota(sb, '|'));
        }   
    }
    
    /**
     * Laitoin, koska Java halusi sen.
     */
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }


    /**
     * Testiohjelma osalle.
     * @param args ei käytössä
     */
    public static void main(String[] args) {
        Osa testiosa = new Osa();
        testiosa.lisaaTiedot(3);
        testiosa.tulosta(System.out);
    }
}