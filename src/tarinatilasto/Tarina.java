package tarinatilasto;

import java.io.*;
import java.util.Random;
import java.util.Comparator;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Luokka tarinalle.
 * @author Noora Jokela ja Janne Taipalus
 * @version 6.7.2019
 *
 */
public class Tarina implements Cloneable, Tietue {
    
    private int idNumero;
    private String nimi = "";
    private int sarjaID;
    private String tekija= "";
    private String kieli= "";
    private double sanamaara = 0;
    private double osienMaara =0;
    private double sivumaara = 0;
    private String julkaisut = "";
    private String lisatietoja = "";
    
    private static int seuraavaNumero = 0;
    

    /**
     * Tarinoiden vertailija.
     */
    public static class Vertailija implements Comparator<Tarina> {
        
        private int k;
        
        
        @SuppressWarnings("javadoc")
        public Vertailija(int k) {
            this.k = k;
        }
        
        
        @Override
        public int compare(Tarina tarina1, Tarina tarina2) {
            
            if(k==1 && (Character.isDigit(tarina1.getAvain(1).charAt(0)) || Character.isDigit(tarina2.getAvain(1).charAt(0)))){
                return Mjonot.erotaInt(tarina1.getAvain(1), 0) - Mjonot.erotaInt(tarina2.getAvain(1), 0);
            }            
            return tarina1.getAvain(k).compareToIgnoreCase(tarina2.getAvain(k));
        }
    }

    
    /**
     * Apumetod, jolla saadaan täytettyä testiarvot tarinalle.
     * @param sarjaId Tarinan sarjan ID-numero.
     */
    public void lisaaTiedot(int sarjaId) {
       Random rand = new Random();
       nimi="Testi." + rand.nextInt(9999);
       sarjaID = sarjaId;
       tekija = "Testi Testaaja";
       kieli = "suomi";
       sanamaara=rand.nextInt(300000);
       osienMaara=rand.nextInt(120);
       sivumaara = rand.nextInt(2000);
       julkaisut="DeviantArt";
       lisatietoja="-";       
    }
    
    
    /**
     * Tyhjä muodostaja tarinalle.
     */
    public Tarina() {
        //
    }
    
    
    /**
     * Tehdään tarinasta identtinen klooni.
     * @return Kloonattu tarina.
     * <pre name="test">
     * #THROWS CloneNotSupportedException
     * Tarina tarina = new Tarina();
     * tarina.parse(" 1 | Oho | 2 ");
     * Tarina kopio = tarina.clone();
     * kopio.toString() === tarina.toString();
     * kopio.parse("   2 | Hups   | 4");
     * kopio.toString().equals(tarina.toString()) === false;
     * </pre>
     */
    @Override
    public Tarina clone() throws CloneNotSupportedException {
        Tarina uusi;
        uusi = (Tarina) super.clone();
        return uusi;
    }
    
    
    /**
     * @return Tarinan sarjan numero.
     * @example
     * <pre name="test">
     *  Tarina testi = new Tarina();
     *  testi.lisaaTiedot(5);
     *  testi.getSarjaNumero() === 5;
     * </pre>
     */
    public int getSarjaNumero() {
        return sarjaID;
    }
    
    
    /**
     * @return Tarinan id-numero.
     * @example
     * <pre name="test">
     * Tarina testi = new Tarina();
     * testi.rekisteroi();
     * testi.getIdNumero() === 1;
     * </pre>
     */
    public int getIdNumero() {
        return idNumero;
    }


