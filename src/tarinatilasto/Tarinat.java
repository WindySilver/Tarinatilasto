package tarinatilasto;

import java.io.*;
import java.sql.*;
import java.util.*;

import static tarinatilasto.Kanta.alustaKanta;

/**
 * Tarinoiden luokka, joka osaa mm. tallentaa tarinat.
 * @author Noora Jokela & Janne Taipalus
 * @version 19.5.2020
 */
public class Tarinat {//implements Iterable<Tarina> {
   /*
    * Alustuksia ja puhdistuksia testiä varten
    * @example
    * <pre name="testJAVA">
    * #import java.io.*;
    * #import java.util.*;
    * 
    * private Tarinat tarinat;
    * private String tiedNimi;
    * private File ftied;
    * 
    * @Before
    * public void alusta() throws SailoException {
    *   tiedNimi = "testitarinat";
    *   ftied = new File(tiedNimi+".db");
    *   ftied.delete();
    *   tarinat = new Tarinat(tiedNimi);
    * }
    * 
    * @After
    * public void siivoa() {
    *   ftied.delete();
    * }
    </pre>
    */
    
    private static final int MAX_TARINOITA = 10;
    private int lkm = 0;
    private Tarina alkiot[] = new Tarina[MAX_TARINOITA];
    private Kanta kanta;
    private static Tarina aputarina = new Tarina();
    
