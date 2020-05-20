package fxtarina;

import java.net.URL;
import java.util.ResourceBundle;

import fi.jyu.mit.fxgui.Dialogs;
import fi.jyu.mit.fxgui.ModalController;
import fi.jyu.mit.fxgui.ModalControllerInterface;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tarinatilasto.Sarja;

/**
 * @author Noora Jokela ja Janne Taipalus
 * @version 20.5.2020
 *
 */
public class TarinatilastoSarjaController implements ModalControllerInterface<Sarja>, Initializable {

    @FXML private TextField editNimi;
    @FXML private Label labelVirhe;
    private Sarja palautettava;
    
    
    @Override
    public void initialize(URL url, ResourceBundle bunble) {
        //
    }
    
    
    @FXML private void handleOK() {
        if (sarjaKohdalla != null && sarjaKohdalla.getNimi().trim().equals("")) {
            naytaVirhe("Nimi ei saa olla tyhjä!");
            return;
        }
        palautettava = sarjaKohdalla;
        ModalController.closeStage(labelVirhe);
    }
    
    
    @FXML private void handleCancel() {
        ModalController.closeStage(labelVirhe);
    }

    
    //===================================
    
    
    private Sarja sarjaKohdalla;
    
    
    /**
     * Alustetaan ikkuna.
     */
    protected void alusta() {
        editNimi.clear();
        editNimi.setOnKeyReleased(e -> kasitteleMuutosSarjaan((TextField)(e.getSource())));
    }
    
    
    private void kasitteleMuutosSarjaan(TextField textField) {
        if (sarjaKohdalla == null) return;
        String s = textField.getText();
        String virhe = sarjaKohdalla.setNimi(s);
        if(virhe == null) {
            Dialogs.setToolTipText(textField, "");
            textField.getStyleClass().removeAll("virhe");
            naytaVirhe(virhe);
        }else {
            Dialogs.setToolTipText(textField, virhe);
            textField.getStyleClass().add("virhe");
            naytaVirhe(virhe);
        }
    }

    
    /**
     * Näyttää sarjan nimen ikkunassa.
     * @param sarja Sarja, jonka nimi näytetään.
     */
    public void naytaSarja(Sarja sarja) {
        if (sarja == null) return;
        editNimi.setText(sarja.getNimi());
    }
    
    
    @Override
    public void setDefault(Sarja oletus) {
        sarjaKohdalla = oletus;
        alusta();
        naytaSarja(sarjaKohdalla);
    }
    
    @Override
    public Sarja getResult() {
        return palautettava;
    }
    
    @Override
    public void handleShown() {
        editNimi.requestFocus();
    }
    
    
    private void naytaVirhe(String virhe) {
        if (virhe == null || virhe.isEmpty()) {
            labelVirhe.setText("");
            labelVirhe.getStyleClass().removeAll("virhe");
            return;
        }
        labelVirhe.setText(virhe);
        labelVirhe.getStyleClass().add("virhe");
    }
    
    /**
     * Luodaan sarjan kysymisdialogi ja palautetaan sama tietue muutettuna tai null
     * @param modalityStage mille ollaan modaalisia
     * @param oletus mitä dataa näytetään oletuksena
     * @return null jos painetaan cancel, muuten täytetty tietue
     */
    public static Sarja kysySarja(Stage modalityStage, Sarja oletus) {
        return ModalController.<Sarja, TarinatilastoSarjaController>showModal(TarinatilastoSarjaController.class.getResource("TarinatilastoUusiSarjaGUI.fxml"), "Tarinatilasto", modalityStage, oletus, null);
    }
}
