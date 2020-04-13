package tarinatilasto;

import java.io.*;
import java.util.*;

import fi.jyu.mit.ohj2.WildChars;

/**
 * Sarjojen luokka, joka osaa mm. tallentaa sarjojen tiedot.
 * @author Janne Taipalus ja Noora Jokela
 * @version 2.5.2019
 *
 */
public class Sarjat implements Iterable<Sarja> {

    private static final int MAX_SARJOJA = 5;
    private boolean muutettu = false;
    private int lkm = 0;
    private String tiedostonNimi = "";
    private Sarja alkiot[] = new Sarja[MAX_SARJOJA];
    
    
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
     * @example
     * <pre name="test">
     *  Sarjat sarjat = new Sarjat();
     *  Sarja testi1 = new Sarja(), testi2 = new Sarja();
     *  sarjat.getLkm() === 0;
     *  sarjat.lisaa(testi1); sarjat.getLkm() === 1;
     *  sarjat.lisaa(testi2); sarjat.getLkm() === 2;
     *  sarjat.lisaa(testi1); sarjat.getLkm() === 3;
     *  sarjat.anna(0) === testi1;
     *  sarjat.anna(1) === testi2;
     *  sarjat.anna(2) === testi1;
     *  sarjat.anna(1) == testi1 === false;
     *  sarjat.anna(1) == testi2 === true;
     *  sarjat.anna(3) === testi1; #THROWS IndexOutOfBoundsException
     * </pre>
     */
    public void lisaa(Sarja sarja){
        if (lkm >= alkiot.length) alkiot = Arrays.copyOf(alkiot, alkiot.length+10);
        alkiot[lkm] = sarja;
        lkm++;
        muutettu = true;
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
         * Annetaan seuraava jäsen.
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
        lisaa(sarja);
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
     * @param hakuehto Hakuehto.
     * @param k Etsittävän kentän indeksi
     * @return Tietorakenne löytyneistä jäsenistä.
     */
    public Collection<Sarja> etsi(String hakuehto, int k) {
        String ehto = "*";
        if (hakuehto != null && hakuehto.length() > 0 ) ehto = hakuehto;
        int hk = k;
        if (hk < 0) hk = 1;
        Collection<Sarja> loytyneet = new ArrayList<Sarja>();
        for (Sarja sarja : this) {
            if (WildChars.onkoSamat(sarja.getNimi(), ehto)) loytyneet.add(sarja);
        }
        return loytyneet;
    }
}