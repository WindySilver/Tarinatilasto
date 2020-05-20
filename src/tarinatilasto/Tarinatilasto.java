package tarinatilasto;

import java.io.File;
import java.util.*;

/**
 * Tarinatilasto-luokka, joka huolehtii sarjoista, tarinoista ja osista. Pääosin kaikki
 * metodit ovat vain "välittäjämetodeja" muihin tietorakenneluokkiin.
 * @author Janne Taipalus & Noora Jokela
 * @version 2.5.2019
 *
 *Testien alustus
 *@example
 *<pre name="testJAVA">
 * #import tarinatilasto.SailoException;
 * private Tarinatilasto tarinatilasto;
 * private Sarja sarja1;
 * private Sarja sarja2;
 * private int sarjaId1;
 * private int sarjaId2;
 * private Tarina tarina1;
 * private Tarina tarina2;
 * private int tId1;
 * private int tId2;
 * private Osa osa1;
 * private Osa osa2;
 * private Osa osa3;
 * private Osa osa4;
 * private Osa osa5;
 * 
 * @SuppressWarnings("javadoc")
 * public void alustaTilasto() {
 *  Tarinatilasto = new Tarinatilasto();
 *  sarja1 = new Sarja(1, "Testi1"); sarja1.rekisteroi();
 *  sarja2 = new Sarja(7, "Testi2"); sarja2.rekisteroi();
 *  sarjaId1 = sarja1.getTunnusNro();
 *  sarjaId2 = sarja2.getTunnusNro();
 *  tarina1 = new Tarina(); tarina1.lisaaTiedot(sarjaId1); tarina1.rekisteroi();
 *  tarina2 = new Tarina(); tarina2.lisaaTiedot(sarjaId2); tarina2.rekisteroi();
 *  tId1 = tarina1.getIdNumero();
 *  tId2 = tarina2.getIdNumero();
 *  osa1 = new Osa(tId1);
 *  osa2 = new OSa(tId2);
 *  osa3 = new Osa(tId1);
 *  osa4 = new Osa(tId2);
 *  osa5 = new OSa(tId1);
 *  try {
 *  tarinatilasto.lisaa(sarja1);
 *  tarinatilasto.lisaa(sarja2);
 *  tarinatilasto.lisaa(tarina1);
 *  tarinatilasto.lisaa(tarina2);
 *  tarinatilasto.lisaa(osa1);
 *  tarinatilasto.lisaa(osa2);
 *  tarinatilasto.lisaa(osa3);
 *  tarinatilasto.lisaa(osa4);
 *  tarinatilasto.lisaa(osa5);
 * } catch (Exception e) {
 *      System.err.println(e.getMessage());
 */
public class Tarinatilasto {

    private Sarjat sarjat;
    private Tarinat tarinat;
    private Osat osat;
    /*
     * Alustuksia ja puhdistuksia testiä varten
     * @example
     * <pre name="testJAVA">
     * #import java.io.*;
     * #import java.util.*;
     * 
     * private Tarinatilasto tarinatilasto;
     * private String tiedNimi;
     * private File ftied;
     * 
     * @Before
     * public void alusta() throws SailoException {
     *      tarinatilasto = new Tarinatilasto();
     *      tiedNimi = "testitarinat";
     *      ftied = new File(tiedNimi+".db");
     *      ftied.delete();
     *      tarinatilasto.lueTiedostosta(tiedNimi);
     * }
     * 
     *@After
     *public void siivoa() {
     *   
     *}
     */
    
