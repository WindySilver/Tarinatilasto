package tarinatilasto;

import java.io.*;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static tarinatilasto.Kanta.alustaKanta;


/*
 * Alustuksia ja puhdistuksia testiä varten
 * @example
 * <pre name="testJAVA">
 * #import java.io.*;
 * #import java.util.*;
 * 
 * private Sarjat sarjat;
 * private String tiedNimi;
 * private File ftied;
 * 
 * @Before
 * public void alusta() throws SailoException { 
 *    tiedNimi = "testitarinat";
 *    ftied = new File(tiedNimi+".db");
 *    ftied.delete();
 *    sarjat = new Sarjat(tiedNimi);
 * }   
 *
 * @After
 * public void siivoa() {
 *    ftied.delete();
 * }   
 * </pre>
 */ 


/**
 * Sarjojen luokka, joka osaa mm. tallentaa sarjojen tiedot.
 * @author Janne Taipalus ja Noora Jokela
 * @version 19.5.2020
 *
 */
public class Sarjat implements Iterable<Sarja> {

    private static final int MAX_SARJOJA = 5;
    private boolean muutettu = false;
    private int lkm = 0;
    private String tiedostonNimi = "";
    private Sarja alkiot[] = new Sarja[MAX_SARJOJA];
    
    private Kanta kanta;
    private static Sarja apusarja = new Sarja();
    
    
    
    /**
     * Tarkistetaan, että kannassa on sarjojen tarvitsema taulu
     * @param nimi Tietokannan nimi
     * @throws SailoException jos jokin menee pieleen
     */
    public Sarjat(String nimi) throws SailoException {
        kanta = alustaKanta(nimi);
        try ( Connection con = kanta.annaKantayhteys() ) {
            // Hankitaan tietokannan metadata ja tarkistetaaan siitä,
            // onko Sarjat-nimistä taulua olemassa.
            // Jos ei ole, luodaan se. Ei puututa siihen, onko
            // mahdollisesti olemassa olevalla taululla oikea rakenne.
            // Käyttäjä saa kuulla siitä virheilmoituksen kautta.
            DatabaseMetaData meta = con.getMetaData();
            
            try ( ResultSet taulu = meta.getTables(null, null, "Sarjat", null) ) {
                if ( !taulu.next() ) {
                    // Luodaan Sarjat-taulu
                    try ( PreparedStatement sql = con.prepareStatement(apusarja.annaLuontilauseke()) ) {
                        sql.execute();
                    }
                }
            }
            
        } catch ( SQLException e ) {
            throw new SailoException (e.getMessage());
        }
    }
    
    
    
