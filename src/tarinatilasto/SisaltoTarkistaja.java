package tarinatilasto;

/**
 * Tarkistaa, että jono sisältää vain valittuja merkkejä. Hyväksyy tyhjän jonon.
 * @author Noora Jokela ja Janne Taipalus
 * @version 2.5.2019
 */
public class SisaltoTarkistaja {
    
    /**
     * Numeroita vastaavat merkit.
     */
    public static final String NUMEROT = "0123456789";
   
    
    /**
     * Desimaalilukuun käyvät merkit.
     */
    public static final String DESIMAALINUMEROT = "0123456789,.";
    
    
    private final String sallitut;
    
    
    /** Luodaan tarkistaja.
     * @param sallitut Sallitut merkit.
     */
    public SisaltoTarkistaja(String sallitut) {
        this.sallitut = sallitut;
    }
    
    
    /**
     * Tyhjä konstruktori.
     */
    public SisaltoTarkistaja() {
        sallitut = null;
    }


    /**
     * Tarkistaa, onko jonossa vain sallittuja merkkejä.
     * @param jono Jono, jota tutkitaan.
     * @param sallitut Sallitut merkit.
     * @return Se, onko vain sallittuja merkkejä vai ei.
     * @example
     * <pre name="test">
     *   onkoVain("123","12")   === false;
     *   onkoVain("123","1234") === true;
     *   onkoVain("","1234") === true;
     * </pre> 
     */
    @SuppressWarnings("hiding")
    public boolean onkoVain(String jono, String sallitut) {
        for (int i=0;i<jono.length();i++)
    {
     if (sallitut.indexOf(jono.charAt(i))<0) return false;   
    }
    return true;
    }
    
    
    /**
     * Tarkistaa, että jono sisältää vain valittuja merkkejä.
     * @param jono Tutkittava jono.
     * @return Null jos on vain valittuja merkkejä, muutoin virheilmoitus.
     * @example
     *  <pre name="test">
     *   SisaltoTarkistaja t = new SisaltoTarkistaja("123");
     *   t.tarkista("12") === null;
     *   t.tarkista("14") === "Saa olla vain merkkejä: 123";
     *   t.tarkista("")   === null;
     * </pre>
     */
    public String tarkista(String jono) {
        if (onkoVain(jono, sallitut)) return null;
        return "Saa olla vain merkkejä " + sallitut;
    }
}