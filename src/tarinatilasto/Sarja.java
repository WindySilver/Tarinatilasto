package tarinatilasto;

import java.io.*;

import fi.jyu.mit.ohj2.Mjonot;

/**
 * Luokka sarjalle, joka määrittää sen, mitkä tarinat kuuluvat samaan jatkumoon.
 * @author Janne Taipalus & Noora Jokela
 * @version 2.5.2019
 */
public class Sarja implements Cloneable {

    private String nimi = "";
    private int tunnusNro;
    private static int seuraavaNro = 0;
    
    
    /**
     * Parametriton muodostaja.
     */
    public Sarja() {
        //
    }
    
    
    /**
     * Tehdään sarjasta identtinen klooni.
     * @return Kloonattu sarja.
     * @example
     * <pre name="test>
     * Sarja sarja = new Sarja();
     * sarja.parse("0|puhe");
     * Object kopio = sarja.clone();
     * kopio.toString() === sarja.toString();
     * sarja.parse("2| Menetetyt sanat");
     * kopio.toString().equals(sarja.toString)) === false;
     * kopio instanceof Sarja === true;
     */
    @Override
    public Sarja clone() throws CloneNotSupportedException {
        return (Sarja)super.clone();
    }
    
    
    @Override
    public boolean equals(Object sarja) {
        return this.toString().equals(sarja.toString());
    }
    
    
    /**
     * Testausta varten luotu muodostaja, joka ottaa tiedot parametreinaan.
     * @param tunnusNro Sarjan tunnusnumero.
     * @param nimi Sarjan nimi.
     */
    public Sarja(int tunnusNro, String nimi) {
        this.tunnusNro=tunnusNro;
        this.nimi=nimi;
        seuraavaNro++;
    }
    
    
    /**
     * @return Palauttaa sarjan nimen.
     * @example
     * <pre name="test">
     *  Sarja testisarja = new Sarja(2, "Testing");
     *  testisarja.getNimi() === "Testing";
     * </pre>
     */
    public String getNimi() {
        return nimi;
    }
    
    
    /**
     * Antaa sarjalle tunnusnumeron.
     * @return Sarjan tunnusnumero.
     * @example
     * <pre name="test">
     *  Sarja huntik = new Sarja();
     *  huntik.getTunnusNro() === 0;
     *  huntik.rekisteroi();
     *  huntik.getTunnusNro() === 1;
     */
    public int rekisteroi() {
        tunnusNro = seuraavaNro;
        seuraavaNro++;
        return tunnusNro;
    }
    
    
    /**
     * Apumetodi jolla saadaan täytettyä testiarvot sarjalle.
     */
    public void lisaaTiedot() {
        nimi = "Testaussarja";
    }
    
    
    /**
     * @return Sarjan tunnusnumero.
     * @example
     * <pre name="test">
     *  Sarja testisarja = new Sarja();
     *  testisarja.getTunnusNro() === 0;
     *  testisarja.rekisteroi();
     *  testisarja.getTunnusNro() === 2;
     * </pre>
     */
    public int getTunnusNro() {
        return tunnusNro;
    }
    
    
    /**
     * Palauttaa sarjan merkkijonona.
     * @return Sarja tolppaeroteltuna merkkijonona.
     * @example
     * <pre name="test">
     *  Sarja sarja = new Sarja();
     *  sarja.parse("   9   |   Testinimi   ");
     *  sarja.toString() === "9|Testinimi";
     * </pre>
     */
    @Override
    public String toString() {
        return tunnusNro + "|" + nimi;
    }
    
    
    /**
     * Tulostetaan sarjan tiedot.
     * @param out Tietovirta, johon tulostetaan.
     */
    public void tulosta(PrintStream out) {
        out.println("Sarjan nimi: " + nimi);
        out.println("Tunnusnumero: " + tunnusNro);
    }
    
    
    /**
     * Tulostetaan sarjan tiedot.
     * @param os Tietovirta johon tulostetaan.
     */
    public void tulosta(OutputStream os) {
        tulosta(new PrintStream(os));
    }


    /**
     * Parsii sarjan tiedot |-merkillä erotellusta merkkijonosta ja pitää huolen, että seuraavaNro on suurempi kuin tuleva tunnusNumero.
     * @param rivi Rivi, josta sarjan tiedot otetaan.
     * @example
     * <pre name="test">
     *  Sarja sarja = new Sarja();
     *  sarja.parse("   8   |   Testi");
     *  sarja.getTunnusNro() === 8;
     *  sarja.toString() === "8|Testi";
     *  
     *  sarja.rekisteroi();
     *  int n = sarja.getTunnusNro();
     *  sarja.parse(""+(n+20));
     *  sarja.rekisteroi();
     *  sarja.getTunnusNro() === n+20+1;
     * </pre>
     */
    public void parse(String rivi) {
        StringBuffer sb = new StringBuffer (rivi);
        setTunnusNro(Mjonot.erota(sb, '|', getTunnusNro()));
        nimi = Mjonot.erota(sb, '|', nimi);
        
    }


    private void setTunnusNro(int nro) {
        tunnusNro = nro;
        if (tunnusNro >= seuraavaNro) seuraavaNro = tunnusNro + 1;      
    }


    /**
     * Antaa sarjalle nimen.
     * @param s Nimi, joka sarjalle annetaan.
     * @return Null jos ok, muutoin virheilmoitus.
     */
    public String setNimi(String s) {
        nimi = s;
        return null;
    }
    
    
    /**
     * Testipääohjelma sarjalle.
     * @param args Ei käytössä
     */
    public static void main(String args[]) {
        Sarja series = new Sarja(), batman = new Sarja();
        series.rekisteroi();
        batman.rekisteroi();
        
        series.tulosta(System.out);
        batman.tulosta(System.out);
    }
}