    /**
     * Lisää tarinan rekisteriin ja antaa sille ID-numeron.
     * @return Tarinan ID-numero.
     * @example
     * <pre name="test">
     *  Tarina tarina1 = new Tarina();
     *  tarina1.getIdNumero() === 0;
     *  tarina1.rekisteroi();
     *  Tarina tarina2 = new Tarina();
     *  tarina2.rekisteroi();
     *  int luku1 = tarina1.getIdNumero();
     *  int luku2 = tarina2.getIdNumero();
     *  luku1 === luku2 - 1;
     * </pre>
     */
    public int rekisteroi() {
        idNumero=seuraavaNumero;
        seuraavaNumero++;
        return idNumero;
        
    }
    
    
    /**
     * Palauttaa tarinan tiedot merkkijonona.
     * @return Tarina tolppaeroteltuna merkkijonona.
     * @example
     * <pre name="test">
     *  Tarina tarina = new Tarina();
     *  tarina.parse("0 |   Testi   |   6   |   Lovecraft   |   englanti    |   3000    |   18  |   3000    |   fyysinen    |   ei lisätietoja");
     *  tarina.toString() === "0|Testi|6|Lovecraft|englanti|3000.0|18.0|3000|fyysinen|ei lisätietoja";
     * </pre>
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        String erotin = "";
        for (int k = 0; k < getKenttia(); k++) {
            sb.append(erotin);
            sb.append(getAvain(k));
            erotin = "|";
        }
        return sb.toString();
    }
    
    
     /**
     * Tulostetaan tarinan tiedot.
     * @param os Tietovirha, johon tulostetaan.
     */
    public void tulosta(OutputStream os) {
        tulosta(new PrintStream(os));
        
    }
    
    
    /**
     * Tulostetaan tarinan tiedot.
     * @param out Tietovirta, johon tulostetaan.
     * @param sarjaNimi Tulostettavan tarinan sarjan nimi
     */
    public void tulosta(PrintStream out, String sarjaNimi) {
        out.println("Tarinan nimi: " + nimi);
        out.println("Tarina kuuluu sarjaan: " + sarjaNimi);
        out.println("Tarinan on kirjoittanut: " + tekija);
        out.println("Tarinan kieli on: " + kieli);
        out.println("Tarinassa on " + 
        sanamaara + " sanaa ja " + osienMaara + " osaa.");
        out.println("Tarinassa on " + sivumaara + " sivua.");
        out.println("Tarina on julkaistu seuraavissa paikoissa: "
        + julkaisut);
        out.println("Lisätietoja tarinasta: " + lisatietoja);
    }


    /**
     * Parsii tarinan tiedot |-merkillä erotellusta merkkijonosta ja pitää huolen, että seuraavaNro on suurempi kuin tuleva IdNumero.
     * @param rivi Rivi josta tarinan tiedot otetaan.
     * @example
     * <pre name="test">
     *  Tarina tarina = new Tarina();
     *  tarina.parse("  3   |   Testi   |   5   |   Testaaja    |   ranska  |   255     |   2   |  398 |    some    |   ei lisätietoja");
     *  tarina.getIdNumero() === 3;
     *  tarina.toString() === "3|Testi|5|Testaaja|ranska|255.0|2.0|398|some|ei lisätietoja";
     *  
     *  tarina.rekisteroi();
     *  int n = tarina.getIdNumero();
     *  tarina.parse(""+(n+20));
     *  tarina.rekisteroi();
     *  tarina.getIdNumero() === n+20+1;
     * </pre>
     */
    public void parse(String rivi) {
        StringBuffer sb = new StringBuffer(rivi);
        for (int k = 0; k < getKenttia(); k++) {
            aseta(k, Mjonot.erota(sb, '|'));
        }
    }


    /**
     * Asettaa ID-numeron ja samalla varmistaa, että seuraava numero on suurempi, kuin tähän mennessä suurin.
     * @param nro asetettava ID-numero.
     */
    private void setIdNumero(int nro) {
        idNumero = nro;
        if (idNumero >= seuraavaNumero) seuraavaNumero = idNumero+1;
        
    }

    
    /**
     * @return Tarinan sanamäärä.
     */
    public double getSanamaara() {
        return sanamaara;
    }


    /**
     * @return Tarinan osien määrä.
     */
    public double getOsienMaara() {
        return osienMaara;
    }