    /**
     * Oletusmuodostaja
     */
    public Sarjat() {
        
    }
    
    
    /**
     * @return Taulukon pituus.
     */
    public int getPituus() {
        return alkiot.length;
    }
    
    
    /**
     * Asettaa tiedoston perusnimen ilman tarkenninta.
     * @param nimi Tallennustiedoston perusnimi.
     */
    public void setTiedostonPerusnimi(String nimi) {
        tiedostonNimi=nimi;
    }
    
    
    /**
     * @param id Poistettavan sarjan ID-numero.
     * @return 1 jos poistettiin, 0 jos ei löydy poistettavaa.
     */
    public int poista(int id) {
        int ind = etsiID(id);
        if (ind <0) return 0;
        lkm--;
        for (int i = ind;i<lkm;i++) alkiot[i] = alkiot[i+1];
        alkiot[lkm] = null;
        muutettu = true;
        return 1;
    }
    
    
    /**
     * @param id Tunnusnumero, jonka mukaan etsitään sarjaa.
     * @return Löytyneen sarjan indeksi tai -1, jos ei löydy mitään.
     */
    public int etsiID(int id) {
        for (int i = 0;i<lkm;i++) {
            if (id == alkiot[i].getTunnusNro()) return i;
        }
        return -1;
    }
    
    
    /**
     * @param id Tunnusnumero, jonka mukaan etsitään.
     * @return Sarja, jolla on etsittävä ID, tai null.
     */
    public Sarja annaID(int id) {
        for (Sarja sarja : this) {
            if (id == sarja.getTunnusNro()) return sarja;
        }
        return null;
    }
    
    
    /**
     * Lisää uuden sarjan tietorakenteeseen.
     * @param sarja Sarjan nimi.
     * @throws SailoException  jos tietorakenne on jo täynnä
     * @example
     * <pre name="test">
      * #THROWS SailoException 
      * 
      * Collection<Sarja> loytyneet = sarjat.etsi("", 1);
      * loytyneet.size() === 0;
      * 
      * Sarja sarja1 = new Sarja(), sarja2 = new Sarja();
      * sarjat.lisaa(sarja1); 
      * sarjat.lisaa(sarja2);
      *  
      * loytyneet = sarjat.etsi("", 1);
      * loytyneet.size() === 2;
      * 
      * // Unique constraint ei hyväksy
      * sarjat.lisaa(sarja1); #THROWS SailoException
      * Sarja sarja3 = new Sarja(); Sarja sarja4 = new Sarja(); Sarja sarja5 = new Sarja();
      * sarjat.lisaa(sarja3);
      * sarjat.lisaa(sarja4);
      * sarjat.lisaa(sarja5);
 
      * loytyneet = sarjat.etsi("", 1);
      * loytyneet.size() === 5;
      * Iterator<Sarja> i = loytyneet.iterator();
      * i.next() === sarja1;
      * i.next() === sarja2;
      * i.next() === sarja3;
      * </pre>
      */
     public void lisaa(Sarja sarja) throws SailoException {
         try ( Connection con = kanta.annaKantayhteys(); PreparedStatement sql = sarja.annaLisayslauseke(con) ) {
             sql.executeUpdate();
             try ( ResultSet rs = sql.getGeneratedKeys() ) {
                sarja.tarkistaId(rs);
             }   
             
         } catch (SQLException e) {
             throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
         }
    }   
    
    
    /**
     * Palauttaa sarjojen lukumäärän.
     * @return Sarjojen lukumäärä.
     */
    public int getLkm() {
        return lkm;
    }
    
    
    /**
     * Lukee sarjojen tiedot tiedostosta.
     * @throws SailoException jos lukeminen epäonnistuu.
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * #import java.io.File;
     * #import java.util.Iterator;
     * 
     * Sarjat sarjat = new Sarjat();
     * Sarja testi1 = new Sarja(), testi2 = new Sarja();
     * testi1.lisaaTiedot();
     * testi2.lisaaTiedot();
     * String hakemisto = "testiSarjat";
     * String tiedNimi = hakemisto+"/nimet";
     * File ftied = new File(tiedNimi+".dat");
     * File dir = new File(hakemisto);
     * dir.mkdir();
     * sarjat.setTiedostonPerusnimi(tiedNimi);
     * ftied.delete();
     * sarjat.lueTiedostosta(); #THROWS SailoException
     * sarjat.lisaa(testi1);
     * testi1.rekisteroi();
     * sarjat.lisaa(testi2);
     * testi2.rekisteroi();
     * sarjat.tallenna();
     * sarjat = new Sarjat();
     * sarjat.setTiedostonPerusnimi(tiedNimi);
     * sarjat.lueTiedostosta();
     * Iterator<Sarja> i = sarjat.iterator();
     * i.next().toString() === testi1.toString();
     * i.next().toString() === testi2.toString();
     * i.hasNext() === false;
     * sarjat.lisaa(testi2);
     * sarjat.tallenna();
     * ftied.delete() === true;
     * File fbak = new File(tiedNimi+".bak");
     * fbak.delete() === true;
     * dir.delete() === true;
     * </pre>
     */
    public void lueTiedostosta() throws SailoException {
        try(BufferedReader fi = new BufferedReader(new FileReader(getTiedostonNimi())) ) {
            String rivi;
            while((rivi = fi.readLine() ) != null) {
                rivi = rivi.trim();
                if("".equals(rivi) || rivi.charAt(0) == ';' || rivi.charAt(1) == ';') continue;
                Sarja sarja = new Sarja();
                sarja.parse(rivi);
                lisaa(sarja);
            }
            muutettu = false;
        }catch (FileNotFoundException e)
        {
            throw new SailoException("Tiedosto " + getTiedostonNimi() + " ei aukea!");
        }catch (IOException e) {
            throw new SailoException ("Ongelmia tiedoston kanssa: " + e.getMessage());
        }
    }
    
    
    /**
     * Tallentaa sarjojen tiedot tiedostoon.
     * @throws SailoException jos tallennus epäonnistuu.
     */
    public void tallenna() throws SailoException {
        if (!muutettu) return;
        
        File fbak = new File(getBakNimi());
        File ftied = new File(getTiedostonNimi());
        fbak.delete();
        ftied.renameTo(fbak);
        
        try (PrintWriter fo = new PrintWriter(new FileWriter(ftied.getCanonicalPath()))) {
            fo.println(";sid|nimi|");
            for (Sarja sarja : this) {
                fo.println(sarja.toString());
            }
        } catch (FileNotFoundException e) {
            throw new SailoException("Tiedosto " + ftied.getName() + " ei aukea!");
        } catch (IOException e) {
            throw new SailoException("Tiedoston " + ftied.getName() + " kirjoittamisessa ongelmia");
        }
        muutettu = false;
    }
    
    
    /**
     * Palauttaa varakopiotiedoston nimen.
     * @return Varakopiotiedoston nimi.
     */
    public String getBakNimi() {
        return tiedostonNimi + ".bak";
    }
    
    
    /**
     * Palauttaa tallennustiedoston nimen.
     * @return Tallennustiedoston nimi.
     */
    public String getTiedostonNimi() {
        return tiedostonNimi+".dat";
    }

    
    /**
     * Palauttaa viitteen i:nteen sarjaan.
     * @param i Monennenko sarjan viitettä pyydetään.
     * @return Viite i:teen sarjaan.
     * @throws IndexOutOfBoundsException jos i ei ole sallitulla alueella.
     */
    public Sarja anna(int i) throws IndexOutOfBoundsException {
        if (i<0||i>= lkm) throw new IndexOutOfBoundsException("Virheellinen indeksi: " + i);
        return alkiot[i];
    }

    
    /**
     * @author Janne Taipalus & Noora Jokela
     * @version 2.5.2019
     *
     */
    public class SarjatIterator implements Iterator<Sarja> {
        