    /**
     * Lisää tilastoon uuden osan.
     * @param osa Lisättävä osa.
     * @throws SailoException jos lisäystä ei voi tehdä.
     * 
     */
    public void lisaa(Osa osa) throws SailoException {
        osat.lisaa(osa);
    }
    
    
    /**
     * Lisää tilastoon uuden sarjan.
     * @param sarja Lisättävä sarja.
     * @throws SailoException jos lisäystä ei voi tehdä.
     */
    public void lisaa(Sarja sarja) throws SailoException {
        sarjat.lisaa(sarja);
    }
    
    
    /**
     * Lisää tilastoon uuden tarinan.
     * @param tarina Lisättävä tarina.
     * @throws SailoException jos lisäystä ei voi tehdä.
     */
    public void lisaa(Tarina tarina) throws SailoException {
        tarinat.lisaa(tarina);
    }
    
    
    /**
     * Poistaa tarinoista tarinan ja osista poistuvan tarinan osat.
     * @param tarina Tarina joka poistetaan.
     * @return Montako tarinaa poistettiin.
     */
/*    public int poista(Tarina tarina) {
        if (tarina == null) return 0;
        int ret = tarinat.poista(tarina.getIdNumero());
        osat.poistaTarinanOsat(tarina.getIdNumero());
        return ret;
    }*/
    
    
    /**
     * Palauttaa "taulukossa" hakuehtoon vastaavien tarinoiden viitteet.
     * @param hakuehto Hakuehto.
     * @param k Etsittävän kentän indeksi.
     * @return Tietorakenne löytyneistä tarinoista.
     * @throws SailoException jos jotakin menee väärin.
     * @example
     * <pre name="test">
     *  #THROWS CloneNotSupportedException, SailoException
     *  alustaTilasto();
     *  tarinatilasto.etsiTarina("*",0).size() === 2;
     *  tarinatilasto.korvaaTaiLisaa(tarina1);
     *  tarinatilasto.etsiTarina("*",0).size() === 2;
     * </pre>
     */
    public Collection<Tarina> etsiTarina(String hakuehto, int k) throws SailoException {
        return tarinat.etsi(hakuehto, k);
    }
    
    
    /**
     * @return Palauttaa sarjojen taulukon pituuden.
     */
    public int getSarjojenPituus() {
        return sarjat.getPituus();
    }

    
    /**
     * Palauttaa "taulukossa" hakuehtoon vastaavien osien viitteet.
     * @param hakuehto Hakuehto.
     * @param k Etsittävän kentän indeksi.
     * @param ID Tarinan ID-numero.
     * @return Tietorakenne löytyneistä osista.
     * @throws SailoException jos jotakin menee väärin.
     * @example
     * <pre name="test">
     *  #THROWS CloneNotSupportedException, SailoException
     *  alustaTilasto();
     *  tarinatilasto.etsiOsa("*",0).size() === 5;
     * </pre>
     */
    public Collection<Osa> etsiOsa(String hakuehto, int k, int ID) throws SailoException {
        return osat.etsi(hakuehto, k, ID);
    }


    /**
     * Palauttaa taulukossa hakuehtoon vastaavien kaikkien osien viitteet tarinasta riippumatta.
     * @param ehto Hakuehto.
     * @param k Etsittävän kentän indeksi.
     * @return Tietorakenne löytyneistä osista.
     * @throws SailoException jos jotakin menee väärin.
     */
    public Collection<Osa> etsiOsa(String ehto, int k) throws SailoException {
        return osat.etsi(ehto, k);
    }
    
    
    /**
     * Lukee tilaston tiedot tiedostosta.
     * @param nimi Nimi jota käytetään lukemisessa.
     * @throws SailoException jos lukeminen epäonnistuu.
     * @throws ClassNotFoundException test
     */
    public void lueTiedostosta(String nimi) throws SailoException, ClassNotFoundException {
        sarjat = new Sarjat(nimi);
        tarinat = new Tarinat(nimi);
        osat = new Osat(nimi);
    }
    
    
    
    /**
     * Tallentaa tilaston tiedot tiedostoon.
     * Tässä tietokantaversiossa ei tarvitse tehdä mitään.
     * @throws SailoException jos tallentamisessa on ongelmia.
     */
    public void tallenna() throws SailoException {
        return;
    }
    
    
    /**
     * Palauttaa i:nnen tarinan.
     * @param i Monesko tarina palautetaan.
     * @return Viite i:nteen tarinaan.
     * @throws IndexOutOfBoundsException jos i on väärin.
     */
 /*   public Tarina annaTarina(int i) throws IndexOutOfBoundsException{
        return tarinat.anna(i);
    }
*/
    