    /**
     * Tarkistetaan, että kannassa on tarinoiden tarvitsema taulu
     * @param nimi tietokannan nimi
     * @throws SailoException jos jokin menee pieleen
     * @throws ClassNotFoundException testi
     */
    public Tarinat(String nimi) throws SailoException, ClassNotFoundException {
        kanta = alustaKanta(nimi);
        try ( Connection con = kanta.annaKantayhteys() ) {
            // Hankitaan tietokannan metadata ja tarkistetaaan siitä,
            // onko Tarinat-nimistä taulua olemassa.
            // Jos ei ole, luodaan se. Ei puututa siihen, onko
            // mahdollisesti olemassa olevalla taululla oikea rakenne.
            // Käyttäjä saa kuulla siitä virheilmoituksen kautta.
            DatabaseMetaData meta = con.getMetaData();
            
            try (ResultSet taulu = meta.getTables(null, null, "Tarinat", null)){
                if ( !taulu.next() ) {
                    // Luodaan Tarinat-taulu
                    try ( PreparedStatement sql = con.prepareStatement(aputarina.annaLuontilauseke())) {
                        sql.execute();
                    }
                }
            }
        } catch ( SQLException e ) {
            throw new SailoException( e.getMessage());
        }
    }
    
    
    /**
     * Lisää uuden tarinan tietorakenteeseen. Ottaa tarinan omistukseensa.
     * @param tarina Lisättävän tarinan viite.
     * @throws SailoException tietorakenne on jo täynnä
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * 
     * Collection<Tarina> loytyneet = tarinat.etsi("",1 );
     * loytyneet.size() === 0;
     * 
     * Tarina stoori1 = new Tarina(), stoori2 = new Tarina();
     * tarinat.lisaa(stoori1);
     * tarinat.lisaa(stoori2);
     * 
     * loytyneet = tarinat.etsi("", 1);
     * loytyneet.size() === 2;
     * 
     * // Unique constraint ei hyväksy
     * tarinat.lisaa(stoori1); #THROWS SailoException
     * Tarina stoori3 = new Tarina(): Tarina stoori4 = new Tarina(); Tarina stoori5 = new Tarina();
     * tarinat.lisaa(stoori3);
     * tarinat.lisaa(stoori4);
     * tarinat.lisaa(stoori5);
     * 
     * loytyneet = tarinat.etsi("", 1);
     * loytyneet.size() === 5;
     * Iterator<Tarina> i = loytyneet.iterator();
     * i.next() === stoori1;
     * i.next() === stoori2;
     * i.next() === stoori3;
     * 
     *  Tarinat tarinat = new Tarinat();
     *  Tarina tarina1 = new Tarina(), tarina2 = new Tarina();
     *  tarinat.getLkm() === 0;
     *  tarinat.lisaa(tarina1); tarinat.getLkm() === 1;
     *  tarinat.lisaa(tarina2); tarinat.getLkm() === 2;
     *  tarinat.lisaa(tarina1); tarinat.getLkm() === 3;
     *  tarinat.anna(0) === tarina1;
     *  tarinat.anna(1) === tarina2;
     *  tarinat.anna(2) === tarina1;
     *  tarinat.anna(1) == tarina1 === false;
     *  tarinat.anna(1) == tarina2 === true;
     *  tarinat.anna(3) === tarina1; #THROWS IndexOutOfBoundsException
     *  tarinat.lisaa(tarina1); tarinat.getLkm() === 4;
     *  tarinat.lisaa(tarina1); tarinat.getLkm() === 5;
     * </pre>
     */
    public void lisaa(Tarina tarina) throws SailoException {
        try ( Connection con = kanta.annaKantayhteys(); PreparedStatement sql = tarina.annaLisayslauseke(con) ) {
            sql.executeUpdate();
            try ( ResultSet rs = sql.getGeneratedKeys() ) {
               tarina.tarkistaId(rs);
            }   
            
        } catch (SQLException e) {
            throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
    }   
    
    
    /**
     * Palauttaa tarinat listassa
     * @param hakuehto Hakuehto
     * @param k Etsittävän kentän indeksi.
     * @return tarinat listassa.
     * @throws SailoException jos tietokannan kanssa ongelmia
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * Tarina stoori1 = new Tarina(); stoori1.lisaaTiedot(1);
     * Tarina stoori2 = new Tarina(); stoori2.lisaaTiedot(2);
     * tarinat.lisaa(stoori1);
     * tarinat.lisaa(stoori2);
     * tarinat.lisaa(stoori2);  #THROWS SailoException // ei saa lisätä samaa id:tä uudelleen
     * Collection<Tarina> loytyneet = tarinat.etsi(stoori1.getNimi(), 1);
     * loytyneet.size() === 1;
     * loytyneet.iterator().next() === stoori1;
     * loytyneet = tarinat.etsi(stoori2.getNimi(), 1);
     * loytyneet.size() === 1;
     * loytyneet.iterator().next() === stoori2;
     * loytyneet = tarinat.etsi("", 15);    #THROWS SailoException
     * 
     * ftied.delete();
     * </pre>
     */
    public Collection<Tarina> etsi(String hakuehto, int k) throws SailoException {
        String ehto = hakuehto;;
        String kysymys = aputarina.getKysymys(k);
        if ( k < 0 ) { kysymys = aputarina.getKysymys(0); ehto = ""; }
        // Avataan yhteys tietokantaan try...with lohkossa.
        try (
            Connection con = kanta.annaKantayhteys();
            PreparedStatement sql = con.prepareStatement("SELECT * FROM Tarinat WHERE " + kysymys + " LIKE ?") ) {
                ArrayList<Tarina> loytyneet = new ArrayList<Tarina>();
                sql.setString(1, "%" + ehto + "%");
                try (ResultSet tulokset = sql.executeQuery() ) {
                    while (tulokset.next() ) {
                        Tarina t = new Tarina();
                        t.parse(tulokset);
                        loytyneet.add(t);
                    }
                } return loytyneet;
            } catch ( SQLException e ) {
                throw new SailoException("Ongelmia tietokannan kanssa:" + e.getMessage());
        }
        
     }

    
    /**
     * @param tarinaIDt Hakuehto.
     * @param k Etsittävän kentän indeksi.
     * @return Tietorakenne löytyneistä tarinoista.
     */
    public Collection<Tarina> etsi(List<Integer> tarinaIDt, int k) {
        List<Tarina> loytyneet = new ArrayList<Tarina>();
        int pituus = tarinaIDt.size()-1;
        int id = 0;
        boolean loytyiko = false;
        for(int i = 0;i<=lkm-1;i++) {
            id = alkiot[i].getIdNumero(); 
            if(id <= tarinaIDt.get((int) (pituus*0.25))) loytyiko=hae(id, 0, pituus*0.25, tarinaIDt);
            else if(id > tarinaIDt.get((int) (pituus*0.25)) && id <= tarinaIDt.get((int) (pituus*0.5))) loytyiko=hae(id, pituus*0.25, pituus*0.5, tarinaIDt);
            else if(id > tarinaIDt.get((int) (pituus*0.5)) && id <= tarinaIDt.get((int) (pituus*0.75))) loytyiko=hae(id, pituus*0.5, pituus*0.75, tarinaIDt);
            else if(id > tarinaIDt.get((int) (pituus*0.75)) && id <= tarinaIDt.get(pituus)) loytyiko=hae(id, pituus*0.75, pituus, tarinaIDt);
            while(loytyiko) {
                loytyneet.add(alkiot[i]);
                loytyiko = false;
            }
        }
        Collections.sort(loytyneet, new Tarina.Vertailija(k));
        return loytyneet;
    }

    
    private boolean hae(int id, double alku, double loppu, List<Integer> tarinaIDt) {
        for (int i = (int)alku;i<=loppu;i++) {
            if (id == tarinaIDt.get(i)) return true;
        }
        return false;
    }
    
    
    /**
     * Korvaa tarinan tietorakenteessa ja ottaa sen omistukseensa.
     * Etsitään samalla tunnusnumerolla oleva tarina. Jos ei löydy, lisätään uutena.
     * @param tarina Lisättävän tarinan viite.
     * @throws SailoException jos tietorakenne on jo täynnä.
     * <pre name="test">
     * #THROWS SailoException,CloneNotSupportedException 
     * #PACKAGEIMPORT
     * Tarinat tarinat = new Tarinat();
     * Tarina t1 = new Tarina(); Tarina t2 = new Tarina();
     * t1.rekisteroi(); t2.rekisteroi();
     * tarinat.getLkm() === 0;
     * tarinat.korvaaTaiLisaa(t1); tarinat.getLkm() === 1;
     * tarinat.korvaaTaiLisaa(t2); tarinat.getLkm() === 2;
     * Tarina t3 = t1.clone();
     * t3.aseta(3,"T.T.");
     * Iterator<Tarina> it = tarinat.iterator();
     * it.next() == t1 === true;
     * tarinat.korvaaTaiLisaa(t3); tarinat.getLkm() === 2;
     * it = tarinat.iterator();
     * Tarina t0 = it.next();
     * t0 === t3;
     * t0 == t3 === true;
     * t0 == t1 === false;
     * </pre>
     */
    public void korvaaTaiLisaa(Tarina tarina) throws SailoException {
        int id = tarina.getIdNumero();
        for (int i = 0; i < lkm;i++) {
            if (alkiot[i].getIdNumero() == id) {
                alkiot[i] = tarina;
                return;
            }
        }
        lisaa(tarina);
    }
    
    
    /**
     * Testiohjelma tarinoille.
     * @param args ei käytössä
     * @throws ClassNotFoundException test
     */
    public static void main(String args[]) throws ClassNotFoundException {
        try {
            new File("kokeilu.db").delete();
            Tarinat tarinat = new Tarinat("kokeilu");
            
            Tarina stoori1 = new Tarina(), stoori2 = new Tarina();
            stoori1.lisaaTiedot(1);
            stoori2.lisaaTiedot(2);
            
            tarinat.lisaa(stoori1);
            tarinat.lisaa(stoori2);
            stoori2.tulosta(System.out);
            
            Tarina testi1 = new Tarina();
            testi1.rekisteroi();
            testi1.tulosta(System.out);
            testi1.lisaaTiedot(1);
            testi1.tulosta(System.out);
        
            tarinat.lisaa(testi1);
            tarinat.lisaa(testi1);
        
            System.out.println("===============Tarinat testi ============");
            int i = 0;
            for( Tarina tarina:tarinat.etsi("", 1)) {
                System.out.println("Tarina nro: " + i++);
                tarina.tulosta(System.out);
            }
            new File("kokeilu.db").delete();
        } catch (SailoException ex ) {
            System.out.println(ex.getMessage());
            }
     }
}