        private int kohdalla = 0;
        
        
        /**
         * Onko olemassa vielä seuraavaa sarjaa.
         * @see java.util.Iterator#hasNext()
         * @return true, jos on vielä sarjoja.
         */
        @Override
        public boolean hasNext() {
            return kohdalla < getLkm();
        }
        
        
        /**
         * Annetaan seuraava sarja.
         * @throws NoSuchElementException jos seuraavaa alkiota ei ole.
         * @see java.util.Iterator#next()
         */
        @Override
        public Sarja next() throws NoSuchElementException {
            if (!hasNext()) throw new NoSuchElementException("Ei ole");
            return anna(kohdalla++);
        }
        
        
        /**
         * Tuhoamista ei ole toteutettu.
         * @throws UnsupportedOperationException aina
         * @see java.util.Iterator#remove()
         */
        @Override
        public void remove() throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Ei poisteta");
        }
    }
    
    
    /**
     * Korvaa tarinan tai lisää sen uutena, jos samalla ID:llä ei löydy.
     * @param sarja Viite sarjaan.
     */
    public void korvaaTaiLisaa(Sarja sarja) {
        int id = sarja.getTunnusNro();
        for (int i = 0; i < lkm;i++) {
            if (alkiot[i].getTunnusNro() == id) {
                alkiot[i] = sarja;
                muutettu = true;
                return;
            }
        }
        try {
            lisaa(sarja);
        } catch (SailoException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Palautetaan iteraattori sarjoista.
     * @return Sarjaiteraattori.
     */
    @Override
    public Iterator<Sarja> iterator(){
        return new SarjatIterator();
    }
    
    
    /**
     * Palauttaa sarjat listassa
     * @param hakuehto hsarjaehto  
     * @param k etsittävän kentän indeksi 
     * @return sarjat listassa
     * @throws SailoException jos tietokannan kanssa ongelmia
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * Sarja sarja1 = new Sarja(); sarja1.vastaasarjaAnkka(); 
     * Sarja sarja2 = new Sarja(); sarja2.vastaasarjaAnkka(); 
     * Sarjat.lisaa(sarja1);
     * Sarjat.lisaa(sarja2);
     * Sarjat.lisaa(sarja2);  #THROWS SailoException  // ei saa lisätä sama id:tä uudelleen
     * Collection<Sarja> loytyneet = Sarjat.etsi(sarja1.getNimi(), 1);
     * loytyneet.size() === 1;
     * loytyneet.iterator().next() === sarja1;
     * loytyneet = Sarjat.etsi(sarja2.getNimi(), 1);
     * loytyneet.size() === 1;
     * loytyneet.iterator().next() === sarja2;
     * loytyneet = Sarjat.etsi("", 15); #THROWS SailoException
     *
     * ftied.delete();
     * </pre>
     */
    public Collection<Sarja> etsi(String hakuehto, int k) throws SailoException {
        String ehto = hakuehto;
        String kysymys = apusarja.getKysymys(k);
        if ( k < 0 ) { kysymys = apusarja.getKysymys(0); ehto = ""; }
        // Avataan yhteys tietokantaan try .. with lohkossa.
        try ( Connection con = kanta.annaKantayhteys();
              PreparedStatement sql = con.prepareStatement("SELECT * FROM Sarjat WHERE " + kysymys + " LIKE ?") ) {
            ArrayList<Sarja> loytyneet = new ArrayList<Sarja>();
            
            sql.setString(1, "%" + ehto + "%");
            try ( ResultSet tulokset = sql.executeQuery() ) {
                while ( tulokset.next() ) {
                    Sarja j = new Sarja();
                    j.parse(tulokset);
                    loytyneet.add(j);
                }
            }
            return loytyneet;
        } catch ( SQLException e ) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }


/**
 * Testiohjelma sarjoille
 * @param args ei käytössä
 */
public static void main(String args[])  {
    try {
        new File("kokeilu.db").delete();
        Sarjat Sarjat = new Sarjat("kokeilu");

        Sarja sarja1 = new Sarja(), sarja2 = new Sarja();
        sarja2.lisaaTiedot();
        //sarja2.rekisteroi();
        sarja2.lisaaTiedot();
        
        Sarjat.lisaa(sarja1);
        Sarjat.lisaa(sarja2);
        sarja2.tulosta(System.out);
        
        System.out.println("============= Sarjat testi =================");

        int i = 0;
        for (Sarja Sarja:Sarjat.etsi("", -1)) {
            System.out.println("Sarja nro: " + i++);
            Sarja.tulosta(System.out);
        }
        
        new File("kokeilu.db").delete();
    } catch ( SailoException ex ) {
        System.out.println(ex.getMessage());
    }
}

}