    /**
     * @return Tarinan sivumäärä.
     */
    public double getSivumaara() {
        return sivumaara;
    }

    
    /**
     * Palauttaa tarinan kenttien lukumäärän.
     * @return Tarinan kenttien lukumäärä.
     */
    @Override
    public int getKenttia() {
        return 10;
    }

    
    /**
     * Eka kenttä, joka on mielekäs kysyttäväksi.
     * @return Ekan kentän indeksi.
     */
    @Override
    public int ekaKentta() {
        return 1;
    }
    
    
    /**
     * Antaa k:n kentän sisällön merkkijonona.
     * @param k Monennenko kentän sisältö palautetaan.
     * @return Kentän sisältö merkkijonona.
     */
    @Override
    public String getAvain(int k) {
        switch (k) {
        case 0: return "" + idNumero;
        case 1: return "" + nimi;
        case 2: return "" + sarjaID;
        case 3: return "" + tekija;
        case 4: return "" + kieli;
        case 5: return "" + sanamaara;
        case 6: return "" + osienMaara;
        case 7: return "" + sivumaara;
        case 8: return "" + julkaisut;
        case 9: return "" + lisatietoja;
        default: return "Äääliö";
        }
    }
    
    
    /**
     * Asettaa k:nnen kentän arvoksi parametrina tuodun merkkijonon arvon.
     * @param k Monennenko kentän arvo asetetaan.
     * @param jono Jono joka asetetaan kentän arvoksi.
     * @return null jos asettaminen onnistu, muuten vastaava virheilmoitus.
     * @example
     * <pre name="test">
     *  Tarina tarina = new Tarina();
     *  tarina.aseta(1, "Testi tarina") === null;
     *  tarina.aseta(8, "Deviantart") === null;
     * </pre>
     */
    @Override
    public String aseta(int k, String jono) {
        String tjono = jono.trim();
        StringBuffer sb = new StringBuffer(tjono);
        switch (k) {
        case 0:
            setIdNumero(Mjonot.erota(sb, '§', getIdNumero()));
            return null;
        case 1:
            nimi = tjono;
            return null;
        case 2:
            sarjaID = Mjonot.erota(sb, '§', getSarjaNumero());
            return null;
        case 3:
            tekija = tjono;
            return null;
        case 4:
            kieli = tjono;
            return null;
        case 5:
            try{
                sanamaara = Mjonot.erotaEx(sb, '§', getSanamaara());;
            }catch (NumberFormatException e)
            {
                return "Sanamäärä on väärin " + e.getMessage();
            }
            return null;
        case 6:
            try{
                osienMaara = Mjonot.erotaEx(sb, '§', getOsienMaara());
            }catch (NumberFormatException e)
            {
                return "Osien mmäärä on väärin " + e.getMessage();
            }
            return null;
        case 7:
            try{
                sivumaara = Mjonot.erotaEx(sb, '§', getSivumaara());
            }catch (NumberFormatException e)
            {
                return "Sivumäärä on väärin " + e.getMessage();
            }
            return null;
        case 8:
            julkaisut = tjono;
            return null;
        case 9: 
            lisatietoja = tjono;
            return null;
        default:
            return "ÄÄliö";
        }
    }
    
    
    /**
     * Palauttaa k:tta tarinan kenttää vastaavan kysymyksen.
     * @param k Kuinka monennen kentän kysymys palautetaan.
     * @return K:netta kenttää vastaava kysymys.
     */
    @Override
    public String getKysymys(int k) {
        switch (k) {
        case 0: return "idNumero";
        case 1: return "Nimi";
        case 2: return "Sarjan nimi";
        case 3: return "Tekijä";
        case 4: return "Kieli";
        case 5: return "Sanamäärä";
        case 6: return "Osien määrä";
        case 7: return "Sivumäärä";
        case 8: return "Julkaisut";
        case 9: return "Lisätietoja";
        default: return "Ääliö";
        }
    }
    
    
    /**
     * Tutkii, ovatko tarinan tiedot samat kuin parametrina tuodun tarinan tiedot.
     * @param tarina Tarina johon verrataan.
     * @return true jos kaikki tiedot samat, false muuten.
     * @example
     * <pre name="test">
     *  Tarina tarina1 = new Tarina();
     *  tarina1.parse(" 3   |   Stoori  |   9");
     *  Tarina tarina2 = new Tarina();
     *  tarina2.parse("3    |   Stoori  |   9");
     *  Tarina tarina3 = new Tarina();
     *  tarina3.parse("3    |   Stoori  |   4");
     *  
     *  tarina1.equals(tarina2) === true;
     *  tarina2.equals(tarina1) === true;
     *  tarina1.equals(tarina3) === false;
     *  tarina3.equals(tarina2) === false;
     * </pre>
     */
    public boolean equals(Tarina tarina) {
        if (tarina == null) return false;
        for (int k = 0; k < getKenttia(); k++) {
            if ( !getAvain(k).equals(tarina.getAvain(k))) return false;
        }
        return true;
    }
    
    
    @Override
    public boolean equals(Object tarina) {
        if (tarina instanceof Tarina) return equals((Tarina)tarina);
        return false;
    }
    
    
    @Override
    public int hashCode() {
        return idNumero;
    }
    
    
    /**
     * Testiohjelma tarinoille.
     * @param args ei käytössä
     */
    public static void main(String[] args)
    {
        Tarina testi1 = new Tarina();
        testi1.rekisteroi();
        testi1.tulosta(System.out);
        testi1.lisaaTiedot(1);
        testi1.tulosta(System.out);
    }
}