package tarinatilasto;

import java.io.*;
import java.util.*;

import fi.jyu.mit.ohj2.WildChars;

/**
 * Tarinatilaston osat-luokka, joka osaa mm. lisätä uuden osan.
 * @author Janne Taipalus & Noora Jokela
 * @version 30.7.2020
 */
public class Osat implements Iterable<Osa> {
    
    private String tiedostonNimi = "";
    private boolean muutettu = false;
    private final List<Osa> alkiot = new ArrayList<Osa>();
   
    
    /**
     * Osien alustaminen.
     */
    public Osat() {
        //
    }
    
    
    /**
     * Asettaa tiedoston perusnimen ilman tarkenninta.
     * @param nimi Tallennustiedoston perusnimi.
     */
    public void setTiedostonPerusnimi(String nimi) {
        tiedostonNimi=nimi;
    }
    
    
    /**
     * Lisää uuden osan tietorakenteeseen. Ottaa osan omistukseensa.
     * @param osa Lisättävä osa.
     */
    public void lisaa(Osa osa) {
        alkiot.add(osa);
        muutettu = true;
    }
    
    
    /**
     * Lukee osien tiedot tiedostosta.
     * @throws SailoException jos lukeminen epäonnistuu.
     * @example
     * <pre name="test">
     *  #THROWS SailoException
     *  #import java.io.File;
     *  Osat osat = new Osat();
     *  Osa osa21 = new Osa(); osa21.lisaaTiedot(2);
     *  Osa osa19 = new Osa(); osa19.lisaaTiedot(1);
     *  Osa osa2 = new Osa(); osa2.lisaaTiedot(2);
     *  Osa osa45 = new Osa(); osa45.lisaaTiedot(1);
     *  Osa osa13 = new Osa(); osa13.lisaaTiedot(2);
     *  String tiedNimi = "testiOsat";
     *  osat.setTiedostonPerusnimi(tiedNimi);
     *  File ftied = new File(tiedNimi+".dat");
     *  ftied.delete();
     *  osat.lueTiedostosta(); #THROWS SailoException
     *  osat.lisaa(osa21);
     *  osat.lisaa(osa19);
     *  osat.lisaa(osa2);
     *  osat.lisaa(osa45);
     *  osat.lisaa(osa13);
     *  osat.tallenna();
     *  osat = new Osat();
     *  osat.setTiedostonPerusnimi(tiedNimi);
     *  osat.lueTiedostosta();
     *  Iterator<Osa> i = osat.iterator();
     *  i.next().toString() === osa21.toString();
     *  i.next().toString() === osa19.toString();
     *  i.next().toString() === osa2.toString();
     *  i.next().toString() === osa45.toString();
     *  i.next().toString() === osa13.toString();
     *  i.hasNext() === false;
     *  osat.lisaa(osa13);
     *  osat.tallenna();
     *  ftied.delete() === true;
     *  File fbak = new File(tiedNimi+".bak");
     *  fbak.delete() === true;
     * </pre>
     */
    public void lueTiedostosta() throws SailoException {
        try(BufferedReader fi = new BufferedReader(new FileReader(getTiedostonNimi())) ) {
            String rivi;
            while((rivi = fi.readLine() ) != null) {
                rivi = rivi.trim();
                if("".equals(rivi) || rivi.charAt(0) == ';' || rivi.charAt(1) == ';') continue;
                Osa osa = new Osa();
                osa.parse(rivi);
                lisaa(osa);
            }
            muutettu = false;
        }catch (FileNotFoundException e)
        {
            throw new SailoException("The file " + getTiedostonNimi() + " doesn't open!");
        }catch (IOException e) {
            throw new SailoException ("Problems with the file: " + e.getMessage());
        }
    }
    
    
    /**
     * Korvaa osan tietorakenteessa.
     * @param osa Lisättävän osan viite.
     * @throws SailoException jos tietorakenne on jo täynnä.
     */
    public void korvaaTaiLisaa(Osa osa) throws SailoException{
        int id = osa.getTunnusNro();
        for (int i = 0;i<getLkm();i++) {
            if (alkiot.get(i).getTunnusNro() == id) {
                alkiot.set(i, osa);
                muutettu = true;
                return;
            }
        }
        lisaa(osa);
    }
    
    
    /**
     * Poistaa osan tietorakenteesta.
     * @param osa Osa, joka poistetaan.
     * @return Tosi, jos löytyi poistettava osa.
     */
    public boolean poista(Osa osa) {
        boolean ret = alkiot.remove(osa);
        if (ret) muutettu = true;
        return ret;
    }
    
    
    /**
     * Poistaa tietyn tarinan osat.
     * @param tunnusnumero Osan tarinan tunnusnumero.
     * @return Montako poistettiin.
     */
    public int poistaTarinanOsat(int tunnusnumero) {
        int n = 0;
        for (Iterator<Osa> it = alkiot.iterator(); it.hasNext();) {
            Osa osa = it.next();
            if(osa.getTarinaNro() == tunnusnumero) {
                it.remove();
                n++;
            }
        }
        if (n>0) muutettu = true;
        return n;
    }

    
    /**
     * @return Tallennustiedoston nimi jota käytetään tallennukseen.
     */
    public String getTiedostonPerusnimi() {
        return tiedostonNimi;
    }
    
    
    /**
     * @return Tallennustiedoston nimi.
     */
    public String getTiedostonNimi() {
        return tiedostonNimi+".dat";
    }
    
    
    /**
     * Tallentaa osien tiedot tiedostoon.
     * @throws SailoException jos tallennus epäonnistuu.
     */
    public void tallenna() throws SailoException {
        if (!muutettu) return;
        
        File fbak = new File(getBakNimi());
        File ftied = new File(getTiedostonNimi());
        fbak.delete();
        ftied.renameTo(fbak);
        
        try (PrintWriter fo = new PrintWriter(new FileWriter(ftied.getCanonicalPath()))) {
            fo.println("; The fields' order in the file is the following:");
            fo.println(";pid|stid|number|name|wordcount|pagecount|");
            for (Osa osa : this) {
                fo.println(osa.toString());
            }
        } catch (FileNotFoundException e) {
            throw new SailoException("The file " + ftied.getName() + " doesn't open!");
        } catch (IOException e) {
            throw new SailoException("Pproblems with writing into the file " + ftied.getName() + "");
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
     * Palauttaa osien lukumäärän.
     * @return Osien lukumäärä.
     */
    public int getLkm() {
        return alkiot.size();
    }
    
    
    /**
     * @param hakuehto Hakuehto.
     * @param k Etsittävän kentän indeksi.
     * @param ID Tarinan ID.
     * @return Tietorakenne löytyneistä osista.
     */
    public Collection<Osa> etsi(String hakuehto, int k, int ID) {
        String ehto = "*";
        if (hakuehto != null && hakuehto.length() > 0 ) ehto = hakuehto;
        int hk = k;
        if (hk < 0) hk = 0;
        Collection<Osa> loytyneet = new ArrayList<Osa>();
        for (Osa osa : this) {
            if (WildChars.onkoSamat(osa.getAvain(hk), ehto) && osa.getTarinaNro() == ID) loytyneet.add(osa);
        }
        return loytyneet;
    }


    /**
     * @param hakuehto Hakuehto.
     * @param k Etsittävän kentän indeksi.
     * @return Tietorakenne löytyneistä osista.
     */
    public Collection<Osa> etsi(String hakuehto, int k) {
        String ehto = "*";
        if (hakuehto != null && hakuehto.length() > 0 ) ehto = hakuehto;
        int hk = k;
        if (hk < 0) hk = 0;
        Collection<Osa> loytyneet = new ArrayList<Osa>();
        for (Osa osa : this) {
            if (WildChars.onkoSamat(osa.getAvain(hk), ehto)) loytyneet.add(osa);
        }
        return loytyneet;
    }
    
    
    /**
     * Iteraattori kaikkien osien läpikäymiseen.
     * @return Osaiteraattori.
     * @example
     * <pre name="test">
     * #PACKAGEIMPORT
     * #import java.util.*;
     * 
     * Osat osat = new Osat();
     * Osa testi11 = new Osa(2); osat.lisaa(testi11);
     * Osa testi25 = new Osa(1); osat.lisaa(testi25);
     * Osa testi15 = new Osa(2); osat.lisaa(testi15);
     * Osa testi09 = new Osa(1); osat.lisaa(testi09);
     * Osa testi99 = new Osa(2); osat.lisaa(testi99);
     * 
     * Iterator<Osa> i2=osat.iterator();
     * i2.next() === testi11;
     * i2.next() === testi25;
     * i2.next() === testi15;
     * i2.next() === testi09;
     * i2.next() === testi99;
     * i2.next() === testi25;   #THROWS NoSuchElementException
     * 
     * int n = 0;
     * int tnrot[] = {2,1,2,1,2};
     * 
     * for (Osa osa:osat) {
     *  osa.getTarinaNro() === tnrot[n]; n++;
     * }
     * n === 5;
     * </pre>
     */
    @Override
    public Iterator<Osa> iterator() {
        return alkiot.iterator();
    }
    
    
    /**
     * Haetaan kaikki tarinan osat.
     * @param tunnusnro Tunnusnumero tarinalle jonka osia haetaan.
     * @return Tietorakenne jossa on viitteet löydettyihin osiin.
     * @example
     * <pre name="test">
     * #import java.util.*;
     * 
     * Osat osat = new Osat();
     * Osa testi11 = new Osa(2); osat.lisaa(testi11);
     * Osa testi25 = new Osa(1); osat.lisaa(testi25);
     * Osa testi15 = new Osa(2); osat.lisaa(testi15);
     * Osa testi09 = new Osa(1); osat.lisaa(testi09);
     * Osa testi99 = new Osa(2); osat.lisaa(testi99);
     * Osa testi13 = new Osa(5); osat.lisaa(testi13);
     * 
     * List<Osa> loytyneet;
     * loytyneet = osat.annaOsat(3);
     * loytyneet.size() === 0;
     * loytyneet = osat.annaOsat(1);
     * loytyneet.size() === 2;
     * loytyneet.get(0) == testi25 === true;
     * loytyneet.get(1) == testi09 === true;
     * loytyneet = osat.annaOsat(5);
     * loytyneet.size() === 1;
     * loytyneet.get(0) == testi13 === true;
     * </pre>
     */
    public List<Osa> annaOsat(int tunnusnro) {
        List<Osa> loydetyt = new ArrayList<Osa>();
        for(Osa osa : alkiot)
            if (osa.getTarinaNro() == tunnusnro) loydetyt.add(osa);
        return loydetyt;
    }
    
    
    /**
     * Testiohjelma osille.
     * @param args Ei käytössä
     */
    public static void main(String[] args) {
        Osat osat = new Osat();
        Osa osa1 = new Osa();
        osa1.lisaaTiedot(1);
        Osa osa2 = new Osa();
        osa2.lisaaTiedot(1);
        Osa osa3 = new Osa();
        osa3.lisaaTiedot(2);
        Osa osa4 = new Osa();
        osa4.lisaaTiedot(2);
        
        osat.lisaa(osa1);
        osat.lisaa(osa2);
        osat.lisaa(osa3);
        osat.lisaa(osa4);
        
        System.out.println("======================Osat testi======================");
        
        List<Osa> osat2 = osat.annaOsat(2);
        
        for (Osa osa : osat2) {
            System.out.println(osa.getTarinaNro() + " ");
            osa.tulosta(System.out);
        }
    }
}