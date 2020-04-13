package tarinatilasto;

/**
 * Poikkeusluokka tietorakenteesta aiheutuville poikkeuksille.
 * @author Noora Jokela & Janne Taipalus
 * @version 2.5.2019
 */
public class SailoException extends Exception {
    
    private static final long serialVersionUID = 1L;

    
    /**
     * Poikkeuksen muodostaja.
     * @param viesti Poikkeuksen viesti, jota käytetään.
     */
    public SailoException(String viesti)
    {
        super(viesti);
    }
}