    /**
     * Palauttaa tarinan osalistan.
     * @param tarina Tarina, jonka osalistaa pyydetään.
     * @return Tietorakenne, jossa on viitteet löydettyihin osiin.
     * @throws SailoException Jos jotain menee pieleen
     * @example
     *  #THROWS SailoException
     *  #import java.util.*;
     *  
     *  Tarinatilasto tilasto = new Tarinatilasto();
     *  Tarina tarina1 = new Tarina(), tarina2 = new Tarina(), tarina3 = new Tarina();
     *  tarina1.rekisteroi(); tarina2.rekisteroi(); tarina3.rekisteroi();
     *  int id1 = tarina1.getIdNumero();
     *  int id2 = tarina2.getIdNumero();
     *  Osa osa1 = new Osa(id1); tilasto.lisaa(osa1);
     *  Osa osa2 = new Osa(id1); tilasto.lisaa(osa2);
     *  Osa osa3 = new Osa(id2); tilasto.lisaa(osa3);
     *  Osa osa4 = new Osa(id2); tilasto.lisaa(osa4);
     *  Osa osa5 = new Osa(id2); tilasto.lisaa(osa5);
     *  
     *  List<Osa> loytyneet;
     *  loytyneet = tilasto.annaOsat(tarina3);
     *  loytyneet.size() === 0;
     *  loytyneet = tilasto.annaOsat(tarina1);
     *  loytyneet.size() === 2;
     *  loytyneet.get(0) == osa1 === true;
     *  loytyneet.get(1) == osa2 === true;
     *  loytyneet = tilasto.annaOsat(tarina2);
     *  loytyneet.size() === 3;
     *  loytyneet.get(0) == osa3 === true;
     * </pre>
     */
    public List<Osa> annaOsat(Tarina tarina) throws SailoException {
        return osat.annaOsat(tarina.getIdNumero());
    }

    
    /**
     * Korvaa tarinan tietorakenteessa ja ottaa sen omistukseensa. Etsitään samalla tunnusnumerolla oleva tarina. Jos ei löydy,
     * lisätään uutena tarinana.
     * @param tarina Lisättävän tarinan viite.
     * @throws SailoException jos tietorakenne on jo täynnä.
     */
    public void korvaaTaiLisaa(Tarina tarina) throws SailoException {
        tarinat.korvaaTaiLisaa(tarina);
    }
    
    
    /**
     * Palauttaa "taulukossa" hakuehtoon vastaavien tarinoiden viitteet.
     * @param tarinaIDt Hakuehto.
     * @param k Etsittävän kentän indeksi.
     * @return Tietorakenne löytyneistä tarinoista.
     * @throws SailoException jos jotakin menee väärin.
     * */
    public Collection<Tarina> etsiTarina(List<Integer> tarinaIDt, int k) throws SailoException {
        return tarinat.etsi(tarinaIDt, k);
    }
    
    
    /**
     * Korvaa sarjan tietorakenteessa. Jos ei löydy samalla tunnusnumerolla samaa sarjaa,
     * se lisätään uutena sarjana.
     * @param sarja Lisättävän sarjan viite.
     * @throws SailoException Jos tietorakenne on täynnä.
     */
    public void korvaaTaiLisaa(Sarja sarja) throws SailoException {
        sarjat.korvaaTaiLisaa(sarja);
    }
    
    
    /**
     * Korvaa osan tietorakenteessa ja ottaa sen omistukseensa. Etsitään samalla tunnusnumerolla oleva tarina. Jos ei löydy,
     * lisätään uutena tarinana.
     * @param osa Lisättävän tarinan viite.
     * @throws SailoException jos tietorakenne on jo täynnä.
     */
    public void korvaaTaiLisaa(Osa osa) throws SailoException {
        osat.korvaaTaiLisaa(osa);
    }
    
    
    /**
     * Palauttaa i:nnen sarjan.
     * @param i Monesko sarjaa palautetaan.
     * @return Viite i:nteen sarjaan.
     * @throws IndexOutOfBoundsException jos i on väärin.
     */
    public Sarja annaSarja(int i) throws IndexOutOfBoundsException {
        return sarjat.anna(i);
    }

    
    /**
     * Etsii sarjan tietyllä hakuehdolla.
     * @param hakuehto Hakuehto, jolla etsitään.
     * @return Löytyneet sarjat.
     * @throws SailoException jos jokin menee vikaan
     */
    public Collection<Sarja> etsiSarja(String hakuehto) throws SailoException {
        return sarjat.etsi(hakuehto, 0);
    }

    
    /**
     * @return Sarjat-olio, joka tietää kaikki sarjat.
     */
    public Sarjat annaSarjat() {
        return sarjat;
    }
    
    
    /**
     * Testiohjelma tarinatilastolle.
     * @param args ei käytössä
     * @throws ClassNotFoundException test
     */
    public static void main(String args[]) throws ClassNotFoundException
    {
        try {
            new File("Kokeilu.db").delete();
            Tarinatilasto tarinatilasto = new Tarinatilasto();
            tarinatilasto.lueTiedostosta("kokeilu");
            
            Tarina testi1 = new Tarina(), testi2 = new Tarina();
            testi1.lisaaTiedot(1);
            testi2.lisaaTiedot(2);
            
            tarinatilasto.lisaa(testi1);
            tarinatilasto.lisaa(testi1);
            int id1 = testi1.getIdNumero();
            int id2 = testi2.getIdNumero();
            Osa osa11 = new Osa(id1); osa11.lisaaTiedot(id1); tarinatilasto.lisaa(osa11);
            Osa osa12 = new Osa(id1); osa12.lisaaTiedot(id1); tarinatilasto.lisaa(osa12);
            Osa osa21 = new Osa(id2); osa21.lisaaTiedot(id2); tarinatilasto.lisaa(osa21);
            Osa osa22 = new Osa(id2); osa22.lisaaTiedot(id2); tarinatilasto.lisaa(osa22);
            System.out.println("===============Tarinat testi ============");
            
            Collection<Tarina> tarinat = tarinatilasto.etsiTarina("", -1);
            int i = 0;
            for(Tarina tarina : tarinat) {
                System.out.println("Tarinan numero: " + i);
                tarina.tulosta(System.out);
                List<Osa> loytyneet = tarinatilasto.annaOsat(tarina);
                for (Osa osa : loytyneet)
                    osa.tulosta(System.out);
                i++;
            }
        } catch ( SailoException ex) {
            System.out.println(ex.getMessage());
        }
        new File("kokeilu.db").delete();
    }
}
