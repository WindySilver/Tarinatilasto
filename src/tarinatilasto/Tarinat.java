package tarinatilasto;

import java.io.*;
import java.util.*;

import fi.jyu.mit.ohj2.WildChars;

/**
 * Tarinoiden luokka, joka osaa mm. tallentaa tarinat.
 * @author Noora Jokela & Janne Taipalus
 * @version 2.5.2019
 */
public class Tarinat implements Iterable<Tarina> {
   
    private static final int MAX_TARINOITA = 10;
    private int lkm = 0;
    private String tiedostonNimi="";
    private Tarina alkiot[] = new Tarina[MAX_TARINOITA];
    private boolean muutettu = false;
    
    
    /**
     * Oletusmuodostaja tarinoille.
     */
    public Tarinat()
    {
        //
    }
    
    
    /**
     * @return Palauttaa taulukon pituuden.
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
     * @return Tallennustiedoston nimi, jota käytetään tallennukseen.
     */
    public String getTiedostonPerusnimi() {
        return tiedostonNimi;
    }
    

    /**
     * Palauttaa viitteen i:nteen tarinaan.
     * @param i Monennenko tarinan viitettä pyydetään.
     * @return Viite i:teen jäseneen.
     * @throws IndexOutOfBoundsException jos i ei ole sallitulla alueella.
     */
    public Tarina anna(int i) throws IndexOutOfBoundsException {
        if (i<0||i>= lkm) throw new IndexOutOfBoundsException("Virheellinen indeksi: " + i);
        return alkiot[i];
    }

    
    /**
     * Lisää uuden tarinan tietorakenteeseen. Ottaa tarinan omistukseensa.
     * @param tarina Lisättävän tarinan viite.
     * @example
     * <pre name="test">
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
    public void lisaa(Tarina tarina) {
        if (lkm >=alkiot.length) alkiot = Arrays.copyOf(alkiot, alkiot.length+10);
        alkiot[lkm] = tarina;
        lkm++;
        muutettu = true;
    }
    
    
    /**
     * Tallentaa tarinoiden tiedot tiedostoon.
     * @throws SailoException jos tallennus epäonnistuu.
     */
    public void tallenna() throws SailoException
    {
        if (!muutettu) return;
        
        File fbak = new File(getBakNimi());
        File ftied = new File(getTiedostonNimi());
        fbak.delete();
        ftied.renameTo(fbak);
        
        try (PrintWriter fo = new PrintWriter(new FileWriter(ftied.getCanonicalPath()))){
            fo.println("; Kenttien järjestys tiedostossa on seuraava:");
            fo.println(";id|tarinan nimi|sarjan id|tekijä|kieli|sanamäärä|osien määrä|sivumäärä|julkaisut|lisätiedot|");
            for (Tarina tarina : this) {
                fo.println(tarina.toString());
            }
        } catch (FileNotFoundException e ) {
            throw new SailoException("Tiedoston " + ftied.getName() + " kirjoittamisessa ongelmia");
        } catch (IOException e ) { 
            throw new SailoException("Tiedosto " + ftied.getName() + " ei aukea");
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
     * Palauttaa tarinoiden lukumäärän.
     * @return Tarinoiden lukumäärä.
     */
    public int getLkm() {
        return lkm;
    }
    
    
    /**
     * Lukee tarinoiden tiedot tiedostosta.
     * @throws SailoException jos lukeminen epäonnistuu.
     * @example
     * <pre name="test">
     * #THROWS SailoException
     * #import java.io.File;
     * #import java.util.Iterator;
     * 
     * Tarinat tarinat = new Tarinat();
     * Tarina testi1 = new Tarina(), testi2 = new Tarina();
     * testi1.lisaaTiedot(1);
     * testi2.lisaaTiedot(2);
     * String hakemisto = "Testistoorit";
     * String tiedNimi = hakemisto+"/nimet";
     * File ftied = new File(tiedNimi+".dat");
     * File dir = new File(hakemisto);
     * dir.mkdir();
     * tarinat.setTiedostonPerusnimi(tiedNimi);
     * ftied.delete();
     * tarinat.lueTiedostosta(); #THROWS SailoException
     * tarinat.lisaa(testi1);
     * tarinat.lisaa(testi2);
     * tarinat.tallenna();
     * tarinat = new Tarinat();
     * tarinat.setTiedostonPerusnimi(tiedNimi);
     * tarinat.lueTiedostosta();
     * Iterator<Tarina> i = tarinat.iterator();
     * i.next().toString() === testi1.toString();
     * i.next().toString() === testi2.toString();
     * i.hasNext() === false;
     * tarinat.lisaa(testi2);
     * tarinat.tallenna();
     * ftied.delete();
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
                Tarina tarina = new Tarina();
                tarina.parse(rivi);
                lisaa(tarina);
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
     * Poistaa tarinan, jolla on valittu tunnusnumero.
     * @param id Poistettavan tarinan tunnusnumero.
     * @return 1 jos poistettiin, 0 jos ei löydy.
     * @example
     * <pre name="test">
     *  #THROWS SailoException
     *  Tarinat tarinat = new Tarinat();
     *  Tarina testi1 = new Tarina(), testi2 = new Tarina(), testi3 = new Tarina();
     *  testi1.rekisteroi(); testi2.rekisteroi(); testi3.rekisteroi();
     *  ind id1 = testi1.getTunnusNro();
     *  tarinat.lisaa(testi1); tarinat.lisaa(testi2); tarinat.lisaa(testi3);
     *  tarinat.poista(id1+1) === 1;
     *  tarinat.annaId(id1+1) === null; tarinat.getLkm() === 2;
     *  tarinat.poista(id1) === 1; tarinat.getLkm() === 1;
     *  tarinat.poista(id1+3) === 0; tarinat.getLkm() === 1;
     * </pre>
     */
    public int poista(int id) {
        int ind = etsiId(id);
        if (ind < 0) return 0;
        lkm--;
        for (int i = ind; i < lkm; i++) {
            alkiot[i] = alkiot[i+1];
        }
        alkiot[lkm] = null;
        muutettu = true;
        return 1;
    }
    
    
    /**
     * Etsii tarinan id:n perusteella.
     * @param id Tunnusnumero, jonka mukaan etsitään.
     * @return Tarina, jolla etsittävä id, tai null.
     * @example
     * <pre name="test">
     *  #THROWS SailoException
     *  Tarinat tarinat = new Tarinat();
     *  Tarina testi1 = new Tarina(), testi2 = new Tarina(), testi3 = new Tarina();
     *  testi1.rekisteroi(); testi2.rekisteroi(); testi3.rekisteroi();
     *  ind id1 = testi1.getTunnusNro();
     *  tarinat.lisaa(testi1); tarinat.lisaa(testi2); tarinat.lisaa(testi3);
     *  tarinat.annaId(id1  ) == testi1 === true;
     *  tarinat.annaId(id1+1) == testi2 === true;
     *  tarinat.annaId(id1+2) == testi3 === true;
     * </pre>
     */
    public Tarina annaId(int id) {
        for (Tarina tarina : this) {
            if (id == tarina.getIdNumero()) return tarina;
        }
        return null;
    }
    
    
    /**
     * Etsii tarinan id:n perusteella.
     * @param id Tunnusnumero, jonka mukaan etsitään.
     * @return Löytyneen tarinan indeksi tai -1 jos ei löydy.
     * @example
     * <pre name="test">
     *  #THROWS SailoException
     *  Tarinat tarinat = new Tarinat();
     *  Tarina testi1 = new Tarina(), testi2 = new Tarina(), testi3 = new Tarina();
     *  testi1.rekisteroi(); testi2.rekisteroi(); testi3.rekisteroi();
     *  ind id1 = testi1.getTunnusNro();
     *  tarinat.lisaa(testi1); tarinat.lisaa(testi2); tarinat.lisaa(testi3);
     *  tarinat.etsiId(id1+1) === 1;
     *  tarinat.etsiId(id1+2) === 2;
     * </pre>
     */
    public int etsiId(int id) {
        for (int i = 0; i < lkm; i++) {
            if (id == alkiot[i].getIdNumero()) return i;
        }
        return -1;
    }
    
    
    /**
     * @return Tallennustiedoston nimi.
     */
    public String getTiedostonNimi() {
        return tiedostonNimi+".dat";
    }
    
    
    /**
     * @author Janne Taipalus & Noora Jokela
     * @version 2.5.2019
     *
     */
    public class TarinatIterator implements Iterator<Tarina> {
        
        private int kohdalla = 0;
        
        
        /**
         * Onko olemassa seuraavaa tarinaa.
         *@see java.util.Iterator#hasNext()
         *@return true, jos on lisää tarinoita.
         */
        @Override
        public boolean hasNext() {
            return kohdalla < getLkm();
        }
        
        
        /**
         * Annetaan seuraava tarina.
         * @return Seuraava tarina.
         * @throws NoSuchElementException jos seuraavaa alkiota ei ole
         * @see java.util.Iterator#next()
         */
        @Override
        public Tarina next() throws NoSuchElementException {
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
            throw new UnsupportedOperationException("Me ei poisteta");
        }
    }


    /**
     * Palautetaan iteraattori tarinoista.
     * @return Tarinaiteraattori.
     */
    @Override
    public Iterator<Tarina> iterator(){
        return new TarinatIterator();
    }
    
    
    /**
     * @param hakuehto Hakuehto.
     * @param k Etsittävän kentän indeksi.
     * @return Tietorakenne löytyneistä tarinoista.
     */
    public Collection<Tarina> etsi(String hakuehto, int k) {
        String ehto = "*";
        if (hakuehto != null && hakuehto.length() > 0 ) ehto = hakuehto;
        int hk = k;
        if (hk < 0) hk = 0;
        List<Tarina> loytyneet = new ArrayList<Tarina>();
        for (Tarina tarina : this) {
            if (WildChars.onkoSamat(tarina.getAvain(hk), ehto)) loytyneet.add(tarina);
        }
        Collections.sort(loytyneet, new Tarina.Vertailija(hk));
        return loytyneet;
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
                muutettu = true;
                return;
            }
        }
        lisaa(tarina);
    }

    
    /**
     * Testiohjelma tarinoille.
     * @param args ei käytössä
     */
    public static void main(String args[])
    {
        Tarinat tarinat = new Tarinat();
        
        Tarina testi1 = new Tarina();
        testi1.rekisteroi();
        testi1.tulosta(System.out);
        testi1.lisaaTiedot(1);
        testi1.tulosta(System.out);
        
        tarinat.lisaa(testi1);
        tarinat.lisaa(testi1);
        
        System.out.println("===============Tarinat testi ============");
        for(int i = 0;i<tarinat.getLkm();i++)
        {
            Tarina tarina = tarinat.anna(i);
            System.out.println("Tarina numero: " + i);
            tarina.tulosta(System.out);
        }
    }
}