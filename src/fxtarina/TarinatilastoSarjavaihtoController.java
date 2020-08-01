package fxtarina;

import java.net.URL;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.ComboBoxChooser;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tarinatilasto.Sarja;
import tarinatilasto.Sarjat;
import tarinatilasto.Tarina;

/**
 * @author Noora Jokela & Janne Taipalus
 * @version 2.5.2019
 */
public class TarinatilastoSarjavaihtoController implements Initializable, ModalControllerInterface<Tarina> {

    @FXML private Label tarinaNimi;
    @FXML private ComboBoxChooser<Sarja> editSarja;
    @FXML private Label labelVirhe;
    
    
    @Override
    public void initialize(URL url, ResourceBundle bundle) {
        //
    }
    
    
    @FXML private void handleOK() {
        ModalController.closeStage(labelVirhe);
    }
    
    
    @FXML private void handleCancel() {
        tarinaKohdalla = null;
        ModalController.closeStage(labelVirhe);
    }
    
    
    //-------------------------------------------------
    
    
    private Tarina tarinaKohdalla;
    private static Sarjat sarjalista;
    private Sarja sarjaKohdalla;
    private int kentta = 0;
    
    
    /**
     * Luodaan sarjan vaihdon kysymisdialogi ja palautetaan sama tarina muutettuna tai null.
     * @param modalityStage Mille ollaan modaalisia.
     * @param tarina Tarina jota muokataan.
     * @param sarjat Lista sarjoista.
     * @param kentta Kenttä, joka saa fokuksen, kun sitä näytetään.
     * @return Muutettu tarina tai null.
     */
    public static Tarina kysyTarina(Stage modalityStage, Tarina tarina, Sarjat sarjat, int kentta) {
        sarjalista = sarjat;
        return ModalController.<Tarina, TarinatilastoSarjavaihtoController>showModal(TarinatilastoSarjavaihtoController.class.getResource("TarinatilastoSarjavaihtoGUI.fxml"), "Story Statistics", modalityStage, tarina, ctrl -> ctrl.setKentta(kentta));        
    }


    @Override
    public Tarina getResult() {
        return tarinaKohdalla;
    }
    
    
    private void setKentta(int kentta) {
        this.kentta=kentta;
    }


    @Override
    public void handleShown() {
        kentta = Math.max(tarinaKohdalla.ekaKentta(), Math.min(kentta, tarinaKohdalla.getKenttia()-1));
        editSarja.requestFocus();
    }


    @Override
    public void setDefault(Tarina oletus) {
        tarinaKohdalla = oletus;
        sarjaKohdalla = sarjalista.anna(tarinaKohdalla.getSarjaNumero());
        alusta();
        naytaTarina(tarinaKohdalla, sarjaKohdalla);
        
    }
    
    
    private void alusta() {
        luoSarjalista();
        editSarja.addSelectionListener(e -> kasitteleMuutos());
    }
    
    
    private void luoSarjalista() {
        for(Sarja sarja : sarjalista) {
            editSarja.add(sarja.getNimi(), sarja);
        }
        editSarja.setSelectedIndex(tarinaKohdalla.getSarjaNumero());
    }


    /**
     * Käsittelee muutoksen sarjaan.
     */
    protected void kasitteleMuutos() {
        if (tarinaKohdalla == null) return;
        Sarja valinta = null;
        valinta = editSarja.getSelectedObject();
        String indeksi = Integer.toString(valinta.getTunnusNro());
        tarinaKohdalla.aseta(2, indeksi);
    }
    
    
    private void naytaTarina(Tarina tarina, Sarja sarja) {
        if (tarina == null) return;
        tarinaNimi.setText(tarina.getAvain(1));
        editSarja.setSelectedIndex(sarja.getTunnusNro());
    }
